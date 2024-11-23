package dev.undefinedteam.gensh1n.settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.function.Consumer;

public class BlockPosSetting extends Setting<BlockPos> {
    public BlockPosSetting(String name, String description, BlockPos defaultValue, Consumer<BlockPos> onChanged, Consumer<Setting<BlockPos>> onModuleActivated, IVisible visible) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    }

    @Override
    protected BlockPos parseImpl(String str) {
        List<String> values = List.of(str.split(","));
        if (values.size() != 3) return null;

        BlockPos bp = null;
        try {
            bp = new BlockPos(Integer.parseInt(values.get(0)), Integer.parseInt(values.get(1)), Integer.parseInt(values.get(2)));
        } catch (NumberFormatException ignored) {
        }
        return bp;
    }

    @Override
    protected boolean isValueValid(BlockPos value) {
        return true;
    }

    @Override
    protected JsonObject save(JsonObject tag) {
        JsonArray array = new JsonArray();
        array.add(value.getX());
        array.add(value.getY());
        array.add(value.getZ());

        tag.add("value", array);

        return tag;
    }

    @Override
    protected BlockPos load(JsonObject tag) {
        JsonArray value = tag.getAsJsonArray("value");
        set(new BlockPos(value.get(0).getAsInt(), value.get(1).getAsInt(), value.get(2).getAsInt()));

        return get();
    }

    public static class Builder extends SettingBuilder<Builder, BlockPos, BlockPosSetting> {
        public Builder() {
            super(new BlockPos(0, 0, 0));
        }

        @Override
        public BlockPosSetting build() {
            return new BlockPosSetting(name, description, defaultValue, onChanged, onModuleActivated, visible);
        }
    }
}
