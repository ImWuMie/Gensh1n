package dev.undefinedteam.gensh1n.system.modules.misc;

import com.mojang.authlib.GameProfile;
import dev.undefinedteam.gensh1n.events.network.PacketEvent;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Category;
import dev.undefinedteam.gensh1n.system.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Author KuChaZi
 * @Date 2024/11/10 01:12
 * @ClassName: FakePlayer
 */
@StringEncryption
@ControlFlowObfuscation
public class FakePlayer extends Module {
    private final SettingGroup sgDefault = settings.getDefaultGroup();
    private final SettingGroup sgCommand = settings.createGroup("Name");

    private final Setting<Boolean> copyInventory = bool(sgDefault,"CopyInventory", false);

    public static OtherClientPlayerEntity fakePlayer;

    private Setting<String> name = text(sgCommand,"Name", "FakePlayer");

    private final List<PlayerState> positions = new ArrayList<>();

    public FakePlayer() {
        super(Categories.Misc, "fake-player", "Fake Player");
    }

    @Override
    public void onActivate() {
        fakePlayer = new OtherClientPlayerEntity(mc.world, new GameProfile(UUID.fromString("66123666-6666-6666-6666-666666666600"), name.get()));
        fakePlayer.copyPositionAndRotation(mc.player);

        if (copyInventory.get()) {
            fakePlayer.setStackInHand(Hand.MAIN_HAND, mc.player.getMainHandStack().copy());
            fakePlayer.setStackInHand(Hand.OFF_HAND, mc.player.getOffHandStack().copy());

            fakePlayer.getInventory().setStack(36, mc.player.getInventory().getStack(36).copy());
            fakePlayer.getInventory().setStack(37, mc.player.getInventory().getStack(37).copy());
            fakePlayer.getInventory().setStack(38, mc.player.getInventory().getStack(38).copy());
            fakePlayer.getInventory().setStack(39, mc.player.getInventory().getStack(39).copy());
        }

        mc.world.addEntity(fakePlayer);
        fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 9999, 2));
        fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 9999, 4));
        fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 9999, 1));
    }

    @Override
    public void onDeactivate() {
        if (fakePlayer == null) return;
        fakePlayer.kill();
        fakePlayer.setRemoved(Entity.RemovalReason.KILLED);
        fakePlayer.onRemoved();
        fakePlayer = null;
        positions.clear();
    }

    private record PlayerState(double x, double y, double z, float yaw, float pitch) {}
}
