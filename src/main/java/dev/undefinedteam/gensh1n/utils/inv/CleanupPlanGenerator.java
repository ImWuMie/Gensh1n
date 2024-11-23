package dev.undefinedteam.gensh1n.utils.inv;

import dev.undefinedteam.gensh1n.utils.inv.items.ItemFacet;
import dev.undefinedteam.gensh1n.utils.inv.slots.ItemSlot;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CleanupPlanGenerator implements ItemPacker.ItemAmountContraintProvider {
    private final CleanupPlanPlacementTemplate template;
    private final List<ItemSlot> availableItems;
    private final ArrayList<InventorySwap> hotbarSwaps = new ArrayList<>();
    private final ItemPacker packer = new ItemPacker();
    private final Map<ItemNumberContraintGroup, Integer> currentLimit = new HashMap<>();
    private final Map<ItemCategory, List<ItemSlot>> categoryToSlotsMap;

    public CleanupPlanGenerator(CleanupPlanPlacementTemplate template, List<ItemSlot> availableItems) {
        this.template = template;
        this.availableItems = availableItems;

        this.categoryToSlotsMap = getCategoryToSlotsMap(template.slotContentMap);
    }

    private Map<ItemCategory, List<ItemSlot>> getCategoryToSlotsMap(Map<ItemSlot, ItemSortChoice> slotContentMap) {
        Map<ItemCategory, List<ItemSlot>> categoryMap = new HashMap<>();

        for (var entry : slotContentMap.entrySet()) {
            var itemType = entry.getValue();
            if (itemType.getCategory() != null) {
                ItemCategory category = itemType.getCategory();
                if (!categoryMap.containsKey(category)) {
                    var list = new ArrayList<ItemSlot>();
                    list.add(entry.getKey());
                    categoryMap.put(category, list);
                } else {
                    categoryMap.get(category).add(entry.getKey());
                }
            }
        }

        return categoryMap;
    }

    public InventoryCleanupPlan generatePlan() {
        ItemCategorization categorizer = new ItemCategorization(availableItems);

        List<ItemFacet> itemFacets = availableItems.stream()
            .flatMap(slot -> Arrays.stream(categorizer.getItemFacets(slot)))
            .collect(Collectors.toList());

        Map<ItemCategory, List<ItemFacet>> facetsGroupedByType = itemFacets.stream()
            .collect(Collectors.groupingBy(ItemFacet::getCategory));

        facetsGroupedByType.forEach(this::processItemCategory);

        packer.usefulItems.addAll(template.getForbiddenSlots());

        return new InventoryCleanupPlan(
            packer.usefulItems,
            hotbarSwaps,
            groupItemsByType()
        );
    }

    private void processItemCategory(ItemCategory category, List<ItemFacet> availableItems) {
        List<ItemSlot> hotbarSlotsToFill = categoryToSlotsMap.get(category);

        availableItems.sort(Comparator.reverseOrder());

        List<InventorySwap> requiredMoves = packer.packItems(
            availableItems,
            hotbarSlotsToFill,
            template.getForbiddenSlots(),
            this
        );

        hotbarSwaps.addAll(requiredMoves);
    }

    private Map<ItemId, List<ItemSlot>> groupItemsByType() {
        Map<ItemId, List<ItemSlot>> itemsByType = new HashMap<>();

        for (ItemSlot availableSlot : availableItems) {
            ItemStack stack = availableSlot.getStack();

            if (stack.isEmpty() || !stack.isStackable() || stack.getCount() >= stack.getMaxCount()) {
                continue;
            }

            ItemId itemType = new ItemId(stack.getItem(), stack.getNbt());
            itemsByType.computeIfAbsent(itemType, k -> new ArrayList<>()).add(availableSlot);
        }

        return itemsByType;
    }

    @Override
    public ItemPacker.SatisfactionStatus getSatisfactionStatus(ItemFacet item) {
        List<ItemConstraintInfo> constraints = template.getItemAmountConstraintProvider().apply(item);

        constraints.sort(Comparator.comparingInt(c -> c.getGroup().priority));

        for (ItemConstraintInfo constraintInfo : constraints) {
            int currentCount = currentLimit.getOrDefault(constraintInfo.getGroup(), 0);

            if (currentCount > constraintInfo.getGroup().acceptableRange.getUpper()) {
                return ItemPacker.SatisfactionStatus.OVERSATURATED;
            } else if (currentCount < constraintInfo.getGroup().acceptableRange.getLower()) {
                return ItemPacker.SatisfactionStatus.NOT_SATISFIED;
            }
        }

        return ItemPacker.SatisfactionStatus.SATISFIED;
    }

    @Override
    public void addItem(ItemFacet item) {
        List<ItemConstraintInfo> constraints = template.getItemAmountConstraintProvider().apply(item);

        for (ItemConstraintInfo constraintInfo : constraints) {
            int current = currentLimit.getOrDefault(constraintInfo.getGroup(), 0);
            currentLimit.put(constraintInfo.getGroup(), current + constraintInfo.getAmountAddedByItem());
        }
    }

    public static class CleanupPlanPlacementTemplate {
        private final Map<ItemSlot, ItemSortChoice> slotContentMap;
        private final Function<ItemFacet, ArrayList<ItemConstraintInfo>> itemAmountConstraintProvider;
        private final boolean isGreedy;
        private final Set<ItemSlot> forbiddenSlots;

        public CleanupPlanPlacementTemplate(Map<ItemSlot, ItemSortChoice> slotContentMap,
                                            Function<ItemFacet, ArrayList<ItemConstraintInfo>> itemAmountConstraintProvider,
                                            boolean isGreedy, Set<ItemSlot> forbiddenSlots) {
            this.slotContentMap = slotContentMap;
            this.itemAmountConstraintProvider = itemAmountConstraintProvider;
            this.isGreedy = isGreedy;
            this.forbiddenSlots = forbiddenSlots;
        }

        public Map<ItemSlot, ItemSortChoice> getSlotContentMap() {
            return slotContentMap;
        }

        public Function<ItemFacet, ArrayList<ItemConstraintInfo>> getItemAmountConstraintProvider() {
            return itemAmountConstraintProvider;
        }

        public boolean isGreedy() {
            return isGreedy;
        }

        public Set<ItemSlot> getForbiddenSlots() {
            return forbiddenSlots;
        }
    }
}
