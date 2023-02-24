package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

/**
 * Represents an aircraft's ICAO address
 *
 * @param string the ICAO address of the aircraft
 * @author Arthur Wolf (344200)
 */
public record IcaoAddress(String string) {
    public static Pattern IcaoAdressPattern = Pattern.compile("[0-9A-F]{6}");

    /**
     * Validates the pattern of the given address if it matches the corresponding regular expression
     *
     * @param string The address to validate
     */
    public IcaoAddress {
        if (!(IcaoAdressPattern.matcher(string).matches()))
            throw new IllegalArgumentException();
    }
}
