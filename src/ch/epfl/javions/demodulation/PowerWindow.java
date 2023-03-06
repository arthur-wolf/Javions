package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Oussama Ghali (341478)
 * @author Arthur Wolf (344200)
 */

public final class PowerWindow {

    private final InputStream inputStream;
    private int windowSize;
    private int[] evenTab;
    private int[] oddTab;

    private long count = 0;

    private

    /**
     * Constructs a new power window
     *
     * @param stream     the input stream to read from
     * @param windowSize the size of the window
     * @throws IOException if an I/O error occurs
     */

        //todo make the constructor public
    PowerWindow(InputStream stream, int windowSize) throws IOException {
        Preconditions.checkArgument(windowSize > 0 && windowSize <= Math.pow(2, 16));
        this.inputStream = stream;
        this.windowSize = windowSize;

    }

    public int size() {
        return windowSize;
    }

    public long position() throws IOException {
        return count;
    }

    public boolean isFull() {
        if (windowSize > Math.pow(2, 16)) {
            return false;
        }
        return true;
    }

    public int get(int i) throws IOException {
        Preconditions.checkArgument(i >= 0 && i < windowSize);
        //todo
        return i;
    }

    public void advance() throws IOException {
        windowSize++;
        Preconditions.checkArgument(windowSize <= Math.pow(2, 16));
        count++;
    }

    public void advanceBy(int offset) throws IOException {
        Preconditions.checkArgument(offset > 0);
        for (int i = 0; i < offset; i--) {
            advance();
        }
    }
}
