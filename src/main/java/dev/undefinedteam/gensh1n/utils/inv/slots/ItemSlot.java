package dev.undefinedteam.gensh1n.utils.inv.slots;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;

import static dev.undefinedteam.gensh1n.Client.mc;

public abstract class ItemSlot {
    public abstract int getIdForServer(GenericContainerScreen screen);

    // Concrete method that calls the abstract method
    public int getIdForServerWithCurrentScreen() {
        if (mc.currentScreen instanceof GenericContainerScreen screen) {
            return getIdForServer(screen);
        }

        return -1;
    }

    public abstract ItemSlotType getSlotType();

    public abstract ItemStack getStack();
}
