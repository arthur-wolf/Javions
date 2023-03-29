package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;
import ch.epfl.javions.Bits;

import java.util.Objects;

/**
 * Represents an Aircraft Identification Message
 *
 * @param timeStampNs the time stamp of the message in nanoseconds
 * @param icaoAddress the ICAO address of the sender of the message
 * @param category    the category of the aircraft
 * @param callSign    the call sign of the aircraft
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public record AircraftIdentificationMessage(long timeStampNs,
                                            IcaoAddress icaoAddress,
                                            int category,
                                            CallSign callSign) implements Message {

    public AircraftIdentificationMessage {
        Objects.requireNonNull(icaoAddress);
        Objects.requireNonNull(callSign);
        Preconditions.checkArgument(timeStampNs >= 0);
    }

    /**
     * Returns an AircraftIdentificationMessage if the raw message is valid, null otherwise
     *
     * @param rawMessage the raw message to extract the AircraftIdentificationMessage from
     * @return an AircraftIdentificationMessage if the raw message is valid, null otherwise
     */
    public static AircraftIdentificationMessage of(RawMessage rawMessage) {
        CallSign callSign1 = callSign(rawMessage);
        return (callSign1 != null) ? new AircraftIdentificationMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), category(rawMessage), callSign1) : null;
    }


    /**
     * Returns the category of the aircraft that sent the message
     *
     * @param rawMessage the raw message to extract the category from
     * @return the category of the aircraft that sent the message
     */
    private static int category(RawMessage rawMessage) {
        int ca = caFormat(rawMessage);
        int tc = rawMessage.typeCode();
        return ((14 - tc) << 4 | ca);
    }

    /**
     * Returns the CA attribute from the raw message
     *
     * @param rawMessage the raw message to extract the CA attribute from
     * @return the CA attribute of the raw message
     */
    private static int caFormat(RawMessage rawMessage) {
        return Bits.extractUInt(rawMessage.payload(), 48, 3);
    }

    /**
     * Returns the call sign of the aircraft that sent the message
     *
     * @param rawMessage the raw message to extract the call sign from
     * @return the call sign of the aircraft that sent the message
     */
    private static CallSign callSign(RawMessage rawMessage) {
        long payload = rawMessage.payload();
        StringBuilder callSignSB = new StringBuilder();
        for (int i = 7; i >= 0; i--) {
            int index = Bits.extractUInt(payload, i * 6, 6);
            if (isLetter(index + 64)) {
                callSignSB.append((char) (index + 64));
            } else if (isDigitOrSpace(index)) {
                callSignSB.append((char) index);
            } else {
                return null;
            }
        }
        return new CallSign(callSignSB.toString().stripTrailing());
    }

    /**
     * Returns true if the given index is a digit or a space
     *
     * @param index the index to check
     * @return true if the given index is a digit or a space
     */
    private static boolean isDigitOrSpace(int index) {
        return (index >= 48 && index <= 57) || index == 32;
    }

    /**
     * Returns true if the given index is a letter
     *
     * @param index the index to check
     * @return true if the given index is a letter
     */
    private static boolean isLetter(int index) {
        return (index >= 65 && index <= 90);
    }
}
