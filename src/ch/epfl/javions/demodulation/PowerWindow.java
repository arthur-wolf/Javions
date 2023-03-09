package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Oussama Ghali (341478)
 * @author Arthur Wolf (344200)
 */

public final class PowerWindow {

    private final InputStream inputStream;
    private byte[] evenTab;
    private byte[] oddTab;

    private final double batchSize = Math.pow(2, 16);
    private int windowSize = (int) Math.pow(2, 16);
    private long position = 0;


    /**
     * Constructs a new power window
     *
     * @param stream     the input stream to read from
     * @param windowSize the size of the window
     * @throws IOException if an I/O error occurs
     */

    public PowerWindow(InputStream stream, int windowSize) throws IOException {
        Preconditions.checkArgument(windowSize > 0 && windowSize <= Math.pow(2, 16));
        this.inputStream = stream;
        this.windowSize = windowSize;
        this.evenTab = new byte[windowSize * 2];
        this.oddTab = new byte[windowSize * 2];

    }

    /**
     * @return the size of the window
     */
    public int size() {
        return windowSize;
    }

    /**
     * @return the current position of the window
     */
    public long position() {
        return position;
    }

    /**
     * @return always true except when the end of the sample stream has been reached, and the window passes it
     */
    public boolean isFull() {
        if (windowSize > Math.pow(2, 16)) {
            return false;
        }
        return true;
    }

    /**
     * @param i the index of the sample to get
     * @return returns the power sample at the given index
     * @throws IOException if i is not in the range [0, windowSize[
     */

    public int get(int i) throws IOException {
        Preconditions.checkArgument(i >= 0 && i < windowSize);
        //todo
        return i;
    }

    /**
     * Advances the window by one sample
     *
     * @throws IOException if windowSize is greater than 2^16
     */
    public void advance() throws IOException {
        Preconditions.checkArgument(windowSize <= Math.pow(2, 16));

        for (int i = 0; i < oddTab.length; i++) {


        }
        position++;
    }

    /**
     * Works as if we were calling advance() offset times
     *
     * @param offset the number of samples to advance by
     * @throws IOException if these number (offset) is equal or less than 0
     */
    public void advanceBy(int offset) throws IOException {
        Preconditions.checkArgument(offset > 0);
        for (int i = 0; i < offset; i--) {
            advance();
        }
    }
}
