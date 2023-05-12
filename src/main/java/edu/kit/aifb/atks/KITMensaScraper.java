package edu.kit.aifb.atks;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

public class KITMensaScraper {

    private final Clock clock;
    private final HttpClient http;
    private final Map<Tuple<MensaLocation, LocalDate>, List<MensaMeal>> cache;
    private boolean noCache;

    private static final String BASE_URL = "https://www.sw-ka.de/en/hochschulgastronomie/speiseplan";

    public KITMensaScraper() {
        clock = Clock.systemDefaultZone();
        cache = new HashMap<>();
        http = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public KITMensaScraper(boolean noCache) {
        this();
        this.noCache = noCache;
    }

    public List<MensaMeal> fetchMeals(MensaLocation location, LocalDate day) {
        var cacheKey = new Tuple<>(location, day);
        if (!noCache && cache.containsKey(cacheKey)) {
            return cloneMeals(cache.get(cacheKey));
        }

        if (day.isBefore(LocalDate.now(clock))) {
            throw new MensaScraperException("you can only fetch data from today or later");  // TODO: proper exceptions
        }

        final URI url = URI.create(String.format("%s/%s?kw=%d", BASE_URL, location.getKey(), day.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())));
        final HttpResponse<InputStream> response;
        try {
            response = http.send(HttpRequest.newBuilder()
                            .GET()
                            .uri(url)
                            .header("Accept", "text/html")
                            .build(),
                    HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() >= 400) {
                throw new MensaScraperException(String.format("failed to fetch data, got status %d", response.statusCode()));
            }

            final Document doc = Jsoup.parse(response.body(), StandardCharsets.UTF_8.name(), response.uri().toString());

            final List<MensaMeal> meals = parseMeals(doc, day);
            cache.put(cacheKey, cloneMeals(meals));
            return meals;
        } catch (IOException | InterruptedException e) {
            throw new MensaScraperException("failed to fetch data", e);
        }
    }

