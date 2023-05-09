# 🐮🌱 kit-mensa-scraper

Scrapes [KIT Mensa Speiseplan](https://www.sw-ka.de/en/hochschulgastronomie/speiseplan). Currently, only "Mensa am Adenauerring" is supported.

## Properties

* [x] Name
* [x] Price
* [x] Type (pork, vegan, etc.)
* [x] Line
* [x] Nutrients
* [ ] Additives
* [ ] Environment Score

## Usage

```java
List<MensaMeal> meals = new KITMensaScraper().fetchMeals(MensaLocation.ADENAUERRING, LocalDate.now());
```

## Build

```bash
mvn clean package
```

## License

MIT