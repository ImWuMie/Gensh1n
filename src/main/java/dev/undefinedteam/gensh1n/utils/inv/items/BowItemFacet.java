package dev.undefinedteam.gensh1n.utils.inv.items;

import dev.undefinedteam.gensh1n.utils.ComparatorChain;
import dev.undefinedteam.gensh1n.utils.inv.EnchantmentValueEstimator;
import dev.undefinedteam.gensh1n.utils.inv.ItemCategory;
import dev.undefinedteam.gensh1n.utils.inv.ItemType;
import dev.undefinedteam.gensh1n.utils.inv.slots.ItemSlot;
import net.minecraft.enchantment.Enchantments;

import java.util.Comparator;

import static dev.undefinedteam.gensh1n.utils.inv.ItemCategorization.PREFER_ITEMS_IN_HOTBAR;
import static dev.undefinedteam.gensh1n.utils.inv.ItemCategorization.STABILIZE_COMPARISON;

public class BowItemFacet extends ItemFacet {
    private static final EnchantmentValueEstimator VALUE_ESTIMATOR =
        new EnchantmentValueEstimator(
            new EnchantmentValueEstimator.WeightedEnchantment(Enchantments.POWER, 0.25f),
            new EnchantmentValueEstimator.WeightedEnchantment(Enchantments.PUNCH, 0.33f),
            new EnchantmentValueEstimator.WeightedEnchantment(Enchantments.FLAME, 4.0f * 0.9f),
            new EnchantmentValueEstimator.WeightedEnchantment(Enchantments.INFINITY, 4.0f),
            new EnchantmentValueEstimator.WeightedEnchantment(Enchantments.UNBREAKING, 0.1f),
            new EnchantmentValueEstimator.WeightedEnchantment(Enchantments.VANISHING_CURSE, -0.1f),
            new EnchantmentValueEstimator.WeightedEnchantment(Enchantments.MENDING, -0.2f)
        );
    private static final Comparator<ItemFacet> COMPARATOR =
        new ComparatorChain<>(
            Comparator.comparingDouble(it -> VALUE_ESTIMATOR.estimateValue(it.getItemStack())),
            PREFER_ITEMS_IN_HOTBAR,
            STABILIZE_COMPARISON
        );

    public BowItemFacet(ItemSlot itemSlot) {
        super(itemSlot);
    }

    @Override
    public ItemCategory getCategory() {
        return new ItemCategory(ItemType.BOW, 0);
    }

    @Override
    public int compareTo(ItemFacet other) {
        return COMPARATOR.compare(this, (BowItemFacet) other);
    }
}
