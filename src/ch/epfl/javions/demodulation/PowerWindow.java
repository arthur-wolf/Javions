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
    private int[] firstTab;
    private int[] secondTab;
    private final int windowSize;
    private long absolutePosition;

    private int readPowers;

    private final PowerComputer powerComputer;

    private int countSample;

    private int positionInBatch;

    private final static int BATCH_SIZE = (int) Math.pow(2, 16);

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
        this.firstTab = new int[windowSize * 2];
        this.secondTab = new int[windowSize * 2];
        this.powerComputer = new PowerComputer(inputStream, windowSize * 2);
        countSample = powerComputer.readBatch(firstTab);

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
        return absolutePosition;
    }

    /**
     * @return always true except when the end of the sample stream has been reached, and the window passes it
     */
    public boolean isFull() {
        return countSample >= windowSize;
    }

    /**
     * Returns the power sample at index i in the window
     *
     * @param i the index of the sample to get
     * @return returns the power sample at the given index
     * @throws IOException if i is not in the range [0, windowSize[
     */

    public int get(int i) throws IOException {
        if (!(i >= 0 && i < windowSize)) {
            throw new IndexOutOfBoundsException();
        }
        if (absolutePosition + i < windowSize) {
            return firstTab[(int) (absolutePosition + i)];
        } else {
            return secondTab[(int) (absolutePosition + i - windowSize)];
        }
    }

    /**
     * Advances the window by one power sample
     *
     * @throws IOException if windowSize is greater than 2^16
     */
    public void advance() throws IOException {
        Preconditions.checkArgument(windowSize <= Math.pow(2, 16));
        absolutePosition++;
        positionInBatch++;
        countSample--;
        if (positionInBatch + windowSize == BATCH_SIZE) {
            countSample += powerComputer.readBatch(secondTab);
        }
        if (positionInBatch == BATCH_SIZE) {
            positionInBatch = 0;
            int[] temp = firstTab;
            firstTab = secondTab;
            secondTab = temp;
        }
    }

    /**
     * Makes the window advance offset times
     *
     * @param offset the number of samples to advance by
     * @throws IOException if offset is not strictly positive
     */
    public void advanceBy(int offset) throws IOException {
        Preconditions.checkArgument(offset > 0);
        for (int i = 0; i < offset; i++) {
            advance();
        }
    }
}
