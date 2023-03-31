package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a demodulator for ADS-B messages
 *
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public final class AdsbDemodulator {
    private static final int MESSAGE_SIZE = 14;
    private static final int WINDOW_SIZE = 1200;
    private final PowerWindow powerWindow;

    /**
     * Constructs a new ADS-B demodulator
     *
     * @param samplesStream the stream of samples
     * @throws IOException if an I/O error occurs
     */
    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        this.powerWindow = new PowerWindow(samplesStream, WINDOW_SIZE);
    }

    /**
     * Returns the next ADS-B message
     *
     * @return the next ADS-B message
     * @throws IOException if an I/O error occurs
     */
    public RawMessage nextMessage() throws IOException {
        int sigmaP = 0, sigmaP1 = 0, sigmaP_1, sigmaV;

        while (powerWindow.isFull()) {
            sigmaP_1 = sigmaP;
            sigmaP = sigmaP1;
            sigmaP1 = computeSigmaP1();

            // We check this condition first in order only to have to compute sigmaV if necessary
            if ((sigmaP > sigmaP1) && (sigmaP_1 < sigmaP)) {

                sigmaV = computeSigmaV();
                // If this condition as well as the two other one are true, we have found a message
                if (sigmaP >= 2 * sigmaV) {
                    byte[] bytes = new byte[MESSAGE_SIZE];
                    // We only fill the first byte since we want to know if the message we found is actually interesting for us (i.e. if its DF attribute is 17)
                    fillFirstByte(bytes);
                    // If the message is interesting, we fill the other bytes
                    if (RawMessage.size(bytes[0]) == MESSAGE_SIZE) {
                        fillOtherBytes(bytes);
                        RawMessage rawMessage = RawMessage.of(powerWindow.position() * 100, bytes);

                        if (rawMessage != null) {
                            powerWindow.advanceBy(WINDOW_SIZE);
                            return rawMessage;
                        }
                    }
                }
            }
            // We still have not found a message so we advance the window
            powerWindow.advance();
        }
        // We have reached the end of the stream and there is no more message
        return null;
    }

    /**
     * Computes the sum of the powers described as Σ_+1
     *
     * @return the computed sum
     */
    private int computeSigmaP1() {
        return powerWindow.get(1) + powerWindow.get(11) + powerWindow.get(36) + powerWindow.get(46);
    }

    /**
     * Computes the sum of the powers described as Σ_v
     *
     * @return the computed sum
     */
    private int computeSigmaV() {
        return powerWindow.get(5) + powerWindow.get(15) + powerWindow.get(20) + powerWindow.get(25) + powerWindow.get(30) + powerWindow.get(40);
    }

    /**
     * Fills the first byte of the message
     *
     * @param bytes the array of bytes to fill
     */
    private void fillFirstByte(byte[] bytes) {
        for (int i = 0; i < Byte.SIZE; i++) {
            bytes[0] |= (powerWindow.get(80 + (10 * i)) < powerWindow.get(85 + (10 * i)) ? 0 : 1) << (7 - i);
        }
    }

    /**
     * Fills the other bytes of the message
     *
     * @param bytes the array of bytes to fill
     */
    private void fillOtherBytes(byte[] bytes) {
        for (int i = Byte.SIZE; i < 112; i++) {
            bytes[i / 8] |= (powerWindow.get(80 + (10 * i)) < powerWindow.get(85 + (10 * i)) ? 0 : 1) << (7 - (i % Byte.SIZE));
        }
    }
}