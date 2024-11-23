package dev.undefinedteam.gensh1n.utils.inv.slots;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;

import static dev.undefinedteam.gensh1n.Client.mc;

public class OffHandSlot extends HotbarItemSlot {

    private static final int HOTBAR_SLOT_FOR_SERVER = 40;
    private static final int ID_FOR_SERVER = 45;

    public OffHandSlot() {
        super(-1); // Assuming the player object is accessible or passed correctly
    }

    @Override
    public ItemStack getStack() {
        return mc.player.getOffHandStack(); // Assuming 'player' has an 'offHandStack' field
    }

    @Override
    public ItemSlotType getSlotType() {
        return ItemSlotType.OFFHAND;
    }

    @Override
    public int getHotbarSlotForServer() {
        return HOTBAR_SLOT_FOR_SERVER;
    }

    @Override
    public int getIdForServer(GenericContainerScreen screen) {
        return (screen == null) ? ID_FOR_SERVER : -1;
    }

    @Override
    public boolean equals(Object other) {
        return this == other;
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }
}
