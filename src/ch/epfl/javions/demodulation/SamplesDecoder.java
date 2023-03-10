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
 * @author Oussama Ghali (341478)
 */
public final class SamplesDecoder {
    private final InputStream inputStream;
    private final int batchSize;
    private final byte[] readTable;

    /**
     * Constructs a new samples decoder
     *
     * @param stream    the input stream to read from
     * @param batchSize the number of samples to read at once
     */
    public SamplesDecoder(InputStream stream, int batchSize) {
        Preconditions.checkArgument(batchSize > 0);
        Objects.requireNonNull(stream);
        this.inputStream = stream;
        this.batchSize = batchSize;
        this.readTable = new byte[batchSize * 2];
    }

    /**
     * Reads a batch of samples from the input stream and converts them into
     * signed samples using the little-endian format
     *
     * @param batch the array to store the samples in
     * @return the number of samples read
     * @throws IOException if an I/O error occurs
     */
    public int readBatch(short[] batch) throws IOException {
        Preconditions.checkArgument(batch.length == batchSize);
        byte[] bytes = new byte[batchSize * 2];
        int size = inputStream.readNBytes(bytes, 0, 2 * batchSize);

        // swap blocks of bytes two by two
        for (int i = 0; i < size; i += 2) {
            readTable[i] = bytes[i + 1];
            readTable[i + 1] = bytes[i];
        }
        int count = 0;
        // Convert the bytes into signed 12 bits samples
        for (int i = 0; i < size / 2; i++) {
            batch[i] = (short) (((readTable[i * 2] << 8) | (readTable[i * 2 + 1] & 0xFF)) - 2048);
            count++;
        }
        return count;
    }
}
