//package dev.undefinedteam.gensh1n.mixins;
//
//import dev.undefinedteam.gensh1n.utils.network.NetPayload;
//import net.fabricmc.fabric.impl.networking.payload.PayloadHelper;
//import net.minecraft.network.PacketByteBuf;
//import net.minecraft.network.codec.PacketCodec;
//import net.minecraft.network.packet.CustomPayload;
//import net.minecraft.util.Identifier;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//@Mixin(targets = "net/minecraft/network/packet/CustomPayload$1")
//public abstract class CustomPayloadMixin<B extends PacketByteBuf> {
//
//    @Shadow
//    protected abstract PacketCodec<? super B, ? extends CustomPayload> getCodec(Identifier par1);
//
//    @Inject(
//        method = "encode(Lnet/minecraft/network/PacketByteBuf;Lnet/minecraft/network/packet/CustomPayload;)V",
//        at = @At(
//            value = "HEAD"
//        ),
//        cancellable = true
//    )
//    private void encode(B buf, CustomPayload payload, CallbackInfo ci) {
//        if (payload instanceof NetPayload.PayloadPacket payloadPacket) {
//            buf.writeIdentifier(payloadPacket.channel);
//            PayloadHelper.write(buf, payloadPacket.buf);
//            ci.cancel();
//        }
//    }
//
//    @Inject(
//        method = "decode(Lnet/minecraft/network/PacketByteBuf;)Lnet/minecraft/network/packet/CustomPayload;",
//        at = @At(
//            value = "HEAD"
//        ),
//        cancellable = true
//    )
//    private void decode(B buf, CallbackInfoReturnable<CustomPayload> cir) {
//        var id = buf.readIdentifier();
//        for (Identifier channel : NetPayload.REGISTERED_CHANNELS.keySet()) {
//            if (channel.toString().equals(id.toString())) {
//                var buffer = PayloadHelper.read(buf, 1145140);
//                NetPayload.REGISTERED_CHANNELS.get(channel).accept(buffer);
//                cir.setReturnValue(new NetPayload.PayloadPacket(channel, buffer));
//                return;
//            }
//        }
//
//        cir.setReturnValue(getCodec(id).decode(buf));
//    }
//}
