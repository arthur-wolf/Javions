package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

import static ch.epfl.javions.Units.Length.FOOT;

/**
 * Represents an Airborne Position Message
 *
 * @param timeStampNs the time stamp of the message in nanoseconds
 * @param icaoAddress the ICAO address of the sender of the message
 * @param altitude    the altitude of the aircraft in meters when the message was sent
 * @param parity      the parity of the message (0 is even, 1 is odd)
 * @param x           the normalised local longitude of the aircraft when the message was sent
 * @param y           the normalised local latitude of the aircraft when the message was sent
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public record AirbornePositionMessage(long timeStampNs,
                                      IcaoAddress icaoAddress,
                                      double altitude,
                                      int parity,
                                      double x,
                                      double y) implements Message {
    private static final int ALTITUDE_INDEX = 36;
    private static final int ALTITUDE_SIZE = 12;
    private static final int QBIT_INDEX = 4;
    private static final int PARITY_INDEX = 34;
    private static final int LONGITUDE_INDEX = 0;
    private static final int LONGITUDE_SIZE = 17;
    private static final int LATITUDE_INDEX = 17;
    private static final int LATITUDE_SIZE = 17;

    /**
     * Constructs a new AirbornePositionMessage
     *
     * @param timeStampNs the time stamp of the message in nanoseconds
     * @param icaoAddress the ICAO address of the sender of the message
     * @param altitude    the altitude of the aircraft in meters when the message was sent
     * @param parity      the parity of the message (0 is even, 1 is odd)
     * @param x           the normalised local longitude of the aircraft when the message was sent
     * @param y           the normalised local latitude of the aircraft when the message was sent
     * @throws NullPointerException     if the ICAO address is null
     * @throws IllegalArgumentException if the time stamp is negative, the parity is not 0 or 1, or if x or y are not in [0, 1[
     */
    public AirbornePositionMessage {
        Objects.requireNonNull(icaoAddress);
        Preconditions.checkArgument(timeStampNs >= 0);
        Preconditions.checkArgument(parity == 0 || parity == 1);
        Preconditions.checkArgument(0 <= x && x < 1);
        Preconditions.checkArgument(0 <= y && y < 1);
    }

    /**
     * Returns an AirbornePositionMessage if the raw message is valid, null otherwise
     *
     * @param rawMessage the raw message to extract the AirbornePositionMessage from
     * @return an AirbornePositionMessage if the raw message is valid, null otherwise
     */
    public static AirbornePositionMessage of(RawMessage rawMessage) {
        double altitude = altitude(rawMessage);
        if (!Double.isNaN(altitude)) {
            return new AirbornePositionMessage(
                    rawMessage.timeStampNs(),
                    rawMessage.icaoAddress(),
                    altitude,
                    parity(rawMessage),
                    longitude(rawMessage),
                    latitude(rawMessage));
        }
        return null;
    }

    /**
     * Returns the altitude of the aircraft that sent the message
     *
     * @param rawMessage the raw message to extract the altitude from
     * @return the altitude of the aircraft that sent the message
     */
    private static double altitude(RawMessage rawMessage) {
        int altitude = Bits.extractUInt(rawMessage.payload(), ALTITUDE_INDEX, ALTITUDE_SIZE);

        if (Bits.testBit(altitude, QBIT_INDEX)) {
            // Take the 4 last bits of the altitude and append them to the first 7 bits of the altitude
            int altitude1 = ((Bits.extractUInt(altitude, 5, 7) << 4) | Bits.extractUInt(altitude, 0, 4)) * 25 - 1000;
            return Units.convertFrom(altitude1, FOOT);
        } else {
            int unraveledAltitude = unravel(altitude);
            int msb9 = grayValueOf(unraveledAltitude >> 3, 9);
            int lsb3 = grayValueOf(unraveledAltitude & 0b111, 3);

            if (lsb3 == 0 || lsb3 == 5 || lsb3 == 6)
                return Double.NaN;

            if (lsb3 == 7)
                lsb3 = 5;

            if (msb9 % 2 == 1)
                lsb3 = 6 - lsb3;

            int altitudeInFeet = lsb3 * 100 + msb9 * 500 - 1300;

            return Units.convertFrom(altitudeInFeet, FOOT);
        }
    }

    /**
     * Unravels the altitude as described in the subject
     *
     * @param altitude the altitude to unravel
     * @return the unraveled altitude
     */
    private static int unravel(int altitude) {
        int altitude1 = 0;
        for (int i = 16; i > 5; i -= 2) {
            altitude1 = altitude1 << 1 | Bits.extractUInt(altitude, i % 12, 1);
        }
        for (int i = 17; i > 6; i -= 2) {
            altitude1 = altitude1 << 1 | Bits.extractUInt(altitude, i % 12, 1);
        }
        return altitude1;
    }

    /**
     * Returns the gray value of the binary number
     *
     * @param binary the binary number
     * @param length the length of the binary number
     * @return the gray value of the binary number
     */
    private static int grayValueOf(int binary, int length) {
        int code = 0;
        for (int i = 0; i < length; i++) {
            code = code ^ (binary >> i);
        }
        return code;
    }

    /**
     * Returns the parity of the message
     *
     * @param rawMessage the raw message to extract the parity from
     * @return the parity of the message
     */
    private static int parity(RawMessage rawMessage) {
        return Bits.extractUInt(rawMessage.payload(), PARITY_INDEX, 1);
    }

    /**
     * Returns the normalised local longitude of the aircraft that sent the message
     *
     * @param rawMessage the raw message to extract the longitude from
     * @return the normalised local longitude of the aircraft that sent the message
     */
    private static double longitude(RawMessage rawMessage) {
        double longitude = Bits.extractUInt(rawMessage.payload(), LONGITUDE_INDEX, LONGITUDE_SIZE);
        return Math.scalb(longitude, -17);
    }

    /**
     * Returns the normalised local latitude of the aircraft that sent the message
     *
     * @param rawMessage the raw message to extract the latitude from
     * @return the normalised local latitude of the aircraft that sent the message
     */
    private static double latitude(RawMessage rawMessage) {
        double latitude = Bits.extractUInt(rawMessage.payload(), LATITUDE_INDEX, LATITUDE_SIZE);
        return Math.scalb(latitude, -17);
    }
}
