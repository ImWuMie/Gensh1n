package dev.undefinedteam.gensh1n.utils.inv.items;

import dev.undefinedteam.gensh1n.utils.ComparatorChain;
import dev.undefinedteam.gensh1n.utils.inv.ItemCategory;
import dev.undefinedteam.gensh1n.utils.inv.ItemType;
import dev.undefinedteam.gensh1n.utils.inv.slots.ItemSlot;

import java.util.Comparator;

import static dev.undefinedteam.gensh1n.utils.inv.ItemCategorization.PREFER_ITEMS_IN_HOTBAR;
import static dev.undefinedteam.gensh1n.utils.inv.ItemCategorization.STABILIZE_COMPARISON;

public class ArrowItemFacet extends ItemFacet {
    private static final Comparator<ItemFacet> COMPARATOR =
            new ComparatorChain<ItemFacet>(
                    Comparator.comparingInt(it -> it.getItemStack().getCount()),
                    PREFER_ITEMS_IN_HOTBAR,
                    STABILIZE_COMPARISON
                    );

    public ArrowItemFacet(ItemSlot itemSlot) {
        super(itemSlot);
    }

    @Override
    public ItemCategory getCategory() {
        return new ItemCategory(ItemType.ARROW, 0);
    }

    @Override
    public int compareTo(ItemFacet other) {
        return COMPARATOR.compare(this, (ArrowItemFacet) other);
    }
}
