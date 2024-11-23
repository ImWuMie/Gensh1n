package dev.undefinedteam.gensh1n.system.modules.player;

import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.events.render.Render3DEvent;
import dev.undefinedteam.gensh1n.render.ShapeMode;
import dev.undefinedteam.gensh1n.settings.*;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.system.modules.combat.InfiniteAura;
import dev.undefinedteam.gensh1n.utils.path.FinderType;
import dev.undefinedteam.gensh1n.utils.path.TeleportPath;
import dev.undefinedteam.gensh1n.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

@StringEncryption
@ControlFlowObfuscation
public class TeleportReach extends Module {
    public TeleportReach() {
        super(Categories.Player, "teleport-reach", "Teleport to reach further.");
    }

    private final SettingGroup sgDefault = settings.getDefaultGroup();

    private final Setting<Double> range = doubleN(sgDefault, "range", 10, 6, 110);
    public final Setting<FinderType> finder = choice(sgDefault, "finder", FinderType.AStar);

    private final Setting<Boolean> enableRenderBlock = sgDefault.add(new BoolSetting.Builder()
        .name("render-block")
        .description("Enable rendering bounding box for Cube and Uniform Cube.")
        .defaultValue(true)
        .build()
    );

    private final Setting<ShapeMode> blockShapeModeBreak = sgDefault.add(new EnumSetting.Builder<ShapeMode>()
        .name("render-block-mode")
        .description("How the shapes for broken blocks are rendered.")
        .defaultValue(ShapeMode.Both)
        .visible(enableRenderBlock::get)
        .build()
    );

    private final Setting<SettingColor> blockSideColor = sgDefault.add(new ColorSetting.Builder()
        .name("block-side-color")
        .description("The side color of the target block rendering.")
        .defaultValue(new SettingColor(255, 0, 0, 20))
        .visible(enableRenderBlock::get)
        .build()
    );

    private final Setting<SettingColor> blockLineColor = sgDefault.add(new ColorSetting.Builder()
        .name("block-line-color")
        .description("The line color of the target block rendering.")
        .defaultValue(new SettingColor(255, 0, 0, 80))
        .visible(enableRenderBlock::get)
        .build()
    );


    private final Setting<Boolean> enableRenderEntity = sgDefault.add(new BoolSetting.Builder()
        .name("entity-blocks")
        .description("Enable rendering bounding box for Cube and Uniform Cube.")
        .defaultValue(true)
        .build()
    );

    private final Setting<ShapeMode> entityShapeModeBreak = sgDefault.add(new EnumSetting.Builder<ShapeMode>()
        .name("entity-block-mode")
        .description("How the shapes for broken blocks are rendered.")
        .defaultValue(ShapeMode.Both)
        .visible(enableRenderEntity::get)
        .build()
    );

    private final Setting<SettingColor> entitySideColor = sgDefault.add(new ColorSetting.Builder()
        .name("entity-side-color")
        .description("The side color of the target block rendering.")
        .defaultValue(new SettingColor(255, 255, 255, 20))
        .visible(enableRenderEntity::get)
        .build()
    );

    private final Setting<SettingColor> entityLineColor = sgDefault.add(new ColorSetting.Builder()
        .name("entity-line-color")
        .description("The line color of the target block rendering.")
        .defaultValue(new SettingColor(255, 255, 255, 80))
        .visible(enableRenderEntity::get)
        .build()
    );

    private BlockPos targetBlock;
    private Entity targetEntity;

    @EventHandler
    private void onTick(TickEvent.Pre e) {
        targetBlock = null;
        targetEntity = null;

        HitResult hitResult = raycast(mc.getCameraEntity(), range.get(), range.get(), 0.0F);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            var blockHit = (BlockHitResult) hitResult;
            targetBlock = blockHit.getBlockPos();

            if (mc.options.attackKey.isPressed()) {
                TeleportPath.teleport(finder.get(), mc.player.getPos(), targetBlock.toCenterPos(), true, () -> {
                    mc.interactionManager.attackBlock(targetBlock, blockHit.getSide());
                });
            } else if (mc.options.useKey.isPressed()) {
                TeleportPath.teleport(finder.get(), mc.player.getPos(), targetBlock.toCenterPos(), true, () -> {
                    mc.getNetworkHandler().sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, blockHit, 0));
                });
            }
        } else if (hitResult.getType() == HitResult.Type.ENTITY) {
            var entityHit = (EntityHitResult) hitResult;
            targetEntity = entityHit.getEntity();

            if (mc.options.attackKey.isPressed()) {
                TeleportPath.teleport(finder.get(), mc.player.getPos(), targetEntity.getPos(), true, () -> {
                    mc.interactionManager.attackEntity(mc.player, targetEntity);
                });
            } else if (mc.options.useKey.isPressed()) {
                TeleportPath.teleport(finder.get(), mc.player.getPos(), targetEntity.getPos(), true, () -> {
                    mc.interactionManager.interactEntity(mc.player, targetEntity, Hand.MAIN_HAND);
                });
            }
        }
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (enableRenderBlock.get()) {
            if (this.targetBlock != null) {
                event.renderer.box(targetBlock, blockSideColor.get(), blockLineColor.get(), blockShapeModeBreak.get(), 0);
            }
        }

        if (enableRenderEntity.get()) {
            if (this.targetEntity != null) {
                event.renderer.box(targetEntity.getBoundingBox(), entitySideColor.get(), entityLineColor.get(), entityShapeModeBreak.get(), 0);
            }
        }
    }

    private HitResult raycast(Entity camera, double blockInteractionRange, double entityInteractionRange, float tickDelta) {
        double range = Math.max(blockInteractionRange, entityInteractionRange);
        double squaredRange = MathHelper.square(range);
        Vec3d cameraPos = camera.getCameraPosVec(tickDelta);
        HitResult hitResult = camera.raycast(range, tickDelta, false);
        double squaredDistanceTo = hitResult.getPos().squaredDistanceTo(cameraPos);
        if (hitResult.getType() != net.minecraft.util.hit.HitResult.Type.MISS) {
            squaredRange = squaredDistanceTo;
            range = Math.sqrt(squaredRange);
        }

        Vec3d rotationVec = camera.getRotationVec(tickDelta);
        Vec3d endVec = cameraPos.add(rotationVec.x * range, rotationVec.y * range, rotationVec.z * range);
        Box box = camera.getBoundingBox().stretch(rotationVec.multiply(range)).expand(1.0, 1.0, 1.0);
        EntityHitResult entityHitResult = ProjectileUtil.raycast(camera, cameraPos, endVec, box, (entity) -> !entity.isSpectator() && entity.canHit(), squaredRange);
        return entityHitResult != null && entityHitResult.getPos().squaredDistanceTo(cameraPos) < squaredDistanceTo ? ensureTargetInRange(entityHitResult, cameraPos, entityInteractionRange) : ensureTargetInRange(hitResult, cameraPos, blockInteractionRange);
    }

    private HitResult ensureTargetInRange(HitResult hitResult, Vec3d cameraPos, double interactionRange) {
        Vec3d pos = hitResult.getPos();
        if (!pos.isInRange(cameraPos, interactionRange)) {
            Vec3d resultPos = hitResult.getPos();
            Direction direction = Direction.getFacing(resultPos.x - cameraPos.x, resultPos.y - cameraPos.y, resultPos.z - cameraPos.z);
            return BlockHitResult.createMissed(resultPos, direction, BlockPos.ofFloored(resultPos));
        } else {
            return hitResult;
        }
    }
}
