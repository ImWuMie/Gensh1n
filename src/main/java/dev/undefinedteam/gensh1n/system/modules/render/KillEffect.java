package dev.undefinedteam.gensh1n.system.modules.render;

import com.google.common.collect.Lists;
import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.events.player.InputTickEvent;
import dev.undefinedteam.gensh1n.events.render.Render3DEvent;
import dev.undefinedteam.gensh1n.settings.ColorSetting;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author KuChaZi
 * @Date 2024/10/27 09:50
 * @ClassName: KillEffect
 */
public class KillEffect extends Module {
    public KillEffect() {
        super(Categories.Render, "kill-effect", "Kill special effects");
    }

    private final SettingGroup sgDefault = settings.getDefaultGroup();
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<Mode> mode = choice(sgGeneral,"Mode", Mode.Orthodox);
    private final Setting<Integer> speed = intN(sgDefault ,"Y Speed", 0, -10, 10);
    private final Setting<Boolean> playSound = bool(sgDefault,"Play Sound", true);
    private final Setting<SettingColor> color = sgRender.add(new ColorSetting.Builder().name("Custom Color").description(COLOR).defaultValue(new SettingColor(255, 255, 255, 100)).build());

    private final Setting<Boolean> mobs = bool(sgDefault, "Mobs", false);

    private final Map<Entity, Long> renderEntities = new ConcurrentHashMap<>();
    private final Map<Entity, Long> lightingEntities = new ConcurrentHashMap<>();

    public static List<LineAction> LINE_QUEUE = new ArrayList<>();
    private Iterable<Entity> threadSafeEntityList = Collections.emptyList();
    public final Identifier ORTHODOX_SOUND = new Identifier("gensh1n:orthodox");
    public SoundEvent ORTHODOX_SOUNDEVENT = SoundEvent.of(ORTHODOX_SOUND);


    private enum Mode {
        Orthodox,
        FallingLava,
        LightningBolt
    }

    @EventHandler
    public void onRender3D(Render3DEvent event) {
        if (mc.world == null) return;

        switch (mode.get()) {
            case Orthodox -> renderEntities.forEach((entity, time) -> {
                if (System.currentTimeMillis() - time > 3000) {
                    renderEntities.remove(entity);
                } else {
                    drawLine(entity.getPos().add(0, calculateSpeed(), 0), entity.getPos().add(0, 3 + calculateSpeed(), 0), color.get().awt());
                    drawLine(entity.getPos().add(1, 2.3 + calculateSpeed(), 0), entity.getPos().add(-1, 2.3 + calculateSpeed(), 0), color.get().awt());
                    drawLine(entity.getPos().add(0.5, 1.2 + calculateSpeed(), 0), entity.getPos().add(-0.5, 0.8 + calculateSpeed(), 0), color.get().awt());
                }
            });
            case FallingLava -> renderEntities.keySet().forEach(entity -> {
                for (int i = 0; i < entity.getHeight() * 10; i++) {
                    for (int j = 0; j < entity.getWidth() * 10; j++) {
                        for (int k = 0; k < entity.getWidth() * 10; k++) {
                            mc.world.addParticle(ParticleTypes.FALLING_LAVA, entity.getX() + j * 0.1, entity.getY() + i * 0.1, entity.getZ() + k * 0.1, 0, 0, 0);
                        }
                    }
                }

                renderEntities.remove(entity);
            });
            case LightningBolt -> renderEntities.forEach((entity, time) -> {
                LightningEntity lightningEntity = new LightningEntity(EntityType.LIGHTNING_BOLT, mc.world);
                lightningEntity.refreshPositionAfterTeleport(entity.getX(), entity.getY(), entity.getZ());
                EntitySpawnS2CPacket pac = new EntitySpawnS2CPacket(lightningEntity);
                pac.apply(mc.getNetworkHandler());
                renderEntities.remove(entity);
                lightingEntities.put(entity, System.currentTimeMillis());
            });
        }
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.world == null) return;
        threadSafeEntityList = Lists.newArrayList(mc.world.getEntities());
    }

    public Iterable<Entity> getAsyncEntities() {
        return threadSafeEntityList;
    }

    @EventHandler
    public void onUpdate(TickEvent event) {
        getAsyncEntities().forEach(entity -> {
            if (!(entity instanceof PlayerEntity) && !mobs.get()) return;
            if (!(entity instanceof LivingEntity liv)) return;

            if (entity == mc.player || renderEntities.containsKey(entity) || lightingEntities.containsKey(entity)) return;
            if (entity.isAlive() || liv.getHealth() != 0) return;

            if (playSound.get() && mode.get() == Mode.Orthodox)
                mc.world.playSound(mc.player, entity.getBlockPos(), ORTHODOX_SOUNDEVENT, SoundCategory.BLOCKS, 10f, 1f);
            renderEntities.put(entity, System.currentTimeMillis());
        });

        if (!lightingEntities.isEmpty()) {
            lightingEntities.forEach((entity, time) -> {
                if (System.currentTimeMillis() - time > 5000) {
                    lightingEntities.remove(entity);
                }
            });
        }
    }

    private double calculateSpeed() {
        return (double) speed.get() / 100;
    }

    public static void drawLine(@NotNull Vec3d start, @NotNull Vec3d end, @NotNull Color color) {
        LINE_QUEUE.add(new LineAction(start, end, color));
    }

    public record LineAction(Vec3d start, Vec3d end, Color color) {
    }
}
