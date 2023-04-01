package ch.epfl.javions.adsb;

/**
 * Represents a parser for ADS-B messages.
 *
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public final class MessageParser {
    private MessageParser() {}

    /**
     * Returns the message corresponding to the given raw message
     *
     * @param rawMessage the raw message to parse
     * @return the message corresponding to the given raw message
     */
    public static Message parse(RawMessage rawMessage) {
        int tc = rawMessage.typeCode();
        return switch (tc) {
            case 1, 2, 3, 4 -> AircraftIdentificationMessage.of(rawMessage);
            case 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 20, 21, 22 -> AirbornePositionMessage.of(rawMessage);
            case 19 -> AirborneVelocityMessage.of(rawMessage);
            default -> null;
        };
    }
}
