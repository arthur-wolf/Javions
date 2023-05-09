package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;

import java.util.function.Consumer;

public final class AircraftTableController {
    private final TableView<ObservableAircraftState> tableView;
    private final ObjectProperty<ObservableAircraftState> selectedAircraftState;

    public AircraftTableController(ObservableSet<ObservableAircraftState> states, ObjectProperty<ObservableAircraftState> selectedAircraftState) {
        tableView = getTableView();
        this.selectedAircraftState = selectedAircraftState;

        createColumns();
        installHandlers();
        addListeners(states);
    }

    public TableView<ObservableAircraftState> pane() {
        return tableView;
    }

    public void setOnDoubleClick(Consumer<ObservableAircraftState> consumer) {
        if (selectedAircraftState.get() != null) {
            consumer.accept(selectedAircraftState.get());
        }
    }

    private void addListeners(ObservableSet<ObservableAircraftState> states) {
        states.addListener((SetChangeListener<ObservableAircraftState>) change -> {
            if (change.wasAdded()) {
                tableView.getItems().add(change.getElementAdded());
                tableView.sort();
            } else if (change.wasRemoved()) {
                tableView.getItems().remove(change.getElementRemoved());
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends ObservableAircraftState> observable,
                                                                          ObservableAircraftState oldValue,
                                                                          ObservableAircraftState newValue) -> selectedAircraftState.set(newValue));
    }


    private void installHandlers() {
        tableView.setOnMouseClicked(event -> {
            if ((event.getClickCount() == 2) && (event.getButton() == MouseButton.PRIMARY)) {
                setOnDoubleClick(selectedAircraftState::set);
            }
        });
    }

    private TableView<ObservableAircraftState> getTableView() {
        TableView<ObservableAircraftState> tableView = new TableView<>();
        tableView.getStyleClass().add("aircraft-table");
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        tableView.setTableMenuButtonVisible(true);
        return tableView;
    }

    private void createColumns() {
        // -------------------------------- ICAO address --------------------------------
        TableColumn<ObservableAircraftState, String> icaoColumn = new TableColumn<>("OACI");
        icaoColumn.setPrefWidth(60);
        icaoColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().address().string()));

        // -------------------------------- callSign ------------------------------------
        TableColumn<ObservableAircraftState, String> callSignColumn = new TableColumn<>("Callsign");
        callSignColumn.setPrefWidth(70);
        callSignColumn.setCellValueFactory(cellData -> cellData.getValue().callSignProperty().map(CallSign::string));

        // -------------------------------- registration --------------------------------
        TableColumn<ObservableAircraftState, String> registrationColumn = new TableColumn<>("Registration");
        registrationColumn.setPrefWidth(90);
        registrationColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().aircraftData()).map(
                aircraftData -> aircraftData.registration().string())
        );

        // -------------------------------- model --------------------------------
        TableColumn<ObservableAircraftState, String> modelColumn = new TableColumn<>("Model");
        modelColumn.setPrefWidth(230);
        modelColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().aircraftData()).map(AircraftData::model)
        );

        // -------------------------------- type designator --------------------------------
        TableColumn<ObservableAircraftState, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setPrefWidth(50);
        typeColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().address().string()));

        // -------------------------------- description --------------------------------
        TableColumn<ObservableAircraftState, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setPrefWidth(70);
        descriptionColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().aircraftData()).map(
                aircraftData -> aircraftData.description().string())
        );

        // -------------------------------- longitude (never null) --------------------------------
        TableColumn<ObservableAircraftState, String> longitudeColumn = new TableColumn<>("Longitude");
        longitudeColumn.setPrefWidth(85);

        // -------------------------------- latitude (never null) --------------------------------
        TableColumn<ObservableAircraftState, String> latitudeColumn = new TableColumn<>("Latitude");
        latitudeColumn.setPrefWidth(85);

        // -------------------------------- altitude --------------------------------
        TableColumn<ObservableAircraftState, String> altitudeColumn = new TableColumn<>("Altitude");
        altitudeColumn.setPrefWidth(85);

        // -------------------------------- speed --------------------------------
        TableColumn<ObservableAircraftState, String> speedColumn = new TableColumn<>("Speed");
        speedColumn.setPrefWidth(85);

        tableView.getColumns().addAll(icaoColumn, callSignColumn, registrationColumn, modelColumn, typeColumn, descriptionColumn, longitudeColumn, latitudeColumn, altitudeColumn, speedColumn);
    }
}
