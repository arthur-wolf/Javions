package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Objects;

/**
 * Represents an observable aircraft state
 *
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public final class ObservableAircraftState implements AircraftStateSetter {
    private long lastMessageTimeStamps = -1L;
    private final IcaoAddress icaoAddress;
    private final AircraftData aircraftData;
    private final LongProperty lastMessageTimeStampsNs;    // ns
    private final IntegerProperty category;
    private final ObjectProperty<CallSign> callSign;  // 8 chars
    private final ObjectProperty<GeoPos> position;    // (lon, lat) rad
    private final DoubleProperty altitude;    // m
    private final DoubleProperty velocity;    // m/s
    private final DoubleProperty trackOrHeading; // radions
    ObservableList<AirbornePos> trajectory = FXCollections.observableArrayList();    // modifiable list
    ObservableList<AirbornePos> trajectoryView = FXCollections.unmodifiableObservableList(trajectory);    // unmodifiable list (view on trajectory)

    /**
     * Constructs an observable aircraft state
     *
     * @param icaoAddress the aircraft ICAO address
     * @param aircraftData the aircraft data
     */
    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftData aircraftData) {
        Objects.requireNonNull(icaoAddress);

        this.icaoAddress = icaoAddress;
        this.aircraftData = aircraftData;

        lastMessageTimeStampsNs = new SimpleLongProperty(0);
        category = new SimpleIntegerProperty(0);
        callSign = new SimpleObjectProperty<>(null);
        position = new SimpleObjectProperty<>(null);
        altitude = new SimpleDoubleProperty(0);
        velocity = new SimpleDoubleProperty(0);
        trackOrHeading = new SimpleDoubleProperty(0);
    }

    /**
     * Returns the aircraft ICAO address
     *
     * @return the aircraft ICAO address
     */
    public IcaoAddress address() {
        return icaoAddress;
    }

    /**
     * Returns the aircraft data
     *
     * @return the aircraft data
     */
    public AircraftData aircraftData() {
        return aircraftData;
    }

    // ----------------- Timestamp -----------------

    /**
     * Returns the last message timestamp property
     * @return the last message timestamp property
     */
    public ReadOnlyLongProperty lastMessageTimeStampNsProperty() {
        return lastMessageTimeStampsNs;
    }

    /**
     * Returns the last message timestamp in nanoseconds
     * @return the last message timestamp in nanoseconds
     */
    public long getLastMessageTimeStampNs() {
        return lastMessageTimeStampsNs.get();
    }

    /**
     * Sets the last message timestamp in nanoseconds
     * @param timeStampNs the time stamp of the last message received by the aircraft
     */
    @Override
    public void setLastMessageTimeStampNs(long timeStampNs) {
        lastMessageTimeStampsNs.set(timeStampNs);
    }

    // ----------------- Category -----------------

    /**
     * Returns the category property
     * @return the category property
     */
    public ReadOnlyIntegerProperty categoryProperty() {
        return category;
    }

    /**
     * Returns the category
     * @return the category
     */
    public int getCategory() {
        return category.get();
    }

    /**
     * Sets the category
     * @param category the category
     */
    @Override
    public void setCategory(int category) {
        this.category.set(category);
    }

    // ----------------- Callsign -----------------

    /**
     * Returns the callsign property
     * @return the callsign property
     */
    public ReadOnlyObjectProperty<CallSign> callSignProperty() {
        return callSign;
    }

    /**
     * Returns the callsign
     * @return the callsign
     */
    public CallSign getCallSign() {
        return callSign.get();
    }

    /**
     * Sets the callsign
     * @param callSign the callsign
     */
    @Override
    public void setCallSign(CallSign callSign) {
        this.callSign.set(callSign);
    }

    // ----------------- Position -----------------

    /**
     * Returns the position property
     * @return the position property
     */
    public ReadOnlyObjectProperty<GeoPos> positionProperty() {
        return position;
    }

    /**
     * Returns the position
     * @return the position
     */
    public GeoPos getPosition() {
        return position.get();
    }

    /**
     * Sets the position
     * @param position the position
     */
    @Override
    public void setPosition(GeoPos position) {
        this.position.set(position);
        updateTrajectory();
    }

    // ----------------- Trajectory -----------------

    /**
     * Returns the trajectory
     * @return the trajectory
     */
    public ObservableList<AirbornePos> trajectory() {
        return trajectoryView;
    }

    /**
     * Updates the trajectory
     */
    private void updateTrajectory() {

        if(getPosition() != null) {
            if (trajectory.isEmpty() || !getPosition().equals(trajectory.get(trajectory.size() - 1).geoPos)) {
                trajectory.add(new AirbornePos(getPosition(), getAltitude()));
                lastMessageTimeStamps = getLastMessageTimeStampNs();
            } else if (lastMessageTimeStamps == getLastMessageTimeStampNs()) {
                trajectory.set(trajectory.size() - 1, new AirbornePos(getPosition(), getAltitude()));
            }
        }
    }

    // ----------------- Altitude -----------------

    /**
     * Returns the altitude property
     * @return the altitude property
     */
    public ReadOnlyDoubleProperty altitudeProperty() {
        return altitude;
    }

    /**
     * Returns the altitude
     * @return the altitude
     */
    public double getAltitude() {
        return altitude.get();
    }

    /**
     * Sets the altitude
     * @param altitude the altitude
     */
    @Override
    public void setAltitude(double altitude) {
        this.altitude.set(altitude);
        updateTrajectory();
    }

    // ----------------- Velocity -----------------

    /**
     * Returns the velocity property
     * @return the velocity property
     */
    public ReadOnlyDoubleProperty velocityProperty() {
        return velocity;
    }

    /**
     * Returns the velocity
     * @return the velocity
     */
    public double getVelocity() {
        return velocity.get();
    }

    /**
     * Sets the velocity
     * @param velocity the velocity
     */
    @Override
    public void setVelocity(double velocity) {
        this.velocity.set(velocity);
    }

    // ----------------- Track or Heading -----------------

    /**
     * Returns the track or heading property
     * @return the track or heading property
     */
    public ReadOnlyDoubleProperty trackOrHeadingProperty() {
        return trackOrHeading;
    }

    /**
     * Returns the track or heading
     * @return the track or heading
     */
    public double getTrackOrHeading() {
        return trackOrHeading.get();
    }

    /**
     * Sets the track or heading
     * @param trackOrHeading the track or heading
     */
    @Override
    public void setTrackOrHeading(double trackOrHeading) {
        this.trackOrHeading.set(trackOrHeading);
    }

    /**
     * Represents a position in the air
     *
     * @param geoPos   the position in the air
     * @param altitude the altitude of the position
     */
    public record AirbornePos(GeoPos geoPos, double altitude) {

        /**
         * Constructs an AirbornePos if the given geoPos is not null
         *
         * @param geoPos   the position in the air
         * @param altitude the altitude of the position
         */
    }
}
