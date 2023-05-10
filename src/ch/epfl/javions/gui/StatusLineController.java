package ch.epfl.javions.gui;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

public final class StatusLineController {

    private final BorderPane pane;
    private final LongProperty aircraftCountProperty;
    private final LongProperty messageCountProperty;

    public StatusLineController() {
        // Initialize properties
        aircraftCountProperty = new SimpleLongProperty();
        messageCountProperty = new SimpleLongProperty();

        // Create nodes
        Text aircraftCountText = new Text();
        aircraftCountText.textProperty().bind(aircraftCountProperty.asString());
        Label aircraftCountLabel = new Label("Aéronefs visibles : " + aircraftCountText.getText());

        //todo add a listener to the property to update the label

        Text messageCountText = new Text();
        messageCountText.textProperty().bind(messageCountProperty.asString());
        Label messageCountLabel = new Label("Messages reçus : " + messageCountText.getText());
        //todo add a listener to the property to update the label


        // Build scene graph
        pane = new BorderPane();
        pane.getStylesheets().add("status.css");
        pane.getStyleClass().add("BorderPane");
        pane.setLeft(aircraftCountLabel);
        pane.setRight(messageCountLabel);
    }

    public BorderPane pane() {
        return pane;
    }

    public LongProperty aircraftCountProperty() {
        return aircraftCountProperty;
    }

    public LongProperty messageCountProperty() {
        return messageCountProperty;
    }
}
