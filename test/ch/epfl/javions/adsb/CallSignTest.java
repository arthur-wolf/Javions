package ch.epfl.javions.adsb;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CallSignTest {
    @Test
    void CallSignValidatesTrivialString() {
        assertDoesNotThrow(() -> new CallSign("DL744"));
    }

    @Test
    void CallSignValidatesOnEmptyString() {
        assertDoesNotThrow(() -> new CallSign(""));
    }

    @Test
    void CallSignThrowsOnInvalidString() {
        assertThrows(IllegalArgumentException.class, () -> new CallSign("@+é"));
    }
}
