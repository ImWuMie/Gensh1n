package dev.undefinedteam.gensh1n.protocol.heypixel.check.c2s;

import dev.undefinedteam.gensh1n.protocol.heypixel.Heypixel;
import dev.undefinedteam.gensh1n.protocol.heypixel.check.HeypixelCheckPacket;
import dev.undefinedteam.gensh1n.protocol.heypixel.check.HeypixelSessionManager;
import dev.undefinedteam.gensh1n.protocol.heypixel.utils.BufferHelper;
import dev.undefinedteam.gensh1n.protocol.heypixel.utils.HeypixelVarUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.hit.HitResult;
import dev.undefinedteam.gensh1n.protocol.heypixel.msgpack.core.MessageBufferPacker;
import dev.undefinedteam.gensh1n.protocol.heypixel.msgpack.value.Variable;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


@StringEncryption
@ControlFlowObfuscation
public class GameDataC2SPacket extends HeypixelCheckPacket {


    public Entity entity;


    public ClientPlayerEntity player;


    public HitResult hitResult;

    public GameDataC2SPacket(ClientPlayerEntity player, Entity entity) {
        this.player = player;
        this.entity = entity;
        this.hitResult = mc.crosshairTarget;
    }


    public GameDataC2SPacket(PacketByteBuf friendlyByteBuf) {
    }

    public static void check(HeypixelSessionManager manager, ClientPlayerEntity player, Entity entity) {
        var minecraft = MinecraftClient.getInstance();
        if (minecraft.getCameraEntity() == null || minecraft.world == null || mc.crosshairTarget == null) {
            return;
        }
        new GameDataC2SPacket(player, entity).m(manager).sendCheckPacketVanilla();
    }


    @Override
    public void writeData(MessageBufferPacker packer) {
        try {

            var uuid = this.entity.getUuidAsString().equals(Heypixel.get().getPlayerUUID()) ? Heypixel.get().getPlayerUUID() : entity.getUuidAsString();
            packer.packValue(new Variable().setStringValue(uuid));
            packer.packInt(this.hitResult.getType().ordinal());
            packer.packDouble(this.hitResult.getPos().x);
            packer.packDouble(this.hitResult.getPos().y);
            packer.packDouble(this.hitResult.getPos().z);
            packer.packValue(new Variable().setIntegerValue(this.player.getPose().ordinal()));
            packer.packValue(new Variable().setArrayValue(
                List.of(
                    new Variable().setFloatValue(this.player.getPos().x),
                    new Variable().setFloatValue(this.player.getPos().y),
                    new Variable().setFloatValue(this.player.getPos().z)
                )));
            packer.packFloat(this.player.getRotationClient().x);
            packer.packFloat(this.player.getRotationClient().y);

            packer.packInt(this.entity.getPose().ordinal());
            packer.packDouble(this.entity.getPos().x);
            packer.packDouble(this.entity.getPos().y);
            packer.packDouble(this.entity.getPos().z);
            packer.packValue(new Variable().setFloatValue(this.entity.getRotationClient().x));
            packer.packValue(new Variable().setFloatValue(this.entity.getRotationClient().y));
        } catch (IOException e) {
        }
    }

    @Override
    public void processBuffer(PacketByteBuf buf, BufferHelper bufferHelper) {
        var uuid = this.entity.getUuidAsString().equals(Heypixel.get().getPlayerUUID()) ? Heypixel.get().getPlayerUUID() : entity.getUuidAsString();
        bufferHelper.writeUUID(buf, UUID.fromString(uuid));
        HeypixelVarUtils.writeVarInt(buf, this.hitResult.getType().ordinal());
        bufferHelper.writeVec3(buf, this.hitResult.getPos());

        bufferHelper.writeEnum(buf, this.player.getPose());
        bufferHelper.writeVec3(buf, this.player.getPos());
        bufferHelper.writeVec2(buf, this.player.getRotationClient());

        bufferHelper.writeEnum(buf, this.entity.getPose());
        bufferHelper.writeVec3(buf, this.entity.getPos());
        bufferHelper.writeVec2(buf, this.entity.getRotationClient());
    }
}
