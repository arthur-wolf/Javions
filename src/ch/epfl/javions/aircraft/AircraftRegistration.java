package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

/**
 * Represents an aircraft's registration string
 *
 * @param string the registration string of the aircraft
 * @author Arthur Wolf (344200)
 */
public record AircraftRegistration(String string) {
    public static Pattern AircraftRegistrationPattern = Pattern.compile("[A-Z0-9 .?/_+-]+");

    /**
     * Validates the pattern of the given address if it matches the corresponding regular expression
     *
     * @param string The registration to validate
     */
    public AircraftRegistration {
        if (!(AircraftRegistrationPattern.matcher(string).matches()))
            throw new IllegalArgumentException();
    }
}
