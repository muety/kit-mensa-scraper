package edu.kit.aifb.atks.mensascraper.lib;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum representing supported canteens.
 * TODO: implement others
 */
@Getter
@AllArgsConstructor
public enum MensaLocation {
    /** SW KA Mensa am Adenauerring */
    ADENAUERRING("mensa_adenauerring");

    private final String key;
}
