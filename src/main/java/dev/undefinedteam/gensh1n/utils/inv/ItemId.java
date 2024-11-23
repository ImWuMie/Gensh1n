package dev.undefinedteam.gensh1n.utils.inv;

import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;

class ItemId {
    private Item item;
    private NbtCompound nbt;

    public ItemId(Item item, NbtCompound nbt) {
        this.item = item;
        this.nbt = nbt;
    }

    public Item getItem() {
        return item;
    }

    public NbtCompound getNbt() {
        return nbt;
    }
}
