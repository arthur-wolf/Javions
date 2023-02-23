package ch.epfl.javions;

/**
 * Methods to project geographic coordinates with regard to the WebMercator projection
 * @author Oussama Ghali (341478)
 */
public class WebMercator {
    private WebMercator (){}

    /**
     * Projects a point based on its geographic coordinates (longitude and latitude) and computes the x-coordinate in the WebMercator projection
     * @param zoomLevel the zoom level of the map
     * @param longitude the longitude expressed in radians
     * @return returns the x-coordinate corresponding to the given longitude (in radians) at the given zoom level
     */
    public static double x(int zoomLevel, double longitude){
        return Math.scalb((Units.convertTo(longitude,Units.Angle.TURN) + 0.5), 8 + zoomLevel);
    }

    /**
     * Projects a point based on its geographic coordinates (longitude and latitude) and computes the y-coordinate in the WebMercator projection
     * @param zoomLevel the zoom level of the map
     * @param latitude the latitude expressed in radians
     * @return returns the y-coordinate corresponding to the given latitude (in radians) at the given zoom level.
     */
    public static double y(int zoomLevel, double latitude){
        double phi = -Math2.asinh(Math.tan(latitude));
        return Math.scalb((Units.convertTo(phi,Units.Angle.TURN) + 0.5), 8 + zoomLevel);
    }

}
