package xyz.qalcyo.corgal.utils;

public class ColorUtils {
    /**
     * @return The red value of the provided RGBA value.
     */
    public static int getRed(int rgba) {
        return (rgba >> 16) & 0xFF;
    }

    /**
     * @return The green value of the provided RGBA value.
     */
    public static int getGreen(int rgba) {
        return (rgba >> 8) & 0xFF;
    }

    /**
     * @return The blue value of the provided RGBA value.
     */
    public static int getBlue(int rgba) {
        return (rgba) & 0xFF;
    }
}
