package ch.epfl.javions.gui;

public final class TileManager {



    public record TileId(int zoom, int x, int y) {

        private boolean isValid() {
            return zoom >= 0 && x >= 0 && y >= 0;
        }
    }
}
