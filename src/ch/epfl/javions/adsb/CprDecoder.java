package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;

/**
 * Decodes the position of an aircraft
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public final class CprDecoder {
    private static final double ZPHI0 = 60;
    private static final double ZPHI1 = 59;

    private CprDecoder() {
    }
    /*public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent) {
        if (mostRecent != 0 && mostRecent != 1) {
            throw new IllegalArgumentException();
        }
        double evenZoneLatitude, oddZoneLatitude, evenZoneLongitude, oddZoneLongitude, numberZoneLatitude, numberZoneLongitude, numberFirstEvenZoneLongitude,
                numberFirstOddZoneLongitude, numberSecondEvenZoneLongitude, numberSecondOddZoneLongitude, evenLatAngle, oddLatAngle, evenLongAngle, oddLongAngle,
                tempEvenLatAngle, tempOddLatAngle, A, B;

        numberZoneLatitude = Math.rint(y0 * ZPHI1 - y1 * ZPHI0);

        if (numberZoneLatitude < 0) {
            evenZoneLatitude = numberZoneLatitude + ZPHI0;
            oddZoneLatitude = numberZoneLatitude + ZPHI1;
        } else {
            evenZoneLatitude = numberZoneLatitude;
            oddZoneLatitude = numberZoneLatitude;
        }
        evenLatAngle = (evenZoneLatitude + y0) / ZPHI0;
        oddLatAngle = (oddZoneLatitude + y1) / ZPHI1;

        tempEvenLatAngle = evenLatAngle;
        tempOddLatAngle = oddLatAngle;

        evenLatAngle = Units.convert(evenLatAngle, Units.Angle.TURN, Units.Angle.RADIAN);
        oddLatAngle = Units.convert(oddLatAngle, Units.Angle.TURN, Units.Angle.RADIAN);

        A = Math.acos(1 - ((1 - Math.cos(2 * Math.PI / ZPHI0)) / (Math.cos(evenLatAngle) * Math.cos(evenLatAngle))));
        if(Double.isNaN(A)){
            numberFirstEvenZoneLongitude = 1;
        }
        else {
            numberFirstEvenZoneLongitude = Math.floor((2 * Math.PI) / A);
        }
        numberFirstOddZoneLongitude = numberFirstEvenZoneLongitude - 1;

        B = Math.acos(1 - ((1 - Math.cos(2 * Math.PI / ZPHI0)) / (Math.cos(oddLatAngle) * Math.cos(oddLatAngle))));
        if(Double.isNaN(B)){
            numberSecondEvenZoneLongitude = 1;
        }
        else {
            numberSecondEvenZoneLongitude = Math.floor((2 * Math.PI) / B);
        }
        numberSecondOddZoneLongitude = numberSecondEvenZoneLongitude - 1;
        if (isValid(numberFirstEvenZoneLongitude, numberSecondEvenZoneLongitude)) {
            if (numberFirstEvenZoneLongitude == 1) {
                evenLongAngle = x0;
                oddLongAngle = x1;
            } else {
                numberZoneLongitude = Math.rint(x0 * numberFirstOddZoneLongitude - x1 * numberFirstEvenZoneLongitude);
                if (numberZoneLongitude < 0) {
                    evenZoneLongitude = numberZoneLongitude + numberFirstEvenZoneLongitude;
                    oddZoneLongitude = numberZoneLongitude + numberFirstOddZoneLongitude;
                } else {
                    evenZoneLongitude = numberZoneLongitude;
                    oddZoneLongitude = numberZoneLongitude;
                }
                evenLongAngle = (evenZoneLongitude + x0) / numberFirstEvenZoneLongitude;
                oddLongAngle = (oddZoneLongitude + x1) / numberFirstOddZoneLongitude;
            }
            if (mostRecent == 0) {
                evenLongAngle = inTurn(evenLongAngle);
                tempEvenLatAngle = inTurn(tempEvenLatAngle);
                evenLongAngle = Math.rint(Units.convert(evenLongAngle, Units.Angle.TURN, Units.Angle.T32));
                tempEvenLatAngle = Math.rint(Units.convert(tempEvenLatAngle, Units.Angle.TURN, Units.Angle.T32));
                GeoPos.isValidLatitudeT32((int) tempEvenLatAngle);

                return new GeoPos((int) evenLongAngle, (int) tempEvenLatAngle);

            } else {
                tempOddLatAngle = inTurn(tempOddLatAngle);
                oddLongAngle = inTurn(oddLongAngle);
                oddLongAngle = Math.rint(Units.convert(oddLongAngle, Units.Angle.TURN, Units.Angle.T32));
                tempOddLatAngle = Math.rint(Units.convert(tempOddLatAngle, Units.Angle.TURN, Units.Angle.T32));
                GeoPos.isValidLatitudeT32((int) tempOddLatAngle);

                return new GeoPos((int) oddLongAngle, (int) tempOddLatAngle);
            }
        } else {
            return null;
        }
    }

     */

    /**
     * Returns the geographical position corresponding to the given normalized local positions depending on different cases (see the comment in the method)
     * @param x0 longitude of an even message
     * @param y0 latitude of an even message
     * @param x1 longitude of an odd message
     * @param y1 latitude of an odd message
     * @param mostRecent the most recent position of the message
     * @return returns the geographical position corresponding to the given normalized local positions
     */
    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent) {
        if (mostRecent != 0 && mostRecent != 1) {
            throw new IllegalArgumentException();
        }
        double evenZoneLat, oddZoneLat, evenZoneLong, oddZoneLong, numberZoneLat, numberZoneLong, numberFirstEvenZoneLong,
        numberFirstOddZoneLong, numberSecondEvenZoneLong, evenLat, oddLat, evenLong, oddLong, A, B;

        numberZoneLat = Math.rint(y0 * ZPHI1 - y1 * ZPHI0);

        evenZoneLat = numberZoneLat < 0 ? numberZoneLat + ZPHI0 : numberZoneLat;
        oddZoneLat = numberZoneLat < 0 ? numberZoneLat + ZPHI1 : numberZoneLat;

        evenLat = (evenZoneLat + y0) / ZPHI0;
        oddLat = (oddZoneLat + y1) / ZPHI1;

        double tempEvenLat = Units.convert(evenLat, Units.Angle.TURN, Units.Angle.RADIAN);
        double tempOddLat = Units.convert(oddLat, Units.Angle.TURN, Units.Angle.RADIAN);

        A = Math.acos(1 - ((1 - Math.cos(2 * Math.PI / ZPHI0)) / (Math.cos(tempEvenLat) * Math.cos(tempEvenLat))));
        numberFirstEvenZoneLong = Double.isNaN(A) ? 1 : Math.floor((2 * Math.PI) / A);
        numberFirstOddZoneLong = numberFirstEvenZoneLong - 1;

        B = Math.acos(1 - ((1 - Math.cos(2 * Math.PI / ZPHI0)) / (Math.cos(tempOddLat) * Math.cos(tempOddLat))));
        numberSecondEvenZoneLong = Double.isNaN(B) ? 1 : Math.floor((2 * Math.PI) / B);

        if (!isValid(numberFirstEvenZoneLong, numberSecondEvenZoneLong)) {
            return null;
        }
        if (numberFirstEvenZoneLong == 1) {
            evenLong = x0;
            oddLong = x1;
        } else {
            numberZoneLong = Math.rint(x0 * numberFirstOddZoneLong - x1 * numberFirstEvenZoneLong);
            double offset = numberZoneLong < 0 ? numberFirstEvenZoneLong : 0;
            evenZoneLong = numberZoneLong + offset;
            oddZoneLong = numberZoneLong + numberFirstOddZoneLong - offset;
            evenLong = (evenZoneLong + x0) / numberFirstEvenZoneLong;
            oddLong = (oddZoneLong + x1) / numberFirstOddZoneLong;
        }
        double longitude = mostRecent == 0 ? inTurn(evenLong) : inTurn(oddLong);
        double latitude = mostRecent == 0 ? inTurn(evenLat) : inTurn(oddLat);

        longitude = Math.rint(Units.convert(longitude, Units.Angle.TURN, Units.Angle.T32));
        latitude = Math.rint(Units.convert(latitude, Units.Angle.TURN, Units.Angle.T32));
        GeoPos.isValidLatitudeT32((int) latitude);
        return new GeoPos((int) longitude, (int) latitude);

    }

    /**
     * Check that we have the same values, if not the aircraft has changed "latitude band", and it is therefore not possible to determine its position.
     * @param x the first value
     * @param y the second value
     * @return true if the values are the same, false otherwise
     */
    public static boolean isValid(double x, double y) {
        return x == y;
    }

    /**
     * Allows to recenter around 0 by converting angles greater than or equal to ½ turn into their negative equivalent.
     * @param x the angle to convert
     * @return the converted angle
     */
    private static double inTurn(double x) {
        return x >= 0.5 ? x - 1 : x;
    }

}