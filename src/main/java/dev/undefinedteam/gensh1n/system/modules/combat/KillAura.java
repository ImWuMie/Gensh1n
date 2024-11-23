package dev.undefinedteam.gensh1n.system.modules.combat;

import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.events.network.PacketEvent;
import dev.undefinedteam.gensh1n.events.render.Render3DEvent;
import dev.undefinedteam.gensh1n.render.ShapeMode;
import dev.undefinedteam.gensh1n.render.world.Renderer3D;
import dev.undefinedteam.gensh1n.settings.*;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.system.modules.Modules;
import dev.undefinedteam.gensh1n.system.modules.misc.AntiBot;
import dev.undefinedteam.gensh1n.system.modules.misc.Teams;
import dev.undefinedteam.gensh1n.system.modules.movement.NoSlow;
import dev.undefinedteam.gensh1n.system.modules.player.Blink;
import dev.undefinedteam.gensh1n.utils.RandomUtils;
import dev.undefinedteam.gensh1n.settings.EntitySettings;
import dev.undefinedteam.gensh1n.utils.chat.ChatUtils;
import dev.undefinedteam.gensh1n.utils.entity.PlayerUtils;
import dev.undefinedteam.gensh1n.utils.entity.SortPriority;
import dev.undefinedteam.gensh1n.utils.entity.TargetUtils;
import dev.undefinedteam.gensh1n.utils.inventory.FindItemResult;
import dev.undefinedteam.gensh1n.utils.inventory.InvUtils;
import dev.undefinedteam.gensh1n.utils.raytrace.RayTraceUtils;
import dev.undefinedteam.gensh1n.utils.render.color.SettingColor;
import dev.undefinedteam.gensh1n.utils.time.MSTimer;
import dev.undefinedteam.gensh1n.utils.world.BlockInfo;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;


