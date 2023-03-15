package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;
import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public final class AdsbDemodulator {

    private final InputStream samplesStream;
    private final PowerWindow powerWindow;

    /**
     * Constructs a new ADS-B demodulator
     *
     * @param samplesStram the stream of samples
     * @throws IOException if an I/O error occurs
     */
    public AdsbDemodulator(InputStream samplesStram) throws IOException {
        this.samplesStream = samplesStram;
        this.powerWindow = new PowerWindow(samplesStream, 1200);


    }

    /**
     * Returns the next ADS-B message
     *
     * @return the next ADS-B message
     * @throws IOException if an I/O error occurs
     */
    public RawMessage nextMessage() throws IOException {
        return null;
    }

}