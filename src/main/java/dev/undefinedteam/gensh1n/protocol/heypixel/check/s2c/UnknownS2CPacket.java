package dev.undefinedteam.gensh1n.protocol.heypixel.check.s2c;

import dev.undefinedteam.gensh1n.protocol.heypixel.check.EncryptDataC2SPacket;
import dev.undefinedteam.gensh1n.protocol.heypixel.check.HeypixelCheckPacket;
import net.minecraft.network.PacketByteBuf;
import dev.undefinedteam.gensh1n.protocol.heypixel.msgpack.core.MessagePack;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.io.IOException;
import java.util.UUID;


@StringEncryption
@ControlFlowObfuscation
public class UnknownS2CPacket extends HeypixelCheckPacket {


    public UUID uuid;
    public Long field_9461;

    public UnknownS2CPacket(PacketByteBuf buf) {
        try(var unpacker = MessagePack.newDefaultUnpacker(helper.readByteArray(buf))) {
            var obj = unpacker.unpackValue();
            var obj1 = unpacker.unpackValue();
            this.uuid = UUID.fromString(obj.asRawValue().asString());
            this.field_9461 = obj1.asIntegerValue().asLong();
            new EncryptDataC2SPacket(getPacketId()).m(manager).sendCheckPacket();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
