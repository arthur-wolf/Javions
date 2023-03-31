package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Represents a power window
 *
 * @author Oussama Ghali (341478)
 * @author Arthur Wolf (344200)
 */

public final class PowerWindow {
    private int[] firstTab;
    private int[] secondTab;
    private final int windowSize;
    private long absolutePosition;
    private final PowerComputer powerComputer;
    private int countSample;
    private int positionInBatch;
    private final static int BATCH_SIZE = 1 << 16;

    /**
     * Constructs a new power window
     *
     * @param stream     the input stream to read from
     * @param windowSize the size of the window
     * @throws IOException if an I/O error occurs
     */
    public PowerWindow(InputStream stream, int windowSize) throws IOException {
        Preconditions.checkArgument(windowSize > 0 && windowSize <= (1 << 16));

        this.windowSize = windowSize;
        this.firstTab = new int[BATCH_SIZE];
        this.secondTab = new int[BATCH_SIZE];
        this.powerComputer = new PowerComputer(stream, BATCH_SIZE);
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
     * @return true if the window is full, false otherwise
     */
    public boolean isFull() {
        return countSample >= windowSize;
    }

    /**
     * Returns the power sample at index i in the window
     *
     * @param i the index of the sample to get
     * @return returns the power sample at the given index
     */
    public int get(int i) {
        Objects.checkIndex(i, windowSize);
        return (positionInBatch + i < BATCH_SIZE) ? firstTab[positionInBatch + i] : secondTab[positionInBatch + i - BATCH_SIZE];
    }

    /**
     * Advances the window by one power sample
     *
     * @throws IOException if windowSize is greater than 2^16
     */
    public void advance() throws IOException {
        absolutePosition++;
        countSample--;
        // If we reach the end of the window, we read the next batch
        if (positionInBatch + windowSize == BATCH_SIZE) {
            countSample += powerComputer.readBatch(secondTab);
        }
        // If we reach the end of the batch, we swap the two arrays
        if (positionInBatch == BATCH_SIZE) {
            positionInBatch = 0;
            int[] temp = firstTab;
            firstTab = secondTab;
            secondTab = temp;
        }
        positionInBatch++;
    }

    /**
     * Makes the window advance offset times
     *
     * @param offset the number of samples to advance by
     * @throws IOException if offset is not strictly positive
     */
    public void advanceBy(int offset) throws IOException {
        Preconditions.checkArgument(offset >= 0);
        for (int i = 0; i < offset; i++) {
            advance();
        }
    }
}
