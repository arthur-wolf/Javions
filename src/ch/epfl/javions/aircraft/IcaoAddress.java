package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * Represents an aircraft's ICAO address
 *
 * @param string the ICAO address of the aircraft
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public record IcaoAddress(String string) {
    private final static Pattern ICAO_ADRESS_PATTERN = Pattern.compile("[0-9A-F]{6}");

    /**
     * Validates the pattern of the given address if it matches the corresponding regular expression
     *
     * @param string The address to validate
     * @throws IllegalArgumentException if the address does not match the regular expression
     */
    public IcaoAddress {
        Preconditions.checkArgument(ICAO_ADRESS_PATTERN.matcher(string).matches());
    }
}
