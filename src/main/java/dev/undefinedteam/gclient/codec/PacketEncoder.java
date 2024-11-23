package dev.undefinedteam.gclient.codec;

import dev.undefinedteam.gclient.packets.NetworkPacketsManager;
import dev.undefinedteam.gclient.packets.Packet;
import dev.undefinedteam.gclient.packets.PacketBuf;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

@StringEncryption
@ControlFlowObfuscation
public class PacketEncoder extends MessageToByteEncoder<Packet> {
    private static final Logger LOGGER = LogManager.getLogger("PacketEncoder");
    public boolean isClient;

    public PacketEncoder(boolean isClient) {
        this.isClient = isClient;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) throws Exception {
        if (packet == null) {
            LOGGER.error("Can't encode null packet");
            return;
        }

        int pid = isClient ? NetworkPacketsManager.INSTANCE.getC2SPid(packet.getClass()) : NetworkPacketsManager.INSTANCE.getS2CPid(packet.getClass());
        if (pid == -1) {
            LOGGER.error("[{}] Can't serialize unregistered packet", isClient ? "Client" : "Server");
            return;
        }
        PacketBuf packetByteBuf = new PacketBuf(out);
        packetByteBuf.writeVarInt(pid);
        packet.buf = packetByteBuf;
        packet.write();
        //out.writeBytes(GEncrypt.encrypt(packet.buf));
        //packet.buf.clear();
        packet.buf = null;
    }
}
