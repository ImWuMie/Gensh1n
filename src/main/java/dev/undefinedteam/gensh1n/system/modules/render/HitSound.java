package dev.undefinedteam.gensh1n.system.modules.render;

import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.Genshin;
import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.events.player.AttackEvent;
import dev.undefinedteam.gensh1n.events.player.DeathEvent;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.system.modules.combat.KillAura;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

/**
 * @Author KuChaZi
 * @Date 2024/11/9 13:51
 * @ClassName: HitSound
 */
@StringEncryption
@ControlFlowObfuscation
public class HitSound extends Module {
    private final SettingGroup sgDefault = settings.getDefaultGroup();
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Double> volume = doubleN(sgDefault,"Volume", 100, 0, 100);
    public final Setting<HitMode> hitSound = choice(sgGeneral,"HitSound", HitMode.OFF);
    public final Setting<KillMode> killSound = choice(sgGeneral,"KillSound", KillMode.OFF);

    public enum HitMode {
        IDK, Sexy, Skeet, Keyboard, Custom, OFF
    }

    public enum KillMode {
        Custom, OFF
    }

    public HitSound() {
        super(Categories.Render, "hit-sound", "Hit Sound");
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onUpdate(TickEvent.Post event) {
        if (mc.world != null) return;
        for (PlayerEntity p : mc.world.getPlayers()) {
            if (p.isDead() || p.getHealth() == 0)
                Client.EVENT_BUS.post(new DeathEvent(p));
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onAttack(@NotNull AttackEvent event) {
        if (!(event.target instanceof EndCrystalEntity))
            Genshin.soundManager.playHitSound(hitSound.get());
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onDeath(DeathEvent e) {
        if (KillAura.getTarget() != null && KillAura.getTarget() == e.getPlayer() && killSound.get().equals(KillMode.Custom)) {
            Genshin.soundManager.playSound("Custom-Hit");
        }
    }
}
