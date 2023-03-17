package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

/**
 * Represents an aircraft state setter
 *
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public interface AircraftStateSetter {

    /**
     * Sets the time stamp of the last message received by the aircraft
     *
     * @param timeStampNs the time stamp of the last message received by the aircraft
     */
    void setLastMessageTimeStampNs(long timeStampNs);

    /**
     * Sets the category of the aircraft to the given value
     *
     * @param category the category to be set
     */
    void setCategory(int category);

    /**
     * Sets the aircraft call sign to the given value
     *
     * @param callSign the call sign to be set
     */
    void setCallSign(CallSign callSign);

    /**
     * Sets the position of the aircraft to the given value
     *
     * @param position the position to be set
     */
    void setPosition(GeoPos position);

    /**
     * Sets the altitude of the aircraft to the given value
     *
     * @param altitude the altitude to be set
     */
    void setAltitude(double altitude);

    /**
     * Sets the velocity of the aircraft to the given value
     *
     * @param velocity the velocity to be set
     */
    void setVelocity(double velocity);

    /**
     * Sets the track or heading of the aircraft to the given value
     *
     * @param trackOrHeading the track or heading to be set
     */
    void setTrackOrHeading(double trackOrHeading);
}