package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

import javafx.animation.AnimationTimer;

/**
 * This is the main class for the Javions application.
 * It sets up the graphical user interface and starts the application.
 *
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public class Main extends Application {

    private static final int INITIAL_ZOOM = 8;
    private static final double INITIAL_LONGITUDE = 33530;
    private static final double INITIAL_LATITUDE = 23070;
    private static final long TO_MILLISECONDS = 1000000;

    /**
     * This is a thread-safe queue used for storing raw ADS-B messages received from aircraft.
     */
    private final ConcurrentLinkedQueue<RawMessage> messageQueue = new ConcurrentLinkedQueue<>();

    /**
     * This is the main method which launches the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * This method reads a raw ADS-B message from an input stream.
     *
     * @param inputStream the input stream to read from
     * @return the raw ADS-B message
     * @throws IOException if an I/O error occurs
     */
    static RawMessage readMessage(DataInputStream inputStream) throws IOException {
        byte[] bytes = new byte[RawMessage.LENGTH];
        long timeStampNs = inputStream.readLong();
        int bytesRead = inputStream.readNBytes(bytes, 0, bytes.length);
        if (bytesRead == RawMessage.LENGTH) {
            return new RawMessage(timeStampNs, new ByteString(bytes));
        }
        return null;
    }

    /**
     * This method initializes the JavaFX application. It sets up the map, aircraft database, status line,
     * and starts the message handling threads.
     *
     * @param primaryStage the primary stage for this application, onto which the application scene is set.
     * @throws Exception if an error occurs during initialization
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        long startTime = System.nanoTime();
        Path tileCachePath = Path.of("tile-cache");
        URL dbUrl = getClass().getResource("/aircraft.zip");
        assert dbUrl != null;
        String dbFilePath = Path.of(dbUrl.toURI()).toString();

        // We're setting up several key components here:
        // - selectedAircraftProperty: which aircraft is currently selected by the user
        // - mapParameters: the parameters for our map, such as zoom level and initial coordinates
        // - tileManager: responsible for managing the map's tiles
        // - baseMapController: controls the base map
        // - aircraftDatabase: a database of all the aircraft
        // - aircraftStateManager: manages the state of all the aircraft
        // - aircraftController: controls the aircraft, including movement and selection
        // - aircraftTableController: controls the table that displays the list of aircraft
        // - statusLineController: controls the line that displays the status of the application

        ObjectProperty<ObservableAircraftState> selectedAircraftProperty = new SimpleObjectProperty<>();
        var mapParameters = new MapParameters(INITIAL_ZOOM, INITIAL_LONGITUDE, INITIAL_LATITUDE);
        var tileManager = new TileManager(tileCachePath, "tile.openstreetmap.org");
        var baseMapController = new BaseMapController(tileManager, mapParameters);
        var aircraftDatabase = new AircraftDatabase(dbFilePath);
        var aircraftStateManager = new AircraftStateManager(aircraftDatabase);
        var aircraftController = new AircraftController(mapParameters, aircraftStateManager.states(), selectedAircraftProperty);
        var aircraftTableController = new AircraftTableController(aircraftStateManager.states(), selectedAircraftProperty);
        var statusLineController = new StatusLineController();

        // We're setting up our GUI layout here, with a split pane for the map and the status bar
        var stackPane = new StackPane(baseMapController.pane(), aircraftController.pane());
        var statusBar = new BorderPane(aircraftTableController.pane(), statusLineController.pane(), null, null, null);
        var root = new SplitPane(stackPane, statusBar);

        // Setting up event handling for when the user double-clicks on an aircraft in the table
        aircraftTableController.setOnDoubleClick(event -> baseMapController.centerOn(event.getPosition()));

        root.setOrientation(javafx.geometry.Orientation.VERTICAL);
        primaryStage.setScene(new Scene(new BorderPane(root, null, null, null, null)));
        primaryStage.setTitle("Javions");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();

        // Setting up a supplier of raw ADS-B messages
        Supplier<RawMessage> messageSupplier = createMessageSupplier(startTime);

        // We're starting a new thread that continuously reads in raw ADS-B messages and adds them to our queue
        Thread messageThread = createMessageThread(messageSupplier);
        messageThread.setDaemon(true);
        messageThread.start();

        // We're starting an animation timer that periodically updates the aircraft states and the application status
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (messageQueue.isEmpty()) return;
                try {
                    for (int i = 0; i < 10; i++) {
                        if (messageQueue.peek() != null) {
                            Message message = MessageParser.parse(messageQueue.poll());

                            if (message != null) aircraftStateManager.updateWithMessage(message);
                            if (i == 9) aircraftStateManager.purge();
                            statusLineController.messageCountProperty().set(statusLineController.messageCountProperty().getValue() + 1);
                            statusLineController.aircraftCountProperty().set(aircraftStateManager.states().size());
                        }
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }.start();
    }

    private Supplier<RawMessage> createMessageSupplier(long startTime) {
        if (!getParameters().getRaw().isEmpty()) {
            try {
                File rawFile = new File(getParameters().getRaw().get(0));
                FileInputStream fileInputStream = new FileInputStream(rawFile);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                DataInputStream inputStream = new DataInputStream(bufferedInputStream);
                return () -> {
                    try {
                        if (inputStream.available() == 0) {
                            inputStream.close();
                            return null;
                        }
                        RawMessage currentMessage = readMessage(inputStream);
                        long currentTime = currentMessage.timeStampNs() - (System.nanoTime() - startTime);
                        if (currentTime >= 0) Thread.sleep(currentTime / TO_MILLISECONDS);
                        return currentMessage;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                };
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return () -> {
                try {
                    AdsbDemodulator adsbDemodulator = new AdsbDemodulator(System.in);
                    return adsbDemodulator.nextMessage();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };
        }
    }


    /**
     * This method creates a new thread for handling raw ADS-B messages.
     * It continuously reads messages from a supplier and adds them to a queue.
     *
     * @param messageSupplier the supplier of raw ADS-B messages
     * @return the new thread
     */
    private Thread createMessageThread(Supplier<RawMessage> messageSupplier) {
        return new Thread(() -> {
            while (true) {
                RawMessage message = messageSupplier.get();
                if (message == null) break;
                messageQueue.add(message);
            }
        });
    }
}
