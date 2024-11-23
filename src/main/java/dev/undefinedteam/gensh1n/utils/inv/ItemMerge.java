package dev.undefinedteam.gensh1n.utils.inv;

import dev.undefinedteam.gensh1n.utils.inv.slots.ItemSlot;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemMerge {
    /**
     * Find all item stack ids which should be double-clicked in order to merge them
     */
    public static List<ItemSlot> findStacksToMerge(InventoryCleanupPlan cleanupPlan) {
        List<ItemSlot> itemsToMerge = new ArrayList<>();

        for (Map.Entry<ItemId, List<ItemSlot>> mergeableItem : cleanupPlan.getMergeableItems().entrySet()) {
            int maxStackSize = mergeableItem.getKey().getItem().getMaxCount();

            if (!canMerge(mergeableItem.getValue(), maxStackSize)) {
                continue;
            }

            List<MergeableStack> stacks = mergeableItem.getValue().stream()
                    .map(slot -> new MergeableStack(slot, slot.getStack().getCount()))
                    .toList();

            mergeStacks(itemsToMerge, stacks, maxStackSize);
        }

        return itemsToMerge;
    }

    static class MergeableStack {
        ItemSlot slot;
        int count;

        public MergeableStack(ItemSlot slot, int count) {
            this.slot = slot;
            this.count = count;
        }
    }

    private static void mergeStacks(List<ItemSlot> itemsToDoubleclick, List<MergeableStack> stacks, int maxStackSize) {
        if (stacks.size() <= 1) {
            return;
        }

        stacks.sort((stack1, stack2) -> stack1.count - stack2.count);

        // Remove
        while (!stacks.isEmpty() && stacks.get(stacks.size() - 1).count + stacks.get(0).count > maxStackSize) {
            stacks.remove(stacks.size() - 1);
        }

        // Find the biggest stack that can be merged
        MergeableStack itemToDoubleclick = stacks.remove(stacks.size() - 1);
        if (itemToDoubleclick != null) {
            itemsToDoubleclick.add(itemToDoubleclick.slot);

            int itemsToRemove = maxStackSize - itemToDoubleclick.count;

            // Remove all small stacks that have been removed by last merge
            while (itemsToRemove > 0 && !stacks.isEmpty()) {
                MergeableStack stack = stacks.get(0);

                int count = stack.count;

                if (count < itemsToRemove) {
                    stacks.remove(0);
                } else {
                    stack.count -= itemsToRemove;
                    itemsToRemove -= stack.count;
                }
            }

            mergeStacks(itemsToDoubleclick, stacks, maxStackSize);
        }
    }

    private static boolean canMerge(List<ItemSlot> items, int maxStackSize) {
        int totalCount = items.stream().mapToInt(i -> i.getStack().getCount()).sum();

        int mergedStackCount = (int) Math.ceil(totalCount / (double) maxStackSize);

        return items.size() > mergedStackCount;
    }
}
