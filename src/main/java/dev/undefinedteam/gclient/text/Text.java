package dev.undefinedteam.gclient.text;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.undefinedteam.gclient.Formatting;
import dev.undefinedteam.gensh1n.utils.misc.ISerializable0;
import net.minecraft.text.MutableText;

import java.net.URI;
import java.util.*;
import java.util.function.UnaryOperator;

public class Text implements ISerializable0<Text> {
    private final TextContent content;
    private final List<Text> siblings;
    private Style style;

    Text(TextContent content, List<Text> siblings, Style style) {
        this.content = content;
        this.siblings = siblings;
        this.style = style;
    }

    public static Text of(TextContent content) {
        return new Text(content, Lists.newArrayList(), Style.EMPTY);
    }

    public TextContent getContent() {
        return this.content;
    }

    public List<Text> getSiblings() {
        return this.siblings;
    }

    public Text setStyle(Style style) {
        this.style = style;
        return this;
    }

    public Style getStyle() {
        return this.style;
    }

    public Text append(String text) {
        return text.isEmpty() ? this : this.append(Text.literal(text));
    }

    public Text append(Text text) {
        this.siblings.add(text);
        return this;
    }

    public Text styled(UnaryOperator<Style> styleUpdater) {
        this.setStyle(styleUpdater.apply(this.getStyle()));
        return this;
    }

    public Text fillStyle(Style styleOverride) {
        this.setStyle(styleOverride.withParent(this.getStyle()));
        return this;
    }

    public Text formatted(Formatting... formatting) {
        this.setStyle(this.getStyle().withFormatting(formatting));
        return this;
    }

    public Text formatted(Formatting formatting) {
        this.setStyle(this.getStyle().withFormatting(formatting));
        return this;
    }

    public Text withColor(int color) {
        this.setStyle(this.getStyle().withColor(color));
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Text text = (Text) o;
        return Objects.equals(content, text.content) && Objects.equals(siblings, text.siblings) && Objects.equals(style, text.style);
    }

    public int hashCode() {
        return Objects.hash(this.content, this.style, this.siblings);
    }

    public Text copyContentOnly() {
        return Text.of(this.getContent());
    }

    public static Text of(String string) {
        return string != null ? literal(string) : empty();
    }

    public static Text literal(String string) {
        return Text.of(TextContent.of(string));
    }

    public static Text empty() {
        return Text.of(TextContent.EMPTY);
    }

    public static Text of(Date date) {
        return literal(date.toString());
    }

    public static Text of(UUID uuid) {
        return literal(uuid.toString());
    }

    public static Text of(URI uri) {
        return literal(uri.toString());
    }

    public Text copy() {
        return new Text(this.getContent(), new ArrayList<>(this.getSiblings()), this.getStyle());
    }

    @Override
    public JsonObject toTag() {
        var tag = new JsonObject();
        tag.add("content", this.content.toTag());
        {
            var array = new JsonArray();
            for (Text sibling : this.siblings) {
                array.add(sibling.toTag());
            }

            tag.add("siblings", array);
        }
        tag.add("style", this.style.toTag());
        return tag;
    }

    public static Text fromTag(JsonObject tag) {
        var content = TextContent.fromTag(tag.get("content").getAsJsonObject());
        var siblings = new ArrayList<Text>();
        {
            var array = tag.get("siblings").getAsJsonArray();
            for (JsonElement element : array) {
                siblings.add(fromTag(element.getAsJsonObject()));
            }
        }

        var style = Style.fromTag(tag.get("style").getAsJsonObject());
        return new Text(content, siblings, style);
    }

    public net.minecraft.text.Text vanilla() {
        var text = MutableText.of(this.content.vanilla()).setStyle(this.style.vanilla());
        for (Text sibling : this.siblings) {
            text.append(sibling.vanilla());
        }
        return text;
    }

    @Override
    public String toString() {
        return getString();
    }

    public String getString() {
        StringBuilder builder = new StringBuilder(this.content.string());
        for (Text sibling : this.siblings) {
            builder.append(sibling.getString());
        }
        return builder.toString();
    }
}
