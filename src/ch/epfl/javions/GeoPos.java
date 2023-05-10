package ch.epfl.javions;

import static ch.epfl.javions.Units.Angle.*;

/**
 * Represents geographic coordinates expressed in t32
 *
 * @param longitudeT32 the longitude in t32
 * @param latitudeT32  the latitude in t32
 * @author Arthur Wolf (344200)
 */
public record GeoPos(int longitudeT32, int latitudeT32) {
    private final static int MAX_T32_VALUE = 1 << 30;

    /**
     * Constructs a GeoPos based on the given longitude and latitude expressed in t32
     *
     * @param longitudeT32 the longitude in t32
     * @param latitudeT32  the latitude in t32
     * @throws IllegalArgumentException if the longitude or the latitude is not expressed as a correct t32 value
     */
    public GeoPos {
        Preconditions.checkArgument(isValidLatitudeT32(latitudeT32));
    }

    /**
     * Checks if the given latitude expressed in t32 is indeed expressed as a correct t32 value
     *
     * @param latitudeT32 the latitude value to check
     * @return true if it is a correct t32 value
     */
    public static boolean isValidLatitudeT32(int latitudeT32) {
        return (latitudeT32 >= -MAX_T32_VALUE && latitudeT32 <= MAX_T32_VALUE);
    }

    /**
     * Converts the longitude from T32 to radians
     *
     * @return the value of the longitude in radians
     */
    public double longitude() {
        return Units.convertFrom(this.longitudeT32, T32);
    }

    /**
     * Converts the latitude from T32 to radians
     *
     * @return the value of the longitude in radians
     */
    public double latitude() {
        return Units.convertFrom(this.latitudeT32, T32);
    }

    /**
     * Creates a string from a GeoPos featuring its own longitude and latitude expressed in degrees
     *
     * @return the string as described above
     */
    @Override
    public String toString() {
        return "("
                + Units.convert(this.longitudeT32, T32, DEGREE)
                + "°, "
                + Units.convert(this.latitudeT32, T32, DEGREE)
                + "°)";
    }
}
