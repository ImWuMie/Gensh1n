package dev.undefinedteam.gensh1n.utils.inv.slots;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;

import java.util.Objects;

public class ContainerItemSlot extends ItemSlot {
    private final int slotInContainer;
    private final GenericContainerScreen screen;

    public ContainerItemSlot(int slotInContainer, GenericContainerScreen screen) {
        this.slotInContainer = slotInContainer;
        this.screen = screen;
    }

    @Override
    public ItemSlotType getSlotType() {
        return ItemSlotType.CONTAINER;
    }

    @Override
    public ItemStack getStack() {
        return this.screen.getScreenHandler().slots.get(slotInContainer).getStack();
    }

    @Override
    public int getIdForServer(GenericContainerScreen screen) {
        return this.slotInContainer;
    }

    public int distance(ContainerItemSlot itemSlot) {
        int slotId = this.slotInContainer;
        int otherId = itemSlot.slotInContainer;

        int rowA = slotId / 9;
        int colA = slotId % 9;

        int rowB = otherId / 9;
        int colB = otherId % 9;

        return (colA - colB) * (colA - colB) + (rowA - rowB) * (rowA - rowB);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        ContainerItemSlot that = (ContainerItemSlot) other;
        return slotInContainer == that.slotInContainer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ContainerItemSlot.class, slotInContainer);
    }
}
