package dev.undefinedteam.gensh1n.system.modules.player;

import dev.undefinedteam.gensh1n.events.player.DamageBlockEvent;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.utils.inventory.InvUtils;
import dev.undefinedteam.gensh1n.utils.world.BlockInfo;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.block.BlockState;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;


@StringEncryption
@ControlFlowObfuscation
public class AutoTool extends Module {
    public AutoTool() {
        super(Categories.Player, "auto-tool", "Automatically switches to the most effective tool when performing an action.");
    }

    private final SettingGroup sgDefault = settings.getDefaultGroup();
    private final Setting<Integer> swapDelay = intN(sgDefault, "swap-delay", 2, 1, 50);

    @EventHandler(priority = EventPriority.HIGH)
    private void onStartBreakingBlock(DamageBlockEvent event) {
        // Get blockState
        BlockState blockState = mc.world.getBlockState(event.blockPos);
        if (!BlockInfo.canBreak(event.blockPos, blockState)) return;

        // Check if we should switch to a better tool
        var slot = InvUtils.findFastestTool(blockState);
        if (slot.found() && !slot.isOffhand()) hotbar.selectSlot(slot.slot(), swapDelay.get());
    }

    @Override
    public void onDeactivate() {
        hotbar.reset();
    }
}
