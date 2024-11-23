package dev.undefinedteam.gensh1n.utils.inv.armor;

import dev.undefinedteam.gensh1n.utils.inv.slots.ItemSlot;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.Item;

import java.util.*;
import java.util.stream.Collectors;

public class ArmorEvaluation {

    private static final float EXPECTED_DAMAGE = 6.0F;

    public static Map<EquipmentSlot, ArmorPiece> findBestArmorPieces(List<ItemSlot> slots) {
        Map<EquipmentSlot, List<ArmorPiece>> armorPiecesGroupedByType = groupArmorByType(slots);

        // We start with assuming that the best pieces are those which have the most damage points.
        Map<EquipmentSlot, ArmorPiece> currentBestPieces = new HashMap<>();
        for (Map.Entry<EquipmentSlot, List<ArmorPiece>> entry : armorPiecesGroupedByType.entrySet()) {
            ArmorPiece maxPiece = entry.getValue().stream().max(Comparator.comparingDouble(armorPiece -> armorPiece.getToughness())).orElse(null);
            currentBestPieces.put(entry.getKey(), maxPiece);
        }

        // Run some passes in which we try to find best armor pieces based on the parameters of the last pass
        for (int ignored = 0; ignored < 2; ignored++) {
            ArmorComparator comparator = getArmorComparatorFor(currentBestPieces);
            currentBestPieces = armorPiecesGroupedByType.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().stream().max(comparator).orElse(null)
                ));
        }

        return currentBestPieces;
    }

    public static Map<EquipmentSlot, ArmorPiece> findBestArmorPiecesWithComparator(List<ItemSlot> slots, ArmorComparator comparator) {
        Map<EquipmentSlot, List<ArmorPiece>> armorPiecesGroupedByType = groupArmorByType(slots);

        return armorPiecesGroupedByType.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream().max(comparator).orElse(null)
            ));
    }

    private static Map<EquipmentSlot, List<ArmorPiece>> groupArmorByType(List<ItemSlot> slots) {
        List<ArmorPiece> armorPieces = slots.stream().map(slot -> {
            Item item = slot.getStack().getItem();
            if (item instanceof ArmorItem && !(item instanceof DyeableArmorItem)) {
                return new ArmorPiece(slot);
            }
            return null;
        }).toList();

        return armorPieces.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.groupingBy(ArmorPiece::getSlotType));
    }

    public static ArmorComparator getArmorComparatorFor(Map<EquipmentSlot, ArmorPiece> currentKit) {
        return getArmorComparatorForParameters(ArmorKitParameters.getParametersForSlots(currentKit));
    }

    public static ArmorComparator getArmorComparatorForParameters(ArmorKitParameters currentParameters) {
        return new ArmorComparator(EXPECTED_DAMAGE, currentParameters);
    }
}
