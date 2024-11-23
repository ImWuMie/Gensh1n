package dev.undefinedteam.gensh1n.utils.inv.slots;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;

import java.util.Objects;

import static dev.undefinedteam.gensh1n.Client.mc;

public class ArmorItemSlot extends ItemSlot {
    private final int armorType;

    public ArmorItemSlot(int armorType) {
        this.armorType = armorType;
    }

    @Override
    public ItemStack getStack() {
        return mc.player.getInventory().armor.get(this.armorType);
    }

    @Override
    public ItemSlotType getSlotType() {
        return ItemSlotType.ARMOR;
    }

    @Override
    public int getIdForServer(GenericContainerScreen screen) {
        return (screen == null) ? 8 - this.armorType : -1;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        ArmorItemSlot that = (ArmorItemSlot) other;
        return armorType == that.armorType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ArmorItemSlot.class, armorType);
    }
}
