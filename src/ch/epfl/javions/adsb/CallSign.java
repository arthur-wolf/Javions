package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * Represents an aircraft's call sign
 *
 * @param string the aircraft's call sign
 * @author Arthur Wolf (344200)
 */
public record CallSign(String string) {
    private final static Pattern callSignPattern = Pattern.compile("[A-Z0-9]{0,8}");

    /**
     * Validates the pattern of the given address if it matches the corresponding regular expression
     *
     * @param string The address to validate
     */
    public CallSign { Preconditions.checkArgument(callSignPattern.matcher(string).matches()); }
}
