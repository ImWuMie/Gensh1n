package dev.undefinedteam.gclient.text;

import com.google.gson.JsonObject;
import dev.undefinedteam.gclient.Formatting;
import dev.undefinedteam.gensh1n.utils.misc.ISerializable0;

import java.util.Locale;
import java.util.Objects;

public class TextColor implements ISerializable0<TextColor> {
    private static final String RGB_PREFIX = "#";
    private final int rgb;
    private final String name;

    private TextColor(int rgb, String name) {
        this.rgb = rgb & 16777215;
        this.name = name;
    }

    private TextColor(int rgb) {
        this.rgb = rgb & 16777215;
        this.name = null;
    }

    public int getRgb() {
        return this.rgb;
    }

    public String getName() {
        return this.name != null ? this.name : this.getHexCode();
    }

    public final String getHexCode() {
        return String.format(Locale.ROOT, "#%06X", this.rgb);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            TextColor textColor = (TextColor) o;
            return this.rgb == textColor.rgb;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(this.rgb, this.name);
    }

    public String toString() {
        return this.getName();
    }

    public static TextColor fromFormatting(Formatting formatting) {
        return new TextColor(formatting.getColor(), formatting.name());
    }

    public static TextColor fromRgb(int rgb) {
        return new TextColor(rgb);
    }

    public net.minecraft.text.TextColor vanilla() {
        return net.minecraft.text.TextColor.fromRgb(this.rgb);
    }

    @Override
    public JsonObject toTag() {
        JsonObject tag = new JsonObject();
        tag.addProperty("name", getName());
        tag.addProperty("rgb", getRgb());
        return tag;
    }

    public static TextColor fromTag(JsonObject tag) {
        var name = tag.get("name").getAsString();
        var rgb = tag.get("rgb").getAsInt();
        return new TextColor(rgb, name);
    }
}
