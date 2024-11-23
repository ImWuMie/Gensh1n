package dev.undefinedteam.gensh1n.utils.inv.armor;

import dev.undefinedteam.gensh1n.utils.inv.slots.ItemSlot;
import dev.undefinedteam.gensh1n.utils.inv.slots.ItemSlotType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

public class ArmorPiece {
    private final ItemSlot slot;

    public ArmorPiece(ItemSlot slot) {
        this.slot = slot;
    }

    public ItemSlot getItemSlot() {
        return slot;
    }

    public EquipmentSlot getSlotType() {
        return slot.getStack().getItem() instanceof ArmorItem armor ? armor.getSlotType() : null;
    }

    public int getEntitySlotId() {
        return this.getSlotType() == null ? -1 : this.getSlotType().getEntitySlotId();
    }

    public int getInventorySlot() {
        return 36 + this.getEntitySlotId();
    }

    public boolean isAlreadyEquipped() {
        return slot.getSlotType() == ItemSlotType.ARMOR;
    }

    public boolean isReachableByHand() {
        return slot.getSlotType() == ItemSlotType.HOTBAR;
    }

    public float getToughness() {
        return slot.getStack().getItem() instanceof ArmorItem armor ? armor.getToughness() : 0;
    }

    public float getDefensePoints() {
        ItemStack itemStack = slot.getStack();
        if (itemStack.getItem() instanceof ArmorItem item) {
            return item.getMaterial().getProtection(item.getType());
        }
        return 0;
    }
}
