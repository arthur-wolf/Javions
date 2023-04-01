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

    /**
     * Constructs a GeoPos based on the given longitude and latitude expressed in t32
     *
     * @param longitudeT32 the longitude in t32
     * @param latitudeT32  the latitude in t32
     */
    public GeoPos {Preconditions.checkArgument(isValidLatitudeT32(latitudeT32));}

    /**
     * Checks if the given latitude expressed in t32 is indeed expressed as a correct t32 value
     *
     * @param latitudeT32 the latitude value to check
     * @return true if it is a correct t32 value
     */
    public static boolean isValidLatitudeT32(int latitudeT32) {
        return (latitudeT32 >= (-(1 << 30)) && latitudeT32 <= (1 << 30));
    }

    /**
     * Converts the longitude from T32 to radians
     *
     * @return the value of the longitude in radians
     */
    public double longitude() {
        return Units.convert(this.longitudeT32, T32, RADIAN);
    }

    /**
     * Converts the latitude from T32 to radians
     *
     * @return the value of the longitude in radians
     */
    public double latitude() { return Units.convert(this.latitudeT32, T32, RADIAN); }

    /**
     * Creates a string from a GeoPos featuring its own longitude and latitude expressed in degrees
     *
     * @return the string as described above
     */
    @Override
    public String toString() {
        return "(" + Units.convert(this.longitudeT32, T32, DEGREE)
                + "°, " + Units.convert(this.latitudeT32, T32, DEGREE) + "°)";
    }
}
