package dev.undefinedteam.gensh1n.utils.inv.items;

import dev.undefinedteam.gensh1n.utils.ComparatorChain;
import dev.undefinedteam.gensh1n.utils.Utils;
import dev.undefinedteam.gensh1n.utils.inv.EnchantmentValueEstimator;
import dev.undefinedteam.gensh1n.utils.inv.ItemCategory;
import dev.undefinedteam.gensh1n.utils.inv.ItemFunction;
import dev.undefinedteam.gensh1n.utils.inv.ItemType;
import dev.undefinedteam.gensh1n.utils.inv.slots.ItemSlot;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static dev.undefinedteam.gensh1n.Client.mc;
import static dev.undefinedteam.gensh1n.utils.inv.ItemCategorization.*;

public class WeaponItemFacet extends ItemFacet {
    private static final EnchantmentValueEstimator DAMAGE_ESTIMATOR =
        new EnchantmentValueEstimator(
            new EnchantmentValueEstimator.WeightedEnchantment(Enchantments.SMITE, 2.0f * 0.1f),
            new EnchantmentValueEstimator.WeightedEnchantment(Enchantments.BANE_OF_ARTHROPODS, 2.0f * 0.1f),
            new EnchantmentValueEstimator.WeightedEnchantment(Enchantments.KNOCKBACK, 0.2f)
        );
    private static final EnchantmentValueEstimator SECONDARY_VALUE_ESTIMATOR =
        new EnchantmentValueEstimator(
            new EnchantmentValueEstimator.WeightedEnchantment(Enchantments.LOOTING, 0.05f),
            new EnchantmentValueEstimator.WeightedEnchantment(Enchantments.UNBREAKING, 0.05f),
            new EnchantmentValueEstimator.WeightedEnchantment(Enchantments.VANISHING_CURSE, -0.1f),
            new EnchantmentValueEstimator.WeightedEnchantment(Enchantments.SWEEPING, 0.2f),
            new EnchantmentValueEstimator.WeightedEnchantment(Enchantments.KNOCKBACK, 0.25f)
        );
    private static final ComparatorChain<ItemFacet> COMPARATOR =
        ComparatorChain.<ItemFacet>builder()
            .compare(Comparator.comparingDouble(a -> estimateDamage(((WeaponItemFacet) a))))
            .compare(Comparator.comparingDouble(it -> SECONDARY_VALUE_ESTIMATOR.estimateValue(it.getItemStack())))
            .thenComparing(Comparator.comparing(it -> it.getItemStack().getItem() instanceof SwordItem))
            .thenComparing(PREFER_BETTER_DURABILITY)
            .thenComparing(Comparator.comparingInt(it -> it.getItemStack().getItem().getEnchantability()))
            .thenComparing(PREFER_ITEMS_IN_HOTBAR)
            .thenComparing(STABILIZE_COMPARISON)
            .build();

    public WeaponItemFacet(ItemSlot itemSlot) {
        super(itemSlot);
    }

    @Override
    public ItemCategory getCategory() {
        return new ItemCategory(ItemType.WEAPON, 0);
    }

    @Override
    public List<Pair<ItemFunction, Integer>> getProvidedItemFunctions() {
        List<Pair<ItemFunction, Integer>> functions = new ArrayList<>();
        functions.add(new Pair<>(ItemFunction.WEAPON_LIKE, 1));
        return functions;
    }

    @Override
    public int compareTo(ItemFacet other) {
        return COMPARATOR.compare(this, other);
    }

    private static double estimateDamage(WeaponItemFacet weaponItemFacet) {
        double attackDamage = Utils.getAttackDamage(weaponItemFacet.getItemStack());
        double attackSpeed = mc.player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_SPEED);

        double p = Math.pow(0.85, 1 / 20.0);
        double bigT = 20.0 / attackSpeed;

        double probabilityAdjustmentFactor = Math.pow(p, Math.ceil(bigT * 0.9));

        double speedAdjustedDamage = attackDamage * attackSpeed * probabilityAdjustmentFactor;

        int fireAspectLevel = EnchantmentHelper.getLevel(Enchantments.FIRE_ASPECT, weaponItemFacet.getItemStack());
        double damageFromFireAspect = (fireAspectLevel * 4.0 - 1) >= 0 ? (fireAspectLevel * 4.0 - 1) * 0.33 : 0;

        double additionalFactor = DAMAGE_ESTIMATOR.estimateValue(weaponItemFacet.getItemStack());

        return speedAdjustedDamage * (1 + additionalFactor) + damageFromFireAspect;
    }
}