    private static List<MensaMeal> parseMeals(Element root, LocalDate day) {
        final List<LocalDate> availableDays = root.selectXpath("//a[contains(@id, 'canteen_day_nav_')]")
                .stream()
                .map(e -> e.attr("rel"))
                .map(LocalDate::parse)
                .collect(Collectors.toList());

        final int dateIndex = availableDays.indexOf(day);
        if (dateIndex < 0) {
            throw new MensaScraperException(String.format("failed to fetch data for %s, maybe too far in the future?", day.toString()));
        }

        final Elements dayRows = root.selectXpath(String.format("//div[@id='canteen_day_%d']/table/tbody/tr", dateIndex + 1));
        return dayRows.stream()
                .parallel()
                .map(KITMensaScraper::parseSingleLine)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private static List<MensaMeal> parseSingleLine(Element el) {
        final List<MensaMeal> meals = new LinkedList<>();

        final String lineTitle = el.selectXpath("./td/div").text().toLowerCase();
        // TODO: use jdk 17 pattern matching for this
        final MensaLine line;
        if (lineTitle.contains("linie 1")) line = MensaLine.LINIE_1;
        else if (lineTitle.contains("linie 2")) line = MensaLine.LINIE_2;
        else if (lineTitle.contains("linie 3")) line = MensaLine.LINIE_3;
        else if (lineTitle.contains("linie 4")) line = MensaLine.LINIE_4;
        else if (lineTitle.contains("linie 5")) line = MensaLine.LINIE_5;
        else if (lineTitle.contains("linie 6")) line = MensaLine.LINIE_6;
        else if (lineTitle.contains("cafeteria")) line = MensaLine.CAFETERIA;
        else if (lineTitle.contains("schnitzel")) line = MensaLine.SCHNITZELBAR;
        else if (lineTitle.contains("[kœri]werk")) line = MensaLine.KOERIWERK;
        else if (lineTitle.contains("[pizza]werk")) line = MensaLine.PIZZAWERK;
        else line = MensaLine.UNKNOWN;

        final Elements mealRows = el.selectXpath(".//table[@class='meal-detail-table']/tbody/tr");

        MensaMeal currentMeal = null;
        for (Element mealRow : mealRows) {
            final boolean isNutrition = !mealRow.selectXpath(".//td[contains(@class, 'nutrition_facts_row')]").isEmpty();

            if (isNutrition) {
                // parse nutrition info
                if (currentMeal == null) {
                    throw new MensaScraperException("got nutrition table without preceeding meal");
                }
                currentMeal.setKcal(parseKcal(mealRow));
                currentMeal.setProteins(parseProteins(mealRow));
                currentMeal.setCarbs(parseCarbs(mealRow));
                currentMeal.setSugar(parseSugar(mealRow));
                currentMeal.setFat(parseFat(mealRow));
                currentMeal.setSaturated(parseSaturated(mealRow));
                currentMeal.setSalt(parseSalt(mealRow));
            } else {
                // parse meal info
                if (currentMeal != null) {
                    meals.add(currentMeal);
                }

                currentMeal = new MensaMeal();
                currentMeal.setName(parseMealName(mealRow));
                currentMeal.setPrice(parseMealPrice(mealRow));
                currentMeal.setType(parseMealType(mealRow));
            }
        }

        meals.forEach(m -> m.setLine(line));

        return meals;
    }

    private static String parseMealName(Element el) {
        return el.selectXpath(".//td[contains(@class, 'menu-title')]//b").text();
    }

    private static float parseMealPrice(Element el) {
        final String priceText = el.selectXpath(".//span[contains(@class, 'price_1')]").text()
                .replace("€", "")
                .replace(",", ".")
                .strip();
        return !priceText.isEmpty() ? Float.parseFloat(priceText) : 0.0f;
    }

    private static MensaMealType parseMealType(Element el) {
        if (!el.selectXpath(".//img[contains(@title, 'Schweinefleisch')]").isEmpty()) return MensaMealType.PORK;
        if (!el.selectXpath(".//img[contains(@title, 'Rindfleisch')]").isEmpty()) return MensaMealType.BEEF;
        if (!el.selectXpath(".//img[contains(@title, 'vegetarisch')]").isEmpty()) return MensaMealType.VEGETARIAN;
        if (!el.selectXpath(".//img[contains(@title, 'MSC')]").isEmpty()) return MensaMealType.MSC;
        if (!el.selectXpath(".//img[contains(@title, 'vegan')]").isEmpty()) return MensaMealType.VEGAN;
        return MensaMealType.NONE;
    }

    private static float parseKcal(Element el) {
        final String kcalText = el.selectXpath(".//div[@class='energie']/div[2]").text()
                .split("/")[1]
                .replace("kcal", "")
                .strip();
        return !kcalText.isEmpty() ? Float.parseFloat(kcalText) : 0.0f;
    }

    private static float parseProteins(Element el) {
        return parseNutrient(el, "proteine");
    }

    private static float parseCarbs(Element el) {
        return parseNutrient(el, "kohlenhydrate");
    }

    private static float parseSugar(Element el) {
        return parseNutrient(el, "zucker");
    }

    private static float parseFat(Element el) {
        return parseNutrient(el, "fett");
    }

    private static float parseSaturated(Element el) {
        return parseNutrient(el, "gesaettigt");
    }

    private static float parseSalt(Element el) {
        return parseNutrient(el, "salz");
    }

    private static float parseNutrient(Element el, String key) {
        final String nutriText = el.selectXpath(String.format(".//div[@class='%s']/div[2]", key)).text()
                .replace("g", "")
                .strip();
        return Float.parseFloat(nutriText);
    }

    private static List<MensaMeal> cloneMeals(List<MensaMeal> meals) {
        return meals.stream().map(MensaMeal::copy).collect(Collectors.toList());
    }
}
