package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

import javafx.animation.AnimationTimer;

/**
 * This is the main class for the Javions application.
 * It sets up the graphical user interface and starts the application.
 * It also handles the process of parsing incoming ADS-B messages and updating the displayed aircraft states.
 * This class employs multithreading and uses a thread-safe queue for storing the incoming messages.
 * A separate thread reads incoming messages and adds them to the queue.
 * The application periodically updates the aircraft states and the application status based on the messages in the queue.
 * The GUI includes a map for displaying aircraft, a table for displaying a list of all aircraft, and a status line for displaying application status.
 * The map and the status bar are displayed in a split pane. The user can select an aircraft in the table by clicking on it.
 * For a double-click, the map centers on the aircraft.
 *
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public class Main extends Application {
    private long lastPurge = System.nanoTime();

    /**
     * This method starts the application.
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * This method reads a raw ADS-B message from an input stream.
     * @param inputStream The input stream to read from.
     * @return The raw ADS-B message or null if the length of the read bytes does not match the expected length.
     * @throws IOException If an I/O error occurs.
     */
    private static RawMessage readMessage(DataInputStream inputStream) throws IOException {
        final int RAW_MESSAGE_LENGTH = RawMessage.LENGTH;
        byte[] bytes = new byte[RAW_MESSAGE_LENGTH];
        long timeStampNs = inputStream.readLong();
        int bytesRead = inputStream.readNBytes(bytes, 0, bytes.length);

        if (bytesRead == RAW_MESSAGE_LENGTH)
            return new RawMessage(timeStampNs, new ByteString(bytes));

        return null;
    }

    /**
     * This method initializes the JavaFX application.
     * It sets up the map, aircraft database, status line, and starts the message handling threads.
     * @param primaryStage The primary stage for this application, onto which the application scene is set.
     * @throws Exception If an error occurs during initialization.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        final int INITIAL_ZOOM = 8;
        final double INITIAL_LONGITUDE = 33530;
        final double INITIAL_LATITUDE = 23070;
        final String TILE_SERVER_ADDRESS = "tile.openstreetmap.org";
        final String TITLE = "Javions";
        final int MIN_WIDTH = 800;
        final int MIN_HEIGHT = 600;
        final ConcurrentLinkedQueue<RawMessage> messageQueue = new ConcurrentLinkedQueue<>();

        long startTime = System.nanoTime();
        Path tileCachePath = Path.of("tile-cache");
        URL dbUrl = getClass().getResource("/aircraft.zip");
        Objects.requireNonNull(dbUrl, "Database URL cannot be null");
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

        SimpleObjectProperty<ObservableAircraftState> selectedAircraftProperty = new SimpleObjectProperty<>();
        MapParameters mapParameters = new MapParameters(INITIAL_ZOOM, INITIAL_LONGITUDE, INITIAL_LATITUDE);
        TileManager tileManager = new TileManager(tileCachePath, TILE_SERVER_ADDRESS);
        BaseMapController baseMapController = new BaseMapController(tileManager, mapParameters);
        AircraftDatabase aircraftDatabase = new AircraftDatabase(dbFilePath);
        AircraftStateManager aircraftStateManager = new AircraftStateManager(aircraftDatabase);
        AircraftController aircraftController = new AircraftController(mapParameters, aircraftStateManager.states(), selectedAircraftProperty);
        AircraftTableController aircraftTableController = new AircraftTableController(aircraftStateManager.states(), selectedAircraftProperty);
        StatusLineController statusLineController = new StatusLineController();

        statusLineController.aircraftCountProperty().bind(Bindings.size(aircraftStateManager.states()));

        StackPane stackPane = new StackPane(baseMapController.pane(), aircraftController.pane());
        BorderPane statusBar = new BorderPane(aircraftTableController.pane(), statusLineController.pane(), null, null, null);
        SplitPane root = new SplitPane(stackPane, statusBar);

        aircraftTableController.setOnDoubleClick(event -> baseMapController.centerOn(event.getPosition()));

        root.setOrientation(javafx.geometry.Orientation.VERTICAL);
        primaryStage.setScene(new Scene(new BorderPane(root, null, null, null, null)));
        primaryStage.setTitle(TITLE);
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.show();

        Supplier<RawMessage> messageSupplier = createMessageSupplier(startTime);
        Thread messageThread = createMessageThread(messageSupplier, messageQueue);
        messageThread.setDaemon(true);
        messageThread.start();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                purgeAndUpdateStatesIfNeeded(now, aircraftStateManager, statusLineController,  messageQueue);
            }
        }.start();
    }

    /**
     * Purge old aircraft states and update with new messages if at least a second has passed since the last purge.
     * @param now Current time in nanoseconds.
     * @param aircraftStateManager The aircraft state manager responsible for managing aircraft states.
     * @param statusLineController The controller responsible for managing the status line.
     */
    private void purgeAndUpdateStatesIfNeeded(long now, AircraftStateManager aircraftStateManager, StatusLineController statusLineController,ConcurrentLinkedQueue<RawMessage> messageQueue) {
        final int ONE_SECOND_IN_NANO = 1_000_000_000;
        if (now - lastPurge > ONE_SECOND_IN_NANO) {
            aircraftStateManager.purge();
            lastPurge = now;
        }
        try {
            if (messageQueue.peek() != null) {
                Message parsedMessage = MessageParser.parse(messageQueue.poll());
                if (parsedMessage != null) {
                    aircraftStateManager.updateWithMessage(parsedMessage);
                    statusLineController.messageCountProperty().set(statusLineController.messageCountProperty().get() + 1);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * This method creates a new supplier of raw ADS-B messages based on the input source (file or System.in).
     *
     * @param startTime Start time of the application in nanoseconds.
     * @return The supplier of raw ADS-B messages.
     * @throws IOException If an I/O error occurs.
     */
    private Supplier<RawMessage> createMessageSupplier(long startTime) throws IOException {
        List<String> params = getParameters().getRaw();
        if (!params.isEmpty())
            return createFileSupplier(params.get(0), startTime);
        else {
            var adsbDemodulator = new AdsbDemodulator(System.in);
            return createSystemInSupplier(adsbDemodulator);
        }
    }

    /**
     * This method creates a new supplier of raw ADS-B messages from a file.
     * @param filePath Path to the file containing raw ADS-B messages.
     * @param startTime Start time of the application in nanoseconds.
     * @return The supplier of raw ADS-B messages.
     */
    private Supplier<RawMessage> createFileSupplier(String filePath, long startTime) {
        final long TO_MILLISECONDS = 1_000_000;
        try {
            File rawFile = new File(filePath);
            FileInputStream fileInputStream = new FileInputStream(rawFile);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            DataInputStream inputStream = new DataInputStream(bufferedInputStream);

            return () -> {
                try {
                    if (inputStream.available() == 0)
                        inputStream.close();
                    RawMessage currentMessage = readMessage(inputStream);
                    Objects.requireNonNull(currentMessage, "Current message cannot be null");
                    long currentTime = currentMessage.timeStampNs() - (System.nanoTime() - startTime);
                    if (currentTime >= 0)
                        Thread.sleep(currentTime / TO_MILLISECONDS);

                    return currentMessage;
                } catch (InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }
            };
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method creates a new supplier of raw ADS-B messages from System.in.
     * @return The supplier of raw ADS-B messages.
     */
    private Supplier<RawMessage> createSystemInSupplier(AdsbDemodulator adsbDemodulator) {
        return () -> {
            try {
                return adsbDemodulator.nextMessage();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * This method creates a new thread for handling raw ADS-B messages.
     * It continuously reads messages from a supplier and adds them to a queue.
     * @param messageSupplier The supplier of raw ADS-B messages.
     * @return The new thread.
     */
    private Thread createMessageThread(Supplier<RawMessage> messageSupplier, ConcurrentLinkedQueue<RawMessage> messageQueue) {
        return new Thread(() -> {
            while (true) {
                RawMessage message = messageSupplier.get();
                if (message == null) break;
                messageQueue.add(message);
            }
        });
    }
}
