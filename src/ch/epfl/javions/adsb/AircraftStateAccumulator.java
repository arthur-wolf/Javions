package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

import java.util.Objects;

/**
 * Represents an aircraft state accumulator, i.e. an object that accumulates ADS-B messages from a single aircraft to determine its state over time
 *
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */

public class AircraftStateAccumulator<T extends AircraftStateSetter> {
    private final T stateSetter;
    private AirbornePositionMessage evenMessage;
    private AirbornePositionMessage oddMessage;

    public AircraftStateAccumulator(T stateSetter) {
        Objects.requireNonNull(stateSetter);
        this.stateSetter = stateSetter;
    }

    /**
     * Returns the state setter of the accumulator
     *
     * @return the state setter of the accumulator
     */
    public T stateSetter() {
        return stateSetter;
    }

    /**
     * Must determine the exact type of the message passed to it as an argument,
     * to know whether it is an instance of AircraftIdentificationMessage, AirbornePositionMessage or AirborneVelocityMessage.
     * Updates the state of the aircraft with the given message.
     *
     * @param message the message to update the state with.
     */
    public void update(Message message) {
        stateSetter.setLastMessageTimeStampNs(message.timeStampNs());

        switch (message) {
            case AirbornePositionMessage apm -> {
                evenMessage = (apm.parity() == 0) ? apm : evenMessage;
                oddMessage = (apm.parity() != 0) ? apm : oddMessage;
                stateSetter.setAltitude(apm.altitude());
                if (isValid()) {
                    GeoPos position = CprDecoder.decodePosition(evenMessage.x(), evenMessage.y(), oddMessage.x(), oddMessage.y(), evenMessage.timeStampNs() - oddMessage.timeStampNs() > 0 ? 0 : 1);
                    if (position != null) {
                        stateSetter.setPosition(position);
                    }
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
     * Returns true if the odd and even messages aren't null and the difference between them is less than or equal to 10 seconds.
     *
     * @return true if the odd and even messages aren't null and the difference between them is less than or equal to 10 seconds.
     */
    private boolean isValid() {
        //timestamp in seconds
        double DELTA = 10e9;

        return (evenMessage != null && oddMessage != null
                && Math.abs(evenMessage.timeStampNs() - oddMessage.timeStampNs()) <= DELTA);
    }
}
