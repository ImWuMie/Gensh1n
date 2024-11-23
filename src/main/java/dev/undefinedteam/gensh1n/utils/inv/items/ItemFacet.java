package dev.undefinedteam.gensh1n.utils.inv.items;

import dev.undefinedteam.gensh1n.utils.inv.ItemCategory;
import dev.undefinedteam.gensh1n.utils.inv.ItemFunction;
import dev.undefinedteam.gensh1n.utils.inv.ItemType;
import dev.undefinedteam.gensh1n.utils.inv.slots.ItemSlot;
import dev.undefinedteam.gensh1n.utils.inv.slots.ItemSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;

import static dev.undefinedteam.gensh1n.utils.Utils.compareValueByCondition;

public class ItemFacet implements Comparable<ItemFacet> {
    protected final ItemSlot itemSlot;

    public ItemFacet(ItemSlot itemSlot) {
        this.itemSlot = itemSlot;
    }

    public ItemSlot getItemSlot() {
        return itemSlot;
    }

    public ItemCategory getCategory() {
        return new ItemCategory(ItemType.NONE, 0);
    }

    public List<Pair<ItemFunction, Integer>> getProvidedItemFunctions() {
        return new ArrayList<>();
    }

    public ItemStack getItemStack() {
        return this.itemSlot.getStack();
    }

    public boolean isInHotbar() {
        return this.itemSlot.getSlotType() == ItemSlotType.HOTBAR || this.itemSlot.getSlotType() == ItemSlotType.OFFHAND;
    }

    public boolean isSignificantlyBetter(ItemFacet other) {
        return false;
    }

    /**
     * Should this item be kept, even if it is not allocated to any slot?
     */
    public boolean shouldKeep() {
        return false;
    }

    @Override
    public int compareTo(ItemFacet other) {
        // Assuming compareValueByCondition is a method that returns an Integer
        // Since the implementation of compareValueByCondition is not provided,
        // you would need to implement this method according to your specific logic.
        return compareValueByCondition(this, other, ItemFacet::isInHotbar);
    }
}
