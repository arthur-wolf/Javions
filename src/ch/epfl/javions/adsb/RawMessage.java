package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.ByteString;
import ch.epfl.javions.Crc24;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

/**
 * Represents a raw ADS-B message (meaning its ME attribute has not been decoded yet).
 *
 * @param timeStampNs the time stamp of the message in nanoseconds
 * @param bytes       the bytes of the message
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public record RawMessage(long timeStampNs, ByteString bytes) {
    public static final int MESSAGE_LENGTH = 14;
    private static final Crc24 crc24 = new Crc24(Crc24.GENERATOR);
    private static final int DF_INDEX = 3;
    private static final int DF_SIZE = 5;
    private static final int TYPECODE_INDEX = 51;
    private static final int TYPECODE_SIZE = 5;
    private static final int ICAO_ADRESS_START_BYTE = 1;
    private static final int ICAO_ADRESS_BYTE_SIZE = 3;
    private static final int ICAO_ADRESS_LENGTH = 6;
    private static final int PAYLOAD_START_BYTE = 4;
    private static final int PAYLOAD_BYTE_SIZE = 7;
    private static final int TYPECODE_IN_PAYLOAD_INDEX = 3;


    /**
     * Constructs a new raw ADS-B message
     *
     * @param timeStampNs the time stamp of the message in nanoseconds
     * @param bytes       the bytes of the message
     */
    public RawMessage {
        Preconditions.checkArgument(timeStampNs >= 0);
        Preconditions.checkArgument(bytes.size() == MESSAGE_LENGTH);
    }

    /**
     * Returns the raw ADS-B message corresponding to the given bytes
     *
     * @param timeStampNs the time stamp of the message in nanoseconds
     * @param bytes       the bytes of the message
     * @return the raw ADS-B message corresponding to the given bytes
     */
    public static RawMessage of(long timeStampNs, byte[] bytes) {
        return (bytes.length == MESSAGE_LENGTH && crc24.crc(bytes) == 0) ? new RawMessage(timeStampNs, new ByteString(bytes)) : null;
    }

    /**
     * Returns the size of the message in bits
     *
     * @param byte0 the first byte of the message
     * @return the size of the message in bits
     */
    public static int size(byte byte0) {
        byte extracted = (byte) Bits.extractUInt(byte0, DF_INDEX, DF_SIZE);
        return (extracted == 17) ? MESSAGE_LENGTH : 0;
    }

    /**
     * Returns the type code of the given message
     *
     * @param payload the payload of the message
     * @return the type code of the given message
     */
    public static int typeCode(long payload) {
        return Bits.extractUInt(payload, TYPECODE_INDEX, TYPECODE_SIZE);
    }

    /**
     * Returns the downlink format of the message
     *
     * @return the downlink format of the message
     */
    public int downLinkFormat() {
        return Bits.extractUInt(this.bytes.byteAt(0), DF_INDEX, DF_SIZE);
    }

    /**
     * Returns the ICAO address of the message
     *
     * @return the ICAO address of the message
     */
    public IcaoAddress icaoAddress() {
        long intIcaoAddres = bytes.bytesInRange(ICAO_ADRESS_START_BYTE, ICAO_ADRESS_START_BYTE + ICAO_ADRESS_BYTE_SIZE);
        // If the address is not 6 characters long, we add 0s at the beginning
        StringBuilder icaoAddress = new StringBuilder(Long.toHexString(intIcaoAddres).toUpperCase());
        if (icaoAddress.length() < ICAO_ADRESS_LENGTH) {
            int length = icaoAddress.length();
            for (int i = 0; i < ICAO_ADRESS_LENGTH - length; i++) {
                icaoAddress.insert(0, "0");
            }
        }
        return new IcaoAddress(icaoAddress.toString());
    }

    /**
     * Returns the payload of the message
     *
     * @return the payload of the message
     */
    public long payload() {
        return bytes.bytesInRange(PAYLOAD_START_BYTE, PAYLOAD_START_BYTE + PAYLOAD_BYTE_SIZE);
    }

    /**
     * Returns the type code of the message
     *
     * @return the type code of the message
     */
    public int typeCode() {
        return Bits.extractUInt(bytes.byteAt(PAYLOAD_START_BYTE), TYPECODE_IN_PAYLOAD_INDEX, TYPECODE_SIZE);
    }
}
