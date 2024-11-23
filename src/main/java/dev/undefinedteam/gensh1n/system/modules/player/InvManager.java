package dev.undefinedteam.gensh1n.system.modules.player;

import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.events.player.AttackEvent;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.system.modules.Modules;
import dev.undefinedteam.gensh1n.utils.RandomUtils;
import dev.undefinedteam.gensh1n.utils.inventory.InvUtils;
import dev.undefinedteam.gensh1n.utils.time.MSTimer;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.*;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;


@StringEncryption
@ControlFlowObfuscation
public class InvManager extends Module {
    public final MSTimer stopwatch = new MSTimer();
    private final SettingGroup sgDefault = settings.getDefaultGroup();
    public final Setting<Integer> minDelay = intN(sgDefault, "Min Delay", 140, 0, 500);
    public final Setting<Integer> maxDelay = intN(sgDefault, "Max Delay", 190, 0, 500);
    public final Setting<Boolean> legit = bool(sgDefault, "Legit", true);
    public final Setting<Boolean> ad = bool(sgDefault, "AutoDisable", true);
    public final Setting<Integer> swordSlot = intN(sgDefault, "Sword Slot", 1, 1, 9);
    //private final RangeVal        ue pickaxeSlot = new NumberValue("Pickaxe Slot", 5d, 1, 9, 1);
    //private final NumberValue axeSlot = new NumberValue("Axe Slot", 3d, 1d, 9d, 1d);
    //private final NumberValue shovelSlot = new NumberValue("Shovel Slot", 4d, 1d, 9d, 1d);
    public final Setting<Integer> blockSlot = intN(sgDefault, "Block Slot", 2, 1, 9);
    public final Setting<Integer> potionSlot = intN(sgDefault, "Potion Slot", 6, 1, 9);
    public final Setting<Integer> foodSlot = intN(sgDefault, "Food Slot", 9, 1, 9);
    private final int INVENTORY_ROWS = 4, INVENTORY_COLUMNS = 9, ARMOR_SLOTS = 4;
    private final int INVENTORY_SLOTS = (INVENTORY_ROWS * INVENTORY_COLUMNS) + ARMOR_SLOTS;
    private int chestTicks, attackTicks, placeTicks;
    private boolean moved, open;
    private long nextClick;

