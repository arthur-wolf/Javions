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
    private int arrayHead;
    short[] powerArray;
    short[] circularTable;


    /**
     * Constructs a new power computer
     *
     * @param stream    the input stream to read from
     * @param batchSize the number of samples to read at once
     */
    public PowerComputer(InputStream stream, int batchSize) {
        Preconditions.checkArgument((batchSize > 0) && (batchSize % Byte.SIZE == 0));
        this.batchSize = batchSize;
        samplesDecoder = new SamplesDecoder(stream, batchSize * 2);
        powerArray = new short[batchSize * 2];
        circularTable = new short[Byte.SIZE];
        this.arrayHead = 0;
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
        // Compute the power of the signal using the given formula (2.4.5)
        int I, Q;
        for (int i = 0; i < count; i += 2) {
            circularTable[arrayHead % Byte.SIZE] = powerArray[i];
            circularTable[(arrayHead + 1) % Byte.SIZE] = powerArray[i + 1];

            I = circularTable[0] - circularTable[2] + circularTable[4] - circularTable[6];
            Q = circularTable[1] - circularTable[3] + circularTable[5] - circularTable[7];

            batch[i / 2] = (I * I) + (Q * Q);

            arrayHead = (arrayHead + 2) % Byte.SIZE;
        }
        return count / 2;
    }
}