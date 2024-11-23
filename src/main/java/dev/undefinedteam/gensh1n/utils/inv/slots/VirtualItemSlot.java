package dev.undefinedteam.gensh1n.utils.inv.slots;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;

public class VirtualItemSlot extends ItemSlot {
    private final int id;
    private ItemStack stack;
    private ItemSlotType slotType;

    public VirtualItemSlot(ItemStack itemStack, ItemSlotType slotType, int id) {
        this.stack = itemStack;
        this.slotType = slotType;
        this.id = id;
    }

    @Override
    public int getIdForServer(GenericContainerScreen screen) {
        throw new RuntimeException();
    }

    @Override
    public ItemSlotType getSlotType() {
        return slotType;
    }

    @Override
    public ItemStack getStack() {
        return stack;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        VirtualItemSlot that = (VirtualItemSlot) other;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
