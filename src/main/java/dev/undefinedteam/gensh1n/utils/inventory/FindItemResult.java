package dev.undefinedteam.gensh1n.utils.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import static dev.undefinedteam.gensh1n.Client.HOTBAR;
import static dev.undefinedteam.gensh1n.Client.mc;

public record FindItemResult(int slot, int count) {
    public boolean found() {
        return slot != -1;
    }

    public Hand getHand() {
        if (slot == SlotUtils.OFFHAND) return Hand.OFF_HAND;
        else if (slot == HOTBAR.getSlot()) return Hand.MAIN_HAND;
        return null;
    }

    public boolean isMainHand() {
        return getHand() == Hand.MAIN_HAND;
    }

    public boolean isOffhand() {
        return getHand() == Hand.OFF_HAND;
    }

    public boolean isHotbar() {
        return slot >= SlotUtils.HOTBAR_START && slot <= SlotUtils.HOTBAR_END;
    }

    public boolean isMain() {
        return slot >= SlotUtils.MAIN_START && slot <= SlotUtils.MAIN_END;
    }

    public boolean isArmor() {
        return slot >= SlotUtils.ARMOR_START && slot <= SlotUtils.ARMOR_END;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FindItemResult that = (FindItemResult) o;
        return slot == that.slot;
    }

    @Override
    public int hashCode() {
        return slot;
    }

    public ItemStack stack() {
        return mc.player.getInventory().getStack(slot);
    }
}
