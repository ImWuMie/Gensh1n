package dev.undefinedteam.gensh1n.system.modules.player;

import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.utils.inv.*;
import dev.undefinedteam.gensh1n.utils.inv.items.ItemFacet;
import dev.undefinedteam.gensh1n.utils.inv.slots.*;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.screen.slot.SlotActionType;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.*;

@StringEncryption
@ControlFlowObfuscation
public class InvManager1 extends Module {
    public InvManager1() {
        super(Categories.Player, "inv-manager1", "Manage your inventory");
    }

    private final SettingGroup sgDefault = settings.getDefaultGroup();
    private final SettingGroup sgSlot = settings.createGroup("Slot");

    private final Setting<Integer> maxBlocks = intN(sgDefault, "maximum-blocks", 512, 0, 2500);
    private final Setting<Integer> maxArrows = intN(sgDefault, "maximum-arrows", 128, 0, 2500);
    private final Setting<Integer> maxThrowables = intN(sgDefault, "maximum-throwables", 64, 0, 600);
    private final Setting<Integer> maxFoods = intN(sgDefault, "maximum-foods", 200, 0, 2000);

    private final Setting<Boolean> isGreedy = bool(sgDefault, "greedy", true);

    private final Setting<ItemSortChoice> offHandItem = choice(sgSlot, "offhand", ItemSortChoice.GAPPLE);
    private final Setting<ItemSortChoice> slotItem1 = choice(sgSlot, "slot-1", ItemSortChoice.WEAPON);
    private final Setting<ItemSortChoice> slotItem2 = choice(sgSlot, "slot-2", ItemSortChoice.BLOCK);
    private final Setting<ItemSortChoice> slotItem3 = choice(sgSlot, "slot-3", ItemSortChoice.WATER);
    private final Setting<ItemSortChoice> slotItem4 = choice(sgSlot, "slot-4", ItemSortChoice.THROWABLES);
    private final Setting<ItemSortChoice> slotItem5 = choice(sgSlot, "slot-5", ItemSortChoice.NONE);
    private final Setting<ItemSortChoice> slotItem6 = choice(sgSlot, "slot-6", ItemSortChoice.NONE);
    private final Setting<ItemSortChoice> slotItem7 = choice(sgSlot, "slot-7", ItemSortChoice.PICKAXE);
    private final Setting<ItemSortChoice> slotItem8 = choice(sgSlot, "slot-8", ItemSortChoice.AXE);
    private final Setting<ItemSortChoice> slotItem9 = choice(sgSlot, "slot-9", ItemSortChoice.PEARL);

    public CleanupPlanGenerator.CleanupPlanPlacementTemplate cleanupTemplateFromSettings() {
        Map<ItemSlot, ItemSortChoice> slotTargets = new HashMap<>();
        slotTargets.put(new OffHandSlot(), offHandItem.get());
        slotTargets.put(new HotbarItemSlot(0), slotItem1.get());
        slotTargets.put(new HotbarItemSlot(1), slotItem2.get());
        slotTargets.put(new HotbarItemSlot(2), slotItem3.get());
        slotTargets.put(new HotbarItemSlot(3), slotItem4.get());
        slotTargets.put(new HotbarItemSlot(4), slotItem5.get());
        slotTargets.put(new HotbarItemSlot(5), slotItem6.get());
        slotTargets.put(new HotbarItemSlot(6), slotItem7.get());
        slotTargets.put(new HotbarItemSlot(7), slotItem8.get());
        slotTargets.put(new HotbarItemSlot(8), slotItem9.get());

        Set<ItemSlot> forbiddenSlots = new HashSet<>();
        for (Map.Entry<ItemSlot, ItemSortChoice> entry : slotTargets.entrySet()) {
            if (entry.getValue() == ItemSortChoice.IGNORE) {
                forbiddenSlots.add(entry.getKey());
            }
        }
        for (int armorSlot = 0; armorSlot < 4; armorSlot++) {
            forbiddenSlots.add(new ArmorItemSlot(armorSlot));
        }

        AmountConstraintProvider constraintProvider = new AmountConstraintProvider(
            new HashMap<>() {{
                put(ItemSortChoice.BLOCK.getCategory(), maxBlocks.get());
                put(ItemSortChoice.THROWABLES.getCategory(), maxThrowables.get());
                put(new ItemCategory(ItemType.ARROW, 0), maxArrows.get());
            }},
            new HashMap<>() {{
                put(ItemFunction.FOOD, maxFoods.get());
                put(ItemFunction.WEAPON_LIKE, 1);
            }}
        );

        return new CleanupPlanGenerator.CleanupPlanPlacementTemplate(
            slotTargets,
            constraintProvider::getConstraints,
            isGreedy.get(),
            forbiddenSlots
        );
    }

    private static final List<HotbarItemSlot> HOTBAR_SLOTS = new ArrayList<>();
    private static final List<InventoryItemSlot> INVENTORY_SLOTS = new ArrayList<>();
    private static final OffHandSlot OFFHAND_SLOT = new OffHandSlot();
    private static final List<ArmorItemSlot> ARMOR_SLOTS = new ArrayList<>();
    private static final List<ItemSlot> ALL_SLOTS_IN_INVENTORY = new ArrayList<>();

