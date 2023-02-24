package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AircraftDataTest {
    @Test
    void AircraftDataWorksOnTrivialData() {
        assertDoesNotThrow(() -> new AircraftData(
                new AircraftRegistration("HB-JDC"),
                new AircraftTypeDesignator("A20N"),
                "model",
                new AircraftDescription("B2J"),
                WakeTurbulenceCategory.MEDIUM));
    }

    @Test
    void AircraftDataThrowsOnOneNullParameter() {
        assertThrows(NullPointerException.class, () -> new AircraftData(
                new AircraftRegistration("HB-JDC"),
                new AircraftTypeDesignator("A20N"),
                null,
                new AircraftDescription("B2J"),
                WakeTurbulenceCategory.MEDIUM));
    }

    @Test
    void AircraftDataThrowsOnOnlyNullParameters() {
        assertThrows(NullPointerException.class, () -> new AircraftData(
                null,
                null,
                null,
                null,
                null));
    }
}
