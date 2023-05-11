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

    public static void main(String[] args) {
        launch(args);
    }

    static List<RawMessage> readAllMessages(String fileName) throws IOException {
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

        // Création des gestionnaires de données
        ObjectProperty<ObservableAircraftState> sap = new SimpleObjectProperty<>();
        AircraftController ac = new AircraftController(mp, asm.states(), sap);
        AircraftTableController atc = new AircraftTableController(asm.states(), sap);
        StatusLineController statusLineController = new StatusLineController();
        statusLineController.aircraftCountProperty().bind(Bindings.size(asm.states()));

        sap.addListener((q, o, n) -> atc.setOnDoubleClick(event -> bmc.centerOn(event.getPosition())));

        // Création du graphe de scène
        StackPane stackPane = new StackPane(bmc.pane(), ac.pane());
        BorderPane statusBar = new BorderPane(atc.pane(), statusLineController.pane(), null, null, null);
        SplitPane root = new SplitPane(stackPane, statusBar);
        root.setOrientation(Orientation.VERTICAL);

        primaryStage.setScene(new Scene(new BorderPane(root,  null, null, null, null)));
        primaryStage.setTitle(APPLICATION_NAME);
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.show();

        var mi = readAllMessages("resources/messages_20230318_0915.bin").iterator();

        // Animation des aéronefs
        new AnimationTimer() {
            @Override
            public void handle ( long now){
                try {
                    for (int i = 0; i < 10; i += 1) {
                        Message m = MessageParser.parse(mi.next());
                        if (m != null) asm.updateWithMessage(m);
                        if (i == 9) asm.purge();
                        statusLineController.messageCountProperty().set(statusLineController.messageCountProperty().get() + 1);
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }.start();
    }
}