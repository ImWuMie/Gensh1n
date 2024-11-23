package dev.undefinedteam.gclient.text;

import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.utils.misc.ISerializable0;

public class HoverEvent implements ISerializable0<HoverEvent> {
    public Action action;
    public Text contents;

    public HoverEvent(Action action, Text contents) {
        this.action = action;
        this.contents = contents;
    }

    @Override
    public JsonObject toTag() {
        var tag = new JsonObject();
        tag.addProperty("action", this.action.asString());
        tag.add("contents", this.contents.toTag());
        return null;
    }

    public static HoverEvent fromTag(JsonObject tag) {
        var action = HoverEvent.Action.fromString(tag.get("action").getAsString());
        var contents = Text.fromTag(tag.getAsJsonObject("contents"));
        return new HoverEvent(action, contents);
    }

    public net.minecraft.text.HoverEvent vanilla() {
        return new net.minecraft.text.HoverEvent(this.action.vanilla(), this.contents.vanilla());
    }

    public enum Action {
       SHOW_TEXT("show_text", true, net.minecraft.text.HoverEvent.Action.SHOW_TEXT);

        private final String name;
        private final boolean parsable;
        private final net.minecraft.text.HoverEvent.Action vanilla;

        Action(String name, boolean parsable, net.minecraft.text.HoverEvent.Action vanilla) {
            this.name = name;
            this.parsable = parsable;
            this.vanilla = vanilla;
        }

        public boolean isParsable() {
            return this.parsable;
        }

        public String asString() {
            return this.name;
        }

        public String toString() {
            return "<action " + this.name + ">";
        }

        public static Action fromString(String name) {
            for (var action : Action.values()) {
                if (action.name.equals(name)) {
                    return action;
                }
            }
            return null;
        }

        public net.minecraft.text.HoverEvent.Action vanilla() {
            return this.vanilla;
        }
    }
}
