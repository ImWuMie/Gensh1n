package dev.undefinedteam.gensh1n.utils.inv;

import dev.undefinedteam.gensh1n.utils.inv.slots.ItemSlot;

 public class InventorySwap {
    private ItemSlot from;
    private ItemSlot to;

    public InventorySwap(ItemSlot from, ItemSlot to) {
        this.from = from;
        this.to = to;
    }

    public ItemSlot getFrom() {
        return from;
    }

    public ItemSlot getTo() {
        return to;
    }
}
