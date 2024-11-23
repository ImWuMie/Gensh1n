package dev.undefinedteam.gensh1n.protocol.heypixel.check.s2c;

import dev.undefinedteam.gensh1n.protocol.heypixel.check.EncryptDataC2SPacket;
import dev.undefinedteam.gensh1n.protocol.heypixel.check.HeypixelCheckPacket;
import dev.undefinedteam.gensh1n.protocol.heypixel.check.c2s.ReflectDataC2SPacket;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import dev.undefinedteam.gensh1n.protocol.heypixel.msgpack.core.MessagePack;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.UUID;


@StringEncryption
@ControlFlowObfuscation
public class ReflectCheckS2CPacket extends HeypixelCheckPacket {


    public UUID uuid;


    public String data;


    public ReflectCheckS2CPacket(PacketByteBuf buf) {
        try(var unpacker = MessagePack.newDefaultUnpacker(helper.readByteArray(buf))) {
            var uuid = unpacker.unpackValue();
            var data = unpacker.unpackValue();
            if (uuid == null) {
                throw new NullPointerException("Rid is null");
            }
            this.uuid = UUID.fromString(uuid.asStringValue().asString());
            this.data = data.asStringValue().asString();
        } catch (Throwable th) {
            if (this.uuid == null) {
                this.uuid = UUID.randomUUID();
            }
            if (this.data == null) {
                this.data = UUID.randomUUID().toString();
            }
            throw new RuntimeException(th);
        }
    }

    @Override
    public void handleClientSide(ClientPlayerEntity player) {
        if (this.data.equals("SCI")) { // Send Client Info
            manager.asyncSendClientData();
        } else {
            ReflectDataC2SPacket.sendCheckPacket(manager, this.data);
            new EncryptDataC2SPacket(getPacketId()).m(manager).sendCheckPacket();
        }
    }
}
