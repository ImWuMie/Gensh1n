package dev.undefinedteam.gensh1n.utils.inv.armor;

import net.minecraft.entity.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;

public class ArmorKitParameters {
    private final Map<EquipmentSlot, ArmorComparator.ArmorParameter> slots;

    public ArmorKitParameters(Map<EquipmentSlot, ArmorComparator.ArmorParameter> slots) {
        this.slots = slots;
    }

    public ArmorComparator.ArmorParameter getParametersForSlot(EquipmentSlot slotType) {
        return slots.get(slotType);
    }

    public static ArmorKitParameters getParametersForSlots(Map<EquipmentSlot, ArmorPiece> currentKit) {
        Map<EquipmentSlot, ArmorComparator.ArmorParameter> totalParameters = new HashMap<>();
        currentKit.forEach((slot, piece) -> {
            if (piece != null) {
                ArmorComparator.ArmorParameter params = totalParameters.get(slot);
                if (params == null) {
                    params = new ArmorComparator.ArmorParameter(0, 0);
                }
                totalParameters.put(slot, new ArmorComparator.ArmorParameter(
                    params.defensePoints + piece.getDefensePoints(),
                    params.toughness + piece.getToughness()
                ));
            }
        });

        Map<EquipmentSlot, ArmorComparator.ArmorParameter> parametersMap = new HashMap<>();
        currentKit.forEach((slot, piece) -> {
            ArmorComparator.ArmorParameter totalParams = totalParameters.get(slot);
            if (piece != null) {
                parametersMap.put(slot, new ArmorComparator.ArmorParameter(
                    totalParams.defensePoints - piece.getDefensePoints(),
                    totalParams.toughness - piece.getToughness()
                ));
            } else {
                parametersMap.put(slot, totalParams);
            }
        });

        return new ArmorKitParameters(parametersMap);
    }
}
