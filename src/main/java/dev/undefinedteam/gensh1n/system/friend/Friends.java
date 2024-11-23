package dev.undefinedteam.gensh1n.system.friend;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.util.UUIDTypeAdapter;
import com.mojang.util.UndashedUuid;
import dev.undefinedteam.gensh1n.system.System;
import dev.undefinedteam.gensh1n.system.Systems;
import dev.undefinedteam.gensh1n.utils.render.color.SettingColor;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Friends extends System<Friends> implements Iterable<Friend> {
    private final List<Friend> friends = new ArrayList<>();
    public final SettingColor color = new SettingColor(0, 255, 0);

    public Friends() {
        super("friends");
    }

    public static Friends get() {
        return Systems.get(Friends.class);
    }

    public boolean add(Friend friend) {
        if (friend.name.isEmpty() || friend.name.contains(" ")) return false;

        if (!friends.contains(friend)) {
            friends.add(friend);
            save();

            return true;
        }

        return false;
    }

    public boolean remove(Friend friend) {
        if (friends.remove(friend)) {
            save();
            return true;
        }

        return false;
    }

    public Friend get(String name) {
        for (Friend friend : friends) {
            if (friend.name.equals(name)) {
                return friend;
            }
        }

        return null;
    }

    public Friend get(PlayerEntity player) {
        return get(player.getName().getString());
    }

    public Friend get(PlayerListEntry player) {
        return get(player.getProfile().getName());
    }

    public boolean isFriend(PlayerEntity player) {
        return player != null && get(player) != null;
    }

    public boolean isFriend(PlayerListEntry player) {
        return get(player) != null;
    }

    public boolean shouldAttack(PlayerEntity player) {
        return !isFriend(player);
    }

    public int count() {
        return friends.size();
    }

    public boolean isEmpty() {
        return friends.isEmpty();
    }

    @Override
    public @NotNull Iterator<Friend> iterator() {
        return friends.iterator();
    }

    @Override
    public JsonObject toTag() {
        JsonObject tag = new JsonObject();

        JsonArray array = new JsonArray();
        for (Friend friend : friends) {
            array.add(friend.toTag());
        }
        tag.add("friends", array);

        return tag;
    }

    @Override
    public Friends fromTag(JsonObject tag) {
        friends.clear();

        for (JsonElement itemTag : tag.getAsJsonArray("friends")) {
            JsonObject friendTag = (JsonObject) itemTag;
            if (!friendTag.has("name")) continue;

            String name = friendTag.get("name").getAsString();
            if (get(name) != null) continue;

            String uuid = friendTag.get("id").getAsString();
            Friend friend = !uuid.isBlank()
                ? new Friend(name, UndashedUuid.fromString(uuid))
                : new Friend(name);

            friends.add(friend);
        }

        Collections.sort(friends);
        return this;
    }
}
