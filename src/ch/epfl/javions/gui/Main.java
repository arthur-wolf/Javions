package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the main class for the Javions application, which is a graphical
 * interface to display aircraft positions and movements using ADS-B messages.
 * The application uses JavaFX for its graphical user interface, and the
 * aircraft positions are displayed on a map provided by OpenStreetMap.
 * The class uses a number of other classes to manage aircraft states, parse
 * messages, manage map parameters, and manage the tile cache for the map.
 * The main entry points are the main() method, which launches the application,
 * and the start() method, which sets up the application's initial state.
 *
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public class Main extends javafx.application.Application {

    public static final String AIRCRAFT_DATABASE = "/aircraft.zip";
    public static final String TILE_SERVER = "tile.openstreetmap.org";
    public static final String TILE_CACHE = "tile-cache";
    public static final String APPLICATION_NAME = "Javions";
    public static final int MIN_WIDTH = 800;
    public static final int MIN_HEIGHT = 600;
    private final int INITIAL_ZOOM = 8;
    private final double INITIAL_LONGITUDE = 33530;
    private final double INITIAL_LATITUDE = 23070;



    /**
     * The main entry point for the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Reads all ADS-B messages from a file.
     *
     * @param fileName the name of the file to read from
     * @return a list of RawMessage containing all the read messages
     * @throws IOException if a reading error occurs
     */
    private static List<RawMessage> readAllMessages(String fileName) throws IOException {
        List<RawMessage> rawMessages = new ArrayList<>();
        try (DataInputStream s = new DataInputStream(
                new BufferedInputStream(new FileInputStream(fileName)))) {
            byte[] bytes = new byte[RawMessage.LENGTH];
            long timeStampNs;
            while (true) {
                timeStampNs = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                rawMessages.add(new RawMessage(timeStampNs, new ByteString(bytes)));
            }
        } catch (EOFException exception) {
            System.out.println("End of file reached.");
        }
        return rawMessages;
    }


    /**
     * Starts the application. This method is called after the init() method has returned,
     * and after the system is sufficiently initialized so that this method can use
     * main features of the JavaFX library.
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     * @throws Exception if an error occurs
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        Path tileCache = Path.of(TILE_CACHE);

        MapParameters mp = new MapParameters(INITIAL_ZOOM, INITIAL_LONGITUDE, INITIAL_LATITUDE);
        TileManager tm = new TileManager(tileCache, TILE_SERVER);
        BaseMapController bmc = new BaseMapController(tm, mp);

        URL dbUrl = getClass().getResource(AIRCRAFT_DATABASE);
        assert dbUrl != null;
        String f = Path.of(dbUrl.toURI()).toString();
        var db = new AircraftDatabase(f);
        AircraftStateManager asm = new AircraftStateManager(db);

        ObjectProperty<ObservableAircraftState> sap = new SimpleObjectProperty<>();
        AircraftController ac = new AircraftController(mp, asm.states(), sap);
        AircraftTableController atc = new AircraftTableController(asm.states(), sap);
        StatusLineController statusLineController = new StatusLineController();
        statusLineController.aircraftCountProperty().bind(Bindings.size(asm.states()));

        sap.addListener((q, o, n) -> {
            atc.setOnDoubleClick(event -> bmc.centerOn(event.getPosition()));
        });

        StackPane stackPane = new StackPane(bmc.pane(), ac.pane());
        BorderPane statusBar = new BorderPane(atc.pane(), statusLineController.pane(), null, null, null);
        SplitPane root = new SplitPane(stackPane, statusBar);
        root.setOrientation(Orientation.VERTICAL);

        BorderPane rootPane = new BorderPane();
        rootPane.setCenter(root);
        primaryStage.setScene(new Scene(rootPane));

        primaryStage.setScene(new Scene(new BorderPane(root, null, null, null, null)));
        primaryStage.setTitle(APPLICATION_NAME);
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.show();

        var mi = readAllMessages("resources/messages_20230318_0915.bin").iterator();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    for (int i = 0; i < 10; i += 1) {
                        Message m = MessageParser.parse(mi.next());
                        if (m != null) asm.updateWithMessage(m);
                        if (i == 9) asm.purge();
                    }
                    statusLineController.messageCountProperty().set(statusLineController.messageCountProperty().get() + 1);

                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }.start();
    }
}