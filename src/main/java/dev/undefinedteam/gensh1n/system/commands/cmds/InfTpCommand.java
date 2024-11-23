package dev.undefinedteam.gensh1n.system.commands.cmds;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.events.render.Render3DEvent;
import dev.undefinedteam.gensh1n.render.ShapeMode;
import dev.undefinedteam.gensh1n.system.commands.Command;
import dev.undefinedteam.gensh1n.system.commands.args.ClientPosArgumentType;
import dev.undefinedteam.gensh1n.system.commands.args.PlayerArgumentType;
import dev.undefinedteam.gensh1n.system.modules.Modules;
import dev.undefinedteam.gensh1n.system.modules.combat.InfiniteAura;
import dev.undefinedteam.gensh1n.utils.StringUtils;
import dev.undefinedteam.gensh1n.utils.Utils;
import dev.undefinedteam.gensh1n.utils.path.AStarPathFinder;
import dev.undefinedteam.gensh1n.utils.path.TeleportPath;
import dev.undefinedteam.gensh1n.utils.render.color.Color;
import dev.undefinedteam.gensh1n.utils.time.TickTimer;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.ArrayList;
import java.util.List;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static dev.undefinedteam.gensh1n.Client.mc;

@StringEncryption
@ControlFlowObfuscation
public class InfTpCommand extends Command {
    private Vec3d to = null;
    private List<Vec3d> path = new ArrayList<>();
    TickTimer timer = new TickTimer();

    private Vec3d boundPosition;

    public InfTpCommand() {
        super("inftp", "infinite tp", "itp", "inf");
        Client.EVENT_BUS.subscribe(this);
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        var i = Modules.get().get(InfiniteAura.class);

        builder.then(literal("player").then(argument("player", PlayerArgumentType.create()).executes(context -> {
            PlayerEntity player = PlayerArgumentType.get(context);
            Vec3d topPlayer = mc.player.getPos();
            to = player.getPos();
            path = TeleportPath.computePath(i.finder.get(),topPlayer, to);

            OtherClientPlayerEntity entity = null;
            if (mc.player.hasVehicle()) {
                entity = new OtherClientPlayerEntity(mc.world, mc.player.getGameProfile());
                entity.setPosition(mc.player.getX(), mc.player.getY(), mc.player.getZ());
                entity.setYaw(mc.player.getYaw());
                entity.setPitch(mc.player.getPitch());
            }

            for (Vec3d pathElm : path) {
                if (entity != null) {
                    entity.setPosition(pathElm);
                    mc.getNetworkHandler().sendPacket(new VehicleMoveC2SPacket(entity));
                } else
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
            }
            if (entity == null) mc.player.updatePosition(to.x, to.y, to.z);
            else mc.player.getVehicle().updatePosition(to.x,to.y,to.z);
            return SINGLE_SUCCESS;
        })));
        builder.then(literal("pos").then(argument("pos", ClientPosArgumentType.pos()).executes(ctx -> {
            Vec3d to = ClientPosArgumentType.getPos(ctx, "pos");
            Vec3d topPlayer = mc.player.getPos();
            path = TeleportPath.computePath(i.finder.get(),topPlayer, to);
            OtherClientPlayerEntity entity = null;
            if (mc.player.hasVehicle()) {
                entity = new OtherClientPlayerEntity(mc.world, mc.player.getGameProfile());
                entity.setPosition(mc.player.getX(), mc.player.getY(), mc.player.getZ());
                entity.setYaw(mc.player.getYaw());
                entity.setPitch(mc.player.getPitch());
            }

            for (Vec3d pathElm : path) {
                if (entity != null) {
                    entity.setPosition(pathElm);
                    mc.getNetworkHandler().sendPacket(new VehicleMoveC2SPacket(entity));
                } else
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
            }
            if (entity == null) mc.player.updatePosition(to.x, to.y, to.z);
            else mc.player.getVehicle().updatePosition(to.x,to.y,to.z);
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("bind").then(argument("pos", ClientPosArgumentType.pos()).executes(ctx -> {
            this.boundPosition = ClientPosArgumentType.getPos(ctx, "pos");
            info(StringUtils.getReplaced("已将坐标绑定至 ({},{},{}) ({}m)", boundPosition.x, boundPosition.y, boundPosition.z, Utils.distance(boundPosition.x, boundPosition.y, boundPosition.z, mc.player.getX(), mc.player.getY(), mc.player.getZ())));
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("teleport").executes(ctx -> {
            if (this.boundPosition == null) {
                error("请绑定坐标 inftp bind");
                return SINGLE_SUCCESS;
            }
            Vec3d to = this.boundPosition;
            Vec3d topPlayer = mc.player.getPos();
            path = TeleportPath.computePath(i.finder.get(),topPlayer, to);

            OtherClientPlayerEntity entity = null;
            if (mc.player.hasVehicle()) {
                entity = new OtherClientPlayerEntity(mc.world, mc.player.getGameProfile());
                entity.setPosition(mc.player.getX(), mc.player.getY(), mc.player.getZ());
                entity.setYaw(mc.player.getYaw());
                entity.setPitch(mc.player.getPitch());
            }

            for (Vec3d pathElm : path) {
                if (entity != null) {
                    entity.setPosition(pathElm);
                    mc.getNetworkHandler().sendPacket(new VehicleMoveC2SPacket(entity));
                } else
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
            }

            if (entity == null) mc.player.updatePosition(to.x, to.y, to.z);
            else mc.player.getVehicle().updatePosition(to.x,to.y,to.z);
            return SINGLE_SUCCESS;
        }));
    }

    private final Color SIDE = new Color(255, 255, 255, 20);
    private final Color LINE = new Color(255, 255, 255, 150);

    @EventHandler
    public void onUpdate(TickEvent.Pre e) {
        if (path != null) {
            if (timer.hasTimePassed(15)) {
                path = null;
                timer.reset();
            }
            timer.update();
        }
    }

    @EventHandler
    public void render(Render3DEvent event) {
//        for (BlockPos scanBlock : AStarPathFinder.scanBlocks) {
//            event.renderer.box(scanBlock,SIDE,LINE, ShapeMode.Sides,0);
//        }

        if (path != null) {
            var aura = Modules.get().get(InfiniteAura.class);
            aura.renderPath(event, path);
        }
    }
}
