package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AircraftDatabaseTest {
    @Test
    void AircraftDatabaseThrowsOnNullFilename() {
        assertThrows(NullPointerException.class, () -> new AircraftDatabase(null));
    }

    @Test
    void AircraftDatabaseWorksOnValidFilename() {
        assertDoesNotThrow(() -> new AircraftDatabase("aircraftDatabase.csv"));
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
            AircraftData actual = new AircraftDatabase("aircraftDatabase.csv").get(new IcaoAddress("4B1814"));
            assertEquals(expected, actual);
        } catch (Exception e) {
            e.printStackTrace();
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
            AircraftData actual = new AircraftDatabase("aircraftDatabase.csv").get(new IcaoAddress("105A14"));
            assertEquals(expected, actual);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
