package ch.epfl.javions.aircraft;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.zip.ZipFile;

/**
 * Represents the mictronics database of aircraft
 *
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public class AircraftDatabase {
    private final String filename;

    /**
     * Creates a new AircraftDatabase
     *
     * @param filename the name of the file containing the aircraft database
     * @throws NullPointerException if the filename is null
     */
    public AircraftDatabase(String filename) {
        this.filename = Objects.requireNonNull(filename);
    }

    /**
     * Returns the AircraftData of the aircraft with the given ICAO address
     *
     * @param address the ICAO address of the aircraft
     * @return the AircraftData of the aircraft with the given ICAO address
     * @throws IOException if an I/O error occurs
     */
    public AircraftData get(IcaoAddress address) throws IOException {
        // store the last two digits of the address in a variable
        String lastTwoDigits = address.string().substring(4);

        try (ZipFile zip = new ZipFile(filename);
             InputStream inputStream = zip.getInputStream(zip.getEntry(lastTwoDigits + ".csv"));
             Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(reader)) {

            String line;
            // read the file line by line, if the ICAO address we search for has a bigger hexadecimal value than
            // the address of the next line, we stop searching for it since the addresses are sorted in the file
            while (true) {
                if ((line = bufferedReader.readLine()) != null) {
                    int compare = line.split(",", -1)[0].compareTo(address.string());
                    if (compare > 0) {
                        return null;
                    }
                    else if (compare == 0) {
                        if (line.startsWith(address.string())) {
                            String[] splitted = line.split(",", -1);
                            return new AircraftData(
                                    new AircraftRegistration(splitted[1]),
                                    new AircraftTypeDesignator(splitted[2]),
                                    splitted[3],
                                    new AircraftDescription(splitted[4]),
                                    WakeTurbulenceCategory.of(splitted[5]));
                        }
                    }
                }
                else {
                    return null;
                }
            }
        }
    }
}
