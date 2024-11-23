package dev.undefinedteam.gensh1n.utils.inv.items;

import dev.undefinedteam.gensh1n.utils.ComparatorChain;
import dev.undefinedteam.gensh1n.utils.inv.ItemCategory;
import dev.undefinedteam.gensh1n.utils.inv.slots.ItemSlot;

import java.util.Comparator;

import static dev.undefinedteam.gensh1n.utils.inv.ItemCategorization.PREFER_ITEMS_IN_HOTBAR;
import static dev.undefinedteam.gensh1n.utils.inv.ItemCategorization.STABILIZE_COMPARISON;

public class PrimitiveItemFacet extends ItemFacet {
    private final int worth;
    private final ItemCategory category;

    public PrimitiveItemFacet(ItemSlot itemSlot, ItemCategory category) {
        super(itemSlot);
        this.category = category;
        this.worth = 0;
    }

    public PrimitiveItemFacet(ItemSlot itemSlot, ItemCategory category, int worth) {
        super(itemSlot);
        this.category = category;
        this.worth = worth;
    }

    private static final ComparatorChain<ItemFacet> COMPARATOR =
        new ComparatorChain<>(
            Comparator.comparingInt(it -> ((PrimitiveItemFacet) it).worth),
            Comparator.comparingInt(it -> it.getItemStack().getCount()),
            PREFER_ITEMS_IN_HOTBAR,
            STABILIZE_COMPARISON
        );

    @Override
    public ItemCategory getCategory() {
        return category;
    }

    @Override
    public int compareTo(ItemFacet other) {
        return COMPARATOR.compare(this, other);
    }
}
