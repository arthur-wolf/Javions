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
 * Represents an observable aircraft state.
 * This class encapsulates the state of an aircraft, including its ICAO address, aircraft data, and various properties such as last message timestamp,
 * category, call sign, position, altitude, velocity, and track or heading.
 * It also provides methods to access and modify the aircraft state.
 * The class implements the AircraftStateSetter interface to provide methods for setting the state values.
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
    private final DoubleProperty trackOrHeading; // Radians
    ObservableList<AirbornePos> trajectory = FXCollections.observableArrayList();
    ObservableList<AirbornePos> trajectoryView = FXCollections.unmodifiableObservableList(trajectory);

    /**
     * Constructs an observable aircraft state with the given ICAO address and aircraft data.
     *
     * @param icaoAddress  The ICAO address of the aircraft
     * @param aircraftData The aircraft data
     * @throws NullPointerException If the ICAO address is null
     */
    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftData aircraftData) {
        Objects.requireNonNull(icaoAddress);
        final int INITIAL_VALUE = 0;
        final double UNKNOWN = Double.NaN; // Value for unknown altitude and velocity

        this.icaoAddress = icaoAddress;
        this.aircraftData = aircraftData;

        lastMessageTimeStampsNs = new SimpleLongProperty(INITIAL_VALUE);
        category = new SimpleIntegerProperty(INITIAL_VALUE);
        callSign = new SimpleObjectProperty<>(null);
        position = new SimpleObjectProperty<>(null);

        altitude = new SimpleDoubleProperty(UNKNOWN);
        velocity = new SimpleDoubleProperty(UNKNOWN);
        trackOrHeading = new SimpleDoubleProperty(INITIAL_VALUE);
    }

    /**
     * Returns the ICAO address of the aircraft.
     *
     * @return The ICAO address of the aircraft
     */
    public IcaoAddress getIcaoAddress() {
        return icaoAddress;
    }

    /**
     * Returns the aircraft data
     *
     * @return The aircraft data
     */
    public AircraftData getAircraftData() {
        return aircraftData;
    }

    // ----------------- Timestamp -----------------
    /**
     * Returns the read-only property for the last message timestamp in nanoseconds.
     *
     * @return The read-only property for the last message timestamp in nanoseconds
     */
    public ReadOnlyLongProperty lastMessageTimeStampNsProperty() {
        return lastMessageTimeStampsNs;
    }

    /**
     * Returns the last message timestamp in nanoseconds
     *
     * @return The last message timestamp in nanoseconds
     */
    public long getLastMessageTimeStampNs() {
        return lastMessageTimeStampsNs.get();
    }

    /**
     * Sets the last message timestamp in nanoseconds
     *
     * @param timeStampNs The time stamp of the last message received by the aircraft
     */
    @Override
    public void setLastMessageTimeStampNs(long timeStampNs) {
        lastMessageTimeStampsNs.set(timeStampNs);
    }

    // ----------------- Category -----------------
    /**
     * Returns the category property
     *
     * @return The category property
     */
    public ReadOnlyIntegerProperty categoryProperty() {
        return category;
    }

    /**
     * Returns the category
     *
     * @return The category
     */
    public int getCategory() {
        return category.get();
    }

    /**
     * Sets the category
     *
     * @param category The category
     */
    @Override
    public void setCategory(int category) {
        this.category.set(category);
    }

    // ----------------- CallSign -----------------
    /**
     * Returns the CallSign property
     *
     * @return The CallSign property
     */
    public ReadOnlyObjectProperty<CallSign> callSignProperty() {
        return callSign;
    }

    /**
     * Returns the CallSign
     *
     * @return The CallSign
     */
    public CallSign getCallSign() {
        return callSign.get();
    }

    /**
     * Sets the CallSign
     *
     * @param callSign The CallSign
     */
    @Override
    public void setCallSign(CallSign callSign) {
        this.callSign.set(callSign);
    }

    // ----------------- Position -----------------
    /**
     * Returns the position property
     *
     * @return The position property
     */
    public ReadOnlyObjectProperty<GeoPos> positionProperty() {
        return position;
    }

    /**
     * Returns the position
     *
     * @return The position
     */
    public GeoPos getPosition() {
        return position.get();
    }

    /**
     * Sets the position
     *
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
     *
     * @return The trajectory
     */
    public ObservableList<AirbornePos> getTrajectory() {
        return trajectoryView;
    }

    /**
     * Updates the trajectory based on the current position and altitude.
     */
    private void updateTrajectory() {
        GeoPos currentPosition = getPosition();
        double currentAltitude = getAltitude();

        // Add position and altitude to trajectory if both are known
        if (currentPosition != null
                && !Double.isNaN(currentAltitude)) {
            trajectory.add(new AirbornePos(currentPosition, currentAltitude));
            lastMessageTimeStamps = getLastMessageTimeStampNs();
            // Update altitude in last position if altitude is updated and position is known
        } else if (!trajectory.isEmpty())
            if (!Double.isNaN(currentAltitude)
                    && currentPosition != null
                    && lastMessageTimeStamps == getLastMessageTimeStampNs()) {
                trajectory.set(trajectory.size() - 1, new AirbornePos(currentPosition, currentAltitude));
            }
    }

    // ----------------- Altitude -----------------
    /**
     * Returns the read-only property for the altitude.
     *
     * @return The read-only property for the altitude
     */
    public ReadOnlyDoubleProperty altitudeProperty() {
        return altitude;
    }

    /**
     * Returns the altitude
     *
     * @return The altitude
     */
    public double getAltitude() {
        return altitude.get();
    }

    /**
     * Sets the altitude
     *
     * @param altitude The altitude
     */
    @Override
    public void setAltitude(double altitude) {
        this.altitude.set(altitude);
        updateTrajectory();
    }

    // ----------------- Velocity -----------------
    /**
     * Returns the read-only property for the velocity.
     *
     * @return The read-only property for the velocity
     */
    public ReadOnlyDoubleProperty velocityProperty() {
        return velocity;
    }

    /**
     * Returns the velocity
     *
     * @return The velocity
     */
    public double getVelocity() {
        return velocity.get();
    }

    /**
     * Sets the velocity
     *
     * @param velocity The velocity
     */
    @Override
    public void setVelocity(double velocity) {
        this.velocity.set(velocity);
    }

    // ----------------- Track or Heading -----------------
    /**
     * Returns the read-only property for the track or heading.
     *
     * @return The read-only property for the track or heading
     */
    public ReadOnlyDoubleProperty trackOrHeadingProperty() {
        return trackOrHeading;
    }

    /**
     * Returns the track or heading
     *
     * @return The track or heading
     */
    public double getTrackOrHeading() {
        return trackOrHeading.get();
    }

    /**
     * Sets the track or heading
     *
     * @param trackOrHeading The track or heading
     */
    @Override
    public void setTrackOrHeading(double trackOrHeading) {
        this.trackOrHeading.set(trackOrHeading);
    }

    /**
     * Represents a position in the air
     *
     * @param geoPos   The position in the air
     * @param altitude The altitude of the position
     */
    public record AirbornePos(GeoPos geoPos, double altitude) {

        /**
         * Constructs an AirbornePos with the given position and altitude.
         *
         * @param geoPos   The position in the air
         * @param altitude The altitude of the position
         * @throws NullPointerException If the position is null
         */
        public AirbornePos {
            Objects.requireNonNull(geoPos);
        }
    }
}
