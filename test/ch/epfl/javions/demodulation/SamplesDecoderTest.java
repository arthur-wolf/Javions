package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SamplesDecoderTest {
    @Test
    void readBatchThrowsOnNullParameter() {
        assertThrows(NullPointerException.class, () -> new SamplesDecoder(null, 1));
    }
    @Test
    void readBatchTest() throws IOException {
        short[] expected = new short[]{-3, 8 ,-9 ,-8, -5 ,-8, -12, -16, -23 ,-9};
        short[] actual = new short[1200];

        InputStream stream = new FileInputStream("resources/samples.bin");
        SamplesDecoder a = new SamplesDecoder(stream,1200);
        int b = a.readBatch(actual);
        for (int i = 0; i < 10; i++) {
            assertEquals(expected[i],actual[i]);
        }
    }
    @Test
    void readBatchWorks() throws IOException {
        File initialFile = new File("//file path");
        try {
            short[] tab = new short[4804];
            InputStream stream = new FileInputStream(initialFile);
            SamplesDecoder decode = new SamplesDecoder(stream, 4804);
            decode.readBatch(tab);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
