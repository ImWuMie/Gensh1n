package dev.undefinedteam.gensh1n.system.modules.player;

import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.events.network.PacketEvent;
import dev.undefinedteam.gensh1n.fakeplayer.FakePlayerEntity;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Category;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.system.modules.Modules;
import dev.undefinedteam.gensh1n.utils.network.PacketUtils;
import lombok.AllArgsConstructor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@StringEncryption
@ControlFlowObfuscation
public class Blink extends Module {
    public Blink() {
        super(Categories.Player, "blink", "Let you blink");
    }

    private final SettingGroup sgDefault = settings.getDefaultGroup();
    public Setting<Boolean> flash = bool(sgDefault, "Flash", true);
    public Setting<Integer> flashLimit = intN(sgDefault, "Flash Time", 5000, 1000, 10000);
    public Setting<Boolean> antiAim = bool(sgDefault, "Anti Aim", true);
    public Setting<Double> projRange = doubleN(sgDefault, "Projectile Range", 5, 3, 8);
    public Setting<Double> playerRange = doubleN(sgDefault, "Player Range", 5, 3, 8);

    @AllArgsConstructor
    static class BlinkPacket {
        long time;
        Packet packet;
    }

    public List<BlinkPacket> pkts = new CopyOnWriteArrayList<>();
    public FakePlayerEntity fakePlayerEntity;
    int timer = 0;

    @Override
    public void onActivate() {
        timer = 0;
        fakePlayerEntity = new FakePlayerEntity(mc.player, mc.player.getGameProfile().getName(), 20, true);
        fakePlayerEntity.doNotPush = true;
        fakePlayerEntity.hideWhenInsideCamera = true;
        fakePlayerEntity.spawn();

    }

    @EventHandler
    public void onPacket(PacketEvent event) {
        if (event.origin == PacketEvent.TransferOrigin.RECEIVE) return;
        if (event.packet instanceof ChatMessageC2SPacket) return;
        if (event.packet instanceof PlayerPositionLookS2CPacket) {
            Modules.get().get(Blink.class).toggle();
            return;
        }
        BlinkPacket packet = new BlinkPacket(System.currentTimeMillis(), event.packet);
        pkts.add(packet);
        event.setCancelled(true);
    }

    @EventHandler
    public void onUpdate(TickEvent.Pre event) {
        timer++;
        for (BlinkPacket packet : pkts) {
            if (System.currentTimeMillis() - packet.time >= flashLimit.get()) {
                Packet pkt = packet.packet;
                pkts.remove(packet);
                PacketUtils.sendNoEvent(pkt);
                if (pkt instanceof PlayerMoveC2SPacket p) {
                    fakePlayerEntity.setPos(p.getX(0), p.getY(0), p.getZ(0));
                    //fakePlayerEntity.getPos().x = p.getX(0);
                }
            }
        }
        while (should()) {
            if (pkts.isEmpty()) {
                Modules.get().get(Blink.class).toggle();
                return;
            }
            Packet pkt = pkts.getFirst().packet;
            pkts.remove(pkts.getFirst());
            PacketUtils.sendNoEvent(pkt);
            if (pkt instanceof PlayerMoveC2SPacket p) {
                fakePlayerEntity.setPos(p.getX(0), p.getY(0), p.getZ(0));
            }
        }
    }

    boolean should() {
        if (!antiAim.get()) return false;
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player != fakePlayerEntity && player.getId() != mc.player.getId() && fakePlayerEntity.distanceTo(player) <= playerRange.get()) {
                return true;
            }
        }
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof ProjectileEntity || entity instanceof SnowballEntity || entity instanceof EggEntity || entity instanceof ArrowEntity) {
                if (fakePlayerEntity.distanceTo(entity) <= projRange.get()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onDeactivate() {
        if (mc.world == null || mc.player.isDead()) {
            Modules.get().get(Blink.class).toggle();
        }
        for (BlinkPacket packet : pkts) {
            Packet pkt = packet.packet;
            pkts.remove(packet);
            PacketUtils.sendNoEvent(pkt);
        }
        mc.world.removeEntity(fakePlayerEntity.getId(), Entity.RemovalReason.DISCARDED);
        fakePlayerEntity = null;
    }

    public static boolean isFakePlayer(Entity e) {
        return e.equals(Modules.get().get(Blink.class).fakePlayerEntity);
    }
}
