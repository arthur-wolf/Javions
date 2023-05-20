package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import javafx.beans.property.*;

/**
 * Represents the parameters of a map. The properties can be observed and modified.
 * This class encapsulates properties of a map view such as zoom level and minimum X/Y coordinates.
 *
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public final class MapParameters {
    private final IntegerProperty zoom;
    private final DoubleProperty minX;
    private final DoubleProperty minY;
    private final int MINIMUM_ZOOM_LEVEL = 6;
    private final int MAXIMUM_ZOOM_LEVEL = 19;

    /**
     * Constructs a MapParameters with the specified initial zoom, initial minimum X value, and initial minimum Y value.
     *
     * @param initialZoom The initial zoom level.
     * @param initialMinX The initial minimum X value.
     * @param initialMinY The initial minimum Y value.
     * @throws IllegalArgumentException If the initialZoom is not in the range [6,19]
     */
    public MapParameters(int initialZoom, double initialMinX, double initialMinY) {
        if (initialZoom < MINIMUM_ZOOM_LEVEL || initialZoom > MAXIMUM_ZOOM_LEVEL)
            throw new IllegalArgumentException();

        zoom = new SimpleIntegerProperty(initialZoom);
        minX = new SimpleDoubleProperty(initialMinX);
        minY = new SimpleDoubleProperty(initialMinY);
    }

    /**
     * Returns the read-only zoom property
     *
     * @return The read-only zoom property
     */
    public ReadOnlyIntegerProperty zoomProperty() {
        return zoom;
    }

    /**
     * Returns the current zoom level.
     *
     * @return The current zoom level.
     */
    public int getZoom() {
        return zoom.get();
    }

    /**
     * Returns the read-only minimum X value property
     *
     * @return The read-only minimum X value property
     */
    public ReadOnlyDoubleProperty minXProperty() {
        return minX;
    }

    /**
     * Sets the minimum X value
     * @param minX The minimum X value
     */
    public void setMinX(double minX) {
        this.minX.set(minX);
    }

    /**
     * Returns the minimum X value
     *
     * @return The minimum X value
     */
    public double getMinX() {
        return minX.get();
    }

    /**
     * Returns the minimum Y value property
     *
     * @return The minimum Y value property
     */
    public ReadOnlyDoubleProperty minYProperty() {
        return minY;
    }

    /**
     * Sets the minimum Y value
     *
     * @param minY The minimum Y value
     */
    public void setMinY(double minY) {
        this.minY.set(minY);
    }

    /**
     * Returns the minimum Y value
     *
     * @return The minimum Y value
     */
    public double getMinY() {
        return minY.get();
    }

    /**
     * Translates the top-left corner of the displayed map portion by the given vector.
     *
     * @param x The horizontal component of the translation vector.
     * @param y The vertical component of the translation vector.
     */
    public void scroll(double x, double y) {
        double newMinX = minX.get() + x;
        double newMinY = minY.get() + y;
        minX.set(newMinX);
        minY.set(newMinY);
    }

    /**
     * Changes the zoom level by a given difference.
     *
     * @param zoomDifference The difference to add to the current zoom level.
     * @throws IllegalArgumentException If the new zoom level is not in the range [6,19]
     */
    public void changeZoomLevel(int zoomDifference) {
        int newZoom = Math2.clamp(MINIMUM_ZOOM_LEVEL, getZoom() + zoomDifference, MAXIMUM_ZOOM_LEVEL);
        if (newZoom != getZoom()) {
            final int SCALB_CONSTANT_ZOOM = 1;
            setMinX(getMinX() * Math.scalb(SCALB_CONSTANT_ZOOM, zoomDifference));
            setMinY(getMinY() * Math.scalb(SCALB_CONSTANT_ZOOM, zoomDifference));
            zoom.set(newZoom);
        }
    }

    /**
     * Sets the map parameters to the given ones.
     * This method is called when the map is moved.
     *
     * @param newMapParametersWhenMoving The new map parameters.
     */
    public void set(MapParameters newMapParametersWhenMoving) {
        zoom.set(newMapParametersWhenMoving.getZoom());
        minX.set(newMapParametersWhenMoving.getMinX());
        minY.set(newMapParametersWhenMoving.getMinY());
    }

}
