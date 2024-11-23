package dev.undefinedteam.gensh1n.utils.inv;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;

public class EnchantmentValueEstimator {
    private WeightedEnchantment[] weightedEnchantments;

    public EnchantmentValueEstimator(WeightedEnchantment... weightedEnchantments) {
        this.weightedEnchantments = weightedEnchantments;
    }

    public float estimateValue(ItemStack itemStack) {
        float sum = 0.0f;

        for (WeightedEnchantment it : this.weightedEnchantments) {
            int enchantmentLevel = EnchantmentHelper.getLevel(it.getEnchantment(), itemStack);
            sum += enchantmentLevel * it.getFactor();
        }

        return sum;
    }

    public static class WeightedEnchantment {
        private Enchantment enchantment;
        private float factor;

        public WeightedEnchantment(Enchantment enchantment, float factor) {
            this.enchantment = enchantment;
            this.factor = factor;
        }

        public Enchantment getEnchantment() {
            return enchantment;
        }

        public float getFactor() {
            return factor;
        }
    }
}
