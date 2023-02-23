package ch.epfl.javions;

/**
 * Represents geographic coordinates expressed in t32
 * @author Arthur Wolf (344200)
 * @param longitudeT32 the longitude in t32
 * @param latitudeT32 the latitude in t32
 */
public record GeoPos(int longitudeT32, int latitudeT32) {
    public GeoPos {
        if (!isValidLatitudeT32(latitudeT32))
            throw new IllegalArgumentException();
    }

    /**
     * Checks if the given latitude expressed in t32 is indeed expressed as a correct t32 value
     * @param latitudeT32 the latitude value to check
     * @return true if it is a correct t32 value
     */
    public static boolean isValidLatitudeT32(int latitudeT32) {
        return (latitudeT32 >= (-(1 << 30)) && latitudeT32 <= (1 << 30));
    }

    /**
     * Converts the longitude from t32 to radians
     * @return the value of the longitude in radians
     */
    public double longitude() {
        return Units.convert(this.longitudeT32, Units.Angle.T32, Units.Angle.RADIAN);
    }

    /**
     * Converts the latitude from t32 to radians
     * @return the value of the longitude in radians
     */
    public double latitude() {
        return Units.convert(this.latitudeT32, Units.Angle.T32, Units.Angle.RADIAN);
    }

    /**
     * Creates a string from a GeoPos featuring its own longitude and latitude expressed in degrees
     * @return the string as described above
     */
    @Override
    public String toString() {
        return "(" + Units.convert(this.longitudeT32, Units.Angle.T32, Units.Angle.DEGREE)
                + "°, " + Units.convert(this.latitudeT32, Units.Angle.T32, Units.Angle.DEGREE) + "°)";
    }
}
