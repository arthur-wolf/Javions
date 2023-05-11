package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.*;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static ch.epfl.javions.Units.Angle.DEGREE;

/**
 * The AircraftController class is responsible for managing and displaying aircrafts on a map.
 * It binds the aircraft state to the graphical representation in the GUI.
 *
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public final class AircraftController {
    private final MapParameters mapParameters;
    private final ObservableSet<ObservableAircraftState> aircraftState;
    private final ObjectProperty<ObservableAircraftState> selectedAircraftState;
    private final Pane pane;
    private final int UNKNOWN = -1;

    /**
     * Constructs a new AircraftController with the given map parameters, aircraft state set,
     * and selected aircraft state property.
     *
     * @param mapParameters          the map parameters
     * @param aircraftState          the set of aircraft states
     * @param selectedAircraftState  the property for the selected aircraft state
     */
    public AircraftController(MapParameters mapParameters,
                              ObservableSet<ObservableAircraftState> aircraftState,
                              ObjectProperty<ObservableAircraftState> selectedAircraftState) {

        this.mapParameters = mapParameters;
        this.aircraftState = aircraftState;
        this.selectedAircraftState = selectedAircraftState;
        this.pane = createAircraftPane();
        this.pane.setPickOnBounds(false);
        bindAircraftSetListeners();
    }

    /**
     * Returns the pane containing the aircraft representations.
     *
     * @return the aircraft pane
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Creates the pane for the aircraft.
     *
     * @return the aircraft pane
     */
    private Pane createAircraftPane() {
        Pane aircraftPane = new Pane();
        aircraftPane.getStylesheets().add("aircraft.css");
        return aircraftPane;
    }

    /**
     * Binds the listeners to changes in the set of aircraft states.
     */
    private void bindAircraftSetListeners() {
        aircraftState.addListener((SetChangeListener<ObservableAircraftState>) change -> {
            if (change.wasAdded()) {
                Group aircraftGroup = createAircraftGroup(change.getElementAdded());
                pane.getChildren().add(aircraftGroup);
            } else if (change.wasRemoved()) {
                ObservableAircraftState removedAircraft = change.getElementRemoved();
                String aircraftId = removedAircraft.address().string();
                pane.getChildren().removeIf(node -> node.getId().equals(aircraftId));
            }
        });

        mapParameters.zoomProperty().addListener((observable, oldValue, newValue) -> {
            for (ObservableAircraftState aircraft : aircraftState) {
                // Look up the aircraft group by its ID
                Group aircraftGroup = (Group) pane.lookup("#" + aircraft.address().string());
                if (aircraftGroup != null) {
                    // Get the trajectory group from the aircraft group
                    Group trajectoryGroup = (Group) aircraftGroup.getChildren().get(0);
                    if (trajectoryGroup.isVisible()) {
                        // Clear the existing trajectory lines
                        trajectoryGroup.getChildren().clear();
                        // Get the trajectory positions of the aircraft
                        List<ObservableAircraftState.AirbornePos> trajectory = aircraft.trajectory();
                        // Build trajectory lines between each pair of positions
                        List<Line> trajectoryLines = IntStream.range(0, trajectory.size() - 1)
                                .mapToObj(i -> buildTrajectoryLine(trajectory.get(i), trajectory.get(i + 1)))
                                .toList();
                        // Add the trajectory lines to the trajectory group
                        trajectoryGroup.getChildren().addAll(trajectoryLines);
                    }
                }
            }
        });
    }

    /**
     * Creates the aircraft group for an observable aircraft state.
     *
     * @param aircraftState     the observable aircraft state
     * @return the aircraft group
     */
    private Group createAircraftGroup(ObservableAircraftState aircraftState) {
        Group iconAndLabelGroup = iconAndLabelGroup(aircraftState);
        Group trajectoryGroup = buildTrajectoryGroup(aircraftState);

        // Bind the visibility of the trajectory group to the selected aircraft state
        trajectoryGroup.visibleProperty().bind(Bindings.createBooleanBinding(() -> selectedAircraftState.get() == aircraftState, selectedAircraftState));

        // Create the aircraft group with the trajectory group and icon/label group
        Group aircraftGroup = new Group(trajectoryGroup, iconAndLabelGroup);

        String aircraftId = aircraftState.address().string();
        // Set the ID of the aircraft group to the aircraft address
        aircraftGroup.setId(aircraftId);

        // Set the view order of the aircraft group based on the altitude (higher altitude appears in front)
        aircraftGroup.viewOrderProperty().bind(aircraftState.altitudeProperty().negate());

        return aircraftGroup;
    }

    /**
     * Creates the group containing the aircraft icon and label.
     *
     * @param aircraftState     the observable aircraft state
     * @return the group containing the icon and label
     */
    private Group iconAndLabelGroup(ObservableAircraftState aircraftState) {
        SVGPath icon = buildIcon(aircraftState);
        Group label = buildLabel(aircraftState);
        // Create a group to contain the icon and label
        Group iconAndLabelGroup = new Group(icon, label);

        // Bind the layout X property of the group to the position of the aircraft (longitude)
        iconAndLabelGroup.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> WebMercator.x(mapParameters.getZoom(),
                        aircraftState.getPosition().longitude()) - mapParameters.getMinX(),
                mapParameters.zoomProperty(), mapParameters.minXProperty(), aircraftState.positionProperty()));

        // Bind the layout Y property of the group to the position of the aircraft (latitude)
        iconAndLabelGroup.layoutYProperty().bind(Bindings.createDoubleBinding(
                () -> WebMercator.y(mapParameters.getZoom(),
                        aircraftState.getPosition().latitude()) - mapParameters.getMinY(),
                mapParameters.zoomProperty(), mapParameters.minYProperty(), aircraftState.positionProperty()));

        return iconAndLabelGroup;
    }


    /**
     * Builds the icon for the aircraft.
     *
     * @param aircraftState     the observable aircraft state
     * @return the aircraft icon
     */
    private SVGPath buildIcon(ObservableAircraftState aircraftState) {
        AircraftIcon aircraftIcon = AircraftIcon.iconFor(
                aircraftState.aircraftData().typeDesignator(),
                aircraftState.aircraftData().description(),
                aircraftState.getCategory(),
                aircraftState.aircraftData().wakeTurbulenceCategory()
        );
        SVGPath icon = new SVGPath();
        icon.getStyleClass().add("aircraft");

        // Bind the content property of the SVGPath to the SVG path of the aircraft icon
        icon.contentProperty().bind(Bindings.createStringBinding(aircraftIcon::svgPath));

        // Bind the rotation property of the SVGPath to the track or heading of the aircraft
        icon.rotateProperty().bind(Bindings.createDoubleBinding(() ->
                        aircraftIcon.canRotate() ? Units.convertTo(aircraftState.trackOrHeadingProperty().doubleValue(), DEGREE) : 0.0,
                aircraftState.trackOrHeadingProperty()));

        // Set a mouse click event handler to toggle the selected state of the aircraft
        icon.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                selectedAircraftState.set(selectedAircraftState.get() == aircraftState ? null : aircraftState);
            }
        });

        // Bind the fill property of the SVGPath to the altitude-based color of the aircraft
        icon.fillProperty().bind(getFillProperty(aircraftState));

        return icon;
    }

    /**
     * Builds the label for the aircraft.
     *
     * @param aircraftState     the observable aircraft state
     * @return the aircraft label
     */
    private Group buildLabel(ObservableAircraftState aircraftState) {
        Text labelText = new Text();
        Rectangle labelBackground = new Rectangle();

        // Determine the registration or call sign or the ICAO of the aircraft
        String identification = Optional.ofNullable(aircraftState.aircraftData().registration().string())
                .orElse(Optional.ofNullable(aircraftState.getCallSign())
                                .map(Object::toString).
                        orElse(aircraftState.address().string()));

        // Bind the text property of the label to the formatted string based on altitude and velocity values
        labelText.textProperty().bind(Bindings.createStringBinding(() -> {
            // Format the velocity and altitude values
            // If the value is -1, then the value is unknown and a question mark is displayed
            String velocity = aircraftState.getVelocity() == UNKNOWN
                    ? "?"
                    : String.format("%.0f", Units.convertTo(aircraftState.getVelocity(), Units.Speed.KILOMETER_PER_HOUR));
            String altitude = aircraftState.getAltitude() == UNKNOWN
                    ? "?"
                    : String.format("%.0f", aircraftState.getAltitude());
            return String.format("%s\n%s\u2002km/h %s\u2002m",
                    identification,
                    velocity,
                    altitude);
        }, aircraftState.altitudeProperty(), aircraftState.velocityProperty()));

        labelText.getStyleClass().add("label-text");

        // Bind the width and height properties of the label background to accommodate the text
        labelBackground.widthProperty().bind(labelText.layoutBoundsProperty().map(bounds -> bounds.getWidth() + 4));
        labelBackground.heightProperty().bind(labelText.layoutBoundsProperty().map(bounds -> bounds.getHeight() + 4));

        labelBackground.getStyleClass().add("label-rectangle");

        Group labelGroup = new Group(labelBackground, labelText);
        labelGroup.getStyleClass().add("label");

        // Set the order of the label group to be behind the icon and trajectory
        labelGroup.setViewOrder(-1);

        // Set a mouse click event handler to select the aircraft
        labelGroup.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                selectedAircraftState.set(aircraftState);
            }
        });

        // Bind the visible property of the label group based on the zoom level and selected state
        labelGroup.visibleProperty().bind(Bindings.createBooleanBinding(() ->
                        mapParameters.getZoom() >= 11
                                || (selectedAircraftState.get() != null
                                && aircraftState.equals(selectedAircraftState.get())),
                mapParameters.zoomProperty(), selectedAircraftState));

        return labelGroup;
    }


    /**
     * Builds the trajectory group for an aircraft state.
     *
     * @param aircraftState the observable aircraft state
     * @return the trajectory group
     */
    private Group buildTrajectoryGroup(ObservableAircraftState aircraftState) {
        Group trajectoryGroup = new Group();
        trajectoryGroup.getStyleClass().add("trajectory");
        trajectoryGroup.setVisible(false);

        // Add a listener to the trajectory property of the aircraft state
        aircraftState.trajectory().addListener((ListChangeListener<ObservableAircraftState.AirbornePos>) change -> {
            if (trajectoryGroup.isVisible()) {
                trajectoryGroup.getChildren().clear();
                List<ObservableAircraftState.AirbornePos> trajectory = aircraftState.trajectory();
                int size = trajectory.size();

                // Build the trajectory lines based on the positions in the trajectory list
                List<Line> trajectoryLines = new ArrayList<>();
                for (int i = 0; i < size - 1; i++) {
                    ObservableAircraftState.AirbornePos start = trajectory.get(i);
                    ObservableAircraftState.AirbornePos end = trajectory.get(i + 1);
                    trajectoryLines.add(buildTrajectoryLine(start, end));
                }

                // Add the trajectory lines to the trajectory group
                trajectoryGroup.getChildren().addAll(trajectoryLines);
            }
        });

        return trajectoryGroup;
    }


    /**
     * Builds a trajectory line between two aircraft positions.
     *
     * @param start     the start position
     * @param end       the end position
     * @return the trajectory line
     */
    private Line buildTrajectoryLine(ObservableAircraftState.AirbornePos start, ObservableAircraftState.AirbornePos end) {
        // Create a line with coordinates based on the start and end positions
        Line line = new Line(
                WebMercator.x(mapParameters.getZoom(), start.geoPos().longitude()), WebMercator.y(mapParameters.getZoom(), start.geoPos().latitude()),
                WebMercator.x(mapParameters.getZoom(), end.geoPos().longitude()), WebMercator.y(mapParameters.getZoom(), end.geoPos().latitude())
        );

        // Bind the layout properties of the line to the map parameters to ensure proper positioning
        line.layoutXProperty().bind(mapParameters.minXProperty().negate());
        line.layoutYProperty().bind(mapParameters.minYProperty().negate());

        // Create color stops based on the altitudes of the start and end positions
        Stop s1 = new Stop(0, getAltitudeColor(start.altitude()));
        Stop s2 = new Stop(1, getAltitudeColor(end.altitude()));

        // Set the stroke of the line using a linear gradient with the altitude colors
        line.setStroke(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, s1, s2));

        return line;
    }

    /**
     * Returns the altitude color corresponding to a given altitude.
     *
     * @param altitude     the altitude
     * @return the corresponding altitude color
     */
    private Color getAltitudeColor(double altitude) {
        final int MAX_ALTITUDE = 12000;
        double c = Math.cbrt(altitude / MAX_ALTITUDE); //Given formula (2.2) for altitude color : https://cs108.epfl.ch/p/09_aircraft-view.html
        return ColorRamp.PLASMA.at(c);
    }
    /**
     * Returns the fill property for the aircraft state.
     *
     * @param aircraftState     the observable aircraft state
     * @return the fill property for the aircraft state
     */
    private ObjectProperty<Color> getFillProperty(ObservableAircraftState aircraftState) {
        double altitude = aircraftState.altitudeProperty().get();
        return new SimpleObjectProperty<>(getAltitudeColor(altitude));
    }
}
