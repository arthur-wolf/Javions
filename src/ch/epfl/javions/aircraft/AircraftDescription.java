package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * Represents an aircraft's description
 *
 * @param string the description of the aircraft
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public record AircraftDescription(String string) {
    private static final Pattern AIRCRAFT_DESCRIPTION_PATTERN = Pattern.compile("[ABDGHLPRSTV-][0123468][EJPT-]");

    /**
     * Validates the pattern of the given address if it matches the corresponding regular expression or if it is an empty string
     *
     * @param string The description to validate
     * @throws IllegalArgumentException if the description does not match the regular expression and is not an empty string
     */
    public AircraftDescription {
        Preconditions.checkArgument(AIRCRAFT_DESCRIPTION_PATTERN.matcher(string).matches() || string.isEmpty());
    }
}
