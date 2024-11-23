package dev.undefinedteam.gensh1n.utils.render;

import dev.undefinedteam.gensh1n.utils.render.color.Color;
import icyllis.arc3d.core.MathUtil;

public class ColorUtils {
    public static Color blend(Color color1, Color color2, double ratio) {
        float r = (float) ratio;
        float ir = 1.0f - r;
        float[] rgb1 = new float[3];
        float[] rgb2 = new float[3];
        color1.awt().getColorComponents(rgb1);
        color2.awt().getColorComponents(rgb2);
        return new Color(rgb1[0] * r + rgb2[0] * ir, rgb1[1] * r + rgb2[1] * ir, rgb1[2] * r + rgb2[2] * ir, 1.0f);
    }

    public static Color blend(Color color1, Color color2) {
        return blend(color1, color2, 0.5);
    }

    public static java.awt.Color rainbow(int delay, boolean b) {
        double rainbowState = Math.ceil((double) (System.currentTimeMillis() + (long) delay) / 20.0D);
        rainbowState %= 360.0D;
        return java.awt.Color.getHSBColor((float) (rainbowState / 360.0D), 0.8F, 0.7F).brighter();
    }

    public static int rainbow(int speed, int index, float saturation, float brightness, float opacity) {
        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
        float hue = angle / 360f;
        return applyOpacitys(java.awt.Color.HSBtoRGB(hue, saturation, brightness), opacity);
    }


    public static int applyOpacitys(int hex, float opacity) {
        return applyOpacity(hex, (int) (opacity * 255));
    }

    public static int applyOpacity(int hex, int opacity) {
        opacity = MathUtil.clamp(opacity, 0, 255);
        return color(red(hex), green(hex), blue(hex), opacity);
    }

    public static int alpha(int hex) {
        return (hex >> 24) & 0xFF;
    }

    public static int red(int hex) {
        return (hex >> 16) & 0xFF;
    }

    public static int green(int hex) {
        return (hex >> 8) & 0xFF;
    }

    public static int blue(int hex) {
        return hex & 0xFF;
    }
    public static int color(int r, int g, int b, int a) {
        return ((a & 0xFF) << 24) |
            ((r & 0xFF) << 16) |
            ((g & 0xFF) << 8) |
            (b & 0xFF);
    }
}
