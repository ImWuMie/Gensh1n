package dev.undefinedteam.gensh1n.protocol.heypixel.check.s2c;

import dev.undefinedteam.gensh1n.protocol.heypixel.Heypixel;
import dev.undefinedteam.gensh1n.protocol.heypixel.check.EncryptDataC2SPacket;
import dev.undefinedteam.gensh1n.protocol.heypixel.check.HeypixelCheckPacket;
import dev.undefinedteam.gensh1n.protocol.heypixel.check.c2s.BlockStateC2SPacket;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import dev.undefinedteam.gensh1n.protocol.heypixel.msgpack.core.MessagePack;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.io.IOException;


@StringEncryption
@ControlFlowObfuscation
public class BlockPosS2CPacket extends HeypixelCheckPacket {


    public BlockPos blockPos;

    public BlockPosS2CPacket(PacketByteBuf buf) {
        try(var unpacker = MessagePack.newDefaultUnpacker(helper.readByteArray(buf))) {
            var uuid = unpacker.unpackValue();
            var posX = unpacker.unpackValue();
            var posY = unpacker.unpackValue();
            var posZ = unpacker.unpackValue();
            if (uuid.asStringValue().asString().equals(Heypixel.get().getPlayerUUID())) {
                this.blockPos = new BlockPos(
                    posX.asIntegerValue().toInt(),
                    posY.asIntegerValue().toInt(),
                    posZ.asIntegerValue().toInt()
                );
            } else {
                this.blockPos = new BlockPos(
                    unpacker.unpackValue().asIntegerValue().toInt(),
                    unpacker.unpackValue().asIntegerValue().toInt(),
                    unpacker.unpackValue().asIntegerValue().toInt()
                );
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void handleClientSide(ClientPlayerEntity player) {
        BlockStateC2SPacket.asyncCheck(manager, this.blockPos);
        new EncryptDataC2SPacket(getPacketId()).m(manager).sendCheckPacket();
    }
}
