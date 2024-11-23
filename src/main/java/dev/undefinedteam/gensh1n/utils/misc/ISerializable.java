package dev.undefinedteam.gensh1n.utils.misc;

import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;

public interface ISerializable<T> {
    JsonObject toTag();

    T fromTag(JsonObject tag);
}

