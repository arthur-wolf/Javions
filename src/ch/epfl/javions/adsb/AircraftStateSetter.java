package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

/**
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public interface AircraftStateSetter {

    /**
     * Sets the time stamp of the last message received by the aircraft
     *
     * @param timeStampNs
     */
    void setLastMessageTimeStampNs(long timeStampNs);


    /**
     * Sets the category of the aircraft to the given value
     *
     * @param category
     */
    abstract void setCategory(int category);

    /**
     * Sets the aircraft designator to the given value
     *
     * @param callSign
     */
    void setCallSign(CallSign callSign);

    /**
     * Sets the position of the aircraft to the given value
     *
     * @param position
     */
    void setPosition(GeoPos position);

    /**
     * Sets the altitude of the aircraft to the given value
     *
     * @param altitude
     */
    void setAltitude(double altitude);

    /**
     * Sets the velocity of the aircraft to the given value
     *
     * @param velocity
     */
    void setVelocity(double velocity);

    /**
     * Sets the track or heading of the aircraft to the given value
     *
     * @param trackOrHeading
     */
    void setTrackOrHeading(double trackOrHeading);

}