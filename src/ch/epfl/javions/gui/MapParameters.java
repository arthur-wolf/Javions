package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import javafx.beans.property.*;

/**
 * A class representing the parameters of a map
 * The properties can be observed and modified
 *
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */

/// TODO: 23/04/2023 JavaDoc
public final class MapParameters {

    private final IntegerProperty zoom;
    private final DoubleProperty minX;
    private final DoubleProperty minY;
    private final static int MINIMUM_ZOOM_LEVEL = 6;
    private final static int MAXIMUM_ZOOM_LEVEL = 19;

    /**
     * Creates a new MapParameters instance
     *
     * @param initialZoom the initial zoom level
     * @param initialMinX the initial minimum X value
     * @param initialMinY the initial minimum Y value
     * @throws IllegalArgumentException if the initialZoom is not in the range [6,19]
     */
    public MapParameters(int initialZoom, double initialMinX, double initialMinY) {
        if (initialZoom < MINIMUM_ZOOM_LEVEL || initialZoom > MAXIMUM_ZOOM_LEVEL) {
            throw new IllegalArgumentException();
        }
        zoom = new SimpleIntegerProperty(initialZoom);
        minX = new SimpleDoubleProperty(initialMinX);
        minY = new SimpleDoubleProperty(initialMinY);
    }


    /**
     * Returns the zoom property
     *
     * @return the zoom property
     */
    public ReadOnlyIntegerProperty zoomProperty() {
        return zoom;
    }

    /**
     * Returns the zoom
     *
     * @return the zoom
     */
    public int getZoom() {
        return zoom.get();
    }


    /**
     * Returns the minimum X value property
     *
     * @return the minimum X value property
     */
    public ReadOnlyDoubleProperty minXProperty() {
        return minX;
    }

    /**
     * Returns the minimum X value
     *
     * @return the minimum X value
     */
    public double getMinX() {
        return minX.get();
    }

    /**
     * Returns the minimum Y value property
     *
     * @return the minimum Y value property
     */
    public ReadOnlyDoubleProperty minYProperty() {
        return minY;
    }

    /**
     * Returns the minimum Y value
     *
     * @return the minimum Y value
     */
    public double getMinY() {
        return minY.get();
    }
    /**
     * Translates the top-left corner of the displayed map portion by the given vector
     *
     * @param x the horizontal component of the translation vector
     * @param y the vertical component of the translation vector
     */
    public void scroll(double x, double y) {
        double zoomLevel = zoom.get();
        double scale = Math.pow(2, zoomLevel - 8);
        double deltaX = x / scale;
        double deltaY = y / scale;
        double newMinX = minX.get() - deltaX;
        double newMinY = minY.get() - deltaY;
        minX.set(newMinX);
        minY.set(newMinY);
    }
    /**
     * Changes the zoom level by a given difference
     *
     * @param zoomDifference the difference to add to the current zoom level
     * @throws IllegalArgumentException if the new zoom level is not in the range [6,19]
     */
    public int changeZoomLevel(int zoomDifference) {
        int newZoom = Math2.clamp(MINIMUM_ZOOM_LEVEL, getZoom() + zoomDifference, MAXIMUM_ZOOM_LEVEL);
        zoom.set(newZoom);
        return newZoom;
    }


    public void set(MapParameters newMapParametersWhenMoving) {
        zoom.set(newMapParametersWhenMoving.getZoom());
        minX.set(newMapParametersWhenMoving.getMinX());
        minY.set(newMapParametersWhenMoving.getMinY());
    }

}
