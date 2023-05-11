package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.WebMercator;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * BaseMapController class
 * Manages the display and interaction with the background map.
 *
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public final class BaseMapController {

    private final TileManager tileManager;
    private final MapParameters mapParameters;
    private final Pane pane;
    private final Canvas canvas;

    // Will be true if redraw is needed
    private boolean redrawNeeded;

    // Size of an OSM Tile in pixels
    private final static int OSM_TILE_SIZE = 256;


    /**
     * BaseMapManager constructor
     *
     * @param tileM     TileManager to get the tiles from the map
     * @param mapParams MapParameters to get the parameters of the map
     */

    public BaseMapController(TileManager tileM, MapParameters mapParams) {
        tileManager = tileM;
        mapParameters = mapParams;

        canvas = new Canvas();
        pane = new Pane();

        pane.getChildren().add(canvas);

        installHandlers();
        installBindings();
        installListeners();

        // Initial draw
        redrawOnNextPulse();
    }

    /**
     * Returns the pane displaying the background map
     *
     * @return Pane
     */

    public Pane pane() {
        return pane;
    }


    /**
     * Moves the visible portion of the map so that it is centred at this point (e.g. centring the map on a particular aircraft)
     *
     * @param point a point on the Earth's surface, of type GeoPos
     */
   public void centerOn(GeoPos point){
       mapParameters.scroll(
               WebMercator.x(mapParameters.getZoom(), point.longitude()) - mapParameters.getMinX() - canvas.getWidth() / 2,
               WebMercator.y(mapParameters.getZoom(),point.latitude())- mapParameters.getMinY() - canvas.getHeight() / 2);
    }


    private void draw() {
        // First clear the graphic context
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        MapParameters mapParameters = this.mapParameters;

        // Index of the top left tile
        int topLeftXIndexTile = (int) (mapParameters.getMinX() / OSM_TILE_SIZE);
        int topLeftYIndexTile = (int) (mapParameters.getMinY() / OSM_TILE_SIZE);

        // Index of the bottom right tile
        int bottomRightXIndexTile = (int) ((mapParameters.getMinX() + canvas.getWidth()) / OSM_TILE_SIZE);
        int bottomRightYIndexTile = (int) ((mapParameters.getMinY() + canvas.getHeight()) / OSM_TILE_SIZE);

        for (int xTileMap = topLeftXIndexTile; xTileMap <= bottomRightXIndexTile; xTileMap++) {
            for (int yTileMap = topLeftYIndexTile; yTileMap <= bottomRightYIndexTile; yTileMap++) {
                try {
                    if (TileManager.TileId.isValid(mapParameters.getZoom(), xTileMap, yTileMap)) {
                        // Get the image corresponding to each tile displayed (at least partially) on the map portion
                        Image image = tileManager.imageForTileAt(new TileManager.TileId(mapParameters.getZoom(),
                                xTileMap,
                                yTileMap));

                        // Draw the image to the corresponding position using the topLeft point
                        graphicsContext.drawImage(image,
                                xTileMap * OSM_TILE_SIZE - mapParameters.getMinX(),
                                yTileMap * OSM_TILE_SIZE - mapParameters.getMinY());
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    /**
     * If the windows properties changed, redraw on next pulse
     */
    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        draw();
    }

    /**
     * Redraws the map on the next pulse
     */
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    /**
     * From a delta Point2D vector and the current MapParameters object,
     * creates the new MapParameters when moving.
     *
     * @param delta            delta of the mouse movement
     * @param oldMapParameters current MapParameters
     * @return new MapParameters when moving
     */

    private MapParameters newMapParametersWhenMoving(Point2D delta, MapParameters oldMapParameters) {
        return new MapParameters(
                oldMapParameters.getZoom(),
                oldMapParameters.getMinX() - delta.getX(),
                oldMapParameters.getMinY() - delta.getY()
        );
    }

    /**
     * Installs the handlers for the map
     */
    //installs the handlers in the constructor
    private void installHandlers() {
        ObjectProperty<Point2D> mouseCoordinatesProperty = new SimpleObjectProperty<>(Point2D.ZERO);
        LongProperty minScrollTime = new SimpleLongProperty();


        // Event handler for movement of the map
        pane.setOnMousePressed(event -> mouseCoordinatesProperty.setValue(
                new Point2D(event.getX(), event.getY())));


        pane.setOnMouseDragged(event -> {
            Point2D delta = new Point2D(event.getX(), event.getY()).subtract(mouseCoordinatesProperty.get());

            mapParameters.set(newMapParametersWhenMoving(delta, mapParameters));

            mouseCoordinatesProperty.setValue(new Point2D(event.getX(), event.getY()));
        });

        // If the mouse did not move since press, create waypoint
        pane.setOnMouseReleased(event -> {
            Point2D delta = new Point2D(event.getX(), event.getY()).subtract(mouseCoordinatesProperty.get());
            mapParameters.set(newMapParametersWhenMoving(delta, mapParameters));
        });

        // Even handler for zooming in and out by scrolling

        pane.setOnScroll(e -> {
            int zoomDelta = (int) Math.signum(e.getDeltaY());
            if (zoomDelta == 0) return;

            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);


            double oldX = e.getX();
            double oldY = e.getY();

            mapParameters.scroll(oldX, oldY);
            mapParameters.changeZoomLevel(zoomDelta);
            mapParameters.scroll(-oldX, -oldY);

        });
    }

    /**
     * Installs the bindings for the map
     */
    private void installBindings() {
        // When the pane width or height will change -> canvas width and height will update accordingly
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
    }


    /**
     * Installs the listeners on the canvas and the MapParameters
     */
    private void installListeners() {
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        // If the height or the width of the canvas change -> redraw
        canvas.widthProperty().addListener((p, o, n) -> redrawOnNextPulse());
        canvas.heightProperty().addListener((p, o, n) -> redrawOnNextPulse());

        // When map properties are changed, redraw
        mapParameters.minXProperty().addListener((p, o, n) -> redrawOnNextPulse());
        mapParameters.minYProperty().addListener((p, o, n) -> redrawOnNextPulse());

    }


}