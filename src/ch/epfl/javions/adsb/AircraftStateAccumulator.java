package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

import java.util.Objects;

/**
 * Represents an "aircraft status accumulator", i.e. an object that accumulates ADS-B messages from a single aircraft to determine its status over time
 *
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */

public class AircraftStateAccumulator<T extends AircraftStateSetter> {
    private T stateSetter;
    private AirbornePositionMessage messageEven;
    private AirbornePositionMessage messageOdd;

    //timestamp in seconds
    private final double DELTA = 10e9;

    public AircraftStateAccumulator(T stateSetter) {
        Objects.requireNonNull(stateSetter);
        this.stateSetter = stateSetter;

    }

    public T stateSetter() {
        return stateSetter;
    }

    /**
     * Updates the state of the aircraft with the given message.
     *
     * @param message the message to update the state with.
     */
    public void update(Message message) {
        stateSetter.setLastMessageTimeStampNs(message.timeStampNs());
        switch (message) {
            case AirbornePositionMessage apm -> {
                if (apm.parity() == 0) {
                    messageEven = apm;
                } else {
                    messageOdd = apm;
                }
                stateSetter.setAltitude(apm.altitude());
                if (checkValid()) {
                    GeoPos position = CprDecoder.decodePosition(messageEven.x(), messageEven.y(), messageOdd.x(), messageOdd.y(), messageEven.timeStampNs() - messageOdd.timeStampNs() > 0 ? 0 : 1);
                    stateSetter.setPosition(position);
                }
            }
            case AircraftIdentificationMessage aim -> {
                stateSetter.setCategory(aim.category());
                stateSetter.setCallSign(aim.callSign());
            }
            case AirborneVelocityMessage avm -> {
                stateSetter.setVelocity(avm.speed());
                stateSetter.setTrackOrHeading(avm.trackOrHeading());
            }

            default -> {
            }
        }

    }

    /**
     * For the position to be determined, the difference between the odd and even messages must be less than or equal to 10 seconds.
     *
     * @return true if the difference between the odd and even messages is less than or equal to 10 seconds.
     */
    private boolean checkValid() {
        return (messageEven != null && messageOdd != null && Math.abs(messageEven.timeStampNs() - messageOdd.timeStampNs()) <= DELTA);
    }


}
