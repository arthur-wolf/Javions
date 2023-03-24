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
     * @return returns the geographical position corresponding to the given normalized local positions
     */
    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent) {
        Preconditions.checkArgument(mostRecent == 0 || mostRecent == 1);

        double evenZoneLatitude, oddZoneLatitude, evenZoneLongitude, oddZoneLongitude,
                latitudeZoneNumber, longitudeZoneNumber, firstLongitudeEvenZoneNumber, firstLatitudeEvenZoneNumber,
                secondLongitudeEvenZoneNumber, evenLatitude, oddLatitude, evenLongitude,
                oddLongitude, A, B;

        // Compute Zphi
        latitudeZoneNumber = Math.rint(y0 * ZPHI1 - y1 * ZPHI0);

        evenZoneLatitude = latitudeZoneNumber < 0 ? latitudeZoneNumber + ZPHI0 : latitudeZoneNumber;
        oddZoneLatitude = latitudeZoneNumber < 0 ? latitudeZoneNumber + ZPHI1 : latitudeZoneNumber;

        evenLatitude = (evenZoneLatitude + y0) / ZPHI0;
        oddLatitude = (oddZoneLatitude + y1) / ZPHI1;

        double tempEvenLat = Units.convert(evenLatitude, Units.Angle.TURN, Units.Angle.RADIAN);
        double tempOddLat = Units.convert(oddLatitude, Units.Angle.TURN, Units.Angle.RADIAN);

        A = Math.acos(1 - ((1 - Math.cos(2 * Math.PI / ZPHI0)) / (Math.cos(tempEvenLat) * Math.cos(tempEvenLat))));
        firstLongitudeEvenZoneNumber = Double.isNaN(A) ? 1 : Math.floor((2 * Math.PI) / A);
        firstLatitudeEvenZoneNumber = firstLongitudeEvenZoneNumber - 1;

        B = Math.acos(1 - ((1 - Math.cos(2 * Math.PI / ZPHI0)) / (Math.cos(tempOddLat) * Math.cos(tempOddLat))));
        secondLongitudeEvenZoneNumber = Double.isNaN(B) ? 1 : Math.floor((2 * Math.PI) / B);

        if (!isValid(firstLongitudeEvenZoneNumber, secondLongitudeEvenZoneNumber)) {
            return null;
        }

        if (firstLongitudeEvenZoneNumber == 1) {
            evenLongitude = x0;
            oddLongitude = x1;
        } else {
            longitudeZoneNumber = Math.rint(x0 * firstLatitudeEvenZoneNumber - x1 * firstLongitudeEvenZoneNumber);
            double offset = longitudeZoneNumber < 0 ? firstLongitudeEvenZoneNumber : 0;
            evenZoneLongitude = longitudeZoneNumber + offset;
            oddZoneLongitude = longitudeZoneNumber + firstLatitudeEvenZoneNumber - offset;
            evenLongitude = (evenZoneLongitude + x0) / firstLongitudeEvenZoneNumber;
            oddLongitude = (oddZoneLongitude + x1) / firstLatitudeEvenZoneNumber;
        }

        double longitude = mostRecent == 0 ? recenter(evenLongitude) : recenter(oddLongitude);
        double latitude = mostRecent == 0 ? recenter(evenLatitude) : recenter(oddLatitude);

        longitude = Math.rint(Units.convert(longitude, Units.Angle.TURN, Units.Angle.T32));
        latitude = Math.rint(Units.convert(latitude, Units.Angle.TURN, Units.Angle.T32));

        return GeoPos.isValidLatitudeT32((int) latitude) ? new GeoPos((int) longitude, (int) latitude) : null;
    }

    /**
     * Check that we have the same values. If not, the aircraft has changed "latitude band", and it is therefore not possible to determine its position.
     *
     * @param x the first value
     * @param y the second value
     * @return true if the values are the same, false otherwise
     */
    private static boolean isValid(double x, double y) {
        return x == y;
    }

    /**
     * Allows to recenter around 0 by converting angles greater than or equal to ½ turn into their negative equivalent.
     *
     * @param x the angle to convert
     * @return the converted angle
     */
    private static double recenter(double x) {
        return x >= 0.5 ? x - 1 : x;
    }
}