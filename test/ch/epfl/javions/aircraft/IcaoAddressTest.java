package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IcaoAddressTest {
    @Test
    void IcaoAddressValidatesTrivialString() {
        assertDoesNotThrow(() -> new IcaoAddress("4B1814"));
    }

    @Test
    void IcaoAddressThrowsOnEmptyString() {
        assertThrows(IllegalArgumentException.class, () -> new IcaoAddress(""));
    }

    @Test
    void IcaoAddressThrowsOnInvalidString() {
        assertThrows(IllegalArgumentException.class, () -> new IcaoAddress("@+é"));
    }
}
