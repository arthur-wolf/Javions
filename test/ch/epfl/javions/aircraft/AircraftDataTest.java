package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AircraftDataTest {
    @Test
    void aircraftDataConstructorThrowsWithNullAttribute() {
        var registration = new AircraftRegistration("HB-JAV");
        var typeDesignator = new AircraftTypeDesignator("B738");
        var model = "Boeing 737-800";
        var description = new AircraftDescription("L2J");
        var wakeTurbulenceCategory = WakeTurbulenceCategory.LIGHT;
        assertThrows(NullPointerException.class, () -> {
            new AircraftData(null, typeDesignator, model, description, wakeTurbulenceCategory);
        });
        assertThrows(NullPointerException.class, () -> {
            new AircraftData(registration, null, model, description, wakeTurbulenceCategory);
        });
        assertThrows(NullPointerException.class, () -> {
            new AircraftData(registration, typeDesignator, null, description, wakeTurbulenceCategory);
        });
        assertThrows(NullPointerException.class, () -> {
            new AircraftData(registration, typeDesignator, model, null, wakeTurbulenceCategory);
        });
        assertThrows(NullPointerException.class, () -> {
            new AircraftData(registration, typeDesignator, model, description, null);
        });
    }
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
