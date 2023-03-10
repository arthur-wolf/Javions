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
    private final InputStream inputStream;
    private final int batchSize;

    /**
     * Constructs a new power computer
     *
     * @param stream    the input stream to read from
     * @param batchSize the number of samples to read at once
     */
    public PowerComputer(InputStream stream, int batchSize) {
        Preconditions.checkArgument((batchSize > 0) && (batchSize % 8 == 0));

        this.inputStream = stream;
        this.batchSize = batchSize;
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

        SamplesDecoder samplesDecoder = new SamplesDecoder(inputStream, batchSize * 2 );
        short[] powerArray = new short[batchSize * 2];
        int count = samplesDecoder.readBatch(powerArray);

        // Compute the power of the signal using the given formula
        for (int i = 1; i < count; i += 2) {
            batch[(i - 1) / 2] = computePower(i, powerArray);
        }
        return count / 2;
    }

    /**
     * Computes the power of the signal at the given index
     *
     * @param index   the index of the sample to compute the power at
     * @param samples the array of samples to compute the power of
     * @return the power of the signal at the given index
     */
    private int computePower(int index, short[] samples) {
        int oddIndexesSum = getSample(index - 6, samples) - getSample(index - 4, samples) + getSample(index - 2, samples) - getSample(index, samples);
        int evenIndexesSum = getSample(index - 7, samples) - getSample(index - 5, samples) + getSample(index - 3, samples) - getSample(index - 1, samples);
        return (int) (Math.pow(oddIndexesSum, 2) + Math.pow(evenIndexesSum, 2));
    }

    /**
     * Returns the sample at the given index or 0 if the index is negative
     *
     * @param index  the index of the sample to return
     * @param sample the array of samples to get the sample from
     * @return the sample at the given index or 0 if the index is negative
     */
    private short getSample(int index, short[] sample) {
        return index < 0 ? 0 : sample[index];
    }
}
