package ch.epfl.javions.gui;

import javafx.scene.paint.Color;

/**
 * Represents a color ramp that interpolates between a series of colors.
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public final class ColorRamp {
    private final Color[] colors;

    /**
     * Constructs a ColorRamp with the given colors.
     *
     * @param colors the colors of the ramp
     * @throws IllegalArgumentException if the number of colors is less than 2
     */
    public ColorRamp(Color... colors) {
        if (colors.length < 2) {
            throw new IllegalArgumentException("A color ramp must have at least two colors.");
        }
        this.colors = colors;
    }

    /**
     * Returns the color at the specified value in the range [0, 1].
     * If the value is less than 0, the first color of the ramp is returned.
     * If the value is greater than 1, the last color of the ramp is returned.
     * Otherwise, the color is interpolated between the adjacent colors of the ramp.
     *
     * @param value the value in the range [0, 1]
     * @return the interpolated color
     */
    public Color at(double value) {
        if (value < 0) {
            return colors[0];
        } else if (value > 1) {
            return colors[colors.length - 1];
        } else {
            double interval = 1.0 / (colors.length - 1);
            int index = (int) (value / interval);
            double percent = (value - index * interval) / interval;
            return colors[index].interpolate(colors[index + 1], percent);
        }
    }

    /**
     * A predefined ColorRamp with the "Plasma" color scheme.
     */
    public static final ColorRamp PLASMA = new ColorRamp(
            Color.valueOf("0x0d0887ff"), Color.valueOf("0x220690ff"),
            Color.valueOf("0x320597ff"), Color.valueOf("0x40049dff"),
            Color.valueOf("0x4e02a2ff"), Color.valueOf("0x5b01a5ff"),
            Color.valueOf("0x6800a8ff"), Color.valueOf("0x7501a8ff"),
            Color.valueOf("0x8104a7ff"), Color.valueOf("0x8d0ba5ff"),
            Color.valueOf("0x9814a0ff"), Color.valueOf("0xa31d9aff"),
            Color.valueOf("0xad2693ff"), Color.valueOf("0xb6308bff"),
            Color.valueOf("0xbf3984ff"), Color.valueOf("0xc7427cff"),
            Color.valueOf("0xcf4c74ff"), Color.valueOf("0xd6556dff"),
            Color.valueOf("0xdd5e66ff"), Color.valueOf("0xe3685fff"),
            Color.valueOf("0xe97258ff"), Color.valueOf("0xee7c51ff"),
            Color.valueOf("0xf3874aff"), Color.valueOf("0xf79243ff"),
            Color.valueOf("0xfa9d3bff"), Color.valueOf("0xfca935ff"),
            Color.valueOf("0xfdb52eff"), Color.valueOf("0xfdc229ff"),
            Color.valueOf("0xfccf25ff"), Color.valueOf("0xf9dd24ff"),
            Color.valueOf("0xf5eb27ff"), Color.valueOf("0xf0f921ff")
    );
}
