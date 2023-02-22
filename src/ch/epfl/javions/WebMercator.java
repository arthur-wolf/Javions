package ch.epfl.javions;

public class WebMercator {
    private WebMercator (){}

    /**
     * This method is a formula for projecting a point whose geographic coordinates are known.
     * It gives the Cartesian x-coordinate of a point on the map at the zoom level, as a function of the geographic coordinates expressed in radians
     * @param zoomLevel : the zoom level of the map
     * @param longitude : the longitude expressed in radians
     * @return returns the x-coordinate corresponding to the given longitude (in radians) at the given zoom level
     */
    public static double x(int zoomLevel, double longitude){
        return Math.scalb((Units.convertTo(longitude,Units.Angle.TURN) + 0.5), 8 + zoomLevel);
    }

    /**
     * This method is a formula for projecting a point whose geographic coordinates are known.
     * It gives the Cartesian y-coordinate of a point on the map at the zoom level, as a function of the geographic coordinates expressed in radians
     * @param zoomLevel : the zoom level of the map
     * @param latitude : the latitude expressed in radians
     * @return returns the y-coordinate corresponding to the given latitude (in radians) at the given zoom level.
     */
    public static double y(int zoomLevel, double latitude){
        double phi = -Math2.asinh(Math.tan(latitude));
        return Math.scalb((Units.convertTo(phi,Units.Angle.TURN) + 0.5), 8 + zoomLevel);
    }

}
