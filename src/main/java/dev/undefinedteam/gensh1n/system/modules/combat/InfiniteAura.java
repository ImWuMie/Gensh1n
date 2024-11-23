package dev.undefinedteam.gensh1n.system.modules.combat;

import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.events.game.GameLeftEvent;
import dev.undefinedteam.gensh1n.events.player.SprintEvent;
import dev.undefinedteam.gensh1n.events.render.Render3DEvent;
import dev.undefinedteam.gensh1n.render.ShapeMode;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.system.modules.UpdateTiming;
import dev.undefinedteam.gensh1n.system.modules.misc.Teams;
import dev.undefinedteam.gensh1n.system.modules.player.Blink;
import dev.undefinedteam.gensh1n.utils.RandomUtils;
import dev.undefinedteam.gensh1n.utils.StringUtils;
import dev.undefinedteam.gensh1n.utils.Utils;
import dev.undefinedteam.gensh1n.settings.EntitySettings;
import dev.undefinedteam.gensh1n.utils.entity.PlayerUtils;
import dev.undefinedteam.gensh1n.utils.entity.SortPriority;
import dev.undefinedteam.gensh1n.utils.entity.TargetUtils;
import dev.undefinedteam.gensh1n.utils.inventory.FindItemResult;
import dev.undefinedteam.gensh1n.utils.inventory.InvUtils;
import dev.undefinedteam.gensh1n.utils.path.FinderType;
import dev.undefinedteam.gensh1n.utils.path.TeleportPath;
import dev.undefinedteam.gensh1n.utils.predict.ExtrapolationUtils;
import dev.undefinedteam.gensh1n.utils.render.color.Color;
import dev.undefinedteam.gensh1n.utils.render.color.SettingColor;
import dev.undefinedteam.gensh1n.utils.time.NSTimer;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