    public InvManager() {
        super(Categories.Player, "inv-manager", "Manage your inventory");
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.world != null && mc.player != null && ad.get()) {
            if (!mc.player.isAlive()) {
                Modules.get().get(InvManager.class).toggle();
                nInfo("[AutoDisable] InvManager",NSHORT);
                return;
            }
            if (mc.player.age <= 1) {
                Modules.get().get(InvManager.class).toggle();
                nInfo("[AutoDisable] InvManager",NSHORT);
            }
        }
    }

    @EventHandler
    public void onUpdate(TickEvent.Post event) {
        if (mc.player.age <= 40) {
            return;
        }
        if (mc.currentScreen instanceof GenericContainerScreen) {
            this.chestTicks = 0;
        } else {
            this.chestTicks++;
        }

        this.attackTicks++;
        this.placeTicks++;
        if (legit.get() && !(mc.currentScreen instanceof AbstractInventoryScreen<?>)) {
            this.stopwatch.reset();
            return;
        }
        if (!this.stopwatch.check(this.nextClick) || this.chestTicks < 10 || this.attackTicks < 10 || this.placeTicks < 10) {
            this.closeInventory();
            return;
        }
        //if (!StarryClient.getInstance().getModuleManager().getByClass(InventoryMove.class).isEnabled() && !(mc.currentScreen instanceof InventoryScreen)) {
        //    return;
        //}
        this.moved = false;

        int helmet = -1;
        int chestplate = -1;
        int leggings = -1;
        int boots = -1;

        int sword = -1;
        int pickaxe = -1;
        int axe = -1;
        int shovel = -1;
        int block = -1;
        int potion = -1;
        int food = -1;


        for (int i = 0; i < INVENTORY_SLOTS; i++) {
            final ItemStack stack = mc.player.getInventory().getStack(i);

            if (stack.isEmpty()) {
                continue;
            }

            final Item item = stack.getItem();

            if (item instanceof ArmorItem armor) {
                final int reduction = this.armorReduction(stack);
                switch (armor.getSlotType()) {
                    case HEAD:
                        if (helmet == -1 || reduction > armorReduction(mc.player.getInventory().getStack(helmet))) {
                            helmet = i;
                        }
                        break;

                    case CHEST:
                        if (chestplate == -1 || reduction > armorReduction(mc.player.getInventory().getStack(chestplate))) {
                            chestplate = i;
                        }
                        break;

                    case LEGS:
                        if (leggings == -1 || reduction > armorReduction(mc.player.getInventory().getStack(leggings))) {
                            leggings = i;
                        }
                        break;

                    case FEET:
                        if (boots == -1 || reduction > armorReduction(mc.player.getInventory().getStack(boots))) {
                            boots = i;
                        }
                        break;
                }
            }
        }

        for (int i = 0; i < INVENTORY_SLOTS; i++) {
            final ItemStack stack = mc.player.getInventory().getStack(i);

            if (stack.isEmpty()) {
                continue;
            }

            final Item item = stack.getItem();

            if (item instanceof ArmorItem armor) {

                switch (armor.getSlotType()) {
                    case HEAD:
                        if (i != helmet) {
                            this.throwItem(i);
                        }
                        break;

                    case CHEST:
                        if (i != chestplate) {
                            this.throwItem(i);
                        }
                        break;

                    case LEGS:
                        if (i != leggings) {
                            this.throwItem(i);
                        }
                        break;

                    case FEET:
                        if (i != boots) {
                            this.throwItem(i);
                        }
                        break;
                }
            }
        }
        if (helmet != -1 && helmet != 39) {
            this.equipItem(helmet);
        }

        if (chestplate != -1 && chestplate != 38) {
            this.equipItem(chestplate);
        }

        if (leggings != -1 && leggings != 37) {
            this.equipItem(leggings);
        }

        if (boots != -1 && boots != 36) {
            this.equipItem(boots);
        }

        for (int i = 0; i < INVENTORY_SLOTS; i++) {
            final ItemStack stack = mc.player.getInventory().getStack(i);

            if (stack.isEmpty()) {
                continue;
            }

            final Item item = stack.getItem();
            //if(item instanceof ArmorItem){
            //    System.out.println(item.getName().getString() + " is on " + i);
            //}
            if (!InvUtils.useful(stack)) {
                this.throwItem(i);
            }
            //a
            if (item instanceof SwordItem) {
                if (sword == -1 || damage(stack) > damage(mc.player.getInventory().getStack(sword))) {
                    sword = i;
                    //System.out.println(i);
                }

                if (i != sword) {
                    this.throwItem(i);
                }
            }

            if (item instanceof PickaxeItem) {
                if (pickaxe == -1 || mineSpeed(stack) > mineSpeed(mc.player.getInventory().getStack(pickaxe))) {
                    pickaxe = i;
                }

                if (i != pickaxe) {
                    this.throwItem(i);
                }
            }
            if (item instanceof AxeItem) {
                if (axe == -1 || mineSpeed(stack) > mineSpeed(mc.player.getInventory().getStack(axe))) {
                    axe = i;
                }

                if (i != axe) {
                    this.throwItem(i);
                }
            }

            if (item instanceof ShovelItem) {
                if (shovel == -1 || mineSpeed(stack) > mineSpeed(mc.player.getInventory().getStack(shovel))) {
                    shovel = i;
                }

                if (i != shovel) {
                    this.throwItem(i);
                }
            }

            if (item instanceof BlockItem) {
                if (block == -1) {
                    block = i;
                } else {
                    final ItemStack currentStack = mc.player.getInventory().getStack(block);


                    if (currentStack.isEmpty() || stack.getCount() > currentStack.getCount()) {
                        block = i;
                    }
                }
            }

            /*if (item instanceof PotionItem) {
                if (potion == -1) {
                    potion = i;
                } else {
                    final ItemStack currentStack = mc.player.getInventory().getStack(potion);

                    if (currentStack.isEmpty()) {
                        continue;
                    }

                    final PotionUtil.ConsumeBehavior currentConsumeBehavior = stack.getOrCreateSubTag("Potion").getBoolean("Tipped") ? PotionUtil.ConsumeBehavior.DRINK : PotionUtil.ConsumeBehavior.NORMAL;
                    final PotionUtil.ConsumeBehavior consumeBehavior = stack.getOrCreateSubTag("Potion").getBoolean("Tipped") ? PotionUtil.ConsumeBehavior.DRINK : PotionUtil.ConsumeBehavior.NORMAL;

                    if ((consumeBehavior == PotionUtil.ConsumeBehavior.EAT || consumeBehavior == PotionUtil.ConsumeBehavior.DRINK) &&
                            consumeBehavior != currentConsumeBehavior) {
                        potion = i;
                    }
                }
            }*/

            if (item.getFoodComponent() != null) {
                if (food == -1) {
                    food = i;
                }
            }

        }


        if (sword != -1 && sword != this.swordSlot.get() - 1) {
            this.moveItem(sword, this.swordSlot.get() - 1);
        }

        /*if (pickaxe != -1 && pickaxe != this.pickaxeSlot.get() - 1) {
            this.moveItem(pickaxe, this.pickaxeSlot.get() - 37);
        }

        if (axe != -1 && axe != this.axeSlot.get() - 1) {
            this.moveItem(axe, this.axeSlot.get() - 37);
        }

        if (shovel != -1 && shovel != this.shovelSlot.get() - 1) {
            this.moveItem(shovel, this.shovelSlot.get() - 37);
        }*/
        if (block != -1 && block != this.blockSlot.get() - 1) {
            this.moveItem(block, this.blockSlot.get() - 1);
        }

        if (potion != -1 && potion != this.potionSlot.get() - 1) {
            this.moveItem(potion, this.potionSlot.get() - 1);
        }

        if (food != -1 && food != this.foodSlot.get() - 1) {
            this.moveItem(food, this.foodSlot.get() - 1);
        }

        if (this.canOpenInventory() && !this.moved) {
            this.closeInventory();
        }

    }

    @EventHandler
    public void onAttack(AttackEvent event) {
        this.attackTicks = 0;
    }

    public void onDisable() {
        if (this.canOpenInventory()) {
            this.closeInventory();
        }
    }

    private int armorReduction(final ItemStack stack) {
        final ArmorItem armor = (ArmorItem) stack.getItem();
        return armor.getProtection();
    }

    private void openInventory() {
        if (!this.open) {
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.OPEN_INVENTORY));
            this.open = true;
        }
    }

    private void closeInventory() {
        if (this.open) {
            mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId));
            this.open = false;
        }
    }

    private boolean canOpenInventory() {
        return true;//StarryClient.getInstance().getModuleManager().getByClass(InventoryMove.class).isEnabled() && !(mc.currentScreen instanceof InventoryScreen);
    }

    private void throwItem(int slot) {
        if ((!this.moved || this.nextClick <= 0)) {

            if (this.canOpenInventory()) {
                this.openInventory();
            }
            int target = slot;
            if (slot >= 36) {
                target = 44 - slot;
            }
            if (slot >= 0 && slot <= 8) {
                target = slot + 36;
            }
            //ChatUtils.SendChat("Throwing: " + slot + " Item: " + mc.player.getInventory().getStack(slot).getName().getString());
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, target, 1, SlotActionType.THROW, mc.player);
            this.nextClick = Math.round(RandomUtils.nextInt(this.minDelay.get(), this.maxDelay.get()));
            this.stopwatch.reset();
            this.moved = true;
        }
    }

    private void moveItem(final int slot, final int destination) {
        if ((!this.moved || this.nextClick <= 0)) {

            if (this.canOpenInventory()) {
                this.openInventory();
            }
            int windowId = mc.player.currentScreenHandler.syncId;
            //int adjustedSlot = slot < 9 ? slot + 36 : slot;
            //int adjustedDestination = destination < 9 ? destination + 36 : destination;

            // Swap the item in the specified slot with the item in the destination slot
            if (slot > 8) {
                mc.interactionManager.clickSlot(windowId, slot, destination, SlotActionType.SWAP, mc.player);
            } else {
                int adjustedSlot = slot < 9 ? slot + 36 : slot;
                int adjustedDestination = destination < 9 ? destination + 36 : destination;
                //PIck up the item from the original slot
                mc.interactionManager.clickSlot(windowId, adjustedSlot, 0, SlotActionType.PICKUP, mc.player);

                // Place the item in the destination slot
                mc.interactionManager.clickSlot(windowId, adjustedDestination, 0, SlotActionType.PICKUP, mc.player);

                // Ensure no item is held (clear the cursor)
                mc.interactionManager.clickSlot(windowId, adjustedSlot, 0, SlotActionType.PICKUP, mc.player);
            }

            this.nextClick = Math.round(RandomUtils.nextInt(this.minDelay.get(), this.maxDelay.get()));
            this.stopwatch.reset();
            this.moved = true;
        }
    }

    private void equipItem(final int slot) {
        if ((!this.moved || this.nextClick <= 0)) {
            if (this.canOpenInventory()) {
                this.openInventory();
            }
            assert mc.player != null;
            ItemStack stack = mc.player.getInventory().getStack(slot);
            Item i = stack.getItem();
            ArmorItem ar = (ArmorItem) i;
            assert mc.interactionManager != null;
            int windowId = mc.player.currentScreenHandler.syncId;
            //mc.interactionManager.clickSlot(windowId, slot, 0, SlotActionType.PICKUP, mc.player);
            if (slot > 8) {
                switch (ar.getSlotType()) {
                    case HEAD:
                        if (!mc.player.getInventory().getStack(39).isEmpty()) throwItem(39);
                        mc.interactionManager.clickSlot(windowId, slot, 0, SlotActionType.QUICK_MOVE, mc.player);
                        break;

                    case CHEST:
                        if (!mc.player.getInventory().getStack(38).isEmpty()) throwItem(38);
                        mc.interactionManager.clickSlot(windowId, slot, 0, SlotActionType.QUICK_MOVE, mc.player);
                        break;

                    case LEGS:
                        if (!mc.player.getInventory().getStack(37).isEmpty()) throwItem(37);
                        mc.interactionManager.clickSlot(windowId, slot, 0, SlotActionType.QUICK_MOVE, mc.player);
                        break;

                    case FEET:
                        if (!mc.player.getInventory().getStack(36).isEmpty()) throwItem(36);
                        mc.interactionManager.clickSlot(windowId, slot, 0, SlotActionType.QUICK_MOVE, mc.player);
                        break;
                }
            } else {
                switch (ar.getSlotType()) {
                    case HEAD:
                        if (!mc.player.getInventory().getStack(39).isEmpty()) throwItem(39);
                        moveItem(slot, 28);
                        break;

                    case CHEST:
                        if (!mc.player.getInventory().getStack(38).isEmpty()) throwItem(38);
                        moveItem(slot, 28);
                        break;

                    case LEGS:
                        if (!mc.player.getInventory().getStack(37).isEmpty()) throwItem(37);
                        moveItem(slot, 28);
                        break;

                    case FEET:
                        if (!mc.player.getInventory().getStack(36).isEmpty()) throwItem(36);
                        moveItem(slot, 28);
                        break;
                }
            }
            //mc.interactionManager.clickSlot(windowId, slot, 0, SlotActionType.PICKUP, mc.player);
            //mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 0, SlotActionType.PICKUP, mc.player);
            this.nextClick = Math.round(RandomUtils.nextInt(this.minDelay.get(), this.maxDelay.get()));
            this.stopwatch.reset();
            this.moved = true;
        }
    }

    private float damage(final ItemStack stack) {
        final SwordItem sword = (SwordItem) stack.getItem();
        final int level = EnchantmentHelper.getLevel(Enchantments.SHARPNESS, stack);
        return (float) (sword.getMaterial().getAttackDamage() + level * 1.25);
    }

    private float mineSpeed(final ItemStack stack) {
        final Item item = stack.getItem();
        int level = EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, stack);

        switch (level) {
            case 1:
                level = 30;
                break;

            case 2:
                level = 69;
                break;

            case 3:
                level = 120;
                break;

            case 4:
                level = 186;
                break;

            case 5:
                level = 271;
                break;

            default:
                level = 0;
                break;
        }

        if (item instanceof PickaxeItem pickaxe) {
            return pickaxe.getMaterial().getMiningSpeedMultiplier() + level;
        } else if (item instanceof ShovelItem shovel) {
            return shovel.getMaterial().getMiningSpeedMultiplier() + level;
        } else if (item instanceof AxeItem axe) {
            return axe.getMaterial().getMiningSpeedMultiplier() + level;
        }

        return 0;
    }
}
