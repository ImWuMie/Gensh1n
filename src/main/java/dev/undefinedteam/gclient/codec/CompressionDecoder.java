package dev.undefinedteam.gclient.codec;

import dev.undefinedteam.gclient.packets.PacketBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.List;
import java.util.zip.Inflater;



@StringEncryption
@ControlFlowObfuscation
public class CompressionDecoder extends ByteToMessageDecoder {
   public static final int MAXIMUM_COMPRESSED_LENGTH = 256;
   public static final int MAXIMUM_UNCOMPRESSED_LENGTH = 8388608;
   private final Inflater inflater;
   public int threshold;
   public boolean validateDecompressed;

   public CompressionDecoder(int threshold, boolean validateDecompressed) {
      this.threshold = threshold;
      this.validateDecompressed = validateDecompressed;
      this.inflater = new Inflater();
   }

   protected void decode(ChannelHandlerContext context, ByteBuf byteBuf, List<Object> out) throws Exception {
      if (byteBuf.readableBytes() != 0) {
         PacketBuf buf = new PacketBuf(byteBuf);
         int i = buf.readVarInt();
         if (i == 0) {
            out.add(buf.readBytes(buf.readableBytes()));
         } else {
            if (this.validateDecompressed) {
               if (i < this.threshold) {
                  throw new DecoderException("Badly compressed packet - size of " + i + " is below server threshold of " + this.threshold);
               }

               if (i > 8388608) {
                  throw new DecoderException("Badly compressed packet - size of " + i + " is larger than protocol maximum of 8388608");
               }
            }

            byte[] abyte = new byte[buf.readableBytes()];
            buf.readBytes(abyte);
            this.inflater.setInput(abyte);
            byte[] abyte1 = new byte[i];
            this.inflater.inflate(abyte1);
            out.add(Unpooled.wrappedBuffer(abyte1));
            this.inflater.reset();
         }
      }
   }

   public void setThreshold(int threshold, boolean validateDecompressed) {
      this.threshold = threshold;
      this.validateDecompressed = validateDecompressed;
   }
}
