package dev.undefinedteam.gclient.text;

import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.utils.misc.ISerializable0;
import net.minecraft.text.PlainTextContent;

public interface TextContent extends ISerializable0<TextContent> {
    TextContent EMPTY = new TextContent() {
        public String toString() {
            return "empty";
        }

        public String string() {
            return "";
        }
    };

    static TextContent of(String string) {
        return string.isEmpty() ? EMPTY : new Literal(string);
    }

    String string();

    default net.minecraft.text.TextContent vanilla() {
        return new PlainTextContent.Literal(string());
    }

    @Override
    default JsonObject toTag() {
        var tag = new JsonObject();
        tag.addProperty("string",string());
        return tag;
    }

    static TextContent fromTag(JsonObject tag) {
        var string = tag.get("string").getAsString();
        if (string.isEmpty()) return EMPTY;
        else return new Literal(string);
    }

    record Literal(String string) implements TextContent {
        public Literal(String string) {
            this.string = string;
        }

        public String toString() {
            return "literal{" + this.string + "}";
        }

        public String string() {
            return this.string;
        }
    }
}
