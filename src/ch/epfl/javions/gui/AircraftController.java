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
    private Group buildAircraftGroup(ObservableAircraftState elementAdded) {
        Group aircraftGroup = new Group();
        return null;
    }

    private Group buildTrajectorygroup(ObservableAircraftState elementAdded) {
        Group trajectoryGroup = new Group();
        return null;
    }

    public Pane pane() {
        return pane;
    }

    private void rotate(AircraftIcon e, double degree){

        if (e.canRotate()){

        }
        else {

        }
    }
}
