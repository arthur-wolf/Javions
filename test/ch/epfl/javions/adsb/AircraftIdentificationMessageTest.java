package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class AircraftIdentificationMessageTest {
    private final static ArrayList<RawMessage> messages = new ArrayList<>();
    private final static int[] TIMESTAMPS = new int[]{0, 0, 0, 0, 0};
    private final static String[] BYTES = {
            "8D4D2228234994B7284820323B81",
            "8F01024C233530F3CF6C60A19669",
            "8D49529923501439CF1820419C55",
            "8DA4F23925101331D73820FC8E9F",
            "8D3C648158AF92F723BC275EC692"};

    private static void loadMessages() {
        for (int i = 0; i < TIMESTAMPS.length; i++) {
            messages.add(new RawMessage(TIMESTAMPS[i], ByteString.ofHexadecimalString(BYTES[i])));
        }
    }

    /*@Test
    void categoryWorks() {
        loadMessages();
        assertEquals(163, AircraftIdentificationMessage.category(messages.get(0)));
    }*/

    /*@Test
    void CallSignWorks() {
        loadMessages();
        assertEquals("RYR7JD", AircraftIdentificationMessage.callSign(messages.get(0)).toString());
        assertEquals("MSC3361", AircraftIdentificationMessage.callSign(messages.get(1)).toString());
        assertEquals("TAP931", AircraftIdentificationMessage.callSign(messages.get(2)).toString());
        assertEquals("DAL153", AircraftIdentificationMessage.callSign(messages.get(3)).toString());
        assertEquals("HBPRO", AircraftIdentificationMessage.callSign(messages.get(4)).toString());
    }*/
}
