package edu.kit.aifb.atks;

import lombok.*;

import java.io.Serializable;

/**
 * Data class to capture all available information about a meal. Use getter methods (e.g. {@code getName()}) to access properties.
 * Prices are in Euro, nutrition facts are in grams.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class MensaMeal implements Serializable {

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


}
