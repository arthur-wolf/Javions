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

    private static final int CA_FORMAT_INDEX = 48;
    private static final int CA_FORMAT_SIZE = 3;
    private static final int CALLSIGN_CHARACTER_SIZE = 6;
    private static final int ASCII_ALPHABET_START_INDEX = 64;
    private static final int ASCII_SPACE_INDEX = 32;
    private static final int ASCII_DIGIT_START_INDEX = 48;
    private static final int ASCII_DIGIT_END_INDEX = 57;
    private static final int ASCII_LETTER_START_INDEX = 65;
    private static final int ASCII_LETTER_END_INDEX = 90;

    /**
     * Constructs a new AircraftIdentificationMessage
     *
     * @param timeStampNs the time stamp of the message in nanoseconds
     * @param icaoAddress the ICAO address of the sender of the message
     * @param category    the category of the aircraft
     * @param callSign    the call sign of the aircraft
     * @throws NullPointerException     if the ICAO address or the call sign is null
     * @throws IllegalArgumentException if the time stamp is negative
     */
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
        return (callSign1 != null)
                ? new AircraftIdentificationMessage(
                rawMessage.timeStampNs(),
                rawMessage.icaoAddress(),
                category(rawMessage),
                callSign1)
                : null;
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
        return Bits.extractUInt(rawMessage.payload(), CA_FORMAT_INDEX, CA_FORMAT_SIZE);
    }

    /**
     * Returns the call sign of the aircraft that sent the message
     *
     * @param rawMessage the raw message to extract the call sign from
     * @return the call sign of the aircraft that sent the message
     */
    private static CallSign callSign(RawMessage rawMessage) {
        StringBuilder callSignSB = new StringBuilder();
        for (int i = 7; i >= 0; i--) {
            int index = Bits.extractUInt(rawMessage.payload(), i * CALLSIGN_CHARACTER_SIZE, CALLSIGN_CHARACTER_SIZE);
            if (isLetter(index + ASCII_ALPHABET_START_INDEX)) {
                callSignSB.append((char) (index + ASCII_ALPHABET_START_INDEX));
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
        return (index >= ASCII_DIGIT_START_INDEX
                && index <= ASCII_DIGIT_END_INDEX)
                || index == ASCII_SPACE_INDEX;
    }

    /**
     * Returns true if the given index is a letter
     *
     * @param index the index to check
     * @return true if the given index is a letter
     */
    private static boolean isLetter(int index) {
        return (index >= ASCII_LETTER_START_INDEX
                && index <= ASCII_LETTER_END_INDEX);
    }
}
