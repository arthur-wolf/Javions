package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WakeTurbulenceCategoryTest {

    @Test
    void WakeTurbulenceCategoryReturnsLight() {
        var actual = WakeTurbulenceCategory.of("L");
        var expected = WakeTurbulenceCategory.LIGHT;
        assertEquals(expected, actual);
    }

    @Test
    void WakeTurbulenceCategoryReturnsHeavy() {
        var actual = WakeTurbulenceCategory.of("H");
        var expected = WakeTurbulenceCategory.HEAVY;
        assertEquals(expected, actual);
    }

    @Test
    void WakeTurbulenceCategoryReturnsMedium() {
        var actual = WakeTurbulenceCategory.of("M");
        var expected = WakeTurbulenceCategory.MEDIUM;
        assertEquals(expected, actual);
    }

    @Test
    void WakeTurbulenceCategoryReturnsUnknown() {
        var expected = WakeTurbulenceCategory.UNKNOWN;
        var actual = WakeTurbulenceCategory.of("");
        assertEquals(expected, actual);

        var actual1 = WakeTurbulenceCategory.of("2");
        assertEquals(expected, actual1);

    }


}