@StringEncryption
@ControlFlowObfuscation
public class KillAura extends Module {
    public KillAura() {
        super(Categories.Combat, "kill-aura", "Attacks specified entities around you.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgAttack = settings.createGroup("Attack");
    private final SettingGroup sgRotation = settings.createGroup("Rotation");
    private final SettingGroup sgHelper = settings.createGroup("Helper");
    private final SettingGroup sgBlock = settings.createGroup("AutoBlock");
    private final SettingGroup sgRender = settings.createGroup("Render");
    private final SettingGroup sgEntities = settings.createGroup("Entities");
    private final SettingGroup sgDebug = settings.createGroup("Debug");

    // A
    private final Setting<TargetMode> targetMode = choice(sgGeneral, "Mode", TargetMode.Single);

    // ATTACK
    private final Setting<Boolean> attackCooldown = bool(sgAttack, "Cooldown", "1.8+ cooldown", true);

    private final Setting<Double> range = doubleN(sgAttack, "range", 3.0, 0.1, 10);
    private final Setting<Double> wallRange = doubleN(sgAttack, "wall-range", 0.5, 0, 10);
    private final Setting<Double> scanRange = doubleN(sgAttack, "scan-range", 4.5, 0.1, 10);
    private final Setting<Boolean> ircFriend = bool(sgAttack, "irc-friend", "Don't attack irc user", true);

    private final Setting<Double> minCps = doubleN(sgAttack, "min-cps", 10.0, 1.0, 20.0, () -> !attackCooldown.get());
    private final Setting<Double> maxCps = doubleN(sgAttack, "max-cps", 10.0, 1.0, 20.0, () -> !attackCooldown.get());
    private final Setting<Double> fov = doubleN(sgAttack, "fov", "Will only aim entities in the fov.", 360, 0, 360);

    private final Setting<Integer> maxTargets = intN(sgAttack, "max-target", 10, 1, 100, () -> !targetMode.get().equals(TargetMode.Single));
    private final Setting<SortPriority> sortPriority = choice(sgAttack, "priority", "How to filter targets within range.", SortPriority.ClosestAngle);

    private final Setting<Integer> switchDelay = intN(sgAttack, "switch-delay", 100, 0, 1000, () -> targetMode.get().equals(TargetMode.Switch));
    private final Setting<Double> baseDelay = doubleN(sgAttack, "cooldown-base-delay", 0.5, 0.0, 10.0, attackCooldown::get);

    // AutoBlock
    private final Setting<Boolean> autoBlock = bool(sgBlock, "autoblock", "Enable autoblock using packet spoofing.", false);


    // Rotation
    private final Setting<Boolean> rotation = bool(sgRotation, "rotation", "Rotates to face the target.", true);
    private final Setting<RotationMode> rotationMode = choice(sgRotation, "rotation-mode", "Rotation mode", RotationMode.Normal, rotation::get);
    private final Setting<Boolean> raycast = bool(sgRotation, "raycast", "Check entity in raytrace", true, rotation::get);
    private final Setting<Integer> minTurnSpeed = intN(sgRotation, "min-turn-speed", 120, 1, 180, rotation::get);
    private final Setting<Integer> maxTurnSpeed = intN(sgRotation, "max-turn-speed", 180, 1, 180, rotation::get);

    private final Setting<Integer> keepLength = intN(sgRotation, "keep-length", 2, 1, 10, rotation::get);

    // Helper
    private final Setting<Boolean> pauseOnUse = bool(sgRotation, "pause-on-use", "Pause killaura on using items", true);
    private final Setting<Boolean> blinkCheck = bool(sgRotation, "blink-check", "Pause killaura when blink active", true);
    private final Setting<Weapon> weapon = sgHelper.add(new EnumSetting.Builder<Weapon>().name("weapon").description("Only attacks an entity when a specified weapon is in your hand.").defaultValue(Weapon.Both).build());
    private final Setting<Boolean> weaponSwitch = sgHelper.add(new BoolSetting.Builder().name("weapon-switch").description("Switches to your selected weapon when attacking the target.").defaultValue(false).build());
    private final Setting<Boolean> onlyOnClick = sgHelper.add(new BoolSetting.Builder().name("only-on-click").description("Only attacks when holding left click.").defaultValue(false).build());
    private final Setting<Boolean> onlyOnLook = sgHelper.add(new BoolSetting.Builder().name("only-on-look").description("Only attacks when looking at an entity.").defaultValue(false).build());
    private final Setting<ShieldMode> shieldMode = sgHelper.add(new EnumSetting.Builder<ShieldMode>().name("shield-mode").description("Will try and use an axe to break target shields.").defaultValue(ShieldMode.Break).visible(() -> weaponSwitch.get() && weapon.get() != Weapon.Axe).build());

    private final Setting<Boolean> displayAttackRange = bool(sgRender, "Display Attack Range", false);
    private final Setting<SettingColor> attackRangeLineColor = sgRender.add(new ColorSetting.Builder().name("Line Color").description(COLOR).defaultValue(new SettingColor(255, 255, 255, 150)).visible(displayAttackRange::get).build());
    private final Setting<Boolean> alwaysESP = bool(sgRender, "Always Esp", false);
    private final Setting<Boolean> renderEsp = bool(sgRender, "Esp", true);
    private final Setting<RenderMode> espMode = choice(sgRender, "Esp Mode", RenderMode.Jello, renderEsp::get);
    private final Setting<SettingColor> boxLine = sgRender.add(new ColorSetting.Builder().name("Box Line").description(COLOR).defaultValue(new SettingColor(255, 0, 0, 255)).visible(() -> espMode.get().equals(RenderMode.Box) && renderEsp.get()).build());
    private final Setting<SettingColor> boxSide = sgRender.add(new ColorSetting.Builder().name("Box Side").description(COLOR).defaultValue(new SettingColor(255, 0, 0, 30)).visible(() -> espMode.get().equals(RenderMode.Box) && renderEsp.get()).build());
    private final Setting<SettingColor> jelloLine = sgRender.add(new ColorSetting.Builder().name("Jello Line").description(COLOR).defaultValue(new SettingColor(149, 149, 149, 170)).visible(() -> renderEsp.get() && espMode.get().equals(RenderMode.Jello)).build());

    private final Setting<Boolean> debugRender = bool(sgDebug, "debug-render", false);
    private final ColorSettings pointColors = colors(sgRender, "points-color", debugRender::get)
        .side(new SettingColor(0, 80, 255, 30))
        .line(new SettingColor(0, 80, 255, 80));
    private final Setting<SettingColor> lineColors = color(sgRender, "line-color", new SettingColor(80, 0, 255, 80), debugRender::get);

    private final EntitySettings entities = entities(sgEntities);

    public final List<Entity> targets = Collections.synchronizedList(new CopyOnWriteArrayList<>());

    private int index;
    private int switchTicks;
    private final MSTimer attackTimer = new MSTimer();

    public static Entity curTarget;
    public static Entity attackingTarget;

    private Vec3d aimPos;

    //AutoBlock
    private boolean isBlocking = false;

    public enum RenderMode {
        None,
        Box,
        Jello,
        Nursultan
    }

    public enum RotationMode {
        Normal,
        Calc
    }

    public enum Weapon {
        Sword,
        Axe,
        Both,
        Any
    }

    public enum ShieldMode {
        Ignore,
        Break,
        None
    }

    public enum TargetMode {
        Single,
        Switch,
        Multi
    }


    @Override
    public void onActivate() {
        targets.clear();
        curTarget = null;
        this.index = 0;
    }

    @Override
    public void onDeactivate() {
        curTarget = null;
        this.targets.clear();
    }

    private void startBlocking() {
        if (!isBlocking) {
            mc.options.rightKey.setPressed(true);
//            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, Mode.START_SNEAKING));
            mc.player.swingHand(Hand.OFF_HAND);
            isBlocking = true;
        }
    }

    private void stopBlocking() {
        if (isBlocking) {
            mc.options.rightKey.setPressed(false);
//            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, Mode.STOP_SNEAKING));
            isBlocking = false;
        }
    }


    @EventHandler
    public void onPacket(PacketEvent event) {
        if (autoBlock.get() && mc.player.isUsingItem()) {
            Object packet = event.packet;
            if (packet instanceof PlayerInteractItemC2SPacket interactPacket) {
                Hand hand = interactPacket.getHand();
                ItemStack itemInHand = mc.player.getStackInHand(hand);

                if (hand == Hand.MAIN_HAND && itemInHand.getItem() instanceof SwordItem) {
                    ItemStack offHandItem = mc.player.getStackInHand(Hand.OFF_HAND);

                    if (!(offHandItem.getItem() instanceof ShieldItem)) {
                        mc.getNetworkHandler().sendPacket(new PlayerInteractItemC2SPacket(Hand.OFF_HAND, 0));

                    } else {
                        event.cancel();
                        PlayerInteractItemC2SPacket cancelPacket = new PlayerInteractItemC2SPacket(Hand.OFF_HAND, interactPacket.getSequence());
                        mc.getNetworkHandler().sendPacket(cancelPacket);
                    }

                }
            }
        }
    }

    @EventHandler
    public void onUpdate(TickEvent.Pre event) {
        Renderer3D.updateJello();

        if (!mc.player.isAlive() || PlayerUtils.getGameMode() == GameMode.SPECTATOR) return;
        if (onlyOnClick.get() && !mc.options.attackKey.isPressed()) return;
        if (pauseOnUse.get() && mc.player.isUsingItem()) return;

        if (onlyOnLook.get()) {
            Entity targeted = mc.targetedEntity;
            if (targeted == null) return;
            if (!entityCheck(targeted)) return;
            targets.clear();
            targets.add(mc.targetedEntity);
        } else TargetUtils.getList(targets, this::entityCheck, sortPriority.get(), maxTargets.get());

        switchTicks--;

        if (this.targets.size() > 1 && this.targetMode.get().equals(TargetMode.Switch)) {
            if (switchTicks <= 0) {
                ++this.index;
                switchTicks = switchDelay.get() / 20;
            }
        }

        if (this.targets.size() > 1 && this.targetMode.get().equals(TargetMode.Single)) {
            this.index = 0;
        }

        if (curTarget != null) {
            curTarget = null;
        }

        if (attackingTarget != null) {
            attackingTarget = null;
        }

        if (!targets.isEmpty()) {
            if (targetMode.get().equals(TargetMode.Multi)) {
                curTarget = targets.getFirst();
            } else {
                if (index >= targets.size()) {
                    index = 0;
                }
                curTarget = targets.get(index);
            }
        }

        if (curTarget != null && !itemInHand()) {
            return;
        }

        if (curTarget != null) {
            var rot = !rotation.get();

            HitResult finalOver = null;
            if (rotation.get()) {
                var rotation = switch (this.rotationMode.get()) {
                    case Calc -> rotM.getBestRotation(curTarget, range.get(), 0.7f);
                    case Normal -> {
                        var box = curTarget.getBoundingBox();
                        var cameraY = mc.player.getCameraPosVec(1).y;
                        var cameraX = mc.player.getCameraPosVec(1).x;
                        var cameraZ = mc.player.getCameraPosVec(1).z;
                        var targetY = box.maxY > cameraY && box.minY < cameraY ? cameraY : cameraY < box.minY ? box.minY : box.maxY;
                        var targetX = curTarget.getX();
                        var targetZ = curTarget.getZ();

                        var xIn = box.maxX > cameraX && box.minX < cameraX;
                        var zIn = box.maxZ > cameraZ && box.minZ < cameraZ;

                        if (xIn) targetX = cameraX;
                        if (zIn) targetZ = cameraZ;

                        var rotVec = new Vec3d(targetX, targetY, targetZ);
                        var tRot = rotM.getRotation(rotVec, mc.player.getCameraPosVec(1));

                        var over = rotM.getRotationOver(tRot, range.get());

                        if (over instanceof BlockHitResult block) {
                            var isDown = box.maxY < cameraY;
                            var isTop = box.minY > cameraY;

                            var dir = Direction.fromRotation(tRot.yaw);
                            var qMax = dir.getDirection().equals(Direction.AxisDirection.POSITIVE);

                            var bBox = Box.enclosing(block.getBlockPos(), block.getBlockPos());
                            if (isTop) {
                                targetY = bBox.maxY + 0.01;
                                dir = block.getSide();

                                var axis = dir.getAxis();
                                qMax = dir.getDirection().equals(Direction.AxisDirection.POSITIVE);

                                if (xIn || zIn) {
                                    switch (axis) {
                                        case X -> {
                                            if (zIn) targetX = qMax ? bBox.maxX + 0.05 : bBox.minX - 0.05;
                                        }
                                        case Z -> {
                                            if (xIn) targetZ = qMax ? bBox.maxZ + 0.05 : bBox.minZ - 0.05;
                                        }
                                    }
                                } else {
                                    var pos = block.getPos();
                                    targetX = pos.x;
                                    targetZ = pos.z;
                                }
                            } else if (isDown) {
                                var axis = dir.getAxis();

                                switch (axis) {
                                    case X -> {
                                        targetX = qMax ? bBox.maxX + 0.05 : bBox.minX - 0.05;
                                    }
                                    case Z -> {
                                        targetZ = qMax ? bBox.maxZ + 0.05 : bBox.minZ - 0.05;
                                    }
                                }

                                var pos = block.getPos();
                                var ebox = block.getPos();
                                switch (axis) {
                                    case X -> {
                                        ebox.x = qMax ? box.maxX - 0.05 : box.minX + 0.05;
                                    }
                                    case Z -> {
                                        ebox.z = qMax ? box.maxZ - 0.05 : box.minZ + 0.05;
                                    }
                                }

                                var raycast = RayTraceUtils.raycast(mc.player.getCameraPosVec(1f), ebox);
                                if (raycast.getType().equals(HitResult.Type.MISS)) {
                                    targetX = ebox.x;
                                    targetZ = ebox.z;
                                }
                            } else {
                                var axis = dir.getAxis();
                                switch (axis) {
                                    case X -> targetX = qMax ? bBox.maxX + 0.05 : bBox.minX - 0.05;
                                    case Z -> targetZ = qMax ? bBox.maxZ + 0.05 : bBox.minZ - 0.05;
                                }

                                dir = block.getSide().getOpposite();
                                axis = dir.getAxis();
                                var ebox = block.getPos();
                                switch (axis) {
                                    case X -> {
                                        ebox.z = qMax ? box.minZ + 0.05 : box.maxZ - 0.05;
                                        ebox.x = qMax ? box.minX : box.maxX;
                                    }
                                    case Z -> {
                                        ebox.x = qMax ? box.minX + 0.05 : box.maxX - 0.05;
                                        ebox.z = qMax ? box.minZ : box.maxZ;
                                    }
                                }

                                var raycast = RayTraceUtils.raycast(mc.player.getCameraPosVec(1f), ebox);
                                if (raycast.getType().equals(HitResult.Type.MISS)) {
                                    targetX = ebox.x;
                                    targetZ = ebox.z;
                                }
                            }

                            rotVec = new Vec3d(targetX, targetY, targetZ);
                            tRot = rotM.getRotation(rotVec, mc.player.getCameraPosVec(1));

                            var o1 = rotM.getRotationOver(tRot, range.get());
                            if (o1.getType().equals(HitResult.Type.ENTITY)) {
                                targetX = o1.getPos().x;
                                targetY = o1.getPos().y;
                                targetZ = o1.getPos().z;
                            }

                            rotVec = new Vec3d(targetX, targetY, targetZ);
                            tRot = rotM.getRotation(rotVec, mc.player.getCameraPosVec(1));
                        }

                        var rRot = tRot;

                        yield rRot;
                    }
                };


                if (minTurnSpeed.get() != 180) {
                    final float rotationSpeed = (float) RandomUtils.nextDouble(minTurnSpeed.get(), maxTurnSpeed.get());

                    rotation = rotM.smooth(rotM.lastRotation, rotation, rotationSpeed);
                }

                if (Modules.get().isActive(AntiBot.class)) {
                    var result = rotM.getRotationOver(rotation, range.get());
                    if (result.getType().equals(HitResult.Type.ENTITY)) {
                        var entityHitResult = (EntityHitResult) result;
                        if (AntiBot.isBot(entityHitResult.getEntity())) curTarget = entityHitResult.getEntity();
                    }
                }

                rot = rotate.rotation(rotation).keep(keepLength.get()).set();

                var over = rotM.getRotationOver();
                aimPos = over.getPos();
                if (raycast.get()) {
                    if (!over.getType().equals(HitResult.Type.ENTITY)) rot = false;
                    if (over instanceof EntityHitResult e && e.getEntity() != curTarget) {
                        curTarget = e.getEntity();
                    }
                }
                finalOver = over;
            }

            if (blinkCheck.get() && Modules.get().isActive(Blink.class)) rot = false;
            if (NoSlow.isBlinking()) rot = false;

            if (rot && rangeCheck(curTarget, finalOver) && shouldAttack() && itemInHand()) {
                attackingTarget = curTarget;

                preAttack();

                attackTimer.reset();

                if (targetMode.get().equals(TargetMode.Multi)) targets.forEach(this::attack);
                else attack(curTarget);
            }
        }

        if (autoBlock.get()) {
            startBlocking();
            ChatUtils.info("ab start");
        } else if (isBlocking) {
            stopBlocking();
            ChatUtils.info("ab stop");
        }
    }

    private boolean rangeCheck(Entity e, HitResult over) {
        var distance = over == null ? e.distanceTo(mc.player) : over.getPos().distanceTo(mc.player.getCameraPosVec(1f));
        if (mc.player.canSee(e) || rotM.inRaycast(e)) {
            return distance <= range.get();
        } else {
            return distance <= wallRange.get();
        }
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (displayAttackRange.get()) {
            double smoothX = MathHelper.lerp(event.tickDelta, mc.player.lastRenderX, mc.player.getX());
            double smoothY = MathHelper.lerp(event.tickDelta, mc.player.lastRenderY, mc.player.getY());
            double smoothZ = MathHelper.lerp(event.tickDelta, mc.player.lastRenderZ, mc.player.getZ());
            event.renderer.circle(event.matrices, smoothX, smoothY, smoothZ, range.get(), attackRangeLineColor.get());
        }

        if (debugRender.get() && this.aimPos != null) {
            var box = new Box(aimPos, aimPos);
            event.renderer.box(box.expand(0.1), pointColors.side.get(), pointColors.line.get(), pointColors.shape.get(), 0);
            event.renderer.line(mc.player.getCameraPosVec(1), aimPos, lineColors.get());
        }

        if (!renderEsp.get()) return;
        if (!itemInHand() && !alwaysESP.get()) return;

        if (targetMode.get().equals(TargetMode.Multi)) {
            for (int i = 0; i < (targets.size() > maxTargets.get() ? maxTargets.get() : targets.size()); ++i) {
                Entity entity = targets.get(i);
                double smoothX = MathHelper.lerp(event.tickDelta, entity.lastRenderX, entity.getX());
                double smoothY = MathHelper.lerp(event.tickDelta, entity.lastRenderY, entity.getY());
                double smoothZ = MathHelper.lerp(event.tickDelta, entity.lastRenderZ, entity.getZ());

                switch (espMode.get()) {
                    case Box -> {
                        double x = smoothX - entity.getX();
                        double y = smoothY - entity.getY();
                        double z = smoothZ - entity.getZ();
                        Box box = entity.getBoundingBox();
                        event.renderer.box(x + box.minX, y + box.minY, z + box.minZ, x + box.maxX, y + box.maxY, z + box.maxZ, this.boxSide.get(), this.boxLine.get(), ShapeMode.Both, 0);
                    }
                    case Jello -> event.renderer.drawJello(event.matrices, entity, jelloLine.get(), event.tickDelta);
                    case Nursultan -> {
                        if (entity instanceof LivingEntity e) {
                            event.renderer.drawNursultan(event.matrices, e);
                        }
                    }
                }
            }
        } else if (curTarget != null) {
            double smoothX = MathHelper.lerp(event.tickDelta, curTarget.lastRenderX, curTarget.getX());
            double smoothY = MathHelper.lerp(event.tickDelta, curTarget.lastRenderY, curTarget.getY());
            double smoothZ = MathHelper.lerp(event.tickDelta, curTarget.lastRenderZ, curTarget.getZ());

            switch (espMode.get()) {
                case Box -> {
                    double x = smoothX - curTarget.getX();
                    double y = smoothY - curTarget.getY();
                    double z = smoothZ - curTarget.getZ();
                    Box box = curTarget.getBoundingBox();
                    event.renderer.box(x + box.minX, y + box.minY, z + box.minZ, x + box.maxX, y + box.maxY, z + box.maxZ, this.boxSide.get(), this.boxLine.get(), ShapeMode.Both, 0);
                }
                case Jello -> event.renderer.drawJello(event.matrices, curTarget, jelloLine.get(), event.tickDelta);
                case Nursultan -> {
                    if (curTarget instanceof LivingEntity e) {
                        event.renderer.drawNursultan(event.matrices, e);
                    }
                }
            }
        }
    }

    public static Entity getTarget() {
        return curTarget;
    }

    public static Entity getAttackingTarget() {
        return attackingTarget;
    }

    private void preAttack() {
        if (weaponSwitch.get()) {
            Predicate<ItemStack> predicate = switch (weapon.get()) {
                case Axe -> stack -> stack.getItem() instanceof AxeItem;
                case Sword -> stack -> stack.getItem() instanceof SwordItem;
                case Both -> stack -> stack.getItem() instanceof AxeItem || stack.getItem() instanceof SwordItem;
                default -> stack -> true;
            };
            FindItemResult weaponResult = InvUtils.findInHotbar(predicate);
            if (shouldShieldBreak()) {
                FindItemResult axeResult = InvUtils.findInHotbar(itemStack -> itemStack.getItem() instanceof AxeItem);
                if (axeResult.found()) weaponResult = axeResult;
            }
            InvUtils.swap(weaponResult.slot(), false);
        }
    }

    private boolean shouldAttack() {
        return attackCooldown.get() ? delayCheck() : attackTimer.check(1000.0D / (RandomUtils.nextDouble(minCps.get(), maxCps.get()) + RandomUtils.nextDouble(0.0D, 5.0D)));
    }

    private boolean shouldShieldBreak() {
        for (Entity target : targets) {
            if (target instanceof PlayerEntity player) {
                if (player.blockedByShield(mc.world.getDamageSources().playerAttack(mc.player)) && shieldMode.get() == ShieldMode.Break) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean entityCheck(Entity entity) {
        return this.entities.checkLiving(entity, e -> {
            if (e instanceof PlayerEntity player) {
                //if (player.isCreative()) return false;
                if (shieldMode.get() == ShieldMode.Ignore && player.blockedByShield(mc.world.getDamageSources().playerAttack(mc.player)))
                    return false;
                if (!PlayerUtils.inFov(entity, fov.get())) return false;

                if (Blink.isFakePlayer(player)) return false;

                if (ircFriend.get() && isIrcUserByProfile(player.getGameProfile())) return false;

                if (Teams.isInTeam(player)) return false;
            }

            if (AntiBot.isBot(e)) return false;

            return PlayerUtils.distanceToPlayer(entity) <= Math.max(scanRange.get(), range.get());
        });
    }

    private boolean delayCheck() {
        float delay = baseDelay.get().floatValue();

        return mc.player.getAttackCooldownProgress(delay) >= 1;
    }

    private void attack(Entity target) {
        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    private boolean itemInHand() {
        if (shouldShieldBreak()) return mc.player.getMainHandStack().getItem() instanceof AxeItem;

        return switch (weapon.get()) {
            case Axe -> mc.player.getMainHandStack().getItem() instanceof AxeItem;
            case Sword -> mc.player.getMainHandStack().getItem() instanceof SwordItem;
            case Both ->
                mc.player.getMainHandStack().getItem() instanceof AxeItem || mc.player.getMainHandStack().getItem() instanceof SwordItem;
            default -> true;
        };
    }

    @Override
    public String getInfoString() {
        return String.valueOf(targets.size());
    }
}
