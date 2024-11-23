package dev.undefinedteam.gensh1n.system.modules.movement;

import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

@StringEncryption
@ControlFlowObfuscation
public class Sprint extends Module {

    private final SettingGroup sgDefault = settings.getDefaultGroup();
    private final Setting<Mode> mode = choice(sgDefault, "mode", Mode.Legit);

    public enum Mode {
        Packet,
        Legit
    }
    public Setting<Boolean> main = bool(sgDefault, "Main", true);
    public Setting<Boolean> allowCrouchSprint = bool(sgDefault, "Allow Crouch Sprint", true);

    public Sprint() {
        super(Categories.Movement, "sprint", "Auto Sprint");
    }

    @EventHandler
    public void onTick(TickEvent.Pre e) {
        if (main.get() && mc.player.forwardSpeed > 0 && !mc.player.horizontalCollision) {
            if (allowCrouchSprint.get() || !mc.player.isSneaking()) {
                if (mode.get() == Mode.Packet) {
                    mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
                    mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(mc.player.sidewaysSpeed, mc.player.forwardSpeed, false, true));
                } else if (mode.get() == Mode.Legit) {
                    mc.options.sprintKey.setPressed(true);
                }
            } else {
                stopSprinting();
            }
        } else {
            stopSprinting();
        }
    }

    private void stopSprinting() {
        if (mode.get() == Mode.Packet) {
            mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
            mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(0, 0, false, false));
        } else if (mode.get() == Mode.Legit) {
            mc.options.sprintKey.setPressed(false);
        }
    }
}
