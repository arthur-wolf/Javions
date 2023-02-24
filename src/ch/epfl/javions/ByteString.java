package ch.epfl.javions;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

/**
 * Represents a byte sequence. A ByteString is immutable and its bytes are interpreted as unsigned.
 *
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public final class ByteString {
    private final byte[] bytes;

    /**
     * Constructs a ByteString based on a byte array
     *
     * @param bytes the given byte array
     */
    public ByteString(byte[] bytes) {
        this.bytes = bytes.clone();
    }

    /**
     * Returns the octet string of which the given string is the hexadecimal representation
     *
     * @param hexString the string in the hexadecimal representation
     * @return the octet string
     * @throws IllegalArgumentException if the string's length is not even
     * @throws NumberFormatException    if one of the character is not among ABCDEF
     */
    public static ByteString ofHexadecimalString(String hexString) {
        HexFormat hf = HexFormat.of().withUpperCase();
        byte[] bytesTemp = hf.parseHex(hexString);
        return new ByteString(bytesTemp);
    }

    /**
     * Returns the size of the byte array
     *
     * @return the size of the byte array
     */
    public int size() {
        return bytes.length;
    }

    /**
     * Returns the byte at the given index
     *
     * @param index the given index
     * @return the byte at the given index
     */
    public int byteAt(int index) {
        if (!(index >= 0 && index < bytes.length))
            throw new IndexOutOfBoundsException();

        return Byte.toUnsignedInt(bytes[index]);
    }

    /**
     * Returns a long value representing the bytes in the range defined by fromIndex and toIndex
     *
     * @param fromIndex the beginning of the range
     * @param toIndex   the end of the range
     * @return a long value representing the bytes in the range
     */
    public long bytesInRange(int fromIndex, int toIndex) {
        Preconditions.checkArgument(toIndex - fromIndex < Long.SIZE);
        Objects.checkFromToIndex(fromIndex, toIndex, bytes.length);

        long temp = 0;
        for (int i = fromIndex; i < toIndex; i++) {
            temp = temp << Byte.SIZE | byteAt(i);
        }
        return temp;
    }

    /**
     * Overriden equals method
     *
     * @param obj the object to compare the ByteString to
     * @return true if obj and the ByteString have the same bytes
     */
    @Override
    public boolean equals(Object obj) {
        if ((obj instanceof ByteString other))
            return Arrays.equals(other.bytes, bytes);
        return false;
    }

    /**
     * returns the hash value of the ByteString
     *
     * @return the hash value of the ByteString
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }

    /**
     * Returns a String that shows the byte in hexadecimal
     *
     * @return the String composed of the bytes in hexadecimal
     */
    @Override
    public String toString() {
        return HexFormat.of().withUpperCase().formatHex(bytes);
    }
}
