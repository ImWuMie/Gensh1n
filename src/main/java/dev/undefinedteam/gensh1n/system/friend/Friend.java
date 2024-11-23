package dev.undefinedteam.gensh1n.system.friend;

import com.google.gson.JsonObject;
import com.mojang.util.UUIDTypeAdapter;
import com.mojang.util.UndashedUuid;
import dev.undefinedteam.gensh1n.utils.misc.ISerializable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public class Friend implements ISerializable<Friend>, Comparable<Friend> {
    public volatile String name;
    private final @Nullable UUID id;
    private volatile boolean updating;

    public Friend(String name, @Nullable UUID id) {
        this.name = name;
        this.id = id;
    }

    public Friend(PlayerEntity player) {
        this(player.getName().getString(), player.getUuid());
    }

    public Friend(String name) {
        this(name, null);
    }

    public String getName() {
        return name;
    }

    @Override
    public JsonObject toTag() {
        JsonObject tag = new JsonObject();

        tag.addProperty("name", name);
        if (id != null) tag.addProperty("id", UndashedUuid.toString(id));

        return tag;
    }

    @Override
    public Friend fromTag(JsonObject tag) {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friend friend = (Friend) o;
        return Objects.equals(name, friend.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public int compareTo(@NotNull Friend friend) {
        return name.compareTo(friend.name);
    }
}
