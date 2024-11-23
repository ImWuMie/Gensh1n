package dev.undefinedteam.gensh1n.utils.inv.armor;

import dev.undefinedteam.gensh1n.utils.ComparatorChain;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;

public class ArmorComparator implements Comparator<ArmorPiece> {
    private static final Enchantment[] DAMAGE_REDUCTION_ENCHANTMENTS = new Enchantment[]{
        Enchantments.PROTECTION,
        Enchantments.PROJECTILE_PROTECTION,
        Enchantments.FIRE_PROTECTION,
        Enchantments.BLAST_PROTECTION
    };
    private static final float[] ENCHANTMENT_FACTORS = new float[]{1.2f, 0.4f, 0.39f, 0.38f};
    private static final float[] ENCHANTMENT_DAMAGE_REDUCTION_FACTOR = new float[]{0.04f, 0.08f, 0.15f, 0.08f};
    private static final Enchantment[] OTHER_ENCHANTMENTS = new Enchantment[]{
        Enchantments.FEATHER_FALLING,
        Enchantments.THORNS,
        Enchantments.RESPIRATION,
        Enchantments.AQUA_AFFINITY,
        Enchantments.UNBREAKING
    };
    private static final float[] OTHER_ENCHANTMENT_PER_LEVEL = new float[]{3.0f, 1.0f, 0.1f, 0.05f, 0.01f};

    private final float expectedDamage;
    private final ArmorKitParameters armorKitParametersForSlot;
    private final ComparatorChain<ArmorPiece> comparator;

    public ArmorComparator(float expectedDamage, ArmorKitParameters armorKitParametersForSlot) {
        this.expectedDamage = expectedDamage;
        this.armorKitParametersForSlot = armorKitParametersForSlot;
        this.comparator = new ComparatorChain<>(
            Comparator.comparingDouble(armorPiece -> round(getThresholdedDamageReduction(armorPiece.getItemSlot().getStack()), 3)),
            Comparator.comparingDouble(armorPiece -> round(getEnchantmentThreshold(armorPiece.getItemSlot().getStack()), 3)),
            Comparator.comparingInt(armorPiece -> armorPiece.getItemSlot().getStack().getEnchantments().size()),
            Comparator.comparingInt(armorPiece -> (armorPiece.getItemSlot().getStack().getItem() instanceof ArmorItem armor ? armor.getEnchantability() : 0)),
            Comparator.comparing(ArmorPiece::isAlreadyEquipped),
            Comparator.comparing(ArmorPiece::isReachableByHand)
        );
    }

    @Override
    public int compare(ArmorPiece o1, ArmorPiece o2) {
        return comparator.compare(o1, o2);
    }

    private float getThresholdedDamageReduction(ItemStack itemStack) {
        ArmorItem item = (ArmorItem) itemStack.getItem();
        ArmorParameter parameters = armorKitParametersForSlot.getParametersForSlot(item.getSlotType());

        return getDamageFactor(
            expectedDamage,
            parameters.defensePoints + item.getMaterial().getProtection(item.getType()) + item.getMaterial().getToughness(),
            parameters.toughness
        ) * (1 - getThresholdedEnchantmentDamageReduction(itemStack));
    }

    public float getDamageFactor(float damage, float defensePoints, float toughness) {
        float f = 2.0f + toughness / 4.0f;
        float g = Math.max(defensePoints - damage / f, defensePoints * 0.2f);

        return 1.0f - g / 25.0f;
    }

    private float getThresholdedEnchantmentDamageReduction(ItemStack itemStack) {
        float sum = 0.0f;

        for (int i = 0; i < DAMAGE_REDUCTION_ENCHANTMENTS.length; i++) {
            int lvl = EnchantmentHelper.getLevel(DAMAGE_REDUCTION_ENCHANTMENTS[i], itemStack);

            sum += lvl * ENCHANTMENT_FACTORS[i] * ENCHANTMENT_DAMAGE_REDUCTION_FACTOR[i];
        }

        return sum;
    }

    private float getEnchantmentThreshold(ItemStack itemStack) {
        float sum = 0.0f;

        for (int i = 0; i < OTHER_ENCHANTMENTS.length; i++) {
            sum += EnchantmentHelper.getLevel(OTHER_ENCHANTMENTS[i],itemStack) * OTHER_ENCHANTMENT_PER_LEVEL[i];
        }

        return sum;
    }

    private double round(double value, int places) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);

        return bd.doubleValue();
    }

    public static class ArmorParameter {
        public final float defensePoints;
        public final float toughness;

        public ArmorParameter(float defensePoints, float toughness) {
            this.defensePoints = defensePoints;
            this.toughness = toughness;
        }
    }
}
