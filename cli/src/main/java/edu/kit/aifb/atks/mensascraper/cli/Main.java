package edu.kit.aifb.atks.mensascraper.cli;

import com.google.gson.Gson;
import edu.kit.aifb.atks.mensascraper.lib.KITMensaScraper;
import edu.kit.aifb.atks.mensascraper.lib.MensaLocation;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        final var scraper = new KITMensaScraper();
        final var day = args.length > 0
                ? LocalDate.parse(args[0])
                : LocalDate.now();
        final var meals = scraper.fetchMeals(MensaLocation.ADENAUERRING, day);
        System.out.println(new Gson().toJson(meals));
    }

}
