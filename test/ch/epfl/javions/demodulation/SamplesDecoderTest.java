package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SamplesDecoderTest {
    @Test
    void constructorThrowsOnNullParameter() {
        assertThrows(NullPointerException.class, () -> new SamplesDecoder(null, 1));
    }

    @Test
    void constructorThrowsOnNegativeBatchSize() {
        assertThrows(IllegalArgumentException.class, () -> new SamplesDecoder(new ByteArrayInputStream(new byte[0]), 0));
        assertThrows(IllegalArgumentException.class, () -> new SamplesDecoder(new ByteArrayInputStream(new byte[0]), -1));
    }

    @Test
    void readBatchThrowsOnInvalidBatchSize() throws IOException {
        InputStream stream = new FileInputStream("resources/samples.bin");
        SamplesDecoder samplesDecoder = new SamplesDecoder(stream,1200);
        short[] actual = new short[2400]; // 1200 * 2, could be any number != 1200
        assertThrows(IllegalArgumentException.class, () -> samplesDecoder.readBatch(actual));
    }

    @Test
    void readBatchTest() throws IOException {
        short[] expected = new short[]{-3, 8, -9, -8, -5, -8, -12, -16, -23, -9};
        short[] actual = new short[1200];

        InputStream stream = new FileInputStream("resources/samples.bin");
        SamplesDecoder samplesDecoder = new SamplesDecoder(stream,1200);
        int signedSamples = samplesDecoder.readBatch(actual);

        for (int i = 0; i < 10; i++) {
            assertEquals(expected[i],actual[i]);
        }
        assertEquals(actual.length, signedSamples);
    }
}
