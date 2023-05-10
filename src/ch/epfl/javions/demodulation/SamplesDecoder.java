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
    private static final int OFFSET = 2048;
    private final InputStream inputStream;
    private final int batchSize;
    private final byte[] bytes;

    /**
     * Constructs a new samples decoder
     *
     * @param stream    the input stream to read from
     * @param batchSize the number of samples to read at once
     */
    public SamplesDecoder(InputStream stream, int batchSize) {
        Preconditions.checkArgument(batchSize > 0);
        this.inputStream = Objects.requireNonNull(stream);
        this.batchSize = batchSize;
        this.bytes = new byte[Short.BYTES * batchSize];
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
        int size = inputStream.readNBytes(bytes, 0, batchSize * 2);

        for (int i = 0; i < size; i += 2) {
            byte msb = bytes[i + 1];
            byte lsb = bytes[i];

            batch[i / 2] = (short) (((Byte.toUnsignedInt(msb) << Byte.SIZE) | Byte.toUnsignedInt(lsb)) - OFFSET);
        }

        return size / 2;
    }
}
