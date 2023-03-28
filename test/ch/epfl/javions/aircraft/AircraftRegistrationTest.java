package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AircraftRegistrationTest {
    @Test
    void AircraftRegistrationValidatesTrivialString() {
        assertDoesNotThrow(() -> new AircraftRegistration("HB-JDC"));
    }

    @Test
    void AircraftRegistrationThrowsOnEmptyString() {
        assertThrows(IllegalArgumentException.class, () -> new AircraftRegistration(""));
    }

    @Test
    void AircraftRegistrationThrowsOnInvalidString() {
        assertThrows(IllegalArgumentException.class, () -> new AircraftRegistration("@+é"));
    }
    @Test
    void aircraftRegistrationConstructorThrowsWithInvalidRegistration() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AircraftRegistration("abc");
        });
    }

    @Test
    void aircraftRegistrationConstructorThrowsWithEmptyRegistration() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AircraftRegistration("");
        });
    }

    @Test
    void aircraftRegistrationConstructorAcceptsValidRegistration() {
        assertDoesNotThrow(() -> {
            new AircraftRegistration("F-HZUK");
        });
    }
}
