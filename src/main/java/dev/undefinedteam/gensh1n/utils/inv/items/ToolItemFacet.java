package dev.undefinedteam.gensh1n.utils.inv.items;

import dev.undefinedteam.gensh1n.utils.ComparatorChain;
import dev.undefinedteam.gensh1n.utils.inv.EnchantmentValueEstimator;
import dev.undefinedteam.gensh1n.utils.inv.ItemCategory;
import dev.undefinedteam.gensh1n.utils.inv.ItemType;
import dev.undefinedteam.gensh1n.utils.inv.slots.ItemSlot;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.*;

import java.util.Comparator;

import static dev.undefinedteam.gensh1n.utils.inv.ItemCategorization.*;

public class ToolItemFacet extends ItemFacet {
    private static final EnchantmentValueEstimator VALUE_ESTIMATOR =
            new EnchantmentValueEstimator(
                    new EnchantmentValueEstimator.WeightedEnchantment(Enchantments.SILK_TOUCH, 1.0f),
                    new EnchantmentValueEstimator.WeightedEnchantment(Enchantments.UNBREAKING, 0.2f),
                    new EnchantmentValueEstimator.WeightedEnchantment(Enchantments.FORTUNE, 0.33f)
            );
    private static final ComparatorChain<ItemFacet> COMPARATOR =
            ComparatorChain.<ItemFacet>builder()
                    .compare(Comparator.comparingDouble(it -> mineSpeed(it.getItemStack())))
                    .compare(Comparator.comparingDouble(it -> VALUE_ESTIMATOR.estimateValue(it.getItemStack())))
                    .thenComparing(PREFER_BETTER_DURABILITY)
                    .thenComparing(PREFER_ITEMS_IN_HOTBAR)
                    .thenComparing(STABILIZE_COMPARISON)
                    .build();

    public ToolItemFacet(ItemSlot itemSlot) {
        super(itemSlot);
    }

    @Override
    public ItemCategory getCategory() {
        ToolItem toolItem = (ToolItem) this.getItemStack().getItem();
        return new ItemCategory(ItemType.TOOL, toolItem instanceof AxeItem ? 0 : 1);
    }

    private static float mineSpeed(final ItemStack stack) {
        final Item item = stack.getItem();
        int level = EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, stack);

        level = switch (level) {
            case 1 -> 30;
            case 2 -> 69;
            case 3 -> 120;
            case 4 -> 186;
            case 5 -> 271;
            default -> 0;
        };

        if (item instanceof PickaxeItem pickaxe) {
            return pickaxe.getMaterial().getMiningSpeedMultiplier() + level;
        } else if (item instanceof ShovelItem shovel) {
            return shovel.getMaterial().getMiningSpeedMultiplier() + level;
        } else if (item instanceof AxeItem axe) {
            return axe.getMaterial().getMiningSpeedMultiplier() + level;
        }

        return 0;
    }

    @Override
    public int compareTo(ItemFacet other) {
        return COMPARATOR.compare(this, (ToolItemFacet) other);
    }
}
