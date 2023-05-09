package edu.kit.aifb.atks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KITMensaScraperIntegrationTest {

    private KITMensaScraper sut;

    @BeforeEach
    void setUp() {
        sut = new KITMensaScraper();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFetchMealsLive() {
        // fetch live data for today, if today is a weekday or next monday otherwise
        // note: this test will fail when run on days when canteen is closed, but should be acceptable
        final var now = LocalDate.now();
        final var targetDate = now.getDayOfWeek().getValue() >= DayOfWeek.SATURDAY.getValue()
                ? now.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                : now;

        var result = sut.fetchMeals(MensaLocation.ADENAUERRING, targetDate);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().noneMatch(m -> m.getName().isBlank()));
        assertTrue(result.stream().anyMatch(m -> m.getPrice() > 0f));
        assertTrue(result.stream().anyMatch(m -> m.getKcal() > 0f));
        assertTrue(result.stream().anyMatch(m -> !m.getLine().equals(MensaLine.UNKNOWN)));
        assertTrue(result.stream().anyMatch(m -> !m.getType().equals(MensaMealType.NONE)));
    }
}