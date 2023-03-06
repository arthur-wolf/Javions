package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Represents a samples decoder that transforms bytes received from the AirSpy digital radio
 * into signed 12 bits samples
 *
 * @author Arthur Wolf (344200)
 */
public final class SamplesDecoder {
    private final InputStream inputStream;
    private final byte[] readTable;

    /**
     * Constructs a new samples decoder
     *
     * @param stream   the input stream to read from
     * @param batchSize the number of samples to read at once
     */
    public SamplesDecoder(InputStream stream, int batchSize) {
        Preconditions.checkArgument(batchSize > 0);
        Objects.requireNonNull(stream);
        this.inputStream = stream;
        this.readTable = new byte[batchSize * 2];
    }

    public int readBatch(short[] batch) throws IOException {
        return 0;
    }
}
