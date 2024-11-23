package dev.undefinedteam.gensh1n.system.modules.player;

import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.events.network.PacketEvent;
import dev.undefinedteam.gensh1n.events.render.Render2DEvent;
import dev.undefinedteam.gensh1n.events.world.WorldChangeEvent;
import dev.undefinedteam.gensh1n.fakeplayer.FakePlayerEntity;
import dev.undefinedteam.gensh1n.render.Renderer;
import dev.undefinedteam.gensh1n.render._new.NText;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.system.modules.Modules;
import dev.undefinedteam.gensh1n.utils.Utils;
import dev.undefinedteam.gensh1n.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.world.GameMode;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author KuChaZi
 * @Date 2024/11/2 22:16
 * @ClassName: Blink2
 */
@StringEncryption
@ControlFlowObfuscation
public class Blink2 extends Module {
    private final SettingGroup sgDefault = settings.getDefaultGroup();

    public Setting<Integer> releaseTicks = intN(sgDefault,"max-ticks",180,1,400);
    public Setting<Boolean> allowBreak = bool(sgDefault, "AllowBreak", true);
    public Setting<Boolean> antiaim = bool(sgDefault, "AntiAim", true);
    public Setting<Boolean> player = bool(sgDefault, "AntiPlayer", true, () -> antiaim.get());
    private final Setting<Double> playerr = doubleN(sgDefault, "PlayerRange", 4.0, 0.0, 8.0, () -> player.get());
    public Setting<Boolean> entity = bool(sgDefault, "AntiEntity", true, () -> antiaim.get());
    private final Setting<Double> entityr = doubleN(sgDefault, "EntityRange", 8.0, 0.0, 12.0, () -> entity.get());


    private final LinkedList<List<Packet<?>>> packets = new LinkedList<>();

    // 好像不能用这个FakePlayerEntity
    public FakePlayerEntity fakePlayer;
    private int ticks;
    private int maxTicksBeforeRelease;


    public Blink2() {
        super(Categories.Player, "blink-test", "test");
    }

    @Override
    public String getInfoString() {
        return antiaim.get() ? "AntiAim" : null;
    }

    public static boolean isFakePlayer(Entity e) {
        return e.equals(Modules.get().get(Blink2.class).fakePlayer);
    }

    @Override
    public void onActivate() {
        if (mc.player == null) return;
        maxTicksBeforeRelease = releaseTicks.get();
        packets.clear();
        packets.add(new ArrayList<>());
        ticks = 0;

        fakePlayer = new FakePlayerEntity(mc.player, mc.player.getGameProfile().getName(), 20, true);
        fakePlayer.doNotPush = true;
        fakePlayer.hideWhenInsideCamera = true;
        fakePlayer.spawn();

        if (allowBreak.get() && !mc.isInSingleplayer()) {
            mc.player.getAbilities().allowModifyWorld = true;
            mc.interactionManager.setGameMode(GameMode.SURVIVAL);
        }
    }


    @Override
    public void onDeactivate() {
        ArrayList<List<Packet<?>>> packetCopy = new ArrayList<>(packets);
        packetCopy.forEach(this::sendTick);

        packets.clear();

        if (fakePlayer != null) {
            if (mc.world != null) {
                mc.world.removeEntity(fakePlayer.getId(), Entity.RemovalReason.DISCARDED);
            }
            fakePlayer = null;
        }
    }


    @EventHandler
    public void onWorld(WorldChangeEvent event) {
        Modules.get().get(Blink2.class).toggle();
    }

