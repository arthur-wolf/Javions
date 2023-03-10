package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class PowerWindowTest {
    @Test
    public void testGetValidIndex() throws IOException {
        //Mettre BatchSize à 8 pour tester le changement de tableau, le changement d'index */
        int[] tab = new int[]{73, 292, 65, 745, 98, 4226, 12244, 25722};
        int[] tab1 = new int[]{36818, 23825, 10730, 1657, 1285, 1280, 394, 521};
        int[] tab2 = new int[]{1370, 200, 292, 290, 106, 116, 194, 64};
        int[] tab3 = new int[]{37, 50, 149, 466, 482, 180, 148, 5576};
        InputStream stream = new FileInputStream("resources/Samples.bin");
        int windowSize = 5;
        PowerWindow window = new PowerWindow(stream, windowSize);

        for (int i = 0; i < 5; i++) {
            assertEquals(tab[i], window.get(i));
        }

        window.advanceBy(8);
        for (int i = 0; i < 5; i++) {
            assertEquals(tab1[i], window.get(i));
        }

        window.advanceBy(8);
        for (int i = 0; i < 5; i++) {
            assertEquals(tab2[i], window.get(i));
        }

        window.advanceBy(6);
        assertEquals(tab2[6], window.get(0));
        assertEquals(tab2[7], window.get(1));
        for (int i = 0; i < 3; i++) {
            assertEquals(tab3[i], window.get(i + 2));
        }
    }
}