package edu.kit.aifb.atks.mensascraper.lib;

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

/**
 * A scraper to fetch the meal menu for <a href="https://www.sw-ka.de/en/hochschulgastronomie/speiseplan">Studierendenwerk Karlsruhe</a> canteens.
 */
public class KITMensaScraper {

    private final Clock clock;
    private final HttpClient http;
    private final Map<Tuple<MensaLocation, LocalDate>, List<MensaMeal>> cache;
    private boolean noCache;

    private static final String BASE_URL = "https://www.sw-ka.de/en/hochschulgastronomie/speiseplan";

    /**
     * Create new default scraper instance.
     */
    public KITMensaScraper() {
        clock = Clock.systemDefaultZone();
        cache = new HashMap<>();
        http = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    /**
     * Create new scraper instance and control caching behavior.
     *
     * @param noCache Whether to cache per-day meal menus. If {@code true}, requesting meals for same canteen and same day will only result in one fetch operation.
     */
    public KITMensaScraper(boolean noCache) {
        this();
        this.noCache = noCache;
    }

    /**
     * Fetch list of meals for a given canteen and given day. Currently, only "Mensa am Adenauerring" is supported. Note that you cannot request data for past days.
     *
     * @param location Which canteen (aka. Mensa) to request meals for.
     * @param day      Which date to request meals for. Must be larger or equal than today ({@code LocalDate.now()}). Usually, only data for the upcoming 5 weeks is available.
     * @return List of meals or empty list if no data is available.
     * @throws MensaScraperException Thrown if requested date is invalid or if anything else went wrong while downloading and parsing the menu.
     */
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
            return List.of();
        }

        final Elements dayRows = root.selectXpath(String.format("//div[@id='canteen_day_%d']/table/tbody/tr", dateIndex + 1));
        Optional.ofNullable(dayRows.first()).ifPresent(d -> d.parents().remove());
        return dayRows.stream()
                .parallel()
                .map(KITMensaScraper::parseSingleLine)
                .flatMap(Collection::stream)
                .filter(m -> m.getPrice() > 0)
                .distinct()
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
            final boolean isDetailsSection = mealRow.selectXpath("./td[1]").hasClass("nutrition_facts_row");

            if (isDetailsSection) {
                // nutrition info, etc. always comes second, after the meal entry
                if (currentMeal == null) {
                    throw new MensaScraperException("got nutrition table without preceding meal");
                }

                // parse nutrition info
                currentMeal.setKcal(parseKcal(mealRow));
                currentMeal.setProteins(parseProteins(mealRow));
                currentMeal.setCarbs(parseCarbs(mealRow));
                currentMeal.setSugar(parseSugar(mealRow));
                currentMeal.setFat(parseFat(mealRow));
                currentMeal.setSaturated(parseSaturated(mealRow));
                currentMeal.setSalt(parseSalt(mealRow));

                // parse environment scores
                currentMeal.setScoreCo2(parseScoreCo2(mealRow));
                currentMeal.setScoreWater(parseScoreWater(mealRow));
                currentMeal.setScoreAnimals(parseScoreAnimalWelfare(mealRow));
                currentMeal.setScoreRainforest(parseScoreRainforest(mealRow));
                currentMeal.setCo2Emissions(parseCo2Emissions(mealRow));
                currentMeal.setWaterConsumption(parseWaterConsumption(mealRow));
            } else {
                // parse meal info
                currentMeal = new MensaMeal();
                currentMeal.setName(parseMealName(mealRow));
                currentMeal.setAdditives(parseMealAdditives(mealRow));
                currentMeal.setPrice(parseMealPrice(mealRow));
                currentMeal.setType(parseMealType(mealRow));
                meals.add(currentMeal);
            }
        }

        meals.forEach(m -> m.setLine(line));

        return meals;
    }

    private static String parseMealName(Element el) {
        return el.selectXpath(".//td[contains(@class, 'menu-title')]/span").text();
    }

    private static List<String> parseMealAdditives(Element el) {
        return Arrays.stream(
                el.selectXpath(".//td[contains(@class, 'menu-title')]//sup").text()
                        .replace("[", "")
                        .replace("]", "")
                        .split(",")
        ).collect(Collectors.toList());
    }

    private static float parseMealPrice(Element el) {
        final String priceText = el.selectXpath(".//span[contains(@class, 'price_1')]").text()
                .replace("€", "")
                .replace(",", ".")
                .replaceAll("[a-zA-Z]", "")
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
        final String kcalText = Arrays.stream(el.selectXpath(".//div[@class='energie']/div[2]").text().split("/"))
                .skip(1)
                .findFirst().orElse("")
                .replaceAll("[^0-9,]", "")
                .replaceAll(",", ".")
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
                .replaceAll("[^0-9,]", "")
                .replaceAll(",", ".")
                .strip();
        return !nutriText.isEmpty() ? Float.parseFloat(nutriText) : 0.0f;
    }

    private static short parseScoreCo2(Element el) {
        final String ratingText = el.selectXpath(".//div[contains(@class, 'co2_bewertung')]/div[1]").attr("data-rating");
        return !ratingText.isEmpty() ? Short.parseShort(ratingText) : 0;
    }

    private static short parseScoreWater(Element el) {
        final String ratingText = el.selectXpath(".//div[contains(@class, 'wasser_bewertung')]/div[1]").attr("data-rating");
        return !ratingText.isEmpty() ? Short.parseShort(ratingText) : 0;
    }

    private static short parseScoreAnimalWelfare(Element el) {
        final String ratingText = el.selectXpath(".//div[contains(@class, 'tierwohl')]/div[1]").attr("data-rating");
        return !ratingText.isEmpty() ? Short.parseShort(ratingText) : 0;
    }

    private static short parseScoreRainforest(Element el) {
        final String ratingText = el.selectXpath(".//div[contains(@class, 'regenwald')]/div[1]").attr("data-rating");
        return !ratingText.isEmpty() ? Short.parseShort(ratingText) : 0;
    }

    private static float parseCo2Emissions(Element el) {
        final String emissionsText = el.selectXpath(".//div[contains(@class, 'co2_bewertung')]/div[3]").text()
                .replaceAll("[^0-9,]+", "")
                .replaceAll(",", ".")
                .strip();
        return !emissionsText.isEmpty() ? Float.parseFloat(emissionsText) : 0;
    }

    private static float parseWaterConsumption(Element el) {
        final String consumptionText = el.selectXpath(".//div[contains(@class, 'wasser_bewertung')]/div[3]").text()
                .replaceAll("[^0-9,]+", "")
                .replaceAll(",", ".")
                .strip();
        return !consumptionText.isEmpty() ? Float.parseFloat(consumptionText) : 0;
    }

    private static List<MensaMeal> cloneMeals(List<MensaMeal> meals) {
        return meals.stream().map(MensaMeal::copy).collect(Collectors.toList());
    }

    private static void allowFail(Runnable r) {
        try {
            r.run();
        } catch (RuntimeException ignored) {
        }
    }
}
