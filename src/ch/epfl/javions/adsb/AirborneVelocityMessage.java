package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

import static ch.epfl.javions.Units.Angle.TURN;
import static ch.epfl.javions.Units.Speed.KNOT;

/**
 * Represents an airborne velocity message
 *
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */

public record AirborneVelocityMessage(long timeStampNs,
                                      IcaoAddress icaoAddress,
                                      double speed,
                                      double trackOrHeading) implements Message {
    private static final int SUBSONIC = 1;
    private static final int SUPERSONIC = 4;
    private static final int VELOCITY_VALUES_INDEX = 21;
    private static final int VELOCITY_VALUES_SIZE = 22;
    private static final int SUBTYPE_INDEX = 48;
    private static final int SUBTYPE_SIZE = 3;

    private static final int VNS_INDEX = 0;
    private static final int DNS_INDEX = 10;
    private static final int VEW_INDEX = 11;
    private static final int DEW_INDEX = 21;
    private static final int GROUND_VALUES_SIZE = 10;
    private static final int GROUND_DIRECTION_SIZE = 1;

    private static final int SH_INDEX = 21;
    private static final int HDG_INDEX = 11;
    private static final int AS_INDEX = 0;
    private static final int AIR_DATA_SIZE = 10;

    /**
     * Returns an AirborneVelocityMessage if the raw message is valid, null otherwise
     *
     * @param timeStampNs    the time stamp of the message in nanoseconds
     * @param icaoAddress    the ICAO address of the sender of the message
     * @param speed          the speed of the aircraft in meters per second when the message was sent
     * @param trackOrHeading the track or heading of the aircraft in radians when the message was sent
     */

    public AirborneVelocityMessage {
        Objects.requireNonNull(icaoAddress);
        Preconditions.checkArgument(timeStampNs >= 0 && speed >= 0 && trackOrHeading >= 0);
    }

    /**
     * Returns an AirborneVelocityMessage if the raw message is valid, null otherwise
     *
     * @param rawMessage the raw message to extract the AirborneVelocityMessage from
     * @return an AirborneVelocityMessage if the raw message is valid, null otherwise
     */
    public static AirborneVelocityMessage of(RawMessage rawMessage) {
        int velocityValues = Bits.extractUInt(rawMessage.payload(), VELOCITY_VALUES_INDEX, VELOCITY_VALUES_SIZE);
        int subType = Bits.extractUInt(rawMessage.payload(), SUBTYPE_INDEX, SUBTYPE_SIZE);
        // if the subtype is valid
        return switch (subType) {
            case 1 -> groundSpeed(rawMessage, velocityValues, SUBSONIC);
            case 2 -> groundSpeed(rawMessage, velocityValues, SUPERSONIC);
            case 3 -> airSpeed(rawMessage, velocityValues, SUBSONIC);
            case 4 -> airSpeed(rawMessage, velocityValues, SUPERSONIC);
            default -> null;
        };
    }

    /**
     * Returns an AirborneVelocityMessage of a rawMessage whose subtype is 1 or 2
     *
     * @param rawMessage     the raw message to extract the data from
     * @param velocityValues the payload of the raw message
     * @param subOrSup       subsonic or supersonic speed
     * @return an AirborneVelocityMessage of a rawMessage whose subtype is 1 or 2
     */
    private static AirborneVelocityMessage groundSpeed(RawMessage rawMessage, int velocityValues, int subOrSup) {
        int vns = Bits.extractUInt(velocityValues, VNS_INDEX, GROUND_VALUES_SIZE);
        int vew = Bits.extractUInt(velocityValues, VEW_INDEX, GROUND_VALUES_SIZE);

        int dns = Bits.extractUInt(velocityValues, DNS_INDEX, GROUND_DIRECTION_SIZE);
        int dew = Bits.extractUInt(velocityValues, DEW_INDEX, GROUND_DIRECTION_SIZE);

        if (vns == 0 || vew == 0)
            return null;

        vew = (dew == 0) ? (vew - 1) : -(vew - 1);
        vns = (dns == 0) ? (vns - 1) : -(vns - 1);

        double speed = subOrSup * Math.hypot(vew, vns);
        double trackOrHeading = Math.atan2(vew, vns) >= 0 ? Math.atan2(vew, vns) : Math.atan2(vew, vns) + (2 * Math.PI);

        return new AirborneVelocityMessage(
                rawMessage.timeStampNs(),
                rawMessage.icaoAddress(),
                Units.convertFrom(speed, KNOT),
                trackOrHeading);
    }

    /**
     * Returns an AirborneVelocityMessage of a rawMessage whose subtype is 3 or 4
     *
     * @param rawMessage     the raw message to extract the data from
     * @param velocityValues the payload of the raw message
     * @param subOrSup       subsonic or supersonic speed
     * @return an AirborneVelocityMessage of a rawMessage whose subtype is 3 or 4
     */
    private static AirborneVelocityMessage airSpeed(RawMessage rawMessage, int velocityValues, int subOrSup) {
        if (Bits.testBit(velocityValues, SH_INDEX)) {
            double hdg = (double) Bits.extractUInt(velocityValues, HDG_INDEX, AIR_DATA_SIZE) / (1 << 10);
            double as = Bits.extractUInt(velocityValues, AS_INDEX, AIR_DATA_SIZE);

            if (as == 0)
                return null;

            return new AirborneVelocityMessage(
                    rawMessage.timeStampNs(),
                    rawMessage.icaoAddress(),
                    Units.convertFrom(subOrSup * (as - 1), KNOT),
                    Units.convertFrom(hdg, TURN));
        }
        return null;
    }
}
