package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;

/**
 * Decodes the position of an aircraft
 *
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public final class CprDecoder {
    private static final double ZPHI0 = 60;
    private static final double ZPHI1 = 59;
    private static final double TURN = Units.Angle.TURN;
    private static final double RADIAN = Units.Angle.RADIAN;
    private static final double T_32 = Units.Angle.T32;

    private CprDecoder() {
    }

    /**
     * Returns the geographical position corresponding to the given normalized local positions depending on different cases
     *
     * @param x0         longitude of an even message
     * @param y0         latitude of an even message
     * @param x1         longitude of an odd message
     * @param y1         latitude of an odd message
     * @param mostRecent the most recent position of the message
     * @return the geographical position corresponding to the given normalized local positions
     * @throws IllegalArgumentException if mostRecent is not 0 or 1
     */
    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent) {
        Preconditions.checkArgument(mostRecent == 0 || mostRecent == 1);


        double zPhi0, zPhi1, zLambda0, zLambda1,
                latitudeZoneNumber, longitudeZoneNumber, Zlambda0, Zlambda1,
                secondLongitudeEvenZoneNumber, evenLatitude, oddLatitude, evenLongitude,
                oddLongitude, A, B;

        // Allows us to determine the latitude zone numbers (Zphi)
        latitudeZoneNumber = Math.rint(y0 * ZPHI1 - y1 * ZPHI0);

        // With y0 and y1 , we can determine zPhi0 and zPhi1, which are the latitude zone numbers in which the aircraft
        // is located in each of the two cutouts
        zPhi0 = latitudeZoneNumber < 0 ? latitudeZoneNumber + ZPHI0 : latitudeZoneNumber;
        zPhi1 = latitudeZoneNumber < 0 ? latitudeZoneNumber + ZPHI1 : latitudeZoneNumber;

        // The latitudes at which it was located when each of the messages was sent can be deduced from this:
        evenLatitude = (zPhi0 + y0) / ZPHI0;
        oddLatitude = (zPhi1 + y1) / ZPHI1;

        double tempEvenLat = Units.convert(evenLatitude, TURN, RADIAN);
        double tempOddLat = Units.convert(oddLatitude, TURN, RADIAN);

        // We can deduce the longitude zone numbers (Zlambda0) in the odd part from the latitude zone numbers (zPhi0)
        // If A is NaN, then the latitude is 0, and the longitude is 1
        A = Math.acos(1 - ((1 - Math.cos(TURN / ZPHI0)) / (Math.cos(tempEvenLat) * Math.cos(tempEvenLat))));
        Zlambda0 = Double.isNaN(A) ? 1 : Math.floor((2 * Math.PI) / A);
        Zlambda1 = Zlambda0 - 1;

        // We can deduce the longitude zone numbers in the even part from the latitude zone numbers (zPhi1)
        B = Math.acos(1 - ((1 - Math.cos(TURN / ZPHI0)) / (Math.cos(tempOddLat) * Math.cos(tempOddLat))));
        secondLongitudeEvenZoneNumber = Double.isNaN(B) ? 1 : Math.floor(TURN / B);

        // Since there are two messages available, this formula can be calculated with two different latitudes.
        // If two different values are obtained, this means that between the two messages the aircraft has changed its
        // "latitude band" and therefore its position cannot be determined.
        if (Zlambda0 != secondLongitudeEvenZoneNumber) {
            return null;
        }

        // Two cases can be distinguished for determining the corresponding longitude.
        // First case is where which corresponds to the polar zones in which there is only one zone of longitude.
        // The longitude (zLambda0 and zLambda1) is then simply given by :
        if (Zlambda0 == 1) {
            evenLongitude = x0;
            oddLongitude = x1;
        } else {
            // Outside the polar zones, as with latitude, the zone indexes corresponding to the two
            // messages must be calculated:
            longitudeZoneNumber = Math.rint(x0 * Zlambda1 - x1 * Zlambda0);
            if (longitudeZoneNumber < 0) {
                zLambda0 = longitudeZoneNumber + Zlambda0;
                zLambda1 = longitudeZoneNumber + Zlambda1;
            } else {
                zLambda0 = longitudeZoneNumber;
                zLambda1 = longitudeZoneNumber;
            }
            // The longitude at which it was located when each of the messages was sent can be deduced from this:
            evenLongitude = (zLambda0 + x0) / Zlambda0;
            oddLongitude = (zLambda1 + x1) / Zlambda1;
        }

        // The positions are always positive, which is contrary to convention.
        // They must be re-centered around 0 by converting angles greater than or equal to ½ turn to their
        // negative equivalent.
        double longitude = mostRecent == 0 ? recenter(evenLongitude) : recenter(oddLongitude);
        double latitude = mostRecent == 0 ? recenter(evenLatitude) : recenter(oddLatitude);

        longitude = Math.rint(Units.convert(longitude, TURN, T_32));
        latitude = Math.rint(Units.convert(latitude, TURN, T_32));


        // Returns null if the latitude of the decoded position is not valid (i.e. if it not within ±2^32)
        return GeoPos.isValidLatitudeT32((int) latitude) ? new GeoPos((int) longitude, (int) latitude) : null;

    }

    /**
     * Recenter the angle around 0 by converting angles greater than or equal to ½ turn into their negative equivalent.
     *
     * @param x the angle to convert
     * @return the converted angle
     */
    private static double recenter(double x) {
        return x >= 0.5 ? x - 1 : x;
    }
}