package ch.epfl.javions.gui;

import ch.epfl.javions.WebMercator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public final class AircraftController {
    private final MapParameters mapParameters;
    private final ObservableSet<ObservableAircraftState> aircraftState;
    private final ObjectProperty<ObservableAircraftState> selectedAircraftState;

    private final Pane pane;

    public AircraftController(MapParameters mapParameters,
                              ObservableSet<ObservableAircraftState> aircraftState,
                              ObjectProperty<ObservableAircraftState> selectedAircraftState) {
        this.mapParameters = mapParameters;
        this.aircraftState = aircraftState;
        this.selectedAircraftState = selectedAircraftState;
        this.pane = createAircraftPane();
        bindAircraftSetListeners();
    }

    public Pane pane() {
        return pane;
    }

    private Pane createAircraftPane() {
        Pane aircraftPane = new Pane();
        aircraftPane.getStylesheets().add("aircraft.css");
        aircraftPane.setPickOnBounds(false);
        return aircraftPane;
    }

    private void bindAircraftSetListeners() {
        aircraftState.addListener((SetChangeListener<ObservableAircraftState>) change -> {
            if (change.wasAdded()) {
                Group aircraftGroup = createAircraftGroup(change.getElementAdded());
                System.out.println("Aircraft added: " + aircraftGroup.getId());
                pane.getChildren().add(aircraftGroup);
            } else if (change.wasRemoved()) {
                System.out.println("Aircraft removed: " + change.getElementRemoved().address().string());
                ObservableAircraftState removedAircraft = change.getElementRemoved();
                String aircraftId = removedAircraft.address().string();
                pane.getChildren().removeIf(node -> node.getId().equals(aircraftId));
            }
        });
    }

    private Group createAircraftGroup(ObservableAircraftState aircraftState) {
        ObservableAircraftState s = aircraftState;
        Group aircraftGroup = new Group(iconAndLabelGroup(s));

        String aircraftId = aircraftState.address().string();
        aircraftGroup.setId(aircraftId);

        SVGPath icon = buildIcon(aircraftState);
        aircraftGroup.getChildren().add(icon);

        Label label = buildLabel(aircraftState);
        aircraftGroup.getChildren().add(label);

        Group trajectoryGroup = buildTrajectoryGroup(aircraftState);
        aircraftGroup.getChildren().add(trajectoryGroup);

        aircraftGroup.viewOrderProperty().bind(aircraftState.altitudeProperty().negate());
        aircraftGroup.setPickOnBounds(false);

        // Other configurations for the aircraft group

        return aircraftGroup;
    }

    private SVGPath buildIcon(ObservableAircraftState aircraftState) {
        AircraftIcon aircraftIcon = AircraftIcon.iconFor(
                aircraftState.aircraftData().typeDesignator(),
                aircraftState.aircraftData().description(),
                aircraftState.getCategory(),
                aircraftState.aircraftData().wakeTurbulenceCategory()
        );

        SVGPath icon = new SVGPath();
        icon.setContent(aircraftIcon.svgPath());
        icon.getStyleClass().add("aircraft-icon");

        // Bind properties for icon appearance
       // icon.rotateProperty().bind(aircraftState.canRotateProperty().conditional(aircraftState.headingProperty()).otherwise(0));
        icon.fillProperty().bind(getFillProperty(aircraftState));

        return icon;
    }

    private Label buildLabel(ObservableAircraftState aircraftState) {
        Label label = new Label(aircraftState.address().string());
        label.setId("aircraft-label");

        // Other configurations for the label

        return label;
    }
    private Group iconAndLabelGroup(ObservableAircraftState aircraftState){
        SVGPath icon = buildIcon(aircraftState);
        Label label = buildLabel(aircraftState);
        Group iconAndLabelGroup = new Group(icon, label);
        iconAndLabelGroup.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> WebMercator.x(mapParameters.getZoom(),
                        aircraftState.getPosition().longitude()) - mapParameters.getMinX()));
        iconAndLabelGroup.layoutYProperty().bind(Bindings.createDoubleBinding(
                () -> WebMercator.y(mapParameters.getZoom(),
                        aircraftState.getPosition().latitude()) - mapParameters.getMinY()));

        updateIconAndLabelGroup(iconAndLabelGroup, aircraftState);
        return iconAndLabelGroup;
    }

    private void updateIconAndLabelGroup(Group iconAndLabelGroup, ObservableAircraftState aircraftState){
        mapParameters.zoomProperty().addListener((observable, oldValue, newValue) -> {
            iconAndLabelGroup.layoutXProperty().bind(Bindings.createDoubleBinding(
                    () -> WebMercator.x(mapParameters.getZoom(),
                            aircraftState.getPosition().longitude()) - mapParameters.getMinX()));
            iconAndLabelGroup.layoutYProperty().bind(Bindings.createDoubleBinding(
                    () -> WebMercator.y(mapParameters.getZoom(),
                            aircraftState.getPosition().latitude()) - mapParameters.getMinY()));
        });
    }

    private ObjectProperty<Color> getFillProperty(ObservableAircraftState aircraftState) {
        // Calculate fill color based on aircraft altitude
        // You can define your own logic here
        double altitude = aircraftState.altitudeProperty().get();
        // Example logic: Green for altitude below 10000 ft, red otherwise
        Color fillColor = altitude < 10000 ? Color.GREEN : Color.RED;
        // Bind fill color property
        return new SimpleObjectProperty<>(fillColor);
    }
    private Group buildTrajectoryGroup(ObservableAircraftState aircraftState) {
        Group trajectoryGroup = new Group();

        // Construction des segments de la trajectoire
        aircraftState.trajectory().forEach(trajectory -> {
            SVGPath trajectoryPath = new SVGPath();
            trajectoryPath.setContent(trajectory.toString());

            // Autres configurations du segment de trajectoire
            trajectoryPath.setStroke(Color.BLUE);
            trajectoryPath.setStrokeWidth(2);

            trajectoryGroup.getChildren().add(trajectoryPath);
        });

        // Positionnement de la trajectoire dans le système de coordonnées de la carte
        trajectoryGroup.layoutXProperty().bind(mapParameters.minXProperty());
        trajectoryGroup.layoutYProperty().bind(mapParameters.minYProperty());

        // Visibilité de la trajectoire en fonction de l'aéronef sélectionné
        trajectoryGroup.visibleProperty().bind(
                selectedAircraftState.isNull().or(selectedAircraftState.isEqualTo(aircraftState))
        );

        return trajectoryGroup;
    }

}
