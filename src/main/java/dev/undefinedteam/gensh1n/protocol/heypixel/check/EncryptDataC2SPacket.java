package dev.undefinedteam.gensh1n.protocol.heypixel.check;

import dev.undefinedteam.gensh1n.protocol.heypixel.utils.BufferHelper;
import dev.undefinedteam.gensh1n.protocol.heypixel.utils.EncryptionUtils;
import dev.undefinedteam.gensh1n.protocol.heypixel.utils.HeypixelVarUtils;
import net.minecraft.network.PacketByteBuf;
import dev.undefinedteam.gensh1n.protocol.heypixel.msgpack.core.MessageBufferPacker;
import dev.undefinedteam.gensh1n.protocol.heypixel.msgpack.value.Variable;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;



@StringEncryption
@ControlFlowObfuscation
public class EncryptDataC2SPacket extends HeypixelCheckPacket {


    public int packetId;


    public EncryptDataC2SPacket(int i) {
        this.packetId = i;
    }

    public EncryptDataC2SPacket(PacketByteBuf friendlyByteBuf) {
    }

    @Override
    public void writeData(MessageBufferPacker packer) {
        try {
            packer.packValue(new Variable().setStringValue(EncryptionUtils.encryptString(manager, String.valueOf(this.packetId))));
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    @Override
    public void processBuffer(PacketByteBuf friendlyByteBuf, BufferHelper bufferHelper) {
        try {
            bufferHelper.writeString(friendlyByteBuf, EncryptionUtils.encryptString(manager, String.valueOf(this.packetId)));
        } catch (Exception e) {
            HeypixelVarUtils.writeVarLong(friendlyByteBuf, this.packetId);
        }
    }
}
