package dev.undefinedteam.gclient.codec;

import dev.undefinedteam.gclient.packets.PacketBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.List;




public class FrameDecoder extends ByteToMessageDecoder {
   protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
      in.markReaderIndex();
      byte[] abyte = new byte[3];

      for(int i = 0; i < abyte.length; ++i) {
         if (!in.isReadable()) {
            in.resetReaderIndex();
            return;
         }

         abyte[i] = in.readByte();
         if (abyte[i] >= 0) {
            PacketBuf buf = new PacketBuf(Unpooled.wrappedBuffer(abyte));

            try {
               int varInt = buf.readVarInt();
               if (in.readableBytes() >= varInt) {
                  out.add(in.readBytes(varInt));
                  return;
               }

               in.resetReaderIndex();
            } finally {
               buf.release();
            }

            return;
         }
      }

      throw new CorruptedFrameException("length wider than 21-bit");
   }
}
