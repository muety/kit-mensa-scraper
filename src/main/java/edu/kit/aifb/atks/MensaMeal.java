package edu.kit.aifb.atks;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MensaMeal {

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
