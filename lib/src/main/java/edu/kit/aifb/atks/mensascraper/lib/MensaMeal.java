package edu.kit.aifb.atks.mensascraper.lib;

import lombok.*;

import java.io.Serializable;
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

    private String name;
    private MensaLine line;
    private float price;
    private MensaMealType type;
    private float kcal;
    private float proteins;
    private float carbs;
    private float sugar;
    private float fat;
    private float saturated;
    private float salt;

    // TODO: implement additives
    // TODO: implement environment score

    public MensaMeal copy() {
        return withPrice(new Random().nextFloat()).withPrice(price);  // hacky way of creating a clone in one line
    }

    @Override
    protected Object clone() {
        return copy();
    }
}
