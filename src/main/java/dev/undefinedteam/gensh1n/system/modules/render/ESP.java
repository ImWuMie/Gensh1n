package dev.undefinedteam.gensh1n.system.modules.render;

import dev.undefinedteam.gensh1n.events.render.Render3DEvent;
import dev.undefinedteam.gensh1n.render.ShapeMode;
import dev.undefinedteam.gensh1n.settings.*;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.settings.EntitySettings;
import dev.undefinedteam.gensh1n.system.modules.Modules;
import dev.undefinedteam.gensh1n.system.modules.misc.Teams;
import dev.undefinedteam.gensh1n.utils.entity.EntityUtils;
import dev.undefinedteam.gensh1n.utils.entity.PlayerUtils;
import dev.undefinedteam.gensh1n.utils.render.color.Color;
import dev.undefinedteam.gensh1n.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

public class ESP extends Module {
    public ESP() {
        super(Categories.Render, "ESP", "Displays players through walls.");
    }

    private final SettingGroup sgDefault = settings.getDefaultGroup();
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgColors = settings.createGroup("Colors");
    private final SettingGroup sgEntity = settings.createGroup("Entities");

    public Setting<Boolean> glowESP = bool(sgDefault,"GlowESP", false);
    public Setting<Boolean> boxESP = bool(sgDefault,"BoxESP", true);

    public final Setting<ShapeMode> shapeMode = sgGeneral.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .description("How the shapes are rendered.")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    public final Setting<Double> fillOpacity = sgGeneral.add(new DoubleSetting.Builder()
        .name("fill-opacity")
        .description("The opacity of the shape fill.")
        .visible(() -> shapeMode.get() != ShapeMode.Lines)
        .defaultValue(0.3)
        .range(0, 1)
        .build()
    );

    private final Setting<Double> fadeDistance = sgGeneral.add(new DoubleSetting.Builder()
        .name("fade-distance")
        .description("The distance from an entity where the color begins to fade.")
        .defaultValue(3)
        .min(0)
        .max(12)
        .build()
    );

    private final EntitySettings entities = entities(sgEntity);

    // Colors
    private final Setting<SettingColor> playersColor = sgColors.add(new ColorSetting.Builder()
        .name("players-color")
        .description("The other player's color.")
        .defaultValue(new SettingColor(255, 255, 255))
        .build()
    );

    public final Setting<SettingColor> friendColor = sgColors.add(new ColorSetting.Builder()
        .name("friends-color")
        .description("The other friend's color.")
        .defaultValue(new SettingColor(0, 200, 0))
        .build()
    );

    private final Setting<SettingColor> animalsColor = sgColors.add(new ColorSetting.Builder()
        .name("animals-color")
        .description("The animal's color.")
        .defaultValue(new SettingColor(25, 255, 25, 255))
        .build()
    );

    private final Setting<SettingColor> waterAnimalsColor = sgColors.add(new ColorSetting.Builder()
        .name("water-animals-color")
        .description("The water animal's color.")
        .defaultValue(new SettingColor(25, 25, 255, 255))
        .build()
    );

    private final Setting<SettingColor> monstersColor = sgColors.add(new ColorSetting.Builder()
        .name("monsters-color")
        .description("The monster's color.")
        .defaultValue(new SettingColor(255, 25, 25, 255))
        .build()
    );

    private final Setting<SettingColor> ambientColor = sgColors.add(new ColorSetting.Builder()
        .name("ambient-color")
        .description("The ambient's color.")
        .defaultValue(new SettingColor(25, 25, 25, 255))
        .build()
    );

    private final Setting<SettingColor> miscColor = sgColors.add(new ColorSetting.Builder()
        .name("misc-color")
        .description("The misc color.")
        .defaultValue(new SettingColor(175, 175, 175, 255))
        .build()
    );


    private final Color lineColor = new Color();
    private final Color sideColor = new Color();
    private final Color baseColor = new Color();

    private int count;


    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (!boxESP.get()) return;
        count = 0;
        for (Entity entity : mc.world.getEntities()) {
            if (shouldSkip(entity)) continue;
            drawBoundingBox(event, entity);
            count++;
        }
    }

    private void drawBoundingBox(Render3DEvent event, Entity entity) {
        Color color = getColor(entity);
        if (color != null) {
            lineColor.set(color);
            sideColor.set(color).a((int) (sideColor.a * fillOpacity.get()));
        }

        double x = MathHelper.lerp(event.tickDelta, entity.lastRenderX, entity.getX()) - entity.getX();
        double y = MathHelper.lerp(event.tickDelta, entity.lastRenderY, entity.getY()) - entity.getY();
        double z = MathHelper.lerp(event.tickDelta, entity.lastRenderZ, entity.getZ()) - entity.getZ();

        Box box = entity.getBoundingBox();
        event.renderer.box(x + box.minX, y + box.minY, z + box.minZ, x + box.maxX, y + box.maxY, z + box.maxZ, sideColor, lineColor, shapeMode.get(), 0);
    }

    // Utils
    public boolean shouldSkip(Entity entity) {
        if (!entities.checkLiving(entity, null)) return true;
        if (entity == mc.player) return true;
        if (entity == mc.cameraEntity && mc.options.getPerspective().isFirstPerson()) return true;
        return !EntityUtils.isInRenderDistance(entity);
    }

    public Color getColor(Entity entity) {
        if (!entities.checkLiving(entity, null)) return null;

        double alpha = getFadeAlpha(entity);
        if (alpha == 0) return null;

        Color color = getEntityTypeColor(entity);
        return baseColor.set(color.r, color.g, color.b, (int) (color.a * alpha));
    }

    private double getFadeAlpha(Entity entity) {
        double dist = PlayerUtils.squaredDistanceToCamera(entity.getX() + entity.getWidth() / 2, entity.getY() + entity.getEyeHeight(entity.getPose()), entity.getZ() + entity.getWidth() / 2);
        double fadeDist = Math.pow(fadeDistance.get(), 2);
        double alpha = 1;
        if (dist <= fadeDist * fadeDist) alpha = (float) (Math.sqrt(dist) / fadeDist);
        if (alpha <= 0.075) alpha = 0;
        return alpha;
    }

    public Color getEntityTypeColor(Entity entity) {
        if (entity instanceof PlayerEntity p) {
            if (isIrcUserByProfile(p.getGameProfile()) || Teams.isInTeam(p)) {
                return PlayerUtils.getPlayerColor(((PlayerEntity) entity), playersColor.get(), true);
            }

            return PlayerUtils.getPlayerColor(((PlayerEntity) entity), playersColor.get());
        } else {
            return switch (entity.getType().getSpawnGroup()) {
                case CREATURE -> animalsColor.get();
                case WATER_AMBIENT, WATER_CREATURE, UNDERGROUND_WATER_CREATURE, AXOLOTLS -> waterAnimalsColor.get();
                case MONSTER -> monstersColor.get();
                case AMBIENT -> ambientColor.get();
                default -> miscColor.get();
            };
        }
    }

    public boolean isGlow() {
        return isActive() && glowESP.get();
    }

    @Override
    public String getInfoString() {
        return Integer.toString(count);
    }
}
