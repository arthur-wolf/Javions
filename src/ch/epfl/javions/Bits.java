package ch.epfl.javions;

import java.util.Objects;

/**
 * Methods to extract a range of bits from a long value
 * @author Arthur Wolf (344200)
 */
public class Bits {
    private Bits(){}

    /**
     * Extracts the range starting from start of length size as an unsigned value
     * @param value the value to extract the range from
     * @param start the index of the starting bit
     * @param size the length of the range to extract
     * @return the range to extract
     * @throws IllegalArgumentException if size is not within 0 and 32 (both excluded)
     * @throws IndexOutOfBoundsException if the length of the range is not within 0 (included) and 64 (excluded)
     */
    public static int extractUInt(long value, int start, int size) {
        Preconditions.checkArgument(0 < size && size < Integer.SIZE);
        Objects.checkFromIndexSize(start, size, Long.SIZE);

        //left arithmetic shift
        long leftArithmeticShifted = value << (Long.SIZE - (start + size));
        //right logical shift
        return (int) (leftArithmeticShifted >>> (Long.SIZE - size));
    }

    /**
     * Tests if the bit from value at position index is a 1
     * @param value a value of type long (64 bits vector)
     * @param index the position of the bit to check
     * @return true if the bit from value at position index is a 1
     * @throws IndexOutOfBoundsException if the given index is not within 0 (included) and 64 (excluded)
     */
    public static boolean testBit(long value, int index) {
        if (! (index >= 0 && index < Long.SIZE))
            throw new IndexOutOfBoundsException();
        // Extract the bit at position index and check if it is a 1 or a 0
        return ((value >> index) & 1) == 1;
    }
}
