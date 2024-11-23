package dev.undefinedteam.gensh1n.utils.inv.items;

import dev.undefinedteam.gensh1n.utils.ComparatorChain;
import dev.undefinedteam.gensh1n.utils.inv.EnchantmentValueEstimator;
import dev.undefinedteam.gensh1n.utils.inv.ItemCategory;
import dev.undefinedteam.gensh1n.utils.inv.ItemType;
import dev.undefinedteam.gensh1n.utils.inv.slots.ItemSlot;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;

import java.util.Comparator;

import static dev.undefinedteam.gensh1n.utils.inv.ItemCategorization.PREFER_ITEMS_IN_HOTBAR;
import static dev.undefinedteam.gensh1n.utils.inv.ItemCategorization.STABILIZE_COMPARISON;

public class RodItemFacet extends ItemFacet {
    private static final EnchantmentValueEstimator VALUE_ESTIMATOR =
            new EnchantmentValueEstimator(
                    new EnchantmentValueEstimator.WeightedEnchantment(Enchantments.UNBREAKING, 0.4f)
            );
    private static final ComparatorChain<ItemFacet> COMPARATOR =
            ComparatorChain.<ItemFacet>builder()
                    .compare(Comparator.comparingDouble(it -> VALUE_ESTIMATOR.estimateValue(it.getItemStack())))
                    .thenComparing(PREFER_ITEMS_IN_HOTBAR)
                    .thenComparing(STABILIZE_COMPARISON)
                    .build();

    public RodItemFacet(ItemSlot itemSlot) {
        super(itemSlot);
    }

    @Override
    public ItemCategory getCategory() {
        return new ItemCategory(ItemType.ROD, 0);
    }

    @Override
    public int compareTo(ItemFacet other) {
        return COMPARATOR.compare(this, (RodItemFacet) other);
    }
}
