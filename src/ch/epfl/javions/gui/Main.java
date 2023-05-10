package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import javafx.animation.AnimationTimer;
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
import java.util.ArrayList;
import java.util.List;

public class Main extends javafx.application.Application {

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
        }
        return rawMessages;
    }
    @Override
    public void start(Stage primaryStage) throws Exception {


        Path tileCache = Path.of("tile-cache");

        MapParameters mp = new MapParameters(INITIAL_ZOOM, INITIAL_LONGITUDE, INITIAL_LATITUDE);
        TileManager tm = new TileManager(tileCache, "tile.openstreetmap.org");
        BaseMapController bmc = new BaseMapController(tm, mp);

        URL dbUrl = getClass().getResource("/aircraft.zip");
        assert dbUrl != null;
        String f = Path.of(dbUrl.toURI()).toString();
        var db = new AircraftDatabase(f);
        AircraftStateManager asm = new AircraftStateManager(db);

        // Création des gestionnaires de données
        AircraftStateManager stateManager = new AircraftStateManager(db);
        ObjectProperty<ObservableAircraftState> sap = new SimpleObjectProperty<>();
        AircraftController ac = new AircraftController(mp, asm.states(), sap);
        AircraftTableController atc = new AircraftTableController(asm.states(), sap);
        StatusLineController statusLineController = new StatusLineController();


        // Création du graphe de scène
        StackPane stackPane = new StackPane(bmc.pane(), ac.pane());
        BorderPane statusBar = new BorderPane(atc.pane(), statusLineController.pane(), null, null, null);
        SplitPane root = new SplitPane(stackPane, statusBar);
        root.setOrientation(javafx.geometry.Orientation.VERTICAL);

        primaryStage.setScene(new Scene(new BorderPane(root,  null, null, null, null)));
        primaryStage.setTitle("Javions");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
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
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }.start();
    }

}