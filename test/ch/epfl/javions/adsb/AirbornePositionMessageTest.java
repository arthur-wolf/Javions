package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AirbornePositionMessageTest {

    private final static ArrayList<RawMessage> messages = new ArrayList<>();
    private final static ArrayList<RawMessage> messages2 = new ArrayList<>();
    private final static int[] TIMESTAMPS = {75898000, 116538700, 138560100, 208135700, 233069800};

    private final static int[] TIMESTAMPS2 = {0, 0};

    private final static String[] BYTES = {
            "8D49529958B302E6E15FA352306B",
            "8D4241A9601B32DA4367C4C3965E",
            "8D4D222860B985F7F53FAB33CE76",
            "8D4D029F594B52EFDB7E94ACEAC8",
            "8D4B2964212024123E0820939C6F"};
    private final static String[] BYTES2 = {
            "8D39203559B225F07550ADBE328F",
            "8DAE02C85864A5F5DD4975A1A3F5"
    };

    private static void loadMessages() {
        for (int i = 0; i < TIMESTAMPS.length; i++) {
            messages.add(new RawMessage(TIMESTAMPS[i], ByteString.ofHexadecimalString(BYTES[i])));
        }
    }
    private static void loadMessages2() {
        for (int i = 0; i < TIMESTAMPS2.length; i++) {
            messages2.add(new RawMessage(TIMESTAMPS2[i], ByteString.ofHexadecimalString(BYTES2[i])));
        }
    }

    /*@Test
    void altitudeWorks() {
        loadMessages();
        assertEquals(10546.08, AirbornePositionMessage.altitude(messages.get(0)));
        assertEquals(1303.02, AirbornePositionMessage.altitude(messages.get(1)));
        assertEquals(10972.800000000001, AirbornePositionMessage.altitude(messages.get(2)));
        assertEquals(4244.34, AirbornePositionMessage.altitude(messages.get(3)));
        assertEquals(10370.82, AirbornePositionMessage.altitude(messages.get(4)));
    }*/

    /*@Test
    void altitudeWorks2() {
        loadMessages2();
        assertEquals(3474.72, AirbornePositionMessage.altitude(messages2.get(0)), 0.001);
        assertEquals(7315.20, AirbornePositionMessage.altitude(messages2.get(1)), 0.001);
    }*/

    /*@Test
    void parityWorks() {
        loadMessages();
        assertEquals(0, AirbornePositionMessage.parity(messages.get(0)));
        assertEquals(0, AirbornePositionMessage.parity(messages.get(1)));
        assertEquals(1, AirbornePositionMessage.parity(messages.get(2)));
        assertEquals(0, AirbornePositionMessage.parity(messages.get(3)));
        assertEquals(0, AirbornePositionMessage.parity(messages.get(4)));
    }*/
    /*@Test
    void longitudeWorks() {
        loadMessages();
        assertEquals(0.6867904663085938, AirbornePositionMessage.longitude(messages.get(0)));
        assertEquals(0.702667236328125, AirbornePositionMessage.longitude(messages.get(1)));
        assertEquals(0.6243515014648438, AirbornePositionMessage.longitude(messages.get(2)));
        assertEquals(0.747222900390625, AirbornePositionMessage.longitude(messages.get(3)));
        assertEquals(0.8674850463867188, AirbornePositionMessage.longitude(messages.get(4)));
    }*/

    /*@Test
    void latitudeWorks() {
        loadMessages();
        assertEquals(0.7254638671875, AirbornePositionMessage.latitude(messages.get(0)));
        assertEquals(0.7131423950195312, AirbornePositionMessage.latitude(messages.get(1)));
        assertEquals(0.4921417236328125, AirbornePositionMessage.latitude(messages.get(2)));
        assertEquals(0.7342300415039062, AirbornePositionMessage.latitude(messages.get(3)));
        assertEquals(0.7413406372070312, AirbornePositionMessage.latitude(messages.get(4)));
    }*/

    /*@Test
    void grayWorks() {
        assertEquals(0b100, AirbornePositionMessage.grayValueOf(0b110, 3));
    }*/
}
