package ch.epfl.javions.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class TestTileManager extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        new TileManager(Path.of("tile-cache"),
                "tile.openstreetmap.org")
                .imageForTileAt(new TileManager.TileId(17, 67927, 46357));
        Platform.exit();
    }

    @Test
    void testValidityTileId(){
        assertThrows(IllegalArgumentException.class, () -> new TileManager.TileId(1, 2, 1));
        assertThrows(IllegalArgumentException.class, () -> new TileManager.TileId(1, 1, 2));
        assertThrows(IllegalArgumentException.class, () -> new TileManager.TileId(2, 3, 4));
        assertThrows(IllegalArgumentException.class, () -> new TileManager.TileId(2, 4, 3));
        assertThrows(IllegalArgumentException.class, () -> new TileManager.TileId(-1, 4, 3));
        assertThrows(IllegalArgumentException.class, () -> new TileManager.TileId(2, -3, 3));
        assertThrows(IllegalArgumentException.class, () -> new TileManager.TileId(2, 2, -3));


        new TileManager.TileId(1, 1, 1);
        new TileManager.TileId(2, 3, 3);
    }
}