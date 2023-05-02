package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
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
        this.pane = new Pane();
        pane.getStylesheets().add("aircraft.css");

        aircraftState.addListener((SetChangeListener<ObservableAircraftState>) change -> {
            if (change.wasAdded()) {
                Group oaciGroup = new Group();
                oaciGroup.setId(change.getElementAdded().address().string());
                oaciGroup.getChildren().add(buildTrajectorygroup(change.getElementAdded()));
                oaciGroup.getChildren().add(buildAircraftGroup(change.getElementAdded()));
                pane.getChildren().add(oaciGroup);
            } else if (change.wasRemoved()) {
                pane.getChildren().remove(change.getElementRemoved().address().string());
            }
        });


    }


    private SVGPath buildIcon() {
        return null;
    }
    private Group buildAircraftGroup(ObservableAircraftState aircraftState) {
        Group aircraftGroup = new Group();
        SVGPath icon = buildIcon();
        aircraftGroup.getChildren().add(icon);
        // Ajouter d'autres éléments visuels pour représenter l'état de l'avion si nécessaire
       // aircraftGroup.setLayoutX(mapParameters.getMinX(aircraftState.position().x()));
        //aircraftGroup.setLayoutY(mapParameters.getMinY(aircraftState.position().y()));
        return aircraftGroup;
    }


    private Group buildTrajectorygroup(ObservableAircraftState aircraftState) {
        Group trajectoryGroup = new Group();
        // Ajouter des éléments visuels représentant la trajectoire de l'avion
        aircraftState.trajectory().forEach(trajectory -> {
            SVGPath trajectoryPath = new SVGPath();
           // trajectoryPath.setContent(trajectory.svgPath());
            trajectoryGroup.getChildren().add(trajectoryPath);
        });
        trajectoryGroup.setLayoutX(mapParameters.getMinX());
        trajectoryGroup.setLayoutY(mapParameters.getMinY());
        return trajectoryGroup;
    }



    public Pane pane() {
        Pane paneWrapper = new Pane();
        paneWrapper.getStylesheets().add("aircraft.css");
        aircraftState.addListener((SetChangeListener<ObservableAircraftState>) change -> {
            if (change.wasAdded()) {
                Group oaciGroup = new Group();
                oaciGroup.setId(change.getElementAdded().address().string());

            } else if (change.wasRemoved()) {
                pane.getChildren().remove(change.getElementRemoved().address().string());
            }
        });
        return paneWrapper;
    }
}