package dev.undefinedteam.gensh1n.utils.input;

import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.utils.Utils;
import dev.undefinedteam.gensh1n.utils.misc.ICopyable;
import dev.undefinedteam.gensh1n.utils.misc.ISerializable;
import lombok.Getter;
import net.minecraft.nbt.NbtCompound;

import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;

public class Keybind implements ISerializable<Keybind>, ICopyable<Keybind> {
    private boolean isKey;
    @Getter private int value;

    private Keybind(boolean isKey, int value) {
        set(isKey, value);
    }

    public static Keybind none() {
        return new Keybind(true, -1);
    }

    public static Keybind fromKey(int key) {
        return new Keybind(true, key);
    }

    public static Keybind fromButton(int button) {
        return new Keybind(false, button);
    }

    public boolean isSet() {
        return value != -1;
    }

    public boolean canBindTo(boolean isKey, int value) {
        if (isKey) return value != GLFW_KEY_ESCAPE;
        return value != GLFW_MOUSE_BUTTON_LEFT && value != GLFW_MOUSE_BUTTON_RIGHT;
    }

    public void set(boolean isKey, int value) {
        this.isKey = isKey;
        this.value = value;
    }

    @Override
    public Keybind set(Keybind value) {
        this.isKey = value.isKey;
        this.value = value.value;

        return this;
    }

    public boolean matches(boolean isKey, int value) {
        if (this.isKey != isKey) return false;
        return this.value == value;
    }

    public boolean isValid() {
        return value != -1;
    }

    public boolean isKey() {
        return isKey;
    }

    public boolean isPressed() {
        return isKey ? Input.isKeyPressed(value) : Input.isButtonPressed(value);
    }

    @Override
    public Keybind copy() {
        return new Keybind(isKey, value);
    }

    @Override
    public String toString() {
        if (value == -1) return "None";
        return isKey ? Utils.getKeyName(value) : Utils.getButtonName(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Keybind keybind = (Keybind) o;
        return isKey == keybind.isKey && value == keybind.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isKey, value);
    }

    // Serialization

    @Override
    public JsonObject toTag() {
        JsonObject tag = new JsonObject();

        tag.addProperty("isKey", isKey);
        tag.addProperty("value", value);

        return tag;
    }

    @Override
    public Keybind fromTag(JsonObject tag) {
        isKey = tag.get("isKey").getAsBoolean();
        value = tag.get("value").getAsInt();

        return this;
    }
}
