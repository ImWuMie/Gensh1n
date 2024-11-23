package dev.undefinedteam.gclient.codec;

import dev.undefinedteam.gclient.packets.PacketBuf;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.zip.Deflater;



@StringEncryption
@ControlFlowObfuscation
public class CompressionEncoder extends MessageToByteEncoder<ByteBuf> {
   public final byte[] encodeBuf = new byte[8192];
   private final Deflater deflater;
   public int threshold;

   public CompressionEncoder(int threshold) {
      this.threshold = threshold;
      this.deflater = new Deflater();
   }

   protected void encode(ChannelHandlerContext context, ByteBuf byteBuf, ByteBuf out) {
      int readableBytes = byteBuf.readableBytes();
      PacketBuf buf = new PacketBuf(out);
      if (readableBytes < this.threshold) {
         buf.writeVarInt(0);
         buf.writeBytes(byteBuf);
      } else {
         byte[] abyte = new byte[readableBytes];
         byteBuf.readBytes(abyte);
         buf.writeVarInt(abyte.length);
         this.deflater.setInput(abyte, 0, readableBytes);
         this.deflater.finish();

         while(!this.deflater.finished()) {
            int j = this.deflater.deflate(this.encodeBuf);
            buf.writeBytes(this.encodeBuf, 0, j);
         }

         this.deflater.reset();
      }
   }

   public int getThreshold() {
      return this.threshold;
   }

   public void setThreshold(int threshold) {
      this.threshold = threshold;
   }
}
