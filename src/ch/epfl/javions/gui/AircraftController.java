package ch.epfl.javions.gui;

import javafx.beans.property.ObjectProperty;
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
    private final Group aircraftGroup;

    public AircraftController(MapParameters mapParameters,
                              ObservableSet<ObservableAircraftState> aircraftState,
                              ObjectProperty<ObservableAircraftState> selectedAircraftState) {
        this.mapParameters = mapParameters;
        this.aircraftState = aircraftState;
        this.selectedAircraftState = selectedAircraftState;
        this.pane = new Pane();
        pane.getStylesheets().add("aircraft.css");

        this.aircraftGroup = new Group();
        pane.getChildren().add(aircraftGroup);

        aircraftState.addListener((SetChangeListener<ObservableAircraftState>) change -> {
            if (change.wasAdded()) {
                Group oaciGroup = new Group();
                oaciGroup.setId(change.getElementAdded().address().string());
                oaciGroup.getChildren().add(buildTrajectoryGroup(change.getElementAdded()));
                oaciGroup.getChildren().add(buildAircraftGroup(change.getElementAdded()));
                pane.getChildren().add(oaciGroup);
            } else if (change.wasRemoved()) {
                pane.getChildren().remove(change.getElementRemoved().address().string());
            }
        });

        selectedAircraftState.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                updateAircraftGroup(oldValue, false);
            }
            if (newValue != null) {
                updateAircraftGroup(newValue, true);
            }
        });
    }

    private SVGPath buildIcon() {
        SVGPath icon = new SVGPath();
        icon.setContent("M0,0 L20,10 L0,20Z"); // Exemple de chemin SVG pour l'icône
        icon.setFill(Color.RED); // Couleur de remplissage de l'icône

        return icon;
    }

    private Group buildAircraftGroup(ObservableAircraftState aircraftState) {
        Group aircraftGroup = new Group();
        SVGPath icon = buildIcon();
        aircraftGroup.getChildren().add(icon);

        String aircraftId = aircraftState.address().string();
        aircraftGroup.setId(aircraftId);
        aircraftGroup.setLayoutX(aircraftState.getPosition().latitude());
        aircraftGroup.setLayoutY(aircraftState.getPosition().longitude());

        // Ajout de l'étiquette
        Label label = buildLabel(aircraftState);
        aircraftGroup.getChildren().add(label);

        // Ajout de la trajectoire
        Group trajectoryGroup = buildTrajectoryGroup(aircraftState);
        aircraftGroup.getChildren().add(trajectoryGroup);

        aircraftGroup.setOnMouseClicked(event -> {
            selectedAircraftState.set(aircraftState);
            event.consume();
        });

        return aircraftGroup;
    }

    private Label buildLabel(ObservableAircraftState aircraftState) {
        Label label = new Label(aircraftState.address().string());
        label.setId("aircraft-label");
        label.setMouseTransparent(true);
        label.setLayoutX(-40);
        label.setLayoutY(-25);
        return label;
    }

    private Group buildTrajectoryGroup(ObservableAircraftState aircraftState) {
        Group trajectoryGroup = new Group();
        aircraftState.trajectory().forEach(trajectory -> {
            SVGPath trajectoryPath = new SVGPath();
            //trajectoryPath.setContent(trajectory.svgPath());
            //trajectoryGroup.getChildren().add(trajectoryPath.setFill(Color.TRANSPARENT));
            trajectoryPath.setStroke(Color.BLUE);
            trajectoryPath.setStrokeWidth(2);
            trajectoryGroup.getChildren().add(trajectoryPath);
        });
        trajectoryGroup.setLayoutX(mapParameters.getMinX());
        trajectoryGroup.setLayoutY(mapParameters.getMinY());
        trajectoryGroup.visibleProperty().bind(selectedAircraftState.isNull().or(selectedAircraftState.isEqualTo(aircraftState)));

        return trajectoryGroup;
    }

    private void updateAircraftGroup(ObservableAircraftState aircraftState, boolean isSelected) {
        String aircraftId = aircraftState.address().string();
        aircraftGroup.getChildren().stream()
                .filter(node -> node.getId().equals(aircraftId))
                .findFirst()
                .ifPresent(node -> {
                    node.setOpacity(isSelected ? 1.0 : 0.5);
                    node.toFront();

                    Label label = (Label) node.lookup("#aircraft-label");
                    label.setVisible(isSelected);
                });
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