@StringEncryption
@ControlFlowObfuscation
public class InfiniteAura extends Module {
    public InfiniteAura() {
        super(Categories.Combat, "infinite-aura", "Infinity Reach Kill Aura (:");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgCombat = settings.createGroup("Combat");
    private final SettingGroup sgHelper = settings.createGroup("Helper");
    private final SettingGroup sgPredict = settings.createGroup("Predict");
    private final SettingGroup sgRender = settings.createGroup("Render");
    private final SettingGroup sgEntities = settings.createGroup("Entities");

    private final Setting<Boolean> autoDisable = bool(sgGeneral, "auto-disable", "Disables module on kick.", true);

    // Combat
    private final Setting<Mode> mode = choice(sgCombat, "mode", Mode.Attack);
    public final Setting<FinderType> finder = choice(sgCombat, "finder", FinderType.AStar);
    private final Setting<Boolean> attackCooldown = bool(sgCombat, "cooldown", "1.8+ cooldown", true);
    private final Setting<Double> attackCps = doubleN(sgCombat, "cps", 10.0, 1.0, 20.0, () -> !attackCooldown.get());
    private final Setting<Double> baseDelay = doubleN(sgCombat, "base-delay", 0.5, 0.0, 10.0, attackCooldown::get);
    private final Setting<Integer> maxTargets = intN(sgCombat, "max-targets", "attack max targets", 1, 1, 100);
    private final Setting<Double> range = doubleN(sgCombat, "range", "teleport Range", 150, 6, 250);
    private final Setting<Boolean> ircFriend = bool(sgCombat, "irc-friend", "Don't attack irc user", true);
    private final Setting<SortPriority> priority = choice(sgCombat, "priority", "How to filter targets within range.", SortPriority.LowestHealth);
    private final Setting<UpdateTiming> updateTiming = choice(sgCombat, "update-timing", "When to update targets.", UpdateTiming.Pre);
    private final Setting<GroundStatus> groundStatus = choice(sgCombat, "ground-status", GroundStatus.False);
    private final Setting<Boolean> keepSprint = bool(sgCombat, "keep-sprint", true);
    private final Setting<Boolean> fullMovePacket = bool(sgCombat, "full-move-packet", false);

    private final Setting<Boolean> debugAttack = bool(sgCombat, "debug-attack", false);
    private final Setting<Boolean> debugPos = bool(sgCombat, "debug-pos", false);
    private final Setting<Integer> debugPosTicks = intN(sgCombat, "debug-pos-ticks", 1, 1, 20, debugPos::get);
    private final Setting<UpdateTiming> updatePosTiming = choice(sgCombat, "debug-pos-timing", UpdateTiming.Pre, debugPos::get);

    // Helper
    private final Setting<Weapon> weapon = choice(sgHelper, "weapon", "Only attacks an entity when a specified weapon is in your hand.", Weapon.Both);
    private final Setting<Boolean> weaponSwitch = bool(sgHelper, "weapon-switch", "Switches to your selected weapon when attacking the target.", false);
    private final Setting<Boolean> onlyOnClick = bool(sgHelper, "only-on-click", "Only attacks when holding left click.", false);
    private final Setting<ShieldMode> shieldMode = choice(sgHelper, "shield-mode", "Will try and use an axe to break target shields.", ShieldMode.Break, () -> weaponSwitch.get() && weapon.get() != Weapon.Axe);

    // Predict
    private final Setting<Boolean> predict = bool(sgPredict, "player-predict", "Predict players movement.", true);
    private final Setting<Integer> predictTicks = intN(sgPredict, "predict-ticks", "How many ticks of movement should be predicted for enemy damage checks.", 0, 0, 100);
    private final Setting<Integer> predictSmoothness = intN(sgPredict, "predict-smoothening", "How many earlier ticks should be used in average calculation for extrapolation motion.", 2, 1, 20);

    // Render
    private final Setting<RenderMode> renderMode = choice(sgRender, "render-mode", "How to render the teleport path.", RenderMode.None);
    private final Setting<SettingColor> color = color(sgRender, "color", "Color of the teleport path.", SettingColor.WHITE);
    private final Setting<Boolean> renderPredict = bool(sgRender, "render-predict", true);

    // Entities
    private final EntitySettings entities = entities(sgEntities);

    // Variables
    private final List<Entity> targets = Collections.synchronizedList(new CopyOnWriteArrayList<>());
    private CopyOnWriteArrayList<Vec3d> path = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Vec3d>[] test = new CopyOnWriteArrayList[100];

    private final NSTimer cps = new NSTimer();

    private final Map<AbstractClientPlayerEntity, Box> extHitbox = new HashMap<>();

    private int attackCount, damageCount;
    private boolean attacking, waiting;
    private LivingEntity attackingEntity;

    private Vec3d targetPos, attackPos;
    private LivingEntity targetEntity;

    private int ticks;

    public static Entity curTarget;

    @Override
    public void onActivate() {
        targets.clear();
        extHitbox.clear();
        attackCount = 0;
        damageCount = 0;
        attacking = false;
        attackingEntity = null;
        waiting = false;
        ticks = 0;
        targetPos = null;
        attackPos = null;
        targetEntity = null;
    }

    @Override
    public void onDeactivate() {
        targets.clear();

        if (debugAttack.get()) {
            info(StringUtils.getReplaced("尝试攻击{}次，有效{}次，命中率{}", attackCount, damageCount, 100 * MathHelper.clamp(((double) damageCount / (double) attackCount), 0.0, 1.0)));
        }
    }

    @EventHandler
    private void onSprint(SprintEvent event) {
        if (keepSprint.get() && curTarget != null) {
            event.isSprint = true;
        }
    }

    @EventHandler
    private void pre(TickEvent.Pre event) {
        if (updateTiming.get().equals(UpdateTiming.Pre) || updateTiming.get().equals(UpdateTiming.Both)) {
            update();
        }

        if (debugAttack.get()) {
            if (attackingEntity != null && attacking) {
                if (attackingEntity.hurtTime != 0) {
                    damageCount++;
                    waiting = true;
                    attacking = false;
                    attackingEntity = null;
                    return;
                }
                if (waiting) {
                    waiting = false;
                    info(StringUtils.getReplaced("尝试攻击{}次，有效{}次，命中率{}", attackCount, damageCount, 100 * MathHelper.clamp(((double) damageCount / (double) attackCount), 0.0, 1.0)));
                }
            }
        }

        if (debugPos.get()) {
            if (updatePosTiming.get().equals(UpdateTiming.Pre) || updatePosTiming.get().equals(UpdateTiming.Both)) {
                onT1ck();
            }
        }
    }

    @EventHandler
    private void post(TickEvent.Post e) {
        if (updateTiming.get().equals(UpdateTiming.Post) || updateTiming.get().equals(UpdateTiming.Both)) {
            update();
        }

        if (debugPos.get()) {
            if (updatePosTiming.get().equals(UpdateTiming.Post) || updatePosTiming.get().equals(UpdateTiming.Both)) {
                onT1ck();
            }
        }
    }

    private void onT1ck() {
        if (this.targetEntity != null && ticks >= this.debugPosTicks.get()) {
            this.targetPos = targetEntity.getPos();
            info(StringUtils.getReplaced("攻击到目标距离 {} b", Utils.distance(attackPos.x, attackPos.y, attackPos.z, targetPos.x, targetPos.y, targetPos.z)));
            ticks = 0;
            targetEntity = null;
            return;
        }

        ticks++;
    }

    private void update() {
        if (!Utils.canUpdate()) return;

        if (predict.get())
            ExtrapolationUtils.extrapolateMap(extHitbox, player -> predictTicks.get(), player -> predictSmoothness.get());

        if (!mc.player.isAlive() || PlayerUtils.getGameMode() == GameMode.SPECTATOR) return;
        if (onlyOnClick.get() && !mc.options.attackKey.isPressed()) return;

        TargetUtils.getList(targets, e -> entities.checkLiving(e, en -> {
            if (e instanceof PlayerEntity player) {
                if (Blink.isFakePlayer(player)) return false;

                if (ircFriend.get() && isIrcUserByProfile(player.getGameProfile())) return false;

                if (Teams.isInTeam(player)) return false;
            }

            return TargetUtils.distanceTo(en) <= this.range.get();
        }), priority.get(), maxTargets.get());

        if (!targets.isEmpty()) {
            if (shouldAttack()) {
                test = new CopyOnWriteArrayList[100];
                for (int i = 0; i < (targets.size() > maxTargets.get() ? maxTargets.get() : targets.size()); i++) {
                    Entity T = targets.get(i);
                    if (weaponSwitch.get()) {
                        Predicate<ItemStack> predicate = switch (weapon.get()) {
                            case Axe -> stack -> stack.getItem() instanceof AxeItem;
                            case Sword -> stack -> stack.getItem() instanceof SwordItem;
                            case Both ->
                                stack -> stack.getItem() instanceof AxeItem || stack.getItem() instanceof SwordItem;
                            default -> stack -> true;
                        };
                        FindItemResult weaponResult = InvUtils.findInHotbar(predicate);
                        if (shouldShieldBreak()) {
                            FindItemResult axeResult = InvUtils.findInHotbar(itemStack -> itemStack.getItem() instanceof AxeItem);
                            if (axeResult.found()) weaponResult = axeResult;
                        }
                        InvUtils.swap(weaponResult.slot(), false);
                    }
                    attack(T, i);
                }
                cps.reset();
            }
        }
    }

    private void attack(Entity T, int i) {
        Vec3d topFrom = mc.player.getPos();
        Vec3d to = T.getPos();

        if (predict.get() && T instanceof AbstractClientPlayerEntity player) {
            var tBox = this.extHitbox.getOrDefault(player, null);
            if (tBox != null) to = tBox.getCenter();
        }

        path = TeleportPath.computePath(finder.get(),topFrom, to);
        test[i] = path;

        if (path.isEmpty()) {
            path = null;
            test[i] = null;
            return;
        }



        OtherClientPlayerEntity entity = null;
        if (mc.player.hasVehicle()) {
            entity = new OtherClientPlayerEntity(mc.world, mc.player.getGameProfile());
            entity.setPosition(mc.player.getX(), mc.player.getY(), mc.player.getZ());
            entity.setYaw(mc.player.getYaw());
            entity.setPitch(mc.player.getPitch());
        }

        for (Vec3d pathElm : path) {
            teleport(pathElm, getGround(), this.fullMovePacket.get(), entity);
        }

        curTarget = T;

        switch (mode.get()) {
            case Attack -> mc.interactionManager.attackEntity(mc.player, T);
            case Interact -> mc.interactionManager.interactEntity(mc.player, T, Hand.MAIN_HAND);
        }
        mc.player.swingHand(Hand.MAIN_HAND);

        {
            if (!path.isEmpty()) {
                attackPos = path.getLast();
            }
        }

        if (T instanceof LivingEntity e) {
            if (debugAttack.get()) {
                attackingEntity = e;
                attacking = true;
                attackCount++;
            }
            if (debugPos.get()) targetEntity = e;
        }

        Collections.reverse(path);
        for (Vec3d pathElmi : path) {
            teleport(pathElmi, getGround(), this.fullMovePacket.get(), entity);
        }
    }

    private boolean getGround() {
        return switch (this.groundStatus.get()) {
            case True -> true;
            case False -> false;
            case Player -> mc.player.isOnGround();
        };
    }

    private void teleport(Vec3d pos, boolean ground, boolean full, Entity entity) {
        if (entity != null) {
            entity.setPosition(pos);
            mc.getNetworkHandler().sendPacket(new VehicleMoveC2SPacket(entity));
            return;
        }

        if (full)
            sendPacket(new PlayerMoveC2SPacket.Full(pos.getX(), pos.getY(), pos.getZ(), mc.player.getYaw(), mc.player.getPitch(), ground));
        else sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pos.getX(), pos.getY(), pos.getZ(), ground));
    }

    private boolean shouldAttack() {
        return attackCooldown.get() ? delayCheck() : cps.hasReached(1000.0D / (this.attackCps.get() + RandomUtils.nextDouble(0.0D, 5.0D)));
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

    private boolean delayCheck() {
        return mc.player.getAttackCooldownProgress(baseDelay.get().floatValue()) >= 1;
    }

    private final Color SIDE = new Color(255, 255, 255, 20);
    private final Color LINE = new Color(255, 255, 255, 150);

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (renderPredict.get() && predict.get()) {
            this.extHitbox.forEach((player, box) -> {
                if (player != mc.player && box != null) {
                    event.renderer.box(box, SIDE, LINE, ShapeMode.Both, 0);
                }
            });
        }

//        for (BlockPos scanBlock : AStarPathFinder.scanBlocks) {
//            event.renderer.box(scanBlock,SIDE,LINE,ShapeMode.Sides,0);
//        }

        if (this.path != null && !this.path.isEmpty() && !renderMode.get().equals(RenderMode.None)) {
            CopyOnWriteArrayList<Vec3d>[] test = this.test.clone();
            for (int i = 0; i < targets.size(); i++) {
                if (test != null && test[i] != null) {
                    renderPath(event, test[i]);
                }
            }
            if (this.cps.hasReached(1000)) {
                this.test = new CopyOnWriteArrayList[100];
                this.path.clear();
            }
        }
    }

    public void renderPath(Render3DEvent event, List<Vec3d> path) {
        Vec3d lastPoint = null;

        for (Vec3d pos : path) {
            switch (renderMode.get()) {
                case Box -> {
                    if (pos != null)
                        drawPath(event, pos, mc.player.getBoundingBox(mc.player.getPose()));
                }
                case Line -> {
                    if (lastPoint != null && pos != null) {
                        int current = event.renderer.lines.vec3(pos.x, pos.y, pos.z).color(color.get()).next();
                        int last = event.renderer.lines.vec3(lastPoint.x, lastPoint.y, lastPoint.z).color(color.get()).next();
                        event.renderer.lines.line(last, current);
                    }

                    lastPoint = pos;
                }
            }
        }
    }

    public void drawPath(Render3DEvent event, Vec3d vec, Box box) {
        double boxWidth = box.maxY - box.minY;
        double centerXWidth = (box.maxX - box.minX) / 2;
        double centerZWidth = (box.maxZ - box.minZ) / 2;
        event.renderer.box(vec.x - centerXWidth, vec.y + boxWidth, vec.z - centerZWidth, vec.x + centerXWidth, vec.y, vec.z + centerZWidth, color.get(), color.get(), ShapeMode.Lines, 0);
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent e) {
        if (autoDisable.get()) toggle();
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

    public enum RenderMode {
        None, Line, Box
    }

    public enum GroundStatus {
        False, True, Player
    }

    public enum Mode {
        Attack, Interact
    }
}
