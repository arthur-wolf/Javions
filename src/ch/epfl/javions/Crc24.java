package ch.epfl.javions;

/**
 * Represents a CRC24 checksum
 *
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */

public final class Crc24 {

    private static final int GENERATOR_LENGTH = 24;
    public static final int GENERATOR = 0xFFF409;
    private final int[] table;

    /**
     * Builds the table used to compute the CRC24 checksum
     *
     * @param generator the generator to use
     */
    public Crc24(int generator) {
        this.table = buildTable(generator);
    }

    /**
     * Computes the CRC24 checksum of the given bytes
     *
     * @param bytes the bytes to compute the checksum of
     * @return the checksum of the given bytes
     */
    public int crc(byte[] bytes) {
        int crc = 0;
        for (byte b : bytes) {
            crc = ((crc << Byte.SIZE) | Byte.toUnsignedInt(b)) ^ table[Bits.extractUInt(crc, GENERATOR_LENGTH - 8, 8)];
        }

        for (int i = 0; i < 24; i++) {
            crc = (crc << 1) ^ (table[Bits.extractUInt(crc, GENERATOR_LENGTH - 1, 1)]);
        }

        // Return the 24 least significant bits
        return Bits.extractUInt(crc, 0, GENERATOR_LENGTH);
    }

    /**
     * Computes the CRC24 checksum of the given bytes using a bitwise algorithm
     *
     * @param generator the generator to use
     * @param bytes     the bytes to compute the checksum of
     * @return the CRC24 checksum of the given bytes
     */
    private static int crc_bitwise(int generator, byte[] bytes) {
        int[] table = new int[]{0, generator};
        int crc = 0;

        for (byte b : bytes) {
            for (int i = 7; i >= 0; i--) {
                crc = ((crc << 1) | Bits.extractUInt(b, i, 1)) ^ (table[Bits.extractUInt(crc, GENERATOR_LENGTH - 1, 1)]);
            }
        }

        for (int i = 0; i < 24; i++) {
            crc = (crc << 1) ^ (table[Bits.extractUInt(crc, GENERATOR_LENGTH - 1, 1)]);
        }

        // Return the 24 least significant bits
        return Bits.extractUInt(crc, 0, GENERATOR_LENGTH);
    }

    /**
     * Builds the table for the given generator
     *
     * @param generator the given generator
     * @return the table built according to the given generator
     */
    private static int[] buildTable(int generator) {
        int[] table = new int[256];
        for (int i = 0; i < 256; i++) {
            table[i] = crc_bitwise(generator, new byte[]{(byte) i});
        }
        return table;
    }
}