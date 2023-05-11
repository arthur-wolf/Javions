package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import java.text.DecimalFormat;
import java.util.function.Consumer;

/**
 * The AircraftTableController class represents a controller for a TableView of aircraft states.
 * It manages the creation and configuration of the columns and also installs listeners and handlers for the table view.
 *
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public final class AircraftTableController {
    private final TableView<ObservableAircraftState> tableView;
    private final ObjectProperty<ObservableAircraftState> selectedAircraftState;

    private final int UNKNOWN = -1;

    private final static DecimalFormat dfWith4Digit = new DecimalFormat("#.####");
    private final static DecimalFormat dfWith0Digit = new DecimalFormat("#");


    /**
     * Constructs an AircraftTableController object with the given set of aircraft states and object property of the selected aircraft state.
     *
     * @param states                the set of aircraft states to display in the table view
     * @param selectedAircraftState the object property of the selected aircraft state
     */
    public AircraftTableController(ObservableSet<ObservableAircraftState> states, ObjectProperty<ObservableAircraftState> selectedAircraftState) {
        tableView = getTableView();
        this.selectedAircraftState = selectedAircraftState;

        createColumns();
       // installHandlers();
        addListeners(states);

    }
    public ObjectProperty<ObservableAircraftState> selectedAircraftStateProperty() {
        return selectedAircraftState;
    }


    /**
     * Returns the table view managed by this controller.
     *
     * @return the table view managed by this controller
     */
    public TableView<ObservableAircraftState> pane() {
        return tableView;
    }

    /**
     * Sets a consumer to be called when the table view is double-clicked.
     *
     * @param consumer the consumer to be called when the table view is double-clicked
     */
    public void setOnDoubleClick(Consumer<ObservableAircraftState> consumer) {
        tableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                if (selectedAircraftState.get() != null){
                    consumer.accept(selectedAircraftState.get());
                }
            }
        });
    }

    /**
     * Adds listeners to the set of aircraft states and to the selected item property of the table view.
     *
     * @param states the set of aircraft states to add listeners to
     */
    private void addListeners(ObservableSet<ObservableAircraftState> states) {
        states.addListener((SetChangeListener<ObservableAircraftState>) change -> {
            if (change.wasAdded()) {
                tableView.getItems().add(change.getElementAdded());
                tableView.sort();
            } else if (change.wasRemoved()) {
                tableView.getItems().remove(change.getElementRemoved());
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends ObservableAircraftState> observable, ObservableAircraftState oldValue, ObservableAircraftState newValue) -> selectedAircraftState.set(newValue));
    }

    /*/**
     * Installs handlers for mouse clicks on the table view.

    private void installHandlers() {
        tableView.setOnMouseClicked(event -> {
            if ((event.getClickCount() == 2) && (event.getButton() == MouseButton.PRIMARY)) {
                setOnDoubleClick(selectedAircraftState::set);
            }
        });
    }


     */

    /**
     * Creates and returns a new TableView object with the appropriate settings.
     *
     * @return a new TableView object with the appropriate settings
     */
    private TableView<ObservableAircraftState> getTableView() {
        TableView<ObservableAircraftState> tableView = new TableView<>();
        tableView.getStylesheets().add("table.css");
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        tableView.setTableMenuButtonVisible(true);
        return tableView;
    }

    /**
     * Creates and configures the columns for the table view.
     */
    private void createColumns() {
        // -------------------------------- ICAO address --------------------------------
        TableColumn<ObservableAircraftState, String> icaoColumn = new TableColumn<>("OACI");
        icaoColumn.setPrefWidth(60);
        icaoColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().address().string()));

        // -------------------------------- callSign ------------------------------------
        TableColumn<ObservableAircraftState, String> callSignColumn = new TableColumn<>("Callsign");
        callSignColumn.setPrefWidth(70);
        callSignColumn.setCellValueFactory(cellData -> cellData.getValue().callSignProperty().map(callSign -> callSign != null ? callSign.string() : ""));

        // -------------------------------- registration --------------------------------
        TableColumn<ObservableAircraftState, String> registrationColumn = new TableColumn<>("Registration");
        registrationColumn.setPrefWidth(90);
        registrationColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().aircraftData().registration() != null ? cellData.getValue().aircraftData().registration().string() : ""));

        // -------------------------------- model --------------------------------
        TableColumn<ObservableAircraftState, String> modelColumn = new TableColumn<>("Model");
        modelColumn.setPrefWidth(230);
        modelColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().aircraftData().model() != null ? cellData.getValue().aircraftData().model() : ""));

        // -------------------------------- type designator --------------------------------
        TableColumn<ObservableAircraftState, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setPrefWidth(50);
        typeColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().address().string()));

        // -------------------------------- description --------------------------------
        TableColumn<ObservableAircraftState, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setPrefWidth(70);
        descriptionColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().aircraftData().description() != null ? cellData.getValue().aircraftData().description().string() : ""));


        // -------------------------------- longitude (never null) --------------------------------
        TableColumn<ObservableAircraftState, String> longitudeColumn = new TableColumn<>("Longitude (°)");
        setNumericFormat(longitudeColumn, 85);
        longitudeColumn.setCellValueFactory(cellData -> {
            double longitude = cellData.getValue().getPosition().longitude();
            return Bindings.createObjectBinding(() -> dfWith4Digit.format(Units.convertTo(longitude, Units.Angle.DEGREE)), cellData.getValue().positionProperty());
        });

// -------------------------------- latitude (never null) --------------------------------
        TableColumn<ObservableAircraftState, String> latitudeColumn = new TableColumn<>("Latitude (°)");
        setNumericFormat(latitudeColumn, 85);
        latitudeColumn.setCellValueFactory(cellData -> {
            double latitude = cellData.getValue().getPosition().latitude();
            return Bindings.createObjectBinding(() -> dfWith4Digit.format(Units.convertTo(latitude, Units.Angle.DEGREE)), cellData.getValue().positionProperty());
        });

// -------------------------------- altitude --------------------------------
        TableColumn<ObservableAircraftState, String> altitudeColumn = new TableColumn<>("Altitude (m)");
        setNumericFormat(altitudeColumn,85);
        altitudeColumn.setCellValueFactory(cellData -> {
            double altitude = cellData.getValue().getAltitude();
            return altitude != UNKNOWN ? Bindings.createStringBinding(() -> dfWith0Digit.format(altitude), cellData.getValue().altitudeProperty()) : new SimpleStringProperty("");
        });

// -------------------------------- speed --------------------------------
        TableColumn<ObservableAircraftState, String> speedColumn = new TableColumn<>("Speed (km/h)");
        setNumericFormat(speedColumn, 85);
        speedColumn.setCellValueFactory(cellData -> {
            double velocity = cellData.getValue().getVelocity();
            return velocity != UNKNOWN ? Bindings.createStringBinding(() -> dfWith0Digit.format(Units.convertTo(velocity, Units.Speed.KILOMETER_PER_HOUR)), cellData.getValue().velocityProperty()) : new SimpleStringProperty("");
        });

        tableView.getColumns().addAll(icaoColumn, callSignColumn, registrationColumn, modelColumn, typeColumn, descriptionColumn, longitudeColumn, latitudeColumn, altitudeColumn, speedColumn);
    }

    /**
     * Sets the "numeric" style class on the given TableColumn object.
     *
     * @param column the TableColumn object to set the "numeric" style class on
     */
    private void setNumericFormat(TableColumn<ObservableAircraftState, String> column, int width) {
        // Set style class for numeric columns
        column.getStyleClass().add("numeric");
        column.setPrefWidth(width);

    }

}
