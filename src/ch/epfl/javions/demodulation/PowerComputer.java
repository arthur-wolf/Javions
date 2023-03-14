package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a power computer.
 * It computes the signal's power samples from the signed samples produced by a SamplesDecoder.
 *
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public final class PowerComputer {
    private final int batchSize;
    private final SamplesDecoder samplesDecoder;
    short[] powerArray;
    byte[] circularTable;


    /**
     * Constructs a new power computer
     *
     * @param stream    the input stream to read from
     * @param batchSize the number of samples to read at once
     */
    public PowerComputer(InputStream stream, int batchSize) {
        Preconditions.checkArgument((batchSize > 0) && (batchSize % 8 == 0));
        this.batchSize = batchSize;
        samplesDecoder = new SamplesDecoder(stream, batchSize * 2);
        powerArray = new short[batchSize * 2];
        circularTable = new byte[8];

    }

    /**
     * Reads a batch of samples from the input stream and computes the power of the signal
     *
     * @param batch the array to store the power samples in
     * @return the number of samples read
     * @throws IOException if an I/O error occurs
     */
    public int readBatch(int[] batch) throws IOException {
        Preconditions.checkArgument(batch.length == batchSize);
        int count = samplesDecoder.readBatch(powerArray);
        // Compute the power of the signal using the given formula
        int I, Q;
        for (int i = 0; i < count; i += 2) {
            circularTable[i % 8] = (byte) powerArray[i];
            circularTable[(i + 1) % 8] = (byte) powerArray[i + 1];
            I = circularTable[i % 8] - circularTable[(i + 2) % 8] + circularTable[(i + 4) % 8] - circularTable[(i + 6) % 8];
            Q = circularTable[(i + 1) % 8] - circularTable[(i + 3) % 8] + circularTable[(i + 5) % 8] - circularTable[(i + 7) % 8];
            batch[i / 2] = (I * I) + (Q * Q);
        }
        return count / 2;
    }

}