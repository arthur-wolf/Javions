package ch.epfl.javions.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

public final class StatusLineController {

    private final BorderPane pane;
    private final IntegerProperty aircraftCountProperty;
    private final LongProperty messageCountProperty;

    public StatusLineController() {
        aircraftCountProperty = new SimpleIntegerProperty(0);
        messageCountProperty = new SimpleLongProperty(0);

        // Create nodes
        Text aircraftCountText = new Text();
        aircraftCountText.textProperty().bind(Bindings.createStringBinding(() -> "Aéronefs visibles : " + aircraftCountProperty.getValue(), aircraftCountProperty));

        Text messageCountText = new Text();
        messageCountText.textProperty().bind(Bindings.createStringBinding(() -> "Messages reçus : " + messageCountProperty.getValue(), messageCountProperty));


        // Build scene graph
        pane = new BorderPane();
        pane.getStylesheets().add("status.css");
        pane.getStyleClass().add("BorderPane");
        pane.setLeft(aircraftCountText);
        pane.setRight(messageCountText);
    }

    public BorderPane pane() {
        return pane;
    }

    public IntegerProperty aircraftCountProperty() {
        return aircraftCountProperty;
    }

    public LongProperty messageCountProperty() {
        return messageCountProperty;
    }
}
