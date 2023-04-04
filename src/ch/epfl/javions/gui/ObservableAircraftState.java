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

    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftData aircraftData) {
        this.icaoAddress = icaoAddress;

        //todo : is this right ? (ces dernières peuvent ne pas exister, la base de données mictronics n'étant pas exhaustive)
        this.aircraftData = aircraftData;

        lastMessageTimeStampsNs = new SimpleLongProperty();
        category = new SimpleIntegerProperty();
        callSign = new SimpleObjectProperty<>();
        position = new SimpleObjectProperty<>();
        altitude = new SimpleDoubleProperty();
        velocity = new SimpleDoubleProperty();
        trackOrHeading = new SimpleDoubleProperty();

        //todo : anything else ?
    }

    // ----------------- Timestamp -----------------

    public ReadOnlyLongProperty lastMessageTimeStampNsProperty() {
        return lastMessageTimeStampsNs;
    }

    public long getLastMessageTimeStampNs() {
        return lastMessageTimeStampsNs.get();
    }

    @Override
    public void setLastMessageTimeStampNs(long timeStampNs) {
        lastMessageTimeStampsNs.set(timeStampNs);
    }

    // ----------------- Category -----------------

    public ReadOnlyIntegerProperty categoryProperty() {
        return category;
    }

    public int getCategory() {
        return category.get();
    }

    @Override
    public void setCategory(int category) {
        this.category.set(category);
    }

    // ----------------- Callsign -----------------

    public ReadOnlyObjectProperty<CallSign> callSignProperty() {
        return callSign;
    }

    public CallSign getCallSign() {
        return callSign.get();
    }

    @Override
    public void setCallSign(CallSign callSign) {
        this.callSign.set(callSign);
    }

    // ----------------- Position -----------------

    public ReadOnlyObjectProperty<GeoPos> positionProperty() {
        return position;
    }

    public GeoPos getPosition() {
        return position.get();
    }

    @Override
    public void setPosition(GeoPos position) {
        this.position.set(position);
    }

    // ----------------- Trajectory -----------------

    public ObservableList<AirbornePos> trajectory() {
        return trajectoryView;
    }

    // ----------------- Altitude -----------------

    public ReadOnlyDoubleProperty altitudeProperty() {
        return altitude;
    }

    public double getAltitude() {
        return altitude.get();
    }

    @Override
    public void setAltitude(double altitude) {
        this.altitude.set(altitude);
    }

    // ----------------- Velocity -----------------

    public ReadOnlyDoubleProperty velocityProperty() {
        return velocity;
    }

    public double getVelocity() {
        return velocity.get();
    }

    @Override
    public void setVelocity(double velocity) {
        this.velocity.set(velocity);
    }

    // ----------------- Track or Heading -----------------

    public ReadOnlyDoubleProperty trackOrHeadingProperty() {
        return trackOrHeading;
    }

    public double getTrackOrHeading() {
        return trackOrHeading.get();
    }

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
        public AirbornePos {
            Objects.requireNonNull(geoPos);
        }
    }
}
