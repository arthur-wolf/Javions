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
    public static final int LENGTH = 14;
    public static final Crc24 crc24 = new Crc24(Crc24.GENERATOR);


    /**
     * Constructs a new raw ADS-B message
     *
     * @param timeStampNs the time stamp of the message in nanoseconds
     * @param bytes       the bytes of the message
     */
    public RawMessage {
        Preconditions.checkArgument(timeStampNs >= 0);
        Preconditions.checkArgument(bytes.size() == LENGTH);
    }

    /**
     * Returns the raw ADS-B message corresponding to the given bytes
     *
     * @param timeStampNs the time stamp of the message in nanoseconds
     * @param bytes       the bytes of the message
     * @return the raw ADS-B message corresponding to the given bytes
     */
    public static RawMessage of(long timeStampNs, byte[] bytes) {
        if (crc24.crc(bytes) == 0) {
            return new RawMessage(timeStampNs, new ByteString(bytes));
        }
        return null;
    }

    /**
     * Returns the size of the message in bits
     *
     * @param byte0 the first byte of the message
     * @return the size of the message in bits
     */
    public static int size(byte byte0) {
        byte extracted = (byte) Bits.extractUInt(byte0, 3, 5);
        return (extracted == 17) ? LENGTH : 0;
    }

    /**
     * Returns the type code of the given message
     *
     * @param payload the payload of the message
     * @return the type code of the given message
     */
    public static int typeCode(long payload) {
        return Bits.extractUInt(payload, 51, 5);
    }

    /**
     * Returns the downlink format of the message
     *
     * @return the downlink format of the message
     */
    public int downLinkFormat() {
        return Bits.extractUInt(this.bytes.byteAt(0), 3, 5);
    }

    /**
     * Returns the ICAO address of the message
     *
     * @return the ICAO address of the message
     */
    public IcaoAddress icaoAddress() {
        long intIcaoAddres = bytes.bytesInRange(1, 4);
        // If the address is not 6 characters long, we add 0s at the beginning
        StringBuilder icaoAddress = new StringBuilder(Long.toHexString(intIcaoAddres).toUpperCase());
        if (icaoAddress.length() < 6) {
            int length = icaoAddress.length();
            for (int i = 0; i < 6 - length; i++) {
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
        return bytes.bytesInRange(4, 11);
    }

    /**
     * Returns the type code of the message
     *
     * @return the type code of the message
     */
    public int typeCode() {
        return Bits.extractUInt(bytes.byteAt(4), 3, 5);
    }
}
