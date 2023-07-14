# 🐮🌱 kit-mensa-scraper

Scrapes [KIT Mensa Speiseplan](https://www.sw-ka.de/en/hochschulgastronomie/speiseplan). Currently, only "Mensa am Adenauerring" is supported. Upcoming 7 days' menus are available as JSON at [in this repo](archive) as well.

## Requirements
* Java >= 11

## Properties

* [x] Name
* [x] Price
* [x] Type (pork, vegan, etc.)
* [x] Line
* [x] Nutrients
* [x] Additives
* [x] Environment Score

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
MensaMeal(name=Scharfe Sombrero - Reispfanne mit Hackfleisch, Gemüse und Tomatensoße, line=LINIE_1, price=3.2, type=BEEF, kcal=737.0, proteins=40.0, carbs=109.0, sugar=7.0, fat=14.0, saturated=3.0, salt=3.0, scoreCo2=1, scoreWater=3, scoreAnimals=3, scoreRainforest=1, co2Emissions=4021.0, waterConsumption=8.78, additives=[Se, We])
MensaMeal(name=Scharfe Sombrero - Reispfanne mit knusprigen Gemüse Crossini und Tomatensoße, line=LINIE_1, price=3.2, type=VEGETARIAN, kcal=851.0, proteins=26.0, carbs=136.0, sugar=22.0, fat=20.0, saturated=4.0, salt=3.0, scoreCo2=2, scoreWater=3, scoreAnimals=1, scoreRainforest=3, co2Emissions=666.0, waterConsumption=13.7, additives=[ML, Se, So, We])
MensaMeal(name=Sellerieschnitzel mit Ratatouille und Ofenkartoffel, line=LINIE_2, price=3.5, type=VEGAN, kcal=716.0, proteins=13.0, carbs=73.0, sugar=13.0, fat=38.0, saturated=3.0, salt=4.0, scoreCo2=3, scoreWater=3, scoreAnimals=1, scoreRainforest=2, co2Emissions=777.0, waterConsumption=11.79, additives=[Se, We])
MensaMeal(name=Veganes Tagesdessert, line=LINIE_2, price=1.05, type=VEGAN, kcal=0.0, proteins=0.0, carbs=0.0, sugar=0.0, fat=0.0, saturated=0.0, salt=0.0, scoreCo2=0, scoreWater=0, scoreAnimals=0, scoreRainforest=0, co2Emissions=0.0, waterConsumption=0.0, additives=[])
MensaMeal(name=Spätzle-Pilz-Pfanne, line=LINIE_3, price=2.95, type=VEGETARIAN, kcal=510.0, proteins=17.0, carbs=41.0, sugar=2.0, fat=29.0, saturated=13.0, salt=2.0, scoreCo2=3, scoreWater=3, scoreAnimals=1, scoreRainforest=1, co2Emissions=462.0, waterConsumption=4.54, additives=[1, Di, Ei, We])
MensaMeal(name=Blattsalat Gurkensalat, line=LINIE_3, price=0.9, type=VEGAN, kcal=116.0, proteins=1.0, carbs=5.0, sugar=5.0, fat=9.0, saturated=0.0, salt=2.0, scoreCo2=2, scoreWater=3, scoreAnimals=3, scoreRainforest=3, co2Emissions=230.0, waterConsumption=1.27, additives=[Sn])
MensaMeal(name=Creme Sahne - Karamellgeschmack, line=LINIE_3, price=0.85, type=VEGETARIAN, kcal=197.0, proteins=5.0, carbs=25.0, sugar=19.0, fat=8.0, saturated=5.0, salt=0.0, scoreCo2=1, scoreWater=3, scoreAnimals=1, scoreRainforest=1, co2Emissions=347.0, waterConsumption=7.67, additives=[1, ML])
MensaMeal(name=Vollkornpenne in fruchtiger Tomatensoße mit Chorizo, line=LINIE_4, price=3.5, type=PORK, kcal=828.0, proteins=26.0, carbs=80.0, sugar=12.0, fat=42.0, saturated=14.0, salt=7.0, scoreCo2=2, scoreWater=3, scoreAnimals=1, scoreRainforest=1, co2Emissions=893.0, waterConsumption=16.23, additives=[1, 2, 3, 5, Se, We])
MensaMeal(name=Vollkornpenne in heller veganer Soja-Kräutersoße mit Erbsen, line=LINIE_4, price=3.2, type=VEGAN, kcal=672.0, proteins=22.0, carbs=87.0, sugar=5.0, fat=22.0, saturated=9.0, salt=10.0, scoreCo2=3, scoreWater=3, scoreAnimals=3, scoreRainforest=1, co2Emissions=281.0, waterConsumption=4.1, additives=[1, Se, So, We])
MensaMeal(name=Blattsalat Gurkensalat, line=LINIE_4, price=0.9, type=VEGAN, kcal=116.0, proteins=1.0, carbs=5.0, sugar=5.0, fat=9.0, saturated=0.0, salt=2.0, scoreCo2=2, scoreWater=3, scoreAnimals=3, scoreRainforest=3, co2Emissions=230.0, waterConsumption=1.27, additives=[Sn])
MensaMeal(name=Creme Sahne - Karamellgeschmack, line=LINIE_4, price=0.85, type=VEGETARIAN, kcal=197.0, proteins=5.0, carbs=25.0, sugar=19.0, fat=8.0, saturated=5.0, salt=0.0, scoreCo2=1, scoreWater=3, scoreAnimals=1, scoreRainforest=1, co2Emissions=347.0, waterConsumption=7.67, additives=[1, ML])
MensaMeal(name=Paniertes Alaska Seelachsfilet mit Kräuterdip, line=LINIE_5, price=3.95, type=MSC, kcal=791.0, proteins=34.0, carbs=31.0, sugar=5.0, fat=58.0, saturated=5.0, salt=2.0, scoreCo2=2, scoreWater=3, scoreAnimals=1, scoreRainforest=3, co2Emissions=919.0, waterConsumption=11.62, additives=[Fi, ML, Sn, We])
MensaMeal(name=Blattsalat Gurkensalat, line=LINIE_5, price=0.9, type=VEGAN, kcal=116.0, proteins=1.0, carbs=5.0, sugar=5.0, fat=9.0, saturated=0.0, salt=2.0, scoreCo2=2, scoreWater=3, scoreAnimals=3, scoreRainforest=3, co2Emissions=230.0, waterConsumption=1.27, additives=[Sn])
MensaMeal(name=Kaisergemüse, line=LINIE_5, price=0.9, type=VEGAN, kcal=91.0, proteins=3.0, carbs=7.0, sugar=6.0, fat=4.0, saturated=2.0, salt=1.0, scoreCo2=2, scoreWater=3, scoreAnimals=3, scoreRainforest=3, co2Emissions=136.0, waterConsumption=1.44, additives=[1, Se])
MensaMeal(name=Creme Sahne - Karamellgeschmack, line=LINIE_5, price=0.85, type=VEGETARIAN, kcal=197.0, proteins=5.0, carbs=25.0, sugar=19.0, fat=8.0, saturated=5.0, salt=0.0, scoreCo2=1, scoreWater=3, scoreAnimals=1, scoreRainforest=1, co2Emissions=347.0, waterConsumption=7.67, additives=[1, ML])
MensaMeal(name=hausgemachter Kartoffelsalat, line=LINIE_5, price=0.85, type=VEGAN, kcal=290.0, proteins=4.0, carbs=35.0, sugar=2.0, fat=13.0, saturated=1.0, salt=1.0, scoreCo2=3, scoreWater=3, scoreAnimals=3, scoreRainforest=3, co2Emissions=104.0, waterConsumption=1.09, additives=[Se, Sn])
MensaMeal(name=Linsensuppe, line=LINIE_5, price=0.55, type=VEGAN, kcal=176.0, proteins=5.0, carbs=14.0, sugar=1.0, fat=9.0, saturated=0.0, salt=1.0, scoreCo2=3, scoreWater=2, scoreAnimals=3, scoreRainforest=3, co2Emissions=95.0, waterConsumption=10.76, additives=[Se, We])
MensaMeal(name=Schweine- und Hähnchenschnitzel, vegetarische Leckereien, Salatbuffet und Pommes - alles zur Wahl zu einem Preis, line=SCHNITZELBAR, price=1.05, type=PORK, kcal=837.0, proteins=32.0, carbs=62.0, sugar=8.0, fat=49.0, saturated=5.0, salt=3.0, scoreCo2=1, scoreWater=2, scoreAnimals=1, scoreRainforest=1, co2Emissions=2435.0, waterConsumption=62.83, additives=[4, 5, Ei, Hf, ML, Se, Sn, So, We])
MensaMeal(name=Creme Sahne - Karamellgeschmack, line=SCHNITZELBAR, price=0.85, type=VEGETARIAN, kcal=197.0, proteins=5.0, carbs=25.0, sugar=19.0, fat=8.0, saturated=5.0, salt=0.0, scoreCo2=1, scoreWater=3, scoreAnimals=1, scoreRainforest=1, co2Emissions=347.0, waterConsumption=7.67, additives=[1, ML])
MensaMeal(name=Verschiedene Desserts im Portionsbecher, line=SCHNITZELBAR, price=0.65, type=NONE, kcal=123.0, proteins=1.0, carbs=20.0, sugar=16.0, fat=3.0, saturated=2.0, salt=0.0, scoreCo2=2, scoreWater=3, scoreAnimals=1, scoreRainforest=1, co2Emissions=259.0, waterConsumption=5.9, additives=[1, Ei, GEL, ML, We])
MensaMeal(name=Heute Geschlossen, line=LINIE_6, price=1.05, type=NONE, kcal=0.0, proteins=0.0, carbs=0.0, sugar=0.0, fat=0.0, saturated=0.0, salt=0.0, scoreCo2=0, scoreWater=0, scoreAnimals=0, scoreRainforest=0, co2Emissions=0.0, waterConsumption=0.0, additives=[])
MensaMeal(name=Spätausgabe 14:00 bis 14:30 an der Linie 2 Info zum Speisenangebot direkt an der Ausgabe, line=UNKNOWN, price=3.2, type=NONE, kcal=0.0, proteins=0.0, carbs=0.0, sugar=0.0, fat=0.0, saturated=0.0, salt=0.0, scoreCo2=0, scoreWater=0, scoreAnimals=0, scoreRainforest=0, co2Emissions=0.0, waterConsumption=0.0, additives=[])
MensaMeal(name=Reine Kalbsbratwurst mit Currysoße, line=KOERIWERK, price=2.1, type=BEEF, kcal=527.0, proteins=11.0, carbs=35.0, sugar=32.0, fat=37.0, saturated=13.0, salt=5.0, scoreCo2=1, scoreWater=3, scoreAnimals=1, scoreRainforest=1, co2Emissions=1491.0, waterConsumption=9.62, additives=[Se, Sn])
MensaMeal(name=Vegane Tofu - Bratwurst mit Currysoße, line=KOERIWERK, price=2.1, type=VEGAN, kcal=556.0, proteins=26.0, carbs=37.0, sugar=34.0, fat=32.0, saturated=3.0, salt=4.0, scoreCo2=3, scoreWater=3, scoreAnimals=3, scoreRainforest=1, co2Emissions=298.0, waterConsumption=5.84, additives=[Ma, Se, Sn, So, We])
MensaMeal(name=koerifrites, line=KOERIWERK, price=1.3, type=VEGAN, kcal=737.0, proteins=7.0, carbs=67.0, sugar=0.0, fat=47.0, saturated=8.0, salt=2.0, scoreCo2=1, scoreWater=3, scoreAnimals=3, scoreRainforest=3, co2Emissions=1805.0, waterConsumption=3.86, additives=[])
MensaMeal(name=Hähnchenschnitzel mit Brötchen, line=CAFETERIA, price=2.8, type=NONE, kcal=0.0, proteins=0.0, carbs=0.0, sugar=0.0, fat=0.0, saturated=0.0, salt=0.0, scoreCo2=0, scoreWater=0, scoreAnimals=0, scoreRainforest=0, co2Emissions=0.0, waterConsumption=0.0, additives=[1, 3, Ge, We])
MensaMeal(name=Börek mit Spinatfüllung, line=CAFETERIA, price=2.0, type=VEGAN, kcal=0.0, proteins=0.0, carbs=0.0, sugar=0.0, fat=0.0, saturated=0.0, salt=0.0, scoreCo2=0, scoreWater=0, scoreAnimals=0, scoreRainforest=0, co2Emissions=0.0, waterConsumption=0.0, additives=[2, 3, 4, Se, So, We])
MensaMeal(name=Pizza alla bolognese - Pizza mit Rinderhackfleisch, Lauch und roten Zwiebeln, line=PIZZAWERK, price=4.55, type=BEEF, kcal=1134.0, proteins=62.0, carbs=138.0, sugar=7.0, fat=34.0, saturated=16.0, salt=6.0, scoreCo2=1, scoreWater=3, scoreAnimals=2, scoreRainforest=1, co2Emissions=3351.0, waterConsumption=32.68, additives=[ML, We])
MensaMeal(name=Pizza Mykonos - Hirtenkäse, Paprika, Oliven, rote Zwiebeln und Mais, line=PIZZAWERK, price=4.45, type=VEGETARIAN, kcal=1124.0, proteins=51.0, carbs=140.0, sugar=8.0, fat=38.0, saturated=19.0, salt=7.0, scoreCo2=2, scoreWater=3, scoreAnimals=1, scoreRainforest=3, co2Emissions=1116.0, waterConsumption=39.48, additives=[8, ML, We])
MensaMeal(name=Pizza Mykonos vegan - Paprika, Oliven, rote Zwiebeln, vegane Käsealternative und Mais, line=PIZZAWERK, price=4.45, type=VEGAN, kcal=936.0, proteins=24.0, carbs=159.0, sugar=9.0, fat=20.0, saturated=9.0, salt=6.0, scoreCo2=3, scoreWater=3, scoreAnimals=3, scoreRainforest=3, co2Emissions=401.0, waterConsumption=22.16, additives=[1, 8, We])
MensaMeal(name=Pizza Margherita vegetarisch - frische Tomaten, Mozzarella, Basilikumpesto, line=PIZZAWERK, price=4.05, type=NONE, kcal=1232.0, proteins=53.0, carbs=139.0, sugar=8.0, fat=49.0, saturated=20.0, salt=7.0, scoreCo2=2, scoreWater=2, scoreAnimals=1, scoreRainforest=1, co2Emissions=1251.0, waterConsumption=63.69, additives=[2, Ei, LAB, ML, We])
MensaMeal(name=Pizza Margherita vegan - frische Tomaten, vegane Käsealternative, Basilikumpesto, line=PIZZAWERK, price=4.05, type=VEGAN, kcal=1123.0, proteins=25.0, carbs=158.0, sugar=8.0, fat=40.0, saturated=12.0, salt=6.0, scoreCo2=3, scoreWater=2, scoreAnimals=3, scoreRainforest=3, co2Emissions=470.0, waterConsumption=59.77, additives=[1, We])
MensaMeal(name=Insalata piccola - kleiner Blattsalat mit Tomate, Gurke und Ei, line=PIZZAWERK, price=1.8, type=VEGETARIAN, kcal=117.0, proteins=9.0, carbs=2.0, sugar=2.0, fat=7.0, saturated=2.0, salt=0.0, scoreCo2=2, scoreWater=3, scoreAnimals=1, scoreRainforest=1, co2Emissions=233.0, waterConsumption=4.58, additives=[Ei])
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