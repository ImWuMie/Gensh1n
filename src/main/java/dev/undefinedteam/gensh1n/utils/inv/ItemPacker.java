package dev.undefinedteam.gensh1n.utils.inv;

import dev.undefinedteam.gensh1n.utils.inv.items.ItemFacet;
import dev.undefinedteam.gensh1n.utils.inv.slots.ItemSlot;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * After discovery phase (find all items, group them by their type, sort them by usefulness), this class tries to fit
 * the given requirements (max blocks, required stack count, etc.) and packs the given items in their target slots.
 * <p>
 * Items that were deemed useful can be found in {@link #usefulItems}.
 */
public class ItemPacker {
    /**
     * Items that have already been used. For example if already we used Inventory slot 12 as a sword, we cannot reuse
     * it as an axe in slot 2.
     */
    private HashSet<ItemSlot> alreadyAllocatedItems = new HashSet<>();

    /**
     * If an item is used by a move, it will be in this list.
     */
    public HashSet<ItemSlot> usefulItems = new HashSet<>();

    /**
     * Takes items from the {@code itemsToFillIn} list until it collected {@code maxItemCount} items is and {@code requiredStackCount}
     * stacks. The items are marked as useful and fills in hotbar slots if there are still slots to fill.
     *
     * @return returns the item moves ("swaps") that should to be executed.
     */
    public List<InventorySwap> packItems(List<ItemFacet> itemsToFillIn, List<ItemSlot> hotbarSlotsToFill, Set<ItemSlot> forbiddenSlots, ItemAmountContraintProvider contraintProvider) {
        List<InventorySwap> moves = new ArrayList<>();

        int requiredStackCount = (hotbarSlotsToFill != null) ? hotbarSlotsToFill.size() : 0;

        int currentStackCount = 0;
        int currentItemCount = 0;

        // The iterator of hotbar slots that still need filling.
        Iterator<ItemSlot> leftHotbarSlotIterator = (hotbarSlotsToFill != null) ? hotbarSlotsToFill.iterator() : null;

        for (ItemFacet filledInItem : itemsToFillIn) {
            SatisfactionStatus constraintsSatisfied = contraintProvider.getSatisfactionStatus(filledInItem);
            boolean allStacksFilled = currentStackCount >= requiredStackCount;

            if (allStacksFilled && (constraintsSatisfied == SatisfactionStatus.SATISFIED || constraintsSatisfied == SatisfactionStatus.OVERSATURATED)) {
                continue;
            }

            ItemSlot filledInItemSlot = filledInItem.getItemSlot();

            // The item is already allocated and marked as useful, so we cannot use it again.
            if (alreadyAllocatedItems.contains(filledInItemSlot)) {
                continue;
            }

            usefulItems.add(filledInItemSlot);

            contraintProvider.addItem(filledInItem);

            currentItemCount += filledInItem.getItemStack().getCount();
            currentStackCount++;

            // Don't fill in the item if (a) there is no place for it to go or (b) we aren't allowed to touch it.
            if (leftHotbarSlotIterator == null || forbiddenSlots.contains(filledInItemSlot)) {
                continue;
            }

            // Now find a fitting slot for the item.
            ItemSlot targetSlot = fillItemIntoSlot(filledInItemSlot, leftHotbarSlotIterator);

            if (targetSlot != null) {
                moves.add(new InventorySwap(filledInItemSlot, targetSlot));
            }
        }

        // Keep items that should be kept
        for (ItemFacet itemFacet : itemsToFillIn) {
            if (itemFacet.shouldKeep()) {
                usefulItems.add(itemFacet.getItemSlot());
            }
        }

        return moves;
    }

    /**
     * Packs the given item into a good slot in the given target slots.
     *
     * @return the target slot that this item should be moved to, if a move should occur.
     */
    private ItemSlot fillItemIntoSlot(ItemSlot filledInItemSlot, Iterator<ItemSlot> leftTargetSlotsToFill) {
        while (leftTargetSlotsToFill.hasNext()) {
            // Get the slots that still need to be filled if there are any (left/at all).
            ItemSlot hotbarSlotToFill = leftTargetSlotsToFill.next();

            // We don't need to move around equivalent items
            boolean areStacksSame = ItemStack.areItemsEqual(filledInItemSlot.getStack(), hotbarSlotToFill.getStack());

            if (filledInItemSlot == hotbarSlotToFill) {
                // The item is already in the potential target slot, don't change anything about it.
                alreadyAllocatedItems.add(hotbarSlotToFill);
                return null;
            } else if (areStacksSame) {
                // We mark the slot as used to prevent it being used for another slot.
                alreadyAllocatedItems.add(hotbarSlotToFill);
                // Find a new slot for the item
                continue;
            } else {
                // A move should occur
                // We will a swap. Both items have changed and should not be touched.
                alreadyAllocatedItems.add(filledInItemSlot);
                alreadyAllocatedItems.add(hotbarSlotToFill);
                return hotbarSlotToFill;
            }
        }

        // We found no target slot
        return null;
    }

    public interface ItemAmountContraintProvider {
        SatisfactionStatus getSatisfactionStatus(ItemFacet item);

        void addItem(ItemFacet item);
    }

    public enum SatisfactionStatus {
        NOT_SATISFIED,
        SATISFIED,
        OVERSATURATED
    }
}
