package dev.undefinedteam.gclient.data;
import dev.undefinedteam.gclient.Formatting;

import java.awt.*;

public enum NameColor {
    WHITE(Color.WHITE,"white", Formatting.WHITE),
    BLACK(Color.BLACK,"black", Formatting.BLACK),

    GREEN(new Color(43520),"green",Formatting.DARK_GREEN),
    RED(new Color(16733525),"red",Formatting.RED),
    AQUA(new Color(5636095),"aqua",Formatting.AQUA);

    public final Color mColor;
    public final int mHex;
    public final String mColorName;
    public final Formatting formatting;

    NameColor(Color color, String color_name,Formatting formatting) {
        this.mColor = color;
        this.mHex = color.getRGB();
        this.mColorName = color_name;
        this.formatting = formatting;
    }

    @Override
    public String toString() {
        return mColorName.toUpperCase();
    }

    public static NameColor fromString(String string) {
        for (NameColor color : values()) {
            if (color.mColorName.equalsIgnoreCase(string)) {
                return color;
            }
        }
        return NameColor.WHITE;
    }
}
