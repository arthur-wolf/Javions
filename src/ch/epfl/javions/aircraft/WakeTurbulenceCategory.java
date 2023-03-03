package ch.epfl.javions.aircraft;

/**
 * Different enum types listed to define the level of turbulence
 *
 * @author Oussama Ghali (341478)
 */
public enum WakeTurbulenceCategory {
    LIGHT,
    MEDIUM,
    HEAVY,
    UNKNOWN;

    /**
     * Returns the turbulence level depending on the string s
     *
     * @param s String used to define the turbulence level
     * @return returns the level of turbulence
     */
    public static WakeTurbulenceCategory of(String s) {
        return switch (s) {
            case "L" -> LIGHT;
            case "M" -> MEDIUM;
            case "H" -> HEAVY;
            default -> UNKNOWN;
        };
    }
}
