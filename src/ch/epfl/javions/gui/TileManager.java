package ch.epfl.javions.gui;

import ch.epfl.javions.Preconditions;
import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;

/**
 * Represents an OpenStreetMap (OSM) tile manager. Its role is to get tiles from a tile server
 * and store them in a memory cache and a disk cache. It manages fetching and caching of map tiles
 * with some degree of thread safety.
 *
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */

public final class TileManager {

    //Memory cache array with access-order
    private final LinkedHashMap<TileId, Image> memoryCache;
    private final Path pathToMemoryDisk;
    private final String tileServerName;
    private static final int CACHE_CAPACITY = 100;

    /**
     * Constructs a TileManager with the specified disk cache path and tile server name.
     *
     * @param pathFolder  The path of the disk cache.
     * @param tileServerN The name of the tile server.
     */
    public TileManager(Path pathFolder, String tileServerN) {
        final float LOAD_FACTOR = 0.75f;
        pathToMemoryDisk = pathFolder;
        tileServerName = tileServerN;

        memoryCache = new LinkedHashMap<>(CACHE_CAPACITY, LOAD_FACTOR, true);
    }

    /**
     * Adds a pair of tile ID and its corresponding image to the memory cache.
     * If the memory cache is at maximum capacity, removes the least recently used pair.
     *
     * @param tileIdentity The ID of the tile associated with the image.
     * @param image        The image of the tile.
     */
    private void addToCache(TileId tileIdentity, Image image) {
        if (memoryCache.size() >= CACHE_CAPACITY)
            memoryCache.remove(memoryCache.entrySet().iterator().next().getKey());

        memoryCache.put(tileIdentity, image);
    }

    /**
     * Returns true if the memory cache contains the image corresponding to the given tile ID.
     *
     * @param tileIdentity The ID of the tile.
     * @return True if the memory cache contains the image of this tile; false otherwise.
     */
    private boolean cacheContains(TileId tileIdentity) {
        return memoryCache.containsKey(tileIdentity);
    }

    /**
     * Returns the image associated with a tile ID.
     * This method first looks in the memory cache, then in the disk cache, and finally on the tile server.
     *
     * @param tileIdentity The ID of the tile.
     * @return The image associated with the tile ID.
     * @throws IOException If an error occurs while getting the image from the tile server.
     */
    public Image imageForTileAt(TileId tileIdentity) throws IOException {
        //Look in memory cache first
        if (cacheContains(tileIdentity))
            return memoryCache.get(tileIdentity);
        else {
            //Look in memory disk
            final String IMAGE_EXTENSION_FORMAT = ".png";
            Path pathToFile = pathToMemoryDisk.resolve(String.valueOf(tileIdentity.zoomLevel))
                    .resolve(String.valueOf(tileIdentity.indexX))
                    .resolve(tileIdentity.indexY + IMAGE_EXTENSION_FORMAT);

            if (Files.exists(pathToFile)) {
                //We load the image and place it in the memory cache
                Image imageTile = new Image(pathToFile.toUri().toString());
                addToCache(tileIdentity, imageTile);
                return imageTile;
            } else {
                //We are going to load the image from the tile server
                final String PROTOCOL_NAME = "https";
                final String USER_AGENT_NAME = "Javions";
                final String URL_DELIMITER = "/";
                final int PORT_NUMBER = 443;
                final int TIMEOUT_IN_MS = 5000;

                URL u = new URL(PROTOCOL_NAME, tileServerName, PORT_NUMBER, URL_DELIMITER + tileIdentity.zoomLevel
                        + URL_DELIMITER + tileIdentity.indexX + URL_DELIMITER
                        + tileIdentity.indexY + IMAGE_EXTENSION_FORMAT);

                URLConnection c = u.openConnection();
                c.setRequestProperty("User-Agent", USER_AGENT_NAME);
                //5 seconds timeout in case something went wrong with url / the server isn't reachable
                c.setConnectTimeout(TIMEOUT_IN_MS);

                try (InputStream i = c.getInputStream()) {
                    //Create the directory in cache folder /zoomLevel/xIndex/
                    Files.createDirectories(pathToFile.getParent());

                    try (OutputStream outStream = new FileOutputStream(pathToFile.toFile())) {
                        //Save image to file
                        i.transferTo(outStream);

                        Image imageTile = new Image(pathToFile.toUri().toString());

                        //Save image in memory cache
                        addToCache(tileIdentity, imageTile);

                        return imageTile;
                    }
                }
            }
        }
    }

    /**
     * Represents the ID of an OSM Tile.
     *
     * @param zoomLevel The zoom level of the tile.
     * @param indexX    The x-index of the tile.
     * @param indexY    The y-index of the tile.
     */
    public record TileId(int zoomLevel, int indexX, int indexY) {

        /**
         * Constructs a TileId with the specified zoom level, x-index, and y-index.
         * Checks if tile parameters are valid.
         *
         * @throws IllegalArgumentException If the tile parameters aren't valid.
         */
        public TileId {
            Preconditions.checkArgument(isValid(zoomLevel, indexX, indexY));
        }

        /**
         * Checks if the specified parameters constitute a valid tile ID.
         *
         * @param zoomLevel The zoom level of the tile to check.
         * @param indexX    The x-index of the tile to check.
         * @param indexY    The y-index of the tile to check.
         * @return True if the parameters form a valid tile ID; false otherwise.
         */
        public static boolean isValid(int zoomLevel, int indexX, int indexY) {
            //No restrictions concerning the zoom level in TileId, will be restricted in the gui.
            double maxIndex_X_and_Y = Math.pow(2, zoomLevel);
            return ((indexX + 1) <= maxIndex_X_and_Y
                    && (indexY + 1) <= maxIndex_X_and_Y
                    && zoomLevel >= 0
                    && indexX >= 0
                    && indexY >= 0);
        }
    }
}