package dev.undefinedteam.gensh1n.hotbar;

import dev.undefinedteam.gensh1n.events.client.TickEvent;
import lombok.AllArgsConstructor;
import meteordevelopment.orbit.EventHandler;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import static dev.undefinedteam.gensh1n.Client.mc;


@NativeObfuscation
@StringEncryption
@ControlFlowObfuscation
public class SilentHotbar {
    public HotbarState current;

    public void selectSlot(int slot, int ticks) {
        current = new HotbarState(slot, ticks);
    }

    public boolean hasSelect() {
        return current != null && current.ticks >= 0;
    }

    public int getSlot() {
        return hasSelect() ? current.slot : mc.player == null ? 0 : mc.player.getInventory().selectedSlot;
    }

    public void reset() {
        current = null;
    }

    @EventHandler(priority = 1145)
    private void onTick(TickEvent.Pre e) {
        if (hasSelect()) {
            current.ticks--;
        }
    }

    @AllArgsConstructor
    public static class HotbarState {
        public int slot;
        public int ticks;
    }
}
