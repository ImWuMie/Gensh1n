package dev.undefinedteam.gclient.text;

import com.google.gson.JsonObject;
import dev.undefinedteam.gclient.Formatting;
import dev.undefinedteam.gensh1n.utils.misc.ISerializable0;

import java.util.Objects;
import java.util.Optional;

public class Style implements ISerializable0<Style> {
    public static final Style EMPTY = new Style(null, null, null, null, null, null, null, null);

    final TextColor color;

    final Boolean bold;

    final Boolean italic;

    final Boolean underlined;

    final Boolean strikethrough;

    final Boolean obfuscated;

    final ClickEvent clickEvent;

    final HoverEvent hoverEvent;

    private Style(TextColor color, Boolean bold ,Boolean italic, Boolean underlined, Boolean strikethrough ,Boolean obfuscated, ClickEvent clickEvent, HoverEvent hoverEvent) {
        this.color = color;
        this.bold = bold;
        this.italic = italic;
        this.underlined = underlined;
        this.strikethrough = strikethrough;
        this.obfuscated = obfuscated;
        this.clickEvent = clickEvent;
        this.hoverEvent = hoverEvent;
    }

    public net.minecraft.text.Style vanilla() {
        var style = net.minecraft.text.Style.EMPTY;
        if (this.color != null) style = style.withColor(this.color.vanilla());
        if (this.bold != null) style = style.withBold(this.bold);
        if (this.italic != null) style = style.withItalic(this.italic);
        if (this.underlined != null) style = style.withUnderline(this.underlined);
        if (this.strikethrough != null) style = style.withStrikethrough(this.strikethrough);
        if (this.obfuscated != null) style = style.withObfuscated(this.obfuscated);
        if (this.clickEvent != null) style = style.withClickEvent(this.clickEvent.vanilla());
        if (this.hoverEvent != null) style = style.withHoverEvent(this.hoverEvent.vanilla());
        return style;
    }

    public TextColor getColor() {
        return this.color;
    }

    public boolean isBold() {
        return this.bold == Boolean.TRUE;
    }

    public boolean isItalic() {
        return this.italic == Boolean.TRUE;
    }

    public boolean isStrikethrough() {
        return this.strikethrough == Boolean.TRUE;
    }

    public boolean isUnderlined() {
        return this.underlined == Boolean.TRUE;
    }

    public boolean isObfuscated() {
        return this.obfuscated == Boolean.TRUE;
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }


    public ClickEvent getClickEvent() {
        return this.clickEvent;
    }


    public HoverEvent getHoverEvent() {
        return this.hoverEvent;
    }

    private static <T> Style with(Style newStyle, T oldAttribute, T newAttribute) {
        return oldAttribute != null && newAttribute == null && newStyle.equals(EMPTY) ? EMPTY : newStyle;
    }

    public Style withColor(TextColor color) {
        return Objects.equals(this.color, color) ? this : with(new Style(color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent), this.color, color);
    }

    public Style withColor(Formatting color) {
        return this.withColor(color != null ? TextColor.fromFormatting(color) : null);
    }

    public Style withColor(int rgbColor) {
        return this.withColor(TextColor.fromRgb(rgbColor));
    }

    public Style withBold(Boolean bold) {
        return Objects.equals(this.bold, bold) ? this : with(new Style(this.color, bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent), this.bold, bold);
    }

    public Style withItalic(Boolean italic) {
        return Objects.equals(this.italic, italic) ? this : with(new Style(this.color, this.bold, italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent), this.italic, italic);
    }

    public Style withUnderline(Boolean underline) {
        return Objects.equals(this.underlined, underline) ? this : with(new Style(this.color, this.bold, this.italic, underline, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent), this.underlined, underline);
    }

    public Style withStrikethrough(Boolean strikethrough) {
        return Objects.equals(this.strikethrough, strikethrough) ? this : with(new Style(this.color, this.bold, this.italic, this.underlined, strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent), this.strikethrough, strikethrough);
    }

