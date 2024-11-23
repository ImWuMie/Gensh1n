package dev.undefinedteam.gensh1n.utils.inv.slots;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;

import java.util.Objects;

import static dev.undefinedteam.gensh1n.Client.mc;

public class HotbarItemSlot extends ItemSlot {
    private final int hotbarSlot;

    public HotbarItemSlot(int hotbarSlot) {
        this.hotbarSlot = hotbarSlot;
    }

    @Override
    public ItemStack getStack() {
        return mc.player.getInventory().getStack(this.hotbarSlot);
    }

    @Override
    public ItemSlotType getSlotType() {
        return ItemSlotType.HOTBAR;
    }

    public int getHotbarSlotForServer() {
        return hotbarSlot;
    }

    @Override
    public int getIdForServer(GenericContainerScreen screen) {
        return (screen == null) ? 36 + hotbarSlot : screen.getScreenHandler().getRows() * 9 + 27 + this.hotbarSlot;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        HotbarItemSlot that = (HotbarItemSlot) other;
        return hotbarSlot == that.hotbarSlot;
    }

    @Override
    public int hashCode() {
        return Objects.hash(HotbarItemSlot.class, hotbarSlot);
    }

    @Override
    public String toString() {
        return "HotbarItemSlot{" +
               "hotbarSlot=" + hotbarSlot +
               ", itemStack=" + getStack() +
               '}';
    }
}
