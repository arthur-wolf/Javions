package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

import static ch.epfl.javions.Units.Angle.DEGREE;

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
        this.pane.setPickOnBounds(false);
        bindAircraftSetListeners();
    }

    public Pane pane() {
        return pane;
    }

    private Pane createAircraftPane() {
        Pane aircraftPane = new Pane();
        aircraftPane.getStylesheets().add("aircraft.css");
        //aircraftPane.setPickOnBounds(false);
        return aircraftPane;
    }

    private void bindAircraftSetListeners() {
        aircraftState.addListener((SetChangeListener<ObservableAircraftState>) change -> {
            if (change.wasAdded()) {
                Group aircraftGroup = createAircraftGroup(change.getElementAdded());
                //System.out.println("Aircraft added: " + aircraftGroup.getId());
                pane.getChildren().add(aircraftGroup);
            } else if (change.wasRemoved()) {
               // System.out.println("Aircraft removed: " + change.getElementRemoved().address().string());
                ObservableAircraftState removedAircraft = change.getElementRemoved();
                String aircraftId = removedAircraft.address().string();
                pane.getChildren().removeIf(node -> node.getId().equals(aircraftId));
            }
        });
    }

    private Group createAircraftGroup(ObservableAircraftState aircraftState) {
        Group iconAndLabelGroup = iconAndLabelGroup(aircraftState);
        Group trajectoryGroup = buildTrajectoryGroup(aircraftState);
        Group aircraftGroup = new Group(iconAndLabelGroup, trajectoryGroup);

        String aircraftId = aircraftState.address().string();
        aircraftGroup.setId(aircraftId);

        aircraftGroup.viewOrderProperty().bind(aircraftState.altitudeProperty().negate());
        //aircraftGroup.setPickOnBounds(false);

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
        icon.getStyleClass().add("aircraft");

        // Bind content property to the aircraftIcon's svgPath
        icon.contentProperty().bind(Bindings.createStringBinding(() -> aircraftIcon.svgPath()));

        // Bind rotate property conditionally to aircraftState's trackOrHeadingProperty
            if (aircraftIcon.canRotate()) {
                icon.rotateProperty().bind(Bindings.createDoubleBinding(() ->  Units.convertTo(aircraftState.trackOrHeadingProperty().doubleValue(), DEGREE),
                        aircraftState.trackOrHeadingProperty()));

            } else {
                icon.rotateProperty().bind(Bindings.createDoubleBinding(() -> 0.0));
            }

        // Bind fill property to the altitude-based color
        icon.fillProperty().bind(getFillProperty(aircraftState));

        return icon;
    }

   private Group buildLabel(ObservableAircraftState aircraftState) {
       Text labelText = new Text();
       Rectangle labelBackground = new Rectangle();

       String address = (aircraftState.getCallSign() == null) ? aircraftState.address().string() : aircraftState.getCallSign().string();

       labelText.textProperty().bind(
               Bindings.createStringBinding(() ->
                               String.format("%s\n%.0f km/h %.0f m",
                                       address,
                                       Double.isNaN(Units.convertTo(aircraftState.getVelocity(), Units.Speed.KILOMETER_PER_HOUR)) ? "?" : Units.convertTo(aircraftState.getVelocity(), Units.Speed.KILOMETER_PER_HOUR),
                                       aircraftState.getAltitude()),
                       aircraftState.altitudeProperty(), aircraftState.velocityProperty()));


       labelText.getStyleClass().add("label-text");

       labelBackground.widthProperty().bind(
               labelText.layoutBoundsProperty().map(bounds -> bounds.getWidth() + 4));
       labelBackground.heightProperty().bind(
               labelText.layoutBoundsProperty().map(bounds -> bounds.getHeight() + 4));
       labelBackground.getStyleClass().add("label-rectangle");

       Group labelGroup = new Group(labelBackground, labelText);
       labelGroup.getStyleClass().add("label");
       labelGroup.visibleProperty().bind(
               Bindings.createBooleanBinding(() ->
                       (mapParameters.getZoom())>= 11));


       return labelGroup;
   }



    private Group iconAndLabelGroup(ObservableAircraftState aircraftState){
        SVGPath icon = buildIcon(aircraftState);
        Group label = buildLabel(aircraftState);
        Group iconAndLabelGroup = new Group(icon, label);

        iconAndLabelGroup.layoutXProperty().bind(Bindings.createObjectBinding(
                () -> WebMercator.x(mapParameters.getZoom(),
                        aircraftState.getPosition().longitude()) - mapParameters.getMinX(),
                mapParameters.zoomProperty(), mapParameters.minXProperty(), aircraftState.positionProperty()));
        iconAndLabelGroup.layoutYProperty().bind(Bindings.createObjectBinding(
                () -> WebMercator.y(mapParameters.getZoom(),
                        aircraftState.getPosition().latitude()) - mapParameters.getMinY(),
                mapParameters.zoomProperty(), mapParameters.minYProperty(), aircraftState.positionProperty()));

        return iconAndLabelGroup;
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
            trajectoryPath.setStroke(Color.RED);
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
