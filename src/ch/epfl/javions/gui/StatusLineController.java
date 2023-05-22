package ch.epfl.javions.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

/**
 * This is the controller for the status line of the Javions application.
 * The status line displays the number of visible aircraft and the number of
 * received messages. These values are updated through binding to the respective properties.
 * The status line is composed of two text nodes, one for each property,
 * placed on the left and right side of the BorderPane.
 *
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public final class StatusLineController {
    private final BorderPane pane;
    private final IntegerProperty aircraftCountProperty;
    private final LongProperty messageCountProperty;

    /**
     * The constructor for the StatusLineController. Initializes the aircraft and
     * message count properties and binds them to text nodes for display.
     */
    public StatusLineController() {
        aircraftCountProperty = new SimpleIntegerProperty(0);
        messageCountProperty = new SimpleLongProperty(0);

        // Create nodes
        Text aircraftCountText = new Text();
        aircraftCountText.textProperty().bind(Bindings.createStringBinding(() ->
                "Aéronefs visibles : " + aircraftCountProperty.getValue(), aircraftCountProperty));

        Text messageCountText = new Text();
        messageCountText.textProperty().bind(Bindings.createStringBinding(() ->
                "Messages reçus : " + messageCountProperty.getValue(), messageCountProperty));

        // Build scene graph
        pane = new BorderPane();
        buildScene(aircraftCountText, messageCountText);
    }

    /**
     * Builds the scene graph for the status line.
     *
     * @param aircraftCountText The text node for the aircraft count.
     * @param messageCountText The text node for the message count.
     */
    private void buildScene(Text aircraftCountText, Text messageCountText) {
        pane.getStylesheets().add("status.css");
        pane.getStyleClass().add("BorderPane");
        pane.setLeft(aircraftCountText);
        pane.setRight(messageCountText);
    }

    /**
     * Returns the BorderPane that contains the status line.
     *
     * @return The BorderPane containing the status line.
     */
    public BorderPane pane() {
        return pane;
    }

    /**
     * Returns the IntegerProperty that represents the number of visible aircraft.
     *
     * @return The IntegerProperty for the aircraft count.
     */
    public IntegerProperty aircraftCountProperty() {
        return aircraftCountProperty;
    }

    /**
     * Returns the LongProperty that represents the number of received messages.
     *
     * @return The LongProperty for the message count.
     */
    public LongProperty messageCountProperty() {
        return messageCountProperty;
    }
}
