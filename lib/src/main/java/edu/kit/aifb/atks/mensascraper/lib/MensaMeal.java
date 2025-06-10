package edu.kit.aifb.atks.mensascraper.lib;

import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

/**
 * Data class to capture all available information about a meal. Use getter methods (e.g. {@code getName()}) to access properties.
 * Prices are in Euro, nutrition facts are in grams.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@With
@Builder
public class MensaMeal implements Serializable, Cloneable {

    public MensaMeal(String name, MensaLine line, float price, MensaMealType type) {
        this.name = name;
        this.line = line;
        this.price = price;
        this.type = type;
    }

    public MensaMeal(String name, MensaLine line, float price, MensaMealType type, float kcal, float proteins, float carbs, float sugar, float fat, float saturated, float salt, List<String> additives) {
        this(name, line, price, type);
        this.kcal = kcal;
        this.proteins = proteins;
        this.carbs = carbs;
        this.sugar = sugar;
        this.fat = fat;
        this.saturated = saturated;
        this.salt = salt;
        this.additives = additives;
    }

    /**
     * Name of the meal.
     */
    private String name;

    /**
     * Counter inside the canteen offering this meal.
     */
    private MensaLine line;

    /**
     * Price of the meal in Euros (€).
     */
    private float price;

    /**
     * Type of the meal.
     */
    private MensaMealType type;

    /**
     * Amount of energy of the meal in kcal.
     */
    private float kcal;

    /**
     * Amount of proteins contained in the meal in grams (g).
     */
    private float proteins;

    /**
     * Amount of carbohydrates contained in the meal in grams (g).
     */
    private float carbs;

    /**
     * Amount of sugar contained in the meal in grams (g).
     */
    private float sugar;

    /**
     * Amount of fat contained in the meal in grams (g).
     */
    private float fat;

    /**
     * Amount of contained saturated fats in grams (g).
     */
    private float saturated;

    /**
     * Amount of salt contained in the meal in grams (g).
     */
    private float salt;

    /**
     * Rating of emitted CO2 emissions of this meal (1-3 stars).
     */
    private short scoreCo2;

    /**
     * Rating of water consumption of this meal (1-3 stars).
     */
    private short scoreWater;

    /**
     * Animal welfare rating of this meal (1-3 stars).
     */
    private short scoreAnimals;

    /**
     * Rainforest friendliness score of this meal (1-3 stars).
     */
    private short scoreRainforest;

    /**
     * CO2 emissions of this meal in grams.
     */
    private float co2Emissions;

    /**
     * Water consumption of this meal in liters.
     */
    private float waterConsumption;

    /**
     * List of additives / ingredients / allergens.
     * See <a href="https://www.sw-ka.de/media/?file=4458_liste_aller_gesetzlich_ausweisungspflichtigen_zusatzstoffe_und_allergene_fuer_website_160218.pdf">List of allergens</a>.
     */
    private List<String> additives;

    // TODO: implement environment score

    /**
     * Create a deep-copy of this object.
     *
     * @return Deep-copy of this object.
     */
    public MensaMeal copy() {
        return withPrice(new Random().nextFloat()).withPrice(price);  // hacky way of creating a clone in one line
    }

    @Override
    protected Object clone() {
        return copy();
    }
}
