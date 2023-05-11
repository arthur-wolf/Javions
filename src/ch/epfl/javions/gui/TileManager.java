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
 * TileManager Class
 * Represents an OSM tile manager.
 * Its role is to get tiles from a tile server and store them in a memory cache and a disk cache.
 *
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */

public final class TileManager {

    //Memory cache array with access-order
    private final LinkedHashMap<TileId, Image> memoryCache;

    //Path to the memory disk folder
    private final Path pathToMemoryDisk;

    //Name of the tile server where are stored tile images
    private final String tileServerName;

    //Memory cache capacity
    private static final int CACHE_CAPACITY = 100;
    private static final String IMAGE_EXTENSION_FORMAT = ".png";
    private static final String USER_AGENT_NAME = "Javions";
    private static final String PROTOCOL_NAME = "https";
    private static final int PORT_NUMBER = 443;
    private static final String URL_DELIMITER = "/";
    private static final int TIMEOUT_IN_MS = 5000;
    private static final float LOAD_FACTOR = 0.75f;

    /**
     * Constructor of TileManager
     *
     * @param pathFolder  path of the disk cache
     * @param tileServerN name of the tile server
     */

    public TileManager(Path pathFolder, String tileServerN) {
        pathToMemoryDisk = pathFolder;
        tileServerName = tileServerN;

        memoryCache = new LinkedHashMap<>(CACHE_CAPACITY,
                LOAD_FACTOR, true);
    }

    /**
     * Add a pair of tileId and its corresponding image to the memory cache
     * If the memory cache is at maximum capacity, we remove the first pair
     *
     * @param tileIdentity identity of the tile associated to the image
     * @param image        image of the tile
     */

    private void addToCache(TileId tileIdentity, Image image) {
        if (memoryCache.size() >= CACHE_CAPACITY) {
            //Complexity is 0(1)
            memoryCache.remove(memoryCache.entrySet().iterator().next().getKey());
        }
        memoryCache.put(tileIdentity, image);
    }

    /**
     * Returns true if the memory cache contains the image corresponding to the given tile identity
     *
     * @param tileIdentity identity of the tile
     * @return if the memory cache contains the image of this tile
     */

    private boolean cacheContains(TileId tileIdentity) {
        return memoryCache.containsKey(tileIdentity);
    }

    /**
     * Returns the image associated to a tile identity
     *
     * @param tileIdentity identity of the tile
     * @return the image associated to the tile identity
     * @throws IOException if something went wrong while getting the corresponding image from the tile server
     */

    public Image imageForTileAt(TileId tileIdentity) throws IOException {
        //Look in memory cache first
        if (cacheContains(tileIdentity)) {
            return memoryCache.get(tileIdentity);
        } else {
            //Look in memory disk
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
     * TileId Record
     * Represents the identity of an OSM Tile
     *
     * @param zoomLevel level of zoom
     * @param indexX    index x of the tile
     * @param indexY    index y of the tile
     */

    public record TileId(int zoomLevel, int indexX, int indexY) {

        /**
         * Check if tile is valid at construction
         *
         * @throws IllegalArgumentException if the tile parameters aren't valid
         */

        public TileId {
            Preconditions.checkArgument(isValid(zoomLevel, indexX, indexY));
        }

        /**
         * Checks if the parameters are a valid tile identity
         *
         * @param zoomLevel level of zoom of the tile to check
         * @param indexX    index x of the tile to check
         * @param indexY    index y of the tile to check
         * @return if the tile is valid or not
         */

        public static boolean isValid(int zoomLevel, int indexX, int indexY) {
            //No restrictions concerning the zoom level (can be greater than 20) in TileId,
            //Will be restricted in the gui.
            double maxIndex_X_and_Y = Math.pow(2, zoomLevel);
            return ((indexX + 1) <= maxIndex_X_and_Y
                    && (indexY + 1) <= maxIndex_X_and_Y
                    && zoomLevel >= 0 && indexX >= 0
                    && indexY >= 0);
        }
    }
}