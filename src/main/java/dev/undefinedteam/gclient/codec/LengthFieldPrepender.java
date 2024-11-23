package dev.undefinedteam.gclient.codec;

import dev.undefinedteam.gclient.packets.PacketBuf;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToByteEncoder;

@Sharable
public class LengthFieldPrepender extends MessageToByteEncoder<ByteBuf> {
   private static final int MAX_BYTES = 3;

   protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) {
      int readableBytes = msg.readableBytes();
      int length = PacketBuf.getVarIntLength(readableBytes);
      if (length > 3) {
         throw new IllegalArgumentException("unable to fit " + readableBytes + " into 3");
      } else {
         PacketBuf buf = new PacketBuf(out);
         buf.ensureWritable(length + readableBytes);
         buf.writeVarInt(readableBytes);
         buf.writeBytes(msg, msg.readerIndex(), readableBytes);
      }
   }
}