    static {
        // Initialize hotbar slots
        for (int i = 0; i < 9; i++) {
            HOTBAR_SLOTS.add(new HotbarItemSlot(i));
        }

        // Initialize inventory slots
        for (int i = 0; i < 27; i++) {
            INVENTORY_SLOTS.add(new InventoryItemSlot(i));
        }

        // Initialize armor slots
        for (int i = 0; i < 4; i++) {
            ARMOR_SLOTS.add(new ArmorItemSlot(i));
        }

        // Combine all slots
        ALL_SLOTS_IN_INVENTORY.addAll(HOTBAR_SLOTS);
        ALL_SLOTS_IN_INVENTORY.add(OFFHAND_SLOT);
        ALL_SLOTS_IN_INVENTORY.addAll(INVENTORY_SLOTS);
        ALL_SLOTS_IN_INVENTORY.addAll(ARMOR_SLOTS);
    }

    @EventHandler
    private void onTick(TickEvent.Post e) {
        if (!(mc.currentScreen instanceof AbstractInventoryScreen<?>)) return;

        var cleanupPlan = new CleanupPlanGenerator(cleanupTemplateFromSettings(), ALL_SLOTS_IN_INVENTORY.stream().filter(a -> !a.getStack().isEmpty()).toList())
            .generatePlan();

        int windowId = mc.player.currentScreenHandler.syncId;
        // Step 1: Move items to the correct slots
        for (var hotbarSwap : cleanupPlan.getSwaps()) {
            if (!(hotbarSwap.getTo() instanceof HotbarItemSlot)) {
                throw new IllegalArgumentException("Cannot swap to non-hotbar-slot");
            }

            var from = hotbarSwap.getFrom().getIdForServer(null);
            var to = ((HotbarItemSlot) hotbarSwap.getTo()).getHotbarSlotForServer();

            if (from == -1 || to == -1) continue;


            mc.interactionManager.clickSlot(windowId, from, to, SlotActionType.SWAP, mc.player);

            // Assuming the method remapSlots exists within the cleanupPlan object
            cleanupPlan.remapSlots(
                new HashMap<>() {{
                    put(hotbarSwap.getFrom(), hotbarSwap.getTo());
                    put(hotbarSwap.getFrom(), hotbarSwap.getTo());
                }}
            );
        }

        // Step 2: Merge stacks
        List<ItemSlot> stacksToMerge = ItemMerge.findStacksToMerge(cleanupPlan);
        for (ItemSlot slot : stacksToMerge) {
            mc.interactionManager.clickSlot(windowId, slot.getIdForServerWithCurrentScreen(), 0, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(windowId, slot.getIdForServerWithCurrentScreen(), 0, SlotActionType.PICKUP_ALL, mc.player);
            mc.interactionManager.clickSlot(windowId, slot.getIdForServerWithCurrentScreen(), 0, SlotActionType.PICKUP, mc.player);
        }

        // It is important that we call findItemSlotsInInventory() here again, because the inventory has changed.
        List<ItemSlot> itemsToThrowOut = findItemsToThrowOut(cleanupPlan, ALL_SLOTS_IN_INVENTORY.stream().filter(a -> !a.getStack().isEmpty()).toList());

        for (ItemSlot slot : itemsToThrowOut) {
            mc.interactionManager.clickSlot(windowId, slot.getIdForServerWithCurrentScreen(), 1, SlotActionType.THROW, mc.player);
        }
    }

    public List<ItemSlot> findItemsToThrowOut(InventoryCleanupPlan cleanupPlan, List<ItemSlot> itemsInInv) {
        return itemsInInv.stream()
            .filter(itemSlot -> !cleanupPlan.getUsefulItems().contains(itemSlot))
            .toList();
    }

    private static class AmountConstraintProvider {
        private final Map<ItemCategory, Integer> maxItemsPerCategory;
        private final Map<ItemFunction, Integer> maxValuePerFunction;

        public AmountConstraintProvider(Map<ItemCategory, Integer> maxItemsPerCategory, Map<ItemFunction, Integer> maxValuePerFunction) {
            this.maxItemsPerCategory = maxItemsPerCategory;
            this.maxValuePerFunction = maxValuePerFunction;
        }

        public ArrayList<ItemConstraintInfo> getConstraints(ItemFacet facet) {
            ArrayList<ItemConstraintInfo> constraints = new ArrayList<>();

            if (facet.getProvidedItemFunctions().isEmpty()) {
                int defaultMin = facet.getCategory().getType().isOneIsSufficient() ? 1 : Integer.MAX_VALUE;
                Integer minValue = maxItemsPerCategory.get(facet.getCategory());
                int effectiveMinValue = (minValue != null) ? minValue : defaultMin;

                ItemConstraintInfo info = new ItemConstraintInfo(
                    new ItemCategoryConstraintGroup(new IntRange(effectiveMinValue, Integer.MAX_VALUE), 10, facet.getCategory()),
                    facet.getItemStack().getCount()
                );

                constraints.add(info);
            } else {
                for (var entry : facet.getProvidedItemFunctions()) {
                    ItemFunction function = entry.getLeft();
                    int amountAdded = entry.getRight();

                    ItemConstraintInfo info = new ItemConstraintInfo(
                        new ItemFunctionCategoryConstraintGroup(
                            new IntRange(maxValuePerFunction.getOrDefault(function, 1), Integer.MAX_VALUE), 10, function
                        ),
                        amountAdded
                    );

                    constraints.add(info);
                }
            }

            return constraints;
        }
    }
}
