# 🐮🌱 kit-mensa-scraper

Scrapes [KIT Mensa Speiseplan](https://www.sw-ka.de/en/hochschulgastronomie/speiseplan). Currently, only "Mensa am Adenauerring" is supported.

## Requirements
* Java >= 11

## Properties

* [x] Name
* [x] Price
* [x] Type (pork, vegan, etc.)
* [x] Line
* [x] Nutrients
* [x] Additives
* [ ] Environment Score

## API Documentation
JavaDoc available [here](https://docs.muetsch.io/kit-mensa-scraper/edu/kit/aifb/atks/KITMensaScraper.html).

## Usage Example

```java
import edu.kit.aifb.atks.mensascraper.lib.*;

public static void main(String[]args) {
    final KITMensaScraper mensa = new KITMensaScraper();
    final List<MensaMeal> meals = mensa.fetchMeals(MensaLocation.ADENAUERRING, LocalDate.now());
    meals.forEach(System.out::println);
}
```

### Output
```
MensaMeal(name=Pasta in Tomatensoße mit Speck, Peperoncini und Reibekäse, line=LINIE_1, price=3.2, type=PORK, kcal=943.0, proteins=32.0, carbs=85.0, sugar=5.0, fat=51.0, saturated=19.0, salt=4.0)
MensaMeal(name=Pasta in Tomaten - Broccolisoße mit Reibekäse, line=LINIE_1, price=3.2, type=VEGETARIAN, kcal=856.0, proteins=27.0, carbs=91.0, sugar=11.0, fat=39.0, saturated=7.0, salt=4.0)
MensaMeal(name=Mini Frühlingsrollen mit Sweet Chili Soße und Mienudeln, line=LINIE_2, price=3.8, type=VEGAN, kcal=1147.0, proteins=23.0, carbs=144.0, sugar=22.0, fat=52.0, saturated=6.0, salt=2.0)
MensaMeal(name=Reispfanne mit Rindfleisch und Balkangemüse, line=LINIE_3, price=3.35, type=BEEF, kcal=721.0, proteins=39.0, carbs=86.0, sugar=7.0, fat=22.0, saturated=4.0, salt=4.0)
MensaMeal(name=Blattsalat, line=LINIE_3, price=0.9, type=VEGAN, kcal=106.0, proteins=0.0, carbs=3.0, sugar=3.0, fat=9.0, saturated=0.0, salt=2.0)
MensaMeal(name=Hausgemachte Gnocchi, line=LINIE_4, price=3.5, type=PORK, kcal=1121.0, proteins=22.0, carbs=132.0, sugar=8.0, fat=55.0, saturated=19.0, salt=6.0)
MensaMeal(name=Hausgemachte Gnocchi, line=LINIE_4, price=3.2, type=VEGAN, kcal=1241.0, proteins=24.0, carbs=162.0, sugar=19.0, fat=54.0, saturated=4.0, salt=11.0)
MensaMeal(name=Blattsalat, line=LINIE_4, price=0.9, type=VEGAN, kcal=106.0, proteins=0.0, carbs=3.0, sugar=3.0, fat=9.0, saturated=0.0, salt=2.0)
MensaMeal(name=Aktion "Afrika Senegal" Thiebouyapp mit Rindfleisch, line=LINIE_5, price=4.15, type=BEEF, kcal=464.0, proteins=33.0, carbs=72.0, sugar=10.0, fat=2.0, saturated=0.0, salt=0.0)
MensaMeal(name=Blattsalat, line=LINIE_5, price=0.9, type=VEGAN, kcal=106.0, proteins=0.0, carbs=3.0, sugar=3.0, fat=9.0, saturated=0.0, salt=2.0)
MensaMeal(name=Ananasquark, line=LINIE_5, price=0.85, type=VEGETARIAN, kcal=122.0, proteins=15.0, carbs=13.0, sugar=13.0, fat=0.0, saturated=0.0, salt=0.0)
MensaMeal(name=Schweine- und Hähnchenschnitzel, vegetarische Leckereien,, line=SCHNITZELBAR, price=1.05, type=PORK, kcal=837.0, proteins=32.0, carbs=62.0, sugar=8.0, fat=49.0, saturated=5.0, salt=3.0)
MensaMeal(name=Ananasquark, line=SCHNITZELBAR, price=0.85, type=VEGETARIAN, kcal=122.0, proteins=15.0, carbs=13.0, sugar=13.0, fat=0.0, saturated=0.0, salt=0.0)
MensaMeal(name=Hamburger Buffet Preis je 100 g, line=LINIE_6, price=1.05, type=BEEF, kcal=597.0, proteins=33.0, carbs=55.0, sugar=7.0, fat=25.0, saturated=10.0, salt=4.0)
MensaMeal(name=Salatbuffet mit frischer Rohkost, Blattsalate und ausgemachten Dressings, Preis je 100 g, line=LINIE_6, price=1.0, type=VEGAN, kcal=0.0, proteins=0.0, carbs=0.0, sugar=0.0, fat=0.0, saturated=0.0, salt=0.0)
MensaMeal(name=Reine Kalbsbratwurst mit Currysoße, line=KOERIWERK, price=2.1, type=BEEF, kcal=527.0, proteins=11.0, carbs=35.0, sugar=32.0, fat=37.0, saturated=13.0, salt=5.0)
MensaMeal(name=Vegane Tofu-Bratwurst mit Currysoße, line=KOERIWERK, price=2.1, type=VEGAN, kcal=556.0, proteins=26.0, carbs=37.0, sugar=34.0, fat=32.0, saturated=3.0, salt=4.0)
MensaMeal(name=koerifrites, line=KOERIWERK, price=1.3, type=VEGAN, kcal=0.0, proteins=0.0, carbs=0.0, sugar=0.0, fat=0.0, saturated=0.0, salt=0.0)
MensaMeal(name=Geflügelfrikadelle mit Brötchen, line=CAFETERIA, price=2.8, type=NONE, kcal=0.0, proteins=0.0, carbs=0.0, sugar=0.0, fat=0.0, saturated=0.0, salt=0.0)
MensaMeal(name=Pizza alla bolognese - Pizza mit Rinderhackfleisch, Lauch und roten Zwiebeln, line=PIZZAWERK, price=4.55, type=BEEF, kcal=1134.0, proteins=62.0, carbs=138.0, sugar=7.0, fat=34.0, saturated=16.0, salt=6.0)
MensaMeal(name=Pizza Funghi - frische Champignons, Frühlingszwiebeln, line=PIZZAWERK, price=4.35, type=VEGETARIAN, kcal=1121.0, proteins=47.0, carbs=138.0, sugar=5.0, fat=40.0, saturated=16.0, salt=6.0)
MensaMeal(name=Pizza Funghi vegan - frische Champignons, Frühlingszwiebeln und vegane Käsealternative, line=PIZZAWERK, price=4.35, type=VEGAN, kcal=999.0, proteins=25.0, carbs=156.0, sugar=6.0, fat=28.0, saturated=10.0, salt=6.0)
MensaMeal(name=Pizza Margherita vegetarisch - frische Tomaten, Mozzarella, Basilikumpesto, line=PIZZAWERK, price=4.05, type=NONE, kcal=1229.0, proteins=53.0, carbs=139.0, sugar=8.0, fat=49.0, saturated=20.0, salt=7.0)
```

## Build

```bash
./mvnw clean package
```

## CLI
We also provide a very minimalistic command-line application to run the scraper for a given day.

### Usage
```bash
./mvnw clean package
java -jar cli/target/*dependencies.jar 2023-05-17 > 2023-05-17.json
```

## License

MIT