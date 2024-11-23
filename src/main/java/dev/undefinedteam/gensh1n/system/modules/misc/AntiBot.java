package dev.undefinedteam.gensh1n.system.modules.misc;

import dev.undefinedteam.gensh1n.events.game.GameJoinedEvent;
import dev.undefinedteam.gensh1n.events.network.PacketEvent;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.system.modules.Modules;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.ArrayList;
import java.util.UUID;

@StringEncryption
@NativeObfuscation
@ControlFlowObfuscation
public class AntiBot extends Module {
    public AntiBot() {
        super(Categories.Misc,"anti-bot","Anti Bots");
    }
    private final SettingGroup sgDefault = settings.getDefaultGroup();
    private final Setting<Mode> mode = choice(sgDefault, "mode", Mode.Heypixel);

    public enum Mode {
        Heypixel
    }

    public static final ArrayList<UUID> matrixBot = new ArrayList<>();

    @Override
    public void onDeactivate() {
        matrixBot.clear();
    }

    @EventHandler
    private void onGameJoin(GameJoinedEvent event) {
        matrixBot.clear();
    }

    @EventHandler
    public void onPacket(PacketEvent event) {
        if (event.packet instanceof PlayerListS2CPacket packet) {
            for (PlayerListS2CPacket.Entry entry : packet.getEntries()) {
                if (packet.getActions().contains(PlayerListS2CPacket.Action.ADD_PLAYER)
                    && entry.profile().getProperties().isEmpty() && packet.getEntries().size() == 1 &&
                    mc.getNetworkHandler() != null && mc.getNetworkHandler().getPlayerListEntry(entry.profile().getId()) != null) {
                    UUID playerId = entry.profile().getId();
                    if (!matrixBot.contains(playerId)) {
                        matrixBot.add(playerId);
                    }
                }
            }
        }
    }

    @NativeObfuscation.Inline
    public static boolean isBot(Entity e) {
        return Modules.get().isActive(AntiBot.class) && matrixBot.contains(e.getUuid());
    }
}
