package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Represents a table of aircraft state accumulators
 *
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public final class AircraftStateManager {
    private final Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> table;
    private final ObservableSet<ObservableAircraftState> observableAircraftStates;
    private final ObservableSet<ObservableAircraftState> observableAircraftStatesView;
    private final AircraftDatabase database;
    private long lastTimeStampsNs;
    private static final long DT = 60_000_000_000L; // 1 minute in nanoseconds

    /**
     * Constructs an aircraft state manager
     *
     * @param database the aircraft database
     */
    public AircraftStateManager(AircraftDatabase database) {
        this.database = database;
        table = new HashMap<>();
        observableAircraftStates = FXCollections.observableSet();
        observableAircraftStatesView = FXCollections.unmodifiableObservableSet(observableAircraftStates);
    }

    /**
     * Returns the observable aircraft states
     *
     * @return the observable aircraft states
     */
    public ObservableSet<ObservableAircraftState> states() {
        return observableAircraftStatesView;
    }

    /**
     * Updates the aircraft state manager with a message
     *
     * @param message the message
     * @throws IOException if an I/O error occurs
     */
    public void updateWithMessage(Message message) throws IOException {
        IcaoAddress address = message.icaoAddress();
        AircraftStateAccumulator<ObservableAircraftState> accumulator = table.get(address);
        if(address == null || database.get(address) == null) return;
        if (accumulator == null) {
            accumulator = new AircraftStateAccumulator<>(new ObservableAircraftState(address, database.get(address)));
            table.put(address, accumulator);
        }
        accumulator.update(message);

        if (!(accumulator.stateSetter().getPosition() == null)) {
            observableAircraftStates.add(accumulator.stateSetter());
            lastTimeStampsNs = message.timeStampNs();
        }
    }

    /**
     * Purges the aircraft state manager
     */

    public void purge() {
        Iterator<AircraftStateAccumulator<ObservableAircraftState>> it = table.values().iterator();
        while (it.hasNext()) {
            AircraftStateAccumulator<ObservableAircraftState> accumulator = it.next();
            if (lastTimeStampsNs - accumulator.stateSetter().getLastMessageTimeStampNs() > DT) {
                observableAircraftStates.remove(accumulator.stateSetter());
                it.remove();
            }
        }
    }
}
