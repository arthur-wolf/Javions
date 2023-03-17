package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;

/**
 * Represents an ADS-B message
 *
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public interface Message {

    /**
     * Returns the time stamp of the message in nanoseconds
     *
     * @return the time stamp of the message in nanoseconds
     */
    long timeStampsNs();

    /**
     * Returns the ICAO address of the sender of the message.
     *
     * @return the ICAO address of the sender of the message.
     */
    IcaoAddress icaoAddress();
}