    @EventHandler
    public void onPacket(PacketEvent event) {
        if (sendingPackets) return;

        Packet<?> packet = event.packet;
        if (packet instanceof PlayerActionC2SPacket
            || packet instanceof PlayerInteractBlockC2SPacket
            || packet instanceof CommonPingS2CPacket
            || packet instanceof CommonPongC2SPacket
            || packet instanceof ClientStatusC2SPacket
            || packet instanceof HandSwingC2SPacket
        )
        {
            return;
        }

        if (event.origin == PacketEvent.TransferOrigin.SEND) {
            mc.execute(() -> {
                if (packets.isEmpty()) {
                    packets.add(new LinkedList<>());
                }
                packets.getLast().add(packet);
            });
            event.setCancelled(true);
        }

        if (antiaim.get()) {
            checkNearbyEntities();
        }
    }



    @EventHandler
    public void onTick(TickEvent.Post event) {
        ticks++;
        packets.add(new ArrayList<>());
    }

    private void poll() {
        if (packets.isEmpty()) return;
        this.sendTick(packets.getFirst());
        packets.removeFirst();
    }


    @EventHandler
    public void onRender(Render2DEvent event) {
        if (ticks >= maxTicksBeforeRelease) {
            poll();
            ticks -= 1;
        }

        var font = NText.regular20;
        int width = Utils.getWindowWidth();
        int height = Utils.getWindowHeight();
        float progress = (float) ticks / maxTicksBeforeRelease;
        float barWidth = 94;
        float barHeight = 5;
        float progressWidth = barWidth * progress;

//        String string = (maxTicksBeforeRelease - ticks) / 20 + "s release...";
//
//        float x = (width - font.getWidth(string)) / 2F;
//        float y = height / 4F;
//
//        font.draw(string, x, y, Color.WHITE.getPacked());



        var renderer1 = Renderer.MAIN;
        var paint1 = renderer1._paint();
        paint1.setRGBA(128, 128, 128, 80);
        paint1.setSmoothWidth(5);

        renderer1._renderer().drawRect((width / 2F) - (barWidth / 2),
            (height / 2F) + 15,
            (width / 2F) + (barWidth / 2),
            (height / 2F) + 15 + barHeight,
            paint1);

        var renderer = Renderer.MAIN;
        var paint = renderer1._paint();
        paint.setRGBA(255, 255, 255, 120);
        paint.setSmoothWidth(5);

        renderer._renderer().drawRect((width / 2F) - (barWidth / 2),
            (height / 2F) + 15,
            (width / 2F) - (barWidth / 2) + progressWidth,
            (height / 2F) + 15 + barHeight,
           paint);


    }

    private boolean sendingPackets = false;

    private void sendTick(List<Packet<?>> tick) {
        if (sendingPackets || mc.getNetworkHandler() == null) return;

        sendingPackets = true;
        List<Packet<?>> copyOfTick = new ArrayList<>(tick);

        copyOfTick.forEach(packet -> {
            mc.getNetworkHandler().getConnection().send(packet);
            handleFakePlayerPacket(packet);
        });

        sendingPackets = false;
    }

    private void checkNearbyEntities() {
        double checkRadiusPlayer = playerr.get();
        double checkRadius = entityr.get();

        if (mc.world != null && fakePlayer != null) {

            // check player
            List<Entity> nearbyPlayers = mc.world.getOtherEntities(
                fakePlayer,
                fakePlayer.getBoundingBox().expand(checkRadiusPlayer),
                entity -> entity instanceof PlayerEntity && entity != fakePlayer && entity != mc.player
            );

            boolean isPlayerNearby = !nearbyPlayers.isEmpty();

            // check entity
            List<Entity> nearbyEntities = mc.world.getOtherEntities(
                fakePlayer,
                fakePlayer.getBoundingBox().expand(checkRadius),
                entity -> (entity instanceof PersistentProjectileEntity && entity.getVelocity().lengthSquared() > 0.1)
                    || entity instanceof FireballEntity
                    || entity instanceof SnowballEntity
                    || entity instanceof TntEntity
                    || entity instanceof EggEntity
            );

            boolean isDangerousEntityNearby = !nearbyEntities.isEmpty();

            if ((entity.get() && isDangerousEntityNearby) || (player.get() && isPlayerNearby)) {
                blinkShortDistance();
            }
        }
    }

    private void blinkShortDistance() {
        final int blinkPacketCount = 6;

        if (packets.size() < blinkPacketCount * 2) {
            return;
        }

        for (int i = 0; i < blinkPacketCount; i++) {
            if (!packets.isEmpty()) {
                try {
                    sendTick(packets.getFirst());
                    packets.removeFirst();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
    }

    private void handleFakePlayerPacket(Packet<?> packet) {
        if (fakePlayer == null) return;

        if (packet instanceof PlayerMoveC2SPacket.PositionAndOnGround positionPacket) {
            updateFakePlayerPosition(positionPacket.x, positionPacket.y, positionPacket.z, positionPacket.onGround);
        }
        else if (packet instanceof PlayerMoveC2SPacket.LookAndOnGround rotationPacket) {
            updateFakePlayerRotation(rotationPacket.yaw, rotationPacket.pitch, rotationPacket.onGround);
        }
        else if (packet instanceof PlayerMoveC2SPacket.Full fullPacket) {
            updateFakePlayerPositionAndRotation(fullPacket.x, fullPacket.y, fullPacket.z, fullPacket.yaw, fullPacket.pitch, fullPacket.onGround);
        }

        if (packet instanceof PlayerInputC2SPacket inputPacket) {
            fakePlayer.sidewaysSpeed = inputPacket.getSideways();
            fakePlayer.forwardSpeed = inputPacket.getForward();
            fakePlayer.setJumping(inputPacket.isJumping());
            fakePlayer.setSneaking(inputPacket.isSneaking());
        }

        else if (packet instanceof ClientCommandC2SPacket commandPacket) {
            switch (commandPacket.getMode()) {
                case START_SPRINTING -> fakePlayer.setSprinting(true);
                case STOP_SPRINTING -> fakePlayer.setSprinting(false);
            }
        }

        else if (packet instanceof HandSwingC2SPacket) {
            fakePlayer.swingHand(Hand.MAIN_HAND);
        }

        mc.execute(this::updateFakePlayerRender);
    }

    private void updateFakePlayerPosition(double x, double y, double z, boolean onGround) {
        fakePlayer.updatePosition(x, y, z);
        fakePlayer.setOnGround(onGround);
    }

    private void updateFakePlayerRotation(float yaw, float pitch, boolean onGround) {
        fakePlayer.setYaw(yaw);
        fakePlayer.setPitch(pitch);
        fakePlayer.setOnGround(onGround);
    }

    private void updateFakePlayerPositionAndRotation(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        fakePlayer.updatePositionAndAngles(x, y, z, yaw, pitch);
        fakePlayer.setOnGround(onGround);
    }

    private void updateFakePlayerRender() {
        if (mc.world != null) {
//            mc.world.tickEntity(fakePlayer);
            fakePlayer.tick();
        }
    }



    //        else if (packet instanceof ClientCommandC2SPacket commandPacket) {
//            switch (commandPacket.getMode()) {
//                case START_SPRINTING -> fakePlayer.setSprinting(true);
//                case STOP_SPRINTING -> fakePlayer.setSprinting(false);
//                case START_SNEAKING -> fakePlayer.setSneaking(true);
//                case STOP_SNEAKING -> fakePlayer.setSneaking(false);
//            }
//        }
//        else if (packet instanceof PlayerInputC2SPacket inputPacket) {
//            float sidewaysSpeed = inputPacket.getSideways();
//            float forwardSpeed = inputPacket.getForward();
//            boolean isSneaking = inputPacket.isSneaking();
//            boolean isJumping = inputPacket.isJumping();
//
//            fakePlayer.sidewaysSpeed = sidewaysSpeed;
//            fakePlayer.forwardSpeed = forwardSpeed;
//            fakePlayer.setSneaking(isSneaking);
//            fakePlayer.setJumping(isJumping);
//        }

}
