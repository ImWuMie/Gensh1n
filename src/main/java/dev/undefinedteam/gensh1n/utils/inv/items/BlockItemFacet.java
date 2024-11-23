package dev.undefinedteam.gensh1n.utils.inv.items;

import dev.undefinedteam.gensh1n.utils.ComparatorChain;
import dev.undefinedteam.gensh1n.utils.inv.ItemCategory;
import dev.undefinedteam.gensh1n.utils.inv.ItemType;
import dev.undefinedteam.gensh1n.utils.inv.slots.ItemSlot;

import java.util.Comparator;

import static dev.undefinedteam.gensh1n.utils.inv.ItemCategorization.PREFER_ITEMS_IN_HOTBAR;
import static dev.undefinedteam.gensh1n.utils.inv.ItemCategorization.STABILIZE_COMPARISON;

public class BlockItemFacet extends ItemFacet {
    private static final Comparator<ItemFacet> COMPARATOR =
        new ComparatorChain<>(
            PREFER_ITEMS_IN_HOTBAR,
            STABILIZE_COMPARISON
        );

    public BlockItemFacet(ItemSlot itemSlot) {
        super(itemSlot);
    }

    @Override
    public ItemCategory getCategory() {
        return new ItemCategory(ItemType.BLOCK, 0);
    }

    @Override
    public int compareTo(ItemFacet other) {
        return COMPARATOR.compare(this, (BlockItemFacet) other);
    }
}
