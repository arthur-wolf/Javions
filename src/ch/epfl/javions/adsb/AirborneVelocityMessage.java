package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

/**
 * Represents an airborne velocity message of type.
 *
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */

public record AirborneVelocityMessage(long timeStampNs,
                                      IcaoAddress icaoAddress,
                                      double speed,
                                      double trackOrHeading) implements Message {
    private static final int SUBSONIC = 1;
    private static final int SUPERSONIC = 2;

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
        int velocityValues = Bits.extractUInt(rawMessage.payload(), 21, 22);
        int subType = Bits.extractUInt(rawMessage.payload(), 48, 3);
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
     * @param rawMessage the raw message to extract the data from
     * @param payload    the payload of the raw message
     * @param subOrSup   subsonic or supersonic speed
     * @return an AirborneVelocityMessage of a rawMessage whose subtype is 1 or 2
     */
    private static AirborneVelocityMessage groundSpeed(RawMessage rawMessage, int payload, int subOrSup) {
        int vns = Bits.extractUInt(payload, 0, 10) - 1;
        int vew = Bits.extractUInt(payload, 11, 10) - 1;

        int dew = Bits.extractUInt(payload, 21, 1);
        int dns = Bits.extractUInt(payload, 10, 1);

        if (vns == -1 || vew == -1)return null;

        vew = (dew == 0) ? vew : -vew;
        vns = (dns == 0) ? vns : -vns;

        double speed = subOrSup * Math.hypot(vew, vns);
        double trackOrHeading = Math.atan2(vew, vns) >= 0 ? Math.atan2(vew, vns) : Math.atan2(vew, vns) + (2 * Math.PI);

        return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), Units.convertFrom(speed, Units.Speed.KNOT), trackOrHeading);
    }

    /**
     * Returns an AirborneVelocityMessage of a rawMessage whose subtype is 3 or 4
     *
     * @param rawMessage the raw message to extract the data from
     * @param payload    the payload of the raw message
     * @param subOrSup   subsonic or supersonic speed
     * @return an AirborneVelocityMessage of a rawMessage whose subtype is 3 or 4
     */
    private static AirborneVelocityMessage airSpeed(RawMessage rawMessage, int payload, int subOrSup) {
        if (Bits.testBit(payload, 21)) {
            int hdg = Bits.extractUInt(payload, 11, 10) / (1 << 10);
            int as = Bits.extractUInt(payload, 0, 10) - 1;

            if (as == -1) return null;

            return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), Units.convertFrom(subOrSup * as, Units.Speed.KNOT), Units.convertFrom(hdg, Units.Angle.TURN));
        }
        return null;
    }
}
