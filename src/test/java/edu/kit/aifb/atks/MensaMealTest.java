package edu.kit.aifb.atks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MensaMealTest {

    @Test
    public void testCopy() {
        var m1 = MensaMeal.builder().name("Meal 1").price(1.99f).line(MensaLine.LINIE_1).type(MensaMealType.VEGAN).build();
        var m2 = m1.copy();
        m1.setName("Meal 2");
        m1.setPrice(2.99f);
        m1.setLine(MensaLine.LINIE_2);
        m2.setType(MensaMealType.VEGETARIAN);
        assertEquals("Meal 2", m1.getName());
        assertEquals("Meal 1", m2.getName());
        assertEquals(2.99f, m1.getPrice());
        assertEquals(1.99f, m2.getPrice());
        assertEquals(MensaLine.LINIE_2, m1.getLine());
        assertEquals(MensaLine.LINIE_1, m2.getLine());
        assertEquals(MensaMealType.VEGAN, m1.getType());
        assertEquals(MensaMealType.VEGETARIAN, m2.getType());
    }

}
