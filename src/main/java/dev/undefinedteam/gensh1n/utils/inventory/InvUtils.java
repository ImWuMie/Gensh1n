package dev.undefinedteam.gensh1n.utils.inventory;

import dev.undefinedteam.gensh1n.mixin_interface.IClientPlayerInteractionManager;
import dev.undefinedteam.gensh1n.utils.world.BlockInfo;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.block.*;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static dev.undefinedteam.gensh1n.Client.HOTBAR;
import static dev.undefinedteam.gensh1n.Client.mc;

@SuppressWarnings("DataFlowIssue")
public class InvUtils {
    private static final Action ACTION = new Action();
    public static int previousSlot = -1;
    public static int pickSlot = -1;
    private static int[] slots;

    // Predicates

    public static boolean testInMainHand(Predicate<ItemStack> predicate) {
        return predicate.test(mc.player.getInventory().getStack(HOTBAR.getSlot()));
    }

    public static boolean testInMainHand(Item... items) {
        return testInMainHand(itemStack -> {
            for (var item : items) if (itemStack.isOf(item)) return true;
            return false;
        });
    }

    public static boolean testInOffHand(Predicate<ItemStack> predicate) {
        return predicate.test(mc.player.getOffHandStack());
    }

    public static boolean testInOffHand(Item... items) {
        return testInOffHand(itemStack -> {
            for (var item : items) if (itemStack.isOf(item)) return true;
            return false;
        });
    }

    public static boolean testInHands(Predicate<ItemStack> predicate) {
        return testInMainHand(predicate) || testInOffHand(predicate);
    }

    public static boolean testInHands(Item... items) {
        return testInMainHand(items) || testInOffHand(items);
    }

    public static boolean testInHotbar(Predicate<ItemStack> predicate) {
        if (testInHands(predicate)) return true;

        for (int i = SlotUtils.HOTBAR_START; i < SlotUtils.HOTBAR_END; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (predicate.test(stack)) return true;
        }

        return false;
    }

    public static boolean testInHotbar(Item... items) {
        return testInHotbar(itemStack -> {
            for (var item : items) if (itemStack.isOf(item)) return true;
            return false;
        });
    }

    // Finding items

    public static FindItemResult findEmpty() {
        return find(ItemStack::isEmpty);
    }

    public static FindItemResult findInHotbar(Item... items) {
        return findInHotbar(itemStack -> {
            for (Item item : items) {
                if (itemStack.getItem().equals(item)) return true;
            }
            return false;
        });
    }

    public static FindItemResult findInHotbar(Predicate<ItemStack> isGood) {
        if (testInOffHand(isGood)) {
            return new FindItemResult(SlotUtils.OFFHAND, mc.player.getOffHandStack().getCount());
        }

        if (testInMainHand(isGood)) {
            var slot = HOTBAR.getSlot();
            return new FindItemResult(slot, mc.player.getInventory().getStack(slot).getCount());
        }

        return find(isGood, 0, 8);
    }

    public static FindItemResult find(Item... items) {
        return find(itemStack -> {
            for (Item item : items) {
                if (itemStack.getItem() == item) return true;
            }
            return false;
        });
    }

    public static FindItemResult find(Predicate<ItemStack> isGood) {
        if (mc.player == null) return new FindItemResult(0, 0);
        return find(isGood, 0, mc.player.getInventory().size());
    }

    public static FindItemResult find(Predicate<ItemStack> isGood, int start, int end) {
        if (mc.player == null) return new FindItemResult(0, 0);

        int slot = -1, count = 0;

        for (int i = start; i <= end; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);

            if (isGood.test(stack)) {
                if (slot == -1) slot = i;
                count += stack.getCount();
            }
        }

