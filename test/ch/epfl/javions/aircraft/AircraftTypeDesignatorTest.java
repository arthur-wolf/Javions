package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AircraftTypeDesignatorTest {
    @Test
    void AircraftTypeDesignatorValidatesTrivialString() {
        assertDoesNotThrow(() -> new AircraftTypeDesignator("A4F"));
    }

    @Test
    void AircraftTypeDesignatorValidatesEmptyString() {
        assertDoesNotThrow(() -> new AircraftTypeDesignator(""));
    }

    @Test
    void AircraftTypeDesignatorThrowsOnInvalidString() {
        assertThrows(IllegalArgumentException.class, () -> new AircraftTypeDesignator("@+é"));
    }
    @Test
    void aircraftTypeDesignatorConstructorThrowsWithInvalidTypeDesignator() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AircraftTypeDesignator("ABCDE");
        });
    }

    @Test
    void aircraftTypeDesignatorConstructorAcceptsEmptyTypeDesignator() {
        assertDoesNotThrow(() -> {
            new AircraftTypeDesignator("");
        });
    }

    @Test
    void aircraftTypeDesignatorConstructorAcceptsValidTypeDesignator() {
        assertDoesNotThrow(() -> {
            new AircraftTypeDesignator("BCS3");
        });
    }
}
