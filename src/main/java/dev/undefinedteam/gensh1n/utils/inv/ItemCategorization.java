package dev.undefinedteam.gensh1n.utils.inv;

import dev.undefinedteam.gensh1n.system.modules.world.Scaffold;
import dev.undefinedteam.gensh1n.utils.inv.armor.ArmorComparator;
import dev.undefinedteam.gensh1n.utils.inv.armor.ArmorEvaluation;
import dev.undefinedteam.gensh1n.utils.inv.armor.ArmorKitParameters;
import dev.undefinedteam.gensh1n.utils.inv.armor.ArmorPiece;
import dev.undefinedteam.gensh1n.utils.inv.items.*;
import dev.undefinedteam.gensh1n.utils.inv.slots.ItemSlot;
import dev.undefinedteam.gensh1n.utils.inv.slots.ItemSlotType;
import dev.undefinedteam.gensh1n.utils.inv.slots.VirtualItemSlot;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.item.*;

import java.util.*;

import static dev.undefinedteam.gensh1n.utils.Utils.compareValueByCondition;

public class ItemCategorization {
    private List<ItemSlot> bestPiecesIfFullArmor;
    private ArmorComparator armorComparator;

    public ItemCategorization(List<ItemSlot> availableItems) {
        Map<EquipmentSlot, ArmorPiece> armorParameterForSlot = new HashMap<>();
        armorParameterForSlot.put(EquipmentSlot.HEAD, constructArmorPiece(Items.DIAMOND_HELMET, 0));
        armorParameterForSlot.put(EquipmentSlot.CHEST, constructArmorPiece(Items.DIAMOND_CHESTPLATE, 1));
        armorParameterForSlot.put(EquipmentSlot.LEGS, constructArmorPiece(Items.DIAMOND_LEGGINGS, 2));
        armorParameterForSlot.put(EquipmentSlot.FEET, constructArmorPiece(Items.DIAMOND_BOOTS, 3));

        ArmorKitParameters parameters = ArmorKitParameters.getParametersForSlots(armorParameterForSlot);
        this.armorComparator = ArmorEvaluation.getArmorComparatorForParameters(parameters);

        this.bestPiecesIfFullArmor = ArmorEvaluation.findBestArmorPiecesWithComparator(
            availableItems,
            armorComparator
        ).values().stream().map(ArmorPiece::getItemSlot).toList();
    }

    private ArmorPiece constructArmorPiece(Item item, int id) {
        return new ArmorPiece(new VirtualItemSlot(new ItemStack(item, 1), ItemSlotType.ARMOR, id));
    }

    public ItemFacet[] getItemFacets(ItemSlot slot) {
        if (slot.getStack().isEmpty()) {
            return new ItemFacet[0];
        }

        List<ItemFacet> specificItemFacets = new ArrayList<>();
        Item item = slot.getStack().getItem();

        // Determine the facets based on the item type
        if (item instanceof ArmorItem) {
            if (!(item instanceof DyeableArmorItem)) {
                specificItemFacets.add(new ArmorItemFacet(slot, this.bestPiecesIfFullArmor, this.armorComparator));
            }
        } else if (item instanceof SwordItem) {
            specificItemFacets.add(new SwordItemFacet(slot));
        } else if (item instanceof BowItem) {
            specificItemFacets.add(new BowItemFacet(slot));
        } else if (item instanceof CrossbowItem) {
            specificItemFacets.add(new CrossbowItemFacet(slot));
        } else if (item instanceof ArrowItem) {
            specificItemFacets.add(new ArrowItemFacet(slot));
        } else if (item instanceof ToolItem) {
            specificItemFacets.add(new ToolItemFacet(slot));
        } else if (item instanceof FishingRodItem) {
            specificItemFacets.add(new RodItemFacet(slot));
            specificItemFacets.add(new ThrowableItemFacet(slot));
        } else if (item instanceof BlockItem) {
            if (Scaffold.validItemIsBlock(slot.getStack())) {
                specificItemFacets.add(new BlockItemFacet(slot));
            } else {
                specificItemFacets.add(new ItemFacet(slot));
            }
        } else if (item instanceof MilkBucketItem) {
            specificItemFacets.add(new PrimitiveItemFacet(slot, new ItemCategory(ItemType.BUCKET, 2)));
        } else if (item instanceof BucketItem bucketItem) {
            if (bucketItem.fluid instanceof WaterFluid) {
                specificItemFacets.add(new PrimitiveItemFacet(slot, new ItemCategory(ItemType.BUCKET, 0)));
            } else if (bucketItem.fluid instanceof LavaFluid) {
                specificItemFacets.add(new PrimitiveItemFacet(slot, new ItemCategory(ItemType.BUCKET, 1)));
            } else {
                specificItemFacets.add(new PrimitiveItemFacet(slot, new ItemCategory(ItemType.BUCKET, 3)));
            }
        } else if (item instanceof EnderPearlItem) {
            specificItemFacets.add(new PrimitiveItemFacet(slot, new ItemCategory(ItemType.PEARL, 0)));
        } else if (item == Items.GOLDEN_APPLE) {
            specificItemFacets.add(new FoodItemFacet(slot));
            specificItemFacets.add(new PrimitiveItemFacet(slot, new ItemCategory(ItemType.GAPPLE, 0)));
        } else if (item == Items.ENCHANTED_GOLDEN_APPLE) {
            specificItemFacets.add(new FoodItemFacet(slot));
            specificItemFacets.add(new PrimitiveItemFacet(slot, new ItemCategory(ItemType.GAPPLE, 0), 1));
        } else if (item == Items.SNOWBALL || item == Items.EGG) {
            specificItemFacets.add(new ThrowableItemFacet(slot));
        } else if (slot.getStack().isFood()) {
            specificItemFacets.add(new FoodItemFacet(slot));
        } else {
            specificItemFacets.add(new ItemFacet(slot));
        }

        // Everything could be a weapon
        specificItemFacets.add(new WeaponItemFacet(slot));

        return specificItemFacets.toArray(new ItemFacet[0]);
    }

    // Comparators
    public static Comparator<ItemFacet> PREFER_ITEMS_IN_HOTBAR = (o1, o2) ->
        compareValueByCondition(o1, o2, ItemFacet::isInHotbar);

    public static Comparator<ItemFacet> STABILIZE_COMPARISON = Comparator.comparingInt(o -> o.getItemStack().getCount());

    public static Comparator<ItemFacet> PREFER_BETTER_DURABILITY = (o1, o2) ->
        o1.getItemStack().getMaxDamage() - o1.getItemStack().getDamage() -
            o2.getItemStack().getMaxDamage() + o2.getItemStack().getDamage();
}