        return new FindItemResult(slot, count);
    }

    public static FindItemResult findFastestTool(BlockState state) {
        float bestScore = 1;
        int slot = -1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isSuitableFor(state)) continue;

            float score = stack.getMiningSpeedMultiplier(state);
            if (score > bestScore) {
                bestScore = score;
                slot = i;
            }
        }

        return new FindItemResult(slot, 1);
    }

    public static FindItemResult findInstaMineTool(BlockPos pos,boolean checkSuitable) {
        var state = mc.world.getBlockState(pos);
        int slot = -1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof SwordItem item && item.getMaterial().equals(ToolMaterials.WOOD)) continue;
            if (!stack.isSuitableFor(state) && checkSuitable) continue;

            float score = BlockInfo.getBlockBreakingSpeed(state, i);
            if (BlockInfo.canInstaBreak(pos,score)) {
                slot = i;
                break;
            }
        }

        return new FindItemResult(slot, 1);
    }

    // Interactions

    public static boolean swap(int slot, boolean swapBack) {
        if (slot == SlotUtils.OFFHAND) return true;
        if (slot < 0 || slot > 8) return false;
        if (swapBack && previousSlot == -1) previousSlot = mc.player.getInventory().selectedSlot;
        else if (!swapBack) previousSlot = -1;

        mc.player.getInventory().selectedSlot = slot;
        ((IClientPlayerInteractionManager) mc.interactionManager).gensh1n$syncSelected();
        return true;
    }

    public static boolean swapBack() {
        if (previousSlot == -1) return false;

        boolean return_ = swap(previousSlot, false);
        previousSlot = -1;
        return return_;
    }

    public static Action move() {
        ACTION.type = SlotActionType.PICKUP;
        ACTION.two = true;
        return ACTION;
    }

    public static Action click() {
        ACTION.type = SlotActionType.PICKUP;
        return ACTION;
    }

    /**
     * When writing code with quickSwap, both to and from should provide the ID of a slot, not the index.
     * From should be the slot in the hotbar, to should be the slot you're switching an item from.
     */

    public static Action quickSwap() {
        ACTION.type = SlotActionType.SWAP;
        return ACTION;
    }

    public static Action shiftClick() {
        ACTION.type = SlotActionType.QUICK_MOVE;
        return ACTION;
    }

    public static Action drop() {
        ACTION.type = SlotActionType.THROW;
        ACTION.data = 1;
        return ACTION;
    }

    public static void dropHand() {
        if (!mc.player.currentScreenHandler.getCursorStack().isEmpty())
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, ScreenHandler.EMPTY_SPACE_SLOT_INDEX, 0, SlotActionType.PICKUP, mc.player);
    }

    public static boolean pickSwitch(int slot) {
        if (slot >= 0) {
            pickSlot = slot;
            mc.getNetworkHandler().sendPacket(new PickFromInventoryC2SPacket(slot));

            return true;
        }
        return false;
    }

    public static void switchTo(int slot) {
        if (mc.player == null || mc.getNetworkHandler() == null) return;
        if (mc.player.getInventory().selectedSlot == slot) return;
        mc.player.getInventory().selectedSlot = slot;
        mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
    }

    public static void packetTo(int slot) {
        if (mc.player == null || mc.getNetworkHandler() == null) return;
        if (mc.player.getInventory().selectedSlot == slot) return;
        mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
    }

    public static void pickSwapBack() {
        if (pickSlot >= 0) {
            mc.getNetworkHandler().sendPacket(new PickFromInventoryC2SPacket(pickSlot));
            pickSlot = -1;
        }
    }

    private static final List<Item> WHITELISTED_ITEMS = Arrays.asList(Items.FISHING_ROD, Items.WATER_BUCKET, Items.BUCKET, Items.ARROW, Items.BOW, Items.SNOWBALL, Items.EGG, Items.ENDER_PEARL,Items.FIRE_CHARGE);

    public static boolean useful(final ItemStack stack) {
        final Item item = stack.getItem();

        //if (item instanceof PotionItem potion) {
        //    return (potion instanceof SplashPotionItem) && PlayerUtils.goodPotion(PotionUtil.getPotionEffects(stack).get(0).getEffectType());
        //}

        if (item instanceof BlockItem) {
            final Block block = ((BlockItem) item).getBlock();
            if (block instanceof TintedGlassBlock || block instanceof StainedGlassBlock || (/*block.isFullBlock() && */!(block instanceof TntBlock || block instanceof SlimeBlock || block instanceof FallingBlock))) {
                return true;
            }
        }

        return item instanceof SwordItem ||
            item instanceof ToolItem ||
            item instanceof ArmorItem ||
            item.getFoodComponent() != null ||
            isGodItem(stack) ||
            WHITELISTED_ITEMS.contains(item);
    }

    public static boolean isGodItem(ItemStack stack) {
        if (!stack.isEmpty()) {
            if (stack.getItem() == Items.TNT) return true;
            if (stack.getItem() == Items.END_CRYSTAL) return true;
            if (stack.getItem() == Items.WATER_BUCKET) return true;
            if (stack.getItem() == Items.TOTEM_OF_UNDYING) return true;
            if (stack.getEnchantments() != null) {
                NbtList enchantments = stack.getEnchantments();
                for (int i = 0; i < enchantments.size(); i++) {
                    NbtCompound enchTag = enchantments.getCompound(i);
                    int lvl = EnchantmentHelper.getLevelFromNbt(enchTag);
                    String name = Objects.requireNonNull(EnchantmentHelper.getIdFromNbt(enchTag)).toString();
                    if (name != null) {
                        if (isGodItemEnch(name, lvl)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean isGodItemEnch(String id, int lvl) {
        if (id.equals("minecraft:sharpness")) {
            if (lvl >= 5) {
                return true;
            }
        }
        if (id.equals("minecraft.knockback")) {
            return lvl >= 3;
        }
        return false;
    }

    public static class Action {
        private SlotActionType type = null;
        private boolean two = false;
        private int from = -1;
        private int to = -1;
        private int data = 0;

        private boolean isRecursive = false;

        private Action() {
        }

        // From

        public Action fromId(int id) {
            from = id;
            return this;
        }

        public Action from(int index) {
            return fromId(SlotUtils.indexToId(index));
        }

        public Action fromHotbar(int i) {
            return from(SlotUtils.HOTBAR_START + i);
        }

        public Action fromOffhand() {
            return from(SlotUtils.OFFHAND);
        }

        public Action fromMain(int i) {
            return from(SlotUtils.MAIN_START + i);
        }

        public Action fromArmor(int i) {
            return from(SlotUtils.ARMOR_START + (3 - i));
        }

        // To

        public void toId(int id) {
            to = id;
            run();
        }

        public void to(int index) {
            toId(SlotUtils.indexToId(index));
        }

        public void toHotbar(int i) {
            to(SlotUtils.HOTBAR_START + i);
        }

        public void toOffhand() {
            to(SlotUtils.OFFHAND);
        }

        public void toMain(int i) {
            to(SlotUtils.MAIN_START + i);
        }

        public void toArmor(int i) {
            to(SlotUtils.ARMOR_START + (3 - i));
        }

        // Slot

        public void slotId(int id) {
            from = to = id;
            run();
        }

        public void slot(int index) {
            slotId(SlotUtils.indexToId(index));
        }

        public void slotHotbar(int i) {
            slot(SlotUtils.HOTBAR_START + i);
        }

        public void slotOffhand() {
            slot(SlotUtils.OFFHAND);
        }

        public void slotMain(int i) {
            slot(SlotUtils.MAIN_START + i);
        }

        public void slotArmor(int i) {
            slot(SlotUtils.ARMOR_START + (3 - i));
        }

        // Other

        private void run() {
            boolean hadEmptyCursor = mc.player.currentScreenHandler.getCursorStack().isEmpty();

            if (type == SlotActionType.SWAP) {
                data = from;
                from = to;
            }

            if (type != null && from != -1 && to != -1) {
                click(from);
                if (two) click(to);
            }

            SlotActionType preType = type;
            boolean preTwo = two;
            int preFrom = from;
            int preTo = to;

            type = null;
            two = false;
            from = -1;
            to = -1;
            data = 0;

            if (!isRecursive && hadEmptyCursor && preType == SlotActionType.PICKUP && preTwo && (preFrom != -1 && preTo != -1) && !mc.player.currentScreenHandler.getCursorStack().isEmpty()) {
                isRecursive = true;
                InvUtils.click().slotId(preFrom);
                isRecursive = false;
            }
        }

        private void click(int id) {
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, id, data, type, mc.player);
        }
    }
}
