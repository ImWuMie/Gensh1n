package dev.undefinedteam.gensh1n.utils.inv;

import dev.undefinedteam.gensh1n.utils.inv.slots.ItemSlot;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
public class InventoryCleanupPlan {
    private Set<ItemSlot> usefulItems;
    private List<InventorySwap> swaps;
    private Map<ItemId, List<ItemSlot>> mergeableItems;

    public InventoryCleanupPlan(Set<ItemSlot> usefulItems, List<InventorySwap> swaps, Map<ItemId, List<ItemSlot>> mergeableItems) {
        this.usefulItems = usefulItems;
        this.swaps = swaps;
        this.mergeableItems = mergeableItems;
    }

    /**
     * Replaces the slot from key to value
     */
    public void remapSlots(Map<ItemSlot, ItemSlot> slotMap) {
        Set<ItemSlot> usefulItemsToAdd = new HashSet<>();
        Set<ItemSlot> usefulItemsToRemove = new HashSet<>();

        for (Map.Entry<ItemSlot, ItemSlot> entry : slotMap.entrySet()) {
            ItemSlot from = entry.getKey();
            ItemSlot to = entry.getValue();

            if (usefulItems.contains(from)) {
                usefulItemsToRemove.add(from);
                usefulItemsToAdd.add(to);
            }
        }

        this.usefulItems.removeAll(usefulItemsToRemove);
        this.usefulItems.addAll(usefulItemsToAdd);

        for (int index = 0; index < swaps.size(); index++) {
            InventorySwap hotbarSwap = swaps.get(index);
            InventorySwap newSwap = new InventorySwap(
                slotMap.getOrDefault(hotbarSwap.getFrom(), hotbarSwap.getFrom()),
                slotMap.getOrDefault(hotbarSwap.getTo(), hotbarSwap.getTo())
            );

            swaps.set(index, newSwap);
        }

        for (List<ItemSlot> mergeableItemsList : mergeableItems.values()) {
            for (int index = 0; index < mergeableItemsList.size(); index++) {
                ItemSlot itemSlot = mergeableItemsList.get(index);
                mergeableItemsList.set(index, slotMap.getOrDefault(itemSlot, itemSlot));
            }
        }
    }

    // Getters and setters for usefulItems, swaps, and mergeableItems
}

