package ch.epfl.javions.gui;

import javafx.scene.paint.Color;

public class ColorRampTest {
    public static void main(String[] args) {
        Color c1 = Color.valueOf("0xf5eb27ff");
        Color c2 = Color.valueOf("0xf0f921ff");
        Color c = c1.interpolate(c2, 0.2);
        System.out.println(c);
    }
}
