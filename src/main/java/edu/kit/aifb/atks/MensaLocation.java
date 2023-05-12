package edu.kit.aifb.atks;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum representing supported canteens.
 */
@Getter
@AllArgsConstructor
public enum MensaLocation {
    ADENAUERRING("mensa_adenauerring");  // TODO: implement others

    private final String key;
}
