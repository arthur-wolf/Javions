package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.*;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;

public class PowerComputerTest {
    @Test
    void constructorThrowsOnInvalidBatchSize() {
        assertThrows(IllegalArgumentException.class, () -> new PowerComputer(new ByteArrayInputStream(new byte[0]), -1));
        assertThrows(IllegalArgumentException.class, () -> new PowerComputer(new ByteArrayInputStream(new byte[0]), 7));
    }

    @Test
    void constructorDoesNotThrowOnValidArguments() {
        assertDoesNotThrow(() -> new PowerComputer(new ByteArrayInputStream(new byte[0]), 8));
    }

    @Test
    void readBatchThrowsOnInvalidBatchsize() throws IOException {
        InputStream stream = new FileInputStream("resources/samples.bin");
        PowerComputer powerComputer = new PowerComputer(stream, 64);
        int[] actual = new int[128]; // 64 * 2, could be any number != 64
        assertThrows(IllegalArgumentException.class, () -> powerComputer.readBatch(actual));
    }

    @Test
    void readBatchTest() throws IOException {
        int[] expected = new int[]{73, 292, 65, 745, 98, 4226, 12244, 25722, 36818, 23825};
        int[] actual = new int[1200];

        InputStream stream = new FileInputStream("resources/samples.bin");
        PowerComputer powerComputer = new PowerComputer(stream, 1200);
        int powered = powerComputer.readBatch(actual);
        for (int i = 0; i < 10; i++) {
            assertEquals(expected[i], actual[i]);
        }
        assertEquals(actual.length, powered);
    }
}
