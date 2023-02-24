package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AircraftDescriptionTest {
    @Test
    void AircraftDescriptionValidatesTrivialString() {
        assertDoesNotThrow(() -> new AircraftDescription("L2J"));
    }

    @Test
    void AircraftDescriptionValidatesEmptyString() {
        assertDoesNotThrow(() -> new AircraftDescription(""));
    }

    @Test
    void AircraftDescriptionThrowsOnInvalidString() {
        assertThrows(IllegalArgumentException.class, () -> new AircraftDescription("@+é"));
    }
}
