package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * Represents an aircraft's type designator
 *
 * @param string the type designator of the aircraft
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public record AircraftTypeDesignator(String string) {
    public static Pattern aircraftTypeDesignatorPattern = Pattern.compile("[A-Z0-9]{2,4}");

    /**
     * Validates the pattern of the given address if it matches the corresponding regular expression or if it is an empty string
     *
     * @param string The type designator to validate
     */
    public AircraftTypeDesignator {
        Preconditions.checkArgument(aircraftTypeDesignatorPattern.matcher(string).matches() || string.isEmpty());
    }
}
