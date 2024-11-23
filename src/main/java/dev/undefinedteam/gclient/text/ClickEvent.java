package dev.undefinedteam.gclient.text;

import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.utils.misc.ISerializable0;

public class ClickEvent implements ISerializable0<ClickEvent> {
    private final ClickEvent.Action action;
    private final String value;

    public ClickEvent(ClickEvent.Action action, String value) {
        this.action = action;
        this.value = value;
    }

    public ClickEvent.Action getAction() {
        return this.action;
    }

    public String getValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            ClickEvent clickEvent = (ClickEvent) o;
            return this.action == clickEvent.action && this.value.equals(clickEvent.value);
        } else {
            return false;
        }
    }

    public String toString() {
        return "ClickEvent{action=" + this.action.toString() + ", value='" + this.value + "'}";
    }

    public int hashCode() {
        int i = this.action.hashCode();
        i = 31 * i + this.value.hashCode();
        return i;
    }

    @Override
    public JsonObject toTag() {
        var tag = new JsonObject();
        tag.addProperty("action", this.action.name);
        tag.addProperty("value", this.value);
        return tag;
    }

    public static ClickEvent fromTag(JsonObject tag) {
        var action = ClickEvent.Action.fromString(tag.get("action").getAsString());
        var value = tag.get("value").getAsString();
        return new ClickEvent(action, value);
    }

    public net.minecraft.text.ClickEvent vanilla() {
        return new net.minecraft.text.ClickEvent(this.action.vanilla, this.value);
    }

    public enum Action {
        OPEN_URL("open_url", true, net.minecraft.text.ClickEvent.Action.OPEN_URL),
        OPEN_FILE("open_file", false, net.minecraft.text.ClickEvent.Action.OPEN_FILE),
        RUN_COMMAND("run_command", true, net.minecraft.text.ClickEvent.Action.RUN_COMMAND),
        SUGGEST_COMMAND("suggest_command", true, net.minecraft.text.ClickEvent.Action.SUGGEST_COMMAND),
        CHANGE_PAGE("change_page", true, net.minecraft.text.ClickEvent.Action.CHANGE_PAGE),
        COPY_TO_CLIPBOARD("copy_to_clipboard", true, net.minecraft.text.ClickEvent.Action.COPY_TO_CLIPBOARD);

        private final boolean userDefinable;
        private final String name;
        private final net.minecraft.text.ClickEvent.Action vanilla;

        Action(final String name, final boolean userDefinable, net.minecraft.text.ClickEvent.Action vanilla) {
            this.name = name;
            this.userDefinable = userDefinable;
            this.vanilla = vanilla;
        }

        public boolean isUserDefinable() {
            return this.userDefinable;
        }

        public String asString() {
            return this.name;
        }

        public static ClickEvent.Action fromString(String name) {
            for (ClickEvent.Action action : values()) {
                if (action.name.equals(name)) {
                    return action;
                }
            }
            return null;
        }
    }
}
