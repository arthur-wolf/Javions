package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class AircraftDatabaseTest {
    String filename = URLDecoder.decode(getClass().getResource("/aircraft.zip").getFile(), StandardCharsets.UTF_8);
    @Test
    void AircraftDatabaseThrowsOnNullFilename() {
        assertThrows(NullPointerException.class, () -> new AircraftDatabase(null));
    }

    @Test
    void AircraftDatabaseWorksOnValidFilename() {
        assertDoesNotThrow(() -> new AircraftDatabase("aircraft.zip"));
    }

    @Test
    void AircraftDatabaseWorksOnTrivialIcaoAddress() {
        AircraftData expected = new AircraftData(
                new AircraftRegistration("HB-JDC"),
                new AircraftTypeDesignator("A20N"),
                "AIRBUS A-320neo",
                new AircraftDescription("L2J"),
                WakeTurbulenceCategory.MEDIUM);
        try {
            AircraftData actual = new AircraftDatabase(filename).get(new IcaoAddress("4B1814"));
            assertEquals(expected, actual);
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    void AircraftDatabaseWorksOnAircraftWithUnknownWakeTurbulence() {
        AircraftData expected = new AircraftData(
                new AircraftRegistration("GND"),
                new AircraftTypeDesignator("GND"),
                "",
                new AircraftDescription("V0-"),
                WakeTurbulenceCategory.UNKNOWN);
        try {
            AircraftData actual = new AircraftDatabase(filename).get(new IcaoAddress("105A14"));
            assertEquals(expected, actual);
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }
}