    public Style withObfuscated(Boolean obfuscated) {
        return Objects.equals(this.obfuscated, obfuscated) ? this : with(new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, obfuscated, this.clickEvent, this.hoverEvent), this.obfuscated, obfuscated);
    }

    public Style withClickEvent(ClickEvent clickEvent) {
        return Objects.equals(this.clickEvent, clickEvent) ? this : with(new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, clickEvent, this.hoverEvent), this.clickEvent, clickEvent);
    }

    public Style withHoverEvent(HoverEvent hoverEvent) {
        return Objects.equals(this.hoverEvent, hoverEvent) ? this : with(new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, hoverEvent), this.hoverEvent, hoverEvent);
    }


    public Style withFormatting(Formatting formatting) {
        TextColor textColor = this.color;
        Boolean boolean_ = this.bold;
        Boolean boolean2 = this.italic;
        Boolean boolean3 = this.strikethrough;
        Boolean boolean4 = this.underlined;
        Boolean boolean5 = this.obfuscated;
        switch (formatting) {
            case OBFUSCATED -> boolean5 = true;
            case BOLD -> boolean_ = true;
            case STRIKETHROUGH -> boolean3 = true;
            case UNDERLINE -> boolean4 = true;
            case ITALIC -> boolean2 = true;
            case RESET -> {
                return EMPTY;
            }
            default -> textColor = TextColor.fromFormatting(formatting);
        }

        return new Style(textColor, boolean_, boolean2, boolean4, boolean3, boolean5, this.clickEvent, this.hoverEvent);
    }

    public Style withExclusiveFormatting(Formatting formatting) {
        TextColor textColor = this.color;
        Boolean boolean_ = this.bold;
        Boolean boolean2 = this.italic;
        Boolean boolean3 = this.strikethrough;
        Boolean boolean4 = this.underlined;
        Boolean boolean5 = this.obfuscated;
        switch (formatting) {
            case OBFUSCATED:
                boolean5 = true;
                break;
            case BOLD:
                boolean_ = true;
                break;
            case STRIKETHROUGH:
                boolean3 = true;
                break;
            case UNDERLINE:
                boolean4 = true;
                break;
            case ITALIC:
                boolean2 = true;
                break;
            case RESET:
                return EMPTY;
            default:
                boolean5 = false;
                boolean_ = false;
                boolean3 = false;
                boolean4 = false;
                boolean2 = false;
                textColor = TextColor.fromFormatting(formatting);
        }

        return new Style(textColor, boolean_, boolean2, boolean4, boolean3, boolean5, this.clickEvent, this.hoverEvent);
    }

    public Style withFormatting(Formatting... formattings) {
        TextColor textColor = this.color;
        Boolean boolean_ = this.bold;
        Boolean boolean2 = this.italic;
        Boolean boolean3 = this.strikethrough;
        Boolean boolean4 = this.underlined;
        Boolean boolean5 = this.obfuscated;
        Formatting[] var8 = formattings;
        int var9 = formattings.length;

        for (int var10 = 0; var10 < var9; ++var10) {
            Formatting formatting = var8[var10];
            switch (formatting) {
                case OBFUSCATED:
                    boolean5 = true;
                    break;
                case BOLD:
                    boolean_ = true;
                    break;
                case STRIKETHROUGH:
                    boolean3 = true;
                    break;
                case UNDERLINE:
                    boolean4 = true;
                    break;
                case ITALIC:
                    boolean2 = true;
                    break;
                case RESET:
                    return EMPTY;
                default:
                    textColor = TextColor.fromFormatting(formatting);
            }
        }

        return new Style(textColor, boolean_, boolean2, boolean4, boolean3, boolean5, this.clickEvent, this.hoverEvent);
    }

    public Style withParent(Style parent) {
        if (this == EMPTY) {
            return parent;
        } else {
            return parent == EMPTY ? this : new Style(this.color != null ? this.color : parent.color, this.bold != null ? this.bold : parent.bold, this.italic != null ? this.italic : parent.italic, this.underlined != null ? this.underlined : parent.underlined, this.strikethrough != null ? this.strikethrough : parent.strikethrough, this.obfuscated != null ? this.obfuscated : parent.obfuscated, this.clickEvent != null ? this.clickEvent : parent.clickEvent, this.hoverEvent != null ? this.hoverEvent : parent.hoverEvent != null ? parent.hoverEvent : null);
        }
    }

    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder("{");

        class Writer {
            private boolean shouldAppendComma;

            Writer(final Style style) {
            }

            private void appendComma() {
                if (this.shouldAppendComma) {
                    stringBuilder.append(',');
                }

                this.shouldAppendComma = true;
            }

            void append(String key, Boolean value) {
                if (value != null) {
                    this.appendComma();
                    if (!value) {
                        stringBuilder.append('!');
                    }

                    stringBuilder.append(key);
                }

            }

            void append(String key, Object value) {
                if (value != null) {
                    this.appendComma();
                    stringBuilder.append(key);
                    stringBuilder.append('=');
                    stringBuilder.append(value);
                }

            }
        }

        Writer writer = new Writer(this);
        writer.append("color", this.color);
        writer.append("bold", this.bold);
        writer.append("italic", this.italic);
        writer.append("underlined", this.underlined);
        writer.append("strikethrough", this.strikethrough);
        writer.append("obfuscated", this.obfuscated);
        writer.append("clickEvent", this.clickEvent);
        writer.append("hoverEvent", this.hoverEvent);
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof Style style)) {
            return false;
        } else {
            return this.bold == style.bold && Objects.equals(this.getColor(), style.getColor()) && this.italic == style.italic && this.obfuscated == style.obfuscated && this.strikethrough == style.strikethrough && this.underlined == style.underlined && Objects.equals(this.clickEvent, style.clickEvent) && Objects.equals(this.hoverEvent, style.hoverEvent);
        }
    }

    public int hashCode() {
        return Objects.hash(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent);
    }

    @Override
    public JsonObject toTag() {
        JsonObject tag = new JsonObject();
        if (this.color != null) tag.add("color", this.color.toTag());
        if (this.bold != null) tag.addProperty("bold", this.bold);
        if (this.italic != null) tag.addProperty("italic", this.italic);
        if (this.underlined != null) tag.addProperty("underlined", this.underlined);
        if (this.strikethrough != null) tag.addProperty("strikethrough", this.strikethrough);
        if (this.obfuscated != null) tag.addProperty("obfuscated", this.obfuscated);
        if (this.clickEvent != null) tag.add("clickEvent", this.clickEvent.toTag());
        if (this.hoverEvent != null) tag.add("hoverEvent", this.hoverEvent.toTag());
        return tag;
    }

    public static Style fromTag(JsonObject tag) {
        var color = tag.has("color") ? TextColor.fromTag(tag.getAsJsonObject("color")) : null;
        var bold = tag.has("bold") ? tag.get("bold").getAsBoolean() : null;
        var italic = tag.has("italic") ? tag.get("italic").getAsBoolean() : null;
        var underlined = tag.has("underlined") ? tag.get("underlined").getAsBoolean() : null;
        var strikethrough = tag.has("strikethrough") ? tag.get("strikethrough").getAsBoolean() : null;
        var obfuscated = tag.has("obfuscated") ? tag.get("obfuscated").getAsBoolean() : null;
        var clickEvent = tag.has("clickEvent") ? ClickEvent.fromTag(tag.getAsJsonObject("clickEvent")) : null;
        var hoverEvent = tag.has("hoverEvent") ? HoverEvent.fromTag(tag.getAsJsonObject("hoverEvent")) : null;
        return new Style(color, bold, italic, underlined, strikethrough, obfuscated, clickEvent, hoverEvent);
    }
}
