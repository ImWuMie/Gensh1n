package dev.undefinedteam.gensh1n.utils.inv.slots;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;

import java.util.Objects;

import static dev.undefinedteam.gensh1n.Client.mc;

public class InventoryItemSlot extends ItemSlot {
    private final int inventorySlot;

    public InventoryItemSlot(int inventorySlot) {
        this.inventorySlot = inventorySlot;
    }

    @Override
    public ItemStack getStack() {
        return mc.player.getInventory().getStack(9 + this.inventorySlot);
    }

    @Override
    public ItemSlotType getSlotType() {
        return ItemSlotType.INVENTORY;
    }

    @Override
    public int getIdForServer(GenericContainerScreen screen) {
        return (screen == null) ? 9 + inventorySlot : screen.getScreenHandler().getRows() * 9 + this.inventorySlot;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        InventoryItemSlot that = (InventoryItemSlot) other;
        return inventorySlot == that.inventorySlot;
    }

    @Override
    public int hashCode() {
        return Objects.hash(InventoryItemSlot.class, inventorySlot);
    }
}
