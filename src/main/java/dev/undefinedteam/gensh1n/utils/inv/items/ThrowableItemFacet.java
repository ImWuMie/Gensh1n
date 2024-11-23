package dev.undefinedteam.gensh1n.utils.inv.items;

import dev.undefinedteam.gensh1n.utils.ComparatorChain;
import dev.undefinedteam.gensh1n.utils.inv.ItemCategory;
import dev.undefinedteam.gensh1n.utils.inv.ItemType;
import dev.undefinedteam.gensh1n.utils.inv.slots.ItemSlot;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;

import java.util.Comparator;

import static dev.undefinedteam.gensh1n.utils.inv.ItemCategorization.PREFER_ITEMS_IN_HOTBAR;
import static dev.undefinedteam.gensh1n.utils.inv.ItemCategorization.STABILIZE_COMPARISON;

public class ThrowableItemFacet extends ItemFacet {
    private static final Comparator<ItemFacet> COMPARATOR =
        ComparatorChain.builder(
            Comparator.comparing(it -> it.getItemStack().getItem() instanceof FishingRodItem),
            Comparator.comparingInt(it -> it.getItemStack().getCount()),
            PREFER_ITEMS_IN_HOTBAR,
            STABILIZE_COMPARISON);

    public ThrowableItemFacet(ItemSlot itemSlot) {
        super(itemSlot);
    }

    @Override
    public ItemCategory getCategory() {
        return new ItemCategory(ItemType.THROWABLE, 0);
    }

    @Override
    public int compareTo(ItemFacet other) {
        return COMPARATOR.compare(this, (ThrowableItemFacet) other);
    }
}
