package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SamplesDecoderTest {
    @Test
    void readBatchThrowsOnNullParameter() {
        assertThrows(NullPointerException.class, () -> new SamplesDecoder(null, 1));
    }
}
