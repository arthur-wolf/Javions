package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public final class AdsbDemodulator {
    private final PowerWindow powerWindow;

    /**
     * Constructs a new ADS-B demodulator
     *
     * @param samplesStream the stream of samples
     * @throws IOException if an I/O error occurs
     */
    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        this.powerWindow = new PowerWindow(samplesStream, 1200);
    }

    /**
     * Returns the next ADS-B message
     *
     * @return the next ADS-B message
     * @throws IOException if an I/O error occurs
     */
    public RawMessage nextMessage() throws IOException {
        int sigmaP = 0;
        int sigmaP1 = 0;
        int sigmaP_1;
        int sigmaV;

        while (powerWindow.isFull()) {
            sigmaP_1 = sigmaP;
            sigmaP = sigmaP1;
            sigmaP1 = powerWindow.get(1) + powerWindow.get(11) + powerWindow.get(36) + powerWindow.get(46);

            // If this condition is verified, we consider that the preamble of the message is indeed at the beginning of the window
            if ((sigmaP > sigmaP1) && (sigmaP_1 < sigmaP)) {
                sigmaV = powerWindow.get(5) + powerWindow.get(15) + powerWindow.get(20) + powerWindow.get(25) + powerWindow.get(30) + powerWindow.get(40);
                if (sigmaP >= 2 * sigmaV) {
                    byte[] bytes = new byte[14];
                    for (int i = 0; i < 8; i++) {
                        bytes[0] |= (powerWindow.get(80 + 10 * i) < powerWindow.get(85 + 10 * i) ? 0 : 1) << (7 - i);
                    }
                    if (RawMessage.size(bytes[0]) == 14) {
                        for (int i = 8; i < 112; i++) {
                            bytes[i / 8] |= (powerWindow.get(80 + 10 * i) < powerWindow.get(85 + 10 * i) ? 0 : 1) << (7 - i % 8);
                        }
                        RawMessage rawMessage = RawMessage.of(powerWindow.position() * 100, bytes);

                        if (rawMessage != null) {
                            powerWindow.advanceBy(1200);
                            return rawMessage;
                        }
                    }
                }
            }
            powerWindow.advance();
        }
        return null;
    }

}