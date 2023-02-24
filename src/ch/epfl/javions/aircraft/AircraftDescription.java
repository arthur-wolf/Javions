package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

/**
 * Represents an aircraft's description
 *
 * @param string the description of the aircraft
 * @author Arthur Wolf (344200)
 */
public record AircraftDescription(String string) {
    public static Pattern AircraftDescriptionPattern = Pattern.compile("[ABDGHLPRSTV-][0123468][EJPT-]");

    /**
     * Validates the pattern of the given address if it matches the corresponding regular expression or if it is an empty string
     *
     * @param string The description to validate
     */
    public AircraftDescription {
        if (!(AircraftDescriptionPattern.matcher(string).matches() || string.isEmpty()))
            throw new IllegalArgumentException();
    }
}
