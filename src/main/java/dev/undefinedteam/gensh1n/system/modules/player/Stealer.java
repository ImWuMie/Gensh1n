package dev.undefinedteam.gensh1n.system.modules.player;

import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.Random;

@StringEncryption
@ControlFlowObfuscation
public class Stealer extends Module {
    public Stealer() {
        super(Categories.Player, "stealer", "Steal Chest");
    }

    private final SettingGroup sgDefault = settings.getDefaultGroup();
    private final Random random_ = new Random();
    public Setting<Integer> middle = intN(sgDefault, "middle-delay", 10, 0, 100);
    public Setting<Integer> delay = intN(sgDefault, "delay", 10, 0, 100);
    public final Setting<Boolean> ad = bool(sgDefault, "auto-disable", true);
    private int tickCounter = 0;

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.currentScreen instanceof GenericContainerScreen screen) {
            tickCounter++;
            var hasItem = false;
            for (int i = 0; i < screen.getScreenHandler().getInventory().size(); i++) {
                ItemStack stack = screen.getScreenHandler().getSlot(i).getStack();
                if (stack.isEmpty()) {
                    continue;
                }

                hasItem = true;

                //if (!ItemUtil.useful(stack)) continue;
                if (tickCounter >= ((middle.get()))) {
                    if (mc.interactionManager == null) continue;
                    mc.interactionManager.clickSlot(screen.getScreenHandler().syncId, i, 0, SlotActionType.QUICK_MOVE, mc.player);
                    tickCounter = 0;
                }
            }

            if (!hasItem) {
                screen.close();
            }
        }
        if (mc.world != null && mc.player != null && ad.get()) {
            if (!mc.player.isAlive()) {
                nInfo("[AutoDisable] Stealer",NSHORT);
                toggle();
                return;
            }
            if (mc.player.age <= 1) {
                nInfo("[AutoDisable] Stealer",NSHORT);
                toggle();
            }
        }
    }
}
