package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.function.Consumer;

/**
 * The AircraftTableController class represents a controller for a TableView of aircraft states.
 * It manages the creation and configuration of the columns and also installs listeners and handlers for the table view.
 * The AircraftTableController class is responsible for creating and configuring the columns,
 * adding listeners to the aircraft states set and selected item property of the table view,
 * setting a consumer to be called on double-click events on the table view, and also providing
 * the table view that this controller manages.
 *
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public final class AircraftTableController {
    private final int NUMERIC_COLUMN_WIDTH = 85;
    private final int CALLSIGN_COLUMN_WIDTH = 70;
    private final int DESIGNATOR_COLUMN_WIDTH = 50;
    private final int REGISTRATION_COLUMN_WIDTH = 90;
    private final int ICAO_ADDRESS_COLUMN_WIDTH = 60;
    private final int MODEL_COLUMN_WIDTH = 230;
    private final int DESCRIPTION_COLUMN_WIDTH = 70;
    private final TableView<ObservableAircraftState> tableView;
    private final ObjectProperty<ObservableAircraftState> selectedAircraftState;
    private final static DecimalFormat dfWith4Digit = new DecimalFormat("#.####");
    private final static DecimalFormat dfWith0Digit = new DecimalFormat("#");


    /**
     * Constructs an AircraftTableController object with the given set of aircraft states
     * and object property of the selected aircraft state.
     * The constructor creates the columns and adds listeners to the provided set of aircraft states.
     *
     * @param states                the set of aircraft states to display in the table view
     * @param selectedAircraftState the object property of the selected aircraft state
     */
    public AircraftTableController(ObservableSet<ObservableAircraftState> states, ObjectProperty<ObservableAircraftState> selectedAircraftState) {
        tableView = getTableView();
        this.selectedAircraftState = selectedAircraftState;

        createColumns();
        addListeners(states);
    }

    /**
     * Returns the table view managed by this controller.
     * The table view displays the aircraft states and allows selection of individual aircraft states.
     *
     * @return the table view managed by this controller
     */
    public TableView<ObservableAircraftState> pane() {
        return tableView;
    }

    /**
     * Sets a consumer to be called when the table view is double-clicked.
     * The consumer is called with the currently selected aircraft state as the argument.
     *
     * @param consumer the consumer to be called when the table view is double-clicked
     */
    public void setOnDoubleClick(Consumer<ObservableAircraftState> consumer) {
        tableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                if (selectedAircraftState.get() != null) {
                    consumer.accept(selectedAircraftState.get());
                }
            }
        });
    }

    /**
     * Adds listeners to the set of aircraft states and to the selected item property of the table view.
     * The listeners update the table view items and the selected aircraft state whenever the aircraft states set or
     * the selected item property of the table view change, respectively.
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

        selectedAircraftState.addListener((observable, oldValue, newValue) -> {
            tableView.getSelectionModel().select(selectedAircraftState.get());
            if(newValue != null) {
                tableView.scrollTo(newValue);

            }
        });
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedAircraftState.set(newValue);
            }
        });
    }

    /**
     * Creates and returns a new TableView object with the appropriate settings.
     * The TableView has its style sheets set, constrained resize policy applied, and table menu button visibility enabled.
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
     * This method creates and configures all the columns for the aircraft states, including the ICAO address,
     * call sign, registration, model, type, description, longitude, latitude, altitude, and speed.
     * The cells of the columns are bound to the corresponding properties of the aircraft states.
     */
    //TODO : ADD CONSTANT VALUE INSTED FOR WIDTH
    private void createColumns() {
        // ---------------------------------ICAO Address-----------------------------------
        TableColumn<ObservableAircraftState, String> icaoColumn = createColumn("ICAO", ICAO_ADDRESS_COLUMN_WIDTH);
        icaoColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().address().string()));

        // ---------------------------------CallSign---------------------------------------
        TableColumn<ObservableAircraftState, String> callSignColumn = createColumn("Indicatif", CALLSIGN_COLUMN_WIDTH);
        callSignColumn.setCellValueFactory(cellData -> cellData.getValue().callSignProperty().map(callSign -> callSign != null ? callSign.string() : ""));

        // ---------------------------------Registration-----------------------------------
        TableColumn<ObservableAircraftState, String> registrationColumn = createColumn("Immatriculation", REGISTRATION_COLUMN_WIDTH);
        registrationColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().aircraftData().registration() != null ? cellData.getValue().aircraftData().registration().string() : ""));

        // ---------------------------------Model------------------------------------------
        TableColumn<ObservableAircraftState, String> modelColumn = createColumn("Modèle", MODEL_COLUMN_WIDTH);
        modelColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().aircraftData().model() != null ? cellData.getValue().aircraftData().model() : ""));

        // ---------------------------------Type Designator---------------------------------
        TableColumn<ObservableAircraftState, String> typeColumn = createColumn("Type", DESIGNATOR_COLUMN_WIDTH);
        typeColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().address().string()));

        // ---------------------------------Description-------------------------------------
        TableColumn<ObservableAircraftState, String> descriptionColumn = createColumn("Description", DESCRIPTION_COLUMN_WIDTH);
        descriptionColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().aircraftData().description() != null ? cellData.getValue().aircraftData().description().string() : ""));

        // ---------------------------------Longitude (never null)--------------------------
        TableColumn<ObservableAircraftState, String> longitudeColumn = createColumn("Longitude (°)", NUMERIC_COLUMN_WIDTH);
        longitudeColumn.setCellValueFactory(cellData -> {
            double longitude = cellData.getValue().getPosition().longitude();
            return Bindings.createObjectBinding(() -> dfWith4Digit.format(Units.convertTo(longitude, Units.Angle.DEGREE)), cellData.getValue().positionProperty());
        });

        // ---------------------------------Latitude (never null)----------------------------
        TableColumn<ObservableAircraftState, String> latitudeColumn = createColumn("Latitude (°)", NUMERIC_COLUMN_WIDTH);
        latitudeColumn.setCellValueFactory(cellData -> {
            double latitude = cellData.getValue().getPosition().latitude();
            return Bindings.createObjectBinding(() -> dfWith4Digit.format(Units.convertTo(latitude, Units.Angle.DEGREE)), cellData.getValue().positionProperty());
        });

        // ---------------------------------Altitude-----------------------------------------
        TableColumn<ObservableAircraftState, String> altitudeColumn = createColumn("Altitude (m)", NUMERIC_COLUMN_WIDTH);
        altitudeColumn.setCellValueFactory(cellData -> {
            double altitude = cellData.getValue().getAltitude();
            return Bindings.createObjectBinding(() -> !Double.isNaN(altitude) ? dfWith0Digit.format(altitude) :  "", cellData.getValue().altitudeProperty());
        });

        // ----------------------------------Speed-------------------------------------------
        TableColumn<ObservableAircraftState, String> speedColumn = createColumn("Vitesse (km/h)", NUMERIC_COLUMN_WIDTH);
        speedColumn.setCellValueFactory(cellData -> {
            double velocity = cellData.getValue().getVelocity();
            return Bindings.createStringBinding(() -> !Double.isNaN(velocity) ? dfWith0Digit.format(Units.convertTo(velocity, Units.Speed.KILOMETER_PER_HOUR)) : "", cellData.getValue().velocityProperty());
        });
        tableView.getColumns().addAll(icaoColumn, callSignColumn, registrationColumn, modelColumn, typeColumn, descriptionColumn, longitudeColumn, latitudeColumn, altitudeColumn, speedColumn);
    }

    /**
     * Creates a new TableColumn object with the given name and width.
     *
     * @param name  the name of the column
     * @param width the width of the column (-1 means the column should be applied the "numeric" style class)
     * @return a new TableColumn object with the given name and width
     */
    private TableColumn<ObservableAircraftState, String> createColumn(String name, int width) {
        TableColumn<ObservableAircraftState, String> column = new TableColumn<>(name);
        if (width == NUMERIC_COLUMN_WIDTH) {
            setNumericFormat(column);
            column.setComparator(getComparator());
        } else
            column.setPrefWidth(width);

        return column;
    }

    /**
     * Returns a comparator for string objects. If both strings are non-empty and can be parsed as numbers,
     * they are compared as numbers. Otherwise, they are compared as strings.
     * The method is used for comparing the cell values of the numeric columns in the table view.
     *
     * @return a comparator for string objects
     */
    private static Comparator<String> getComparator() {
        return (o1, o2) -> {
            if (o1.isEmpty() || o2.isEmpty())
                return o1.compareTo(o2);
            else {
                // If both strings are non-empty, try to parse them as numbers
                try {
                    Double i1 = Double.parseDouble(o1);
                    Double i2 = Double.parseDouble(o2); // Fixed: parse o2 instead of o1
                    return i1.compareTo(i2);
                } catch (NumberFormatException e) {
                    // If either string cannot be parsed as an integer, compare them as strings
                    return o1.compareTo(o2);
                }
            }
        };
    }

    /**
     * Sets the "numeric" style class on the given TableColumn object and assigns it a default width.
     * This method is used to apply the "numeric" style class and a default width to numeric columns in the table view.
     *
     * @param column the TableColumn object to set the "numeric" style class on
     */
    private void setNumericFormat(TableColumn<ObservableAircraftState, String> column) {
        column.getStyleClass().add("numeric");
        column.setPrefWidth(NUMERIC_COLUMN_WIDTH);
    }
}
