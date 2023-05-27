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
 * This class represents a manager for a collection of aircraft state accumulators.
 * An AircraftStateManager is designed to manage a table of accumulators of observable aircraft states,
 * identified by their IcaoAddress. It provides functionalities to update the state of an aircraft based
 * on received messages and to purge old states based on timestamps.
 * The class supports adding and removing accumulators from the table, updating an accumulator with a message,
 * and purging the table of old aircraft states based on their last message timestamp. The purging process
 * helps to maintain the efficiency of the system and avoid unnecessary memory usage.
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

    /**
     * Constructs an aircraft state manager
     *
     * @param database The aircraft database
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
     * @return The observable aircraft states
     */
    public ObservableSet<ObservableAircraftState> states() {
        return observableAircraftStatesView;
    }

    /**
     * Updates the aircraft state manager with a given message.
     * This method first retrieves the IcaoAddress from the message and attempts to get the associated
     * AircraftStateAccumulator from the table.
     * If the address is null or not found in the database, the method does not make any changes.
     *
     * @param message The message used to update the aircraft state manager
     * @throws IOException If an I/O error occurs
     */
    public void updateWithMessage(Message message) throws IOException {
        // Get the IcaoAddress from the message
        IcaoAddress address = message.icaoAddress();

        // Attempt to get the AircraftStateAccumulator associated with the address from the table
        AircraftStateAccumulator<ObservableAircraftState> accumulator = table.get(address);

        // If the accumulator is not found in the table, create a new one and add it to the table
        if (accumulator == null) {
            accumulator = new AircraftStateAccumulator<>(new ObservableAircraftState(address, database.get(address)));
            table.put(address, accumulator);
        }

        // Update the accumulator with the message
        accumulator.update(message);

        // If the accumulator's state setter's position is not null, add the accumulator's state setter
        // to the observable aircraft states and update the last timestamp with the message's timestamp
        if (!(accumulator.stateSetter().getPosition() == null)) {
            observableAircraftStates.add(accumulator.stateSetter());
            lastTimeStampsNs = message.timeStampNs();
        }
    }

    /**
     * Purges the aircraft state manager.
     * This method iterates over the entries in the table. Each entry contains an IcaoAddress
     * and an associated AircraftStateAccumulator.
     * This method is keeping the data up-to-date.
     */
    public void purge() {
        // 1 minute in nanoseconds
        final long DT = 60_000_000_000L;

        // Create an iterator for the entry set of the table
        Iterator<Map.Entry<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>>> it = table.entrySet().iterator();
        // Iterate over the entries in the table
        while (it.hasNext()) {
            // Get the AircraftStateAccumulator associated with the current entry
            Map.Entry<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> entry = it.next();
            AircraftStateAccumulator<ObservableAircraftState> accumulator = entry.getValue();
            // If the difference between the last timestamp and the accumulator's last message
            // timestamp is greater than DT, remove the accumulator from the observable aircraft states and the table

            if (lastTimeStampsNs - accumulator.stateSetter().getLastMessageTimeStampNs() > DT) {
                observableAircraftStates.remove(accumulator.stateSetter());
                it.remove();
            }
        }
    }
}
