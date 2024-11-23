package dev.undefinedteam.gensh1n.mixins;

import dev.undefinedteam.gensh1n.utils.network.NetPayload;
import net.fabricmc.fabric.impl.networking.payload.PayloadHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CustomPayloadS2CPacket.class)
public class MixinCustomPayloadS2CPacket {
    @Inject(method = "readPayload", at = @At("HEAD"), cancellable = true)
    private static void onRead(Identifier id, PacketByteBuf buf, CallbackInfoReturnable<CustomPayload> cir) {
        if (NetPayload.REGISTERED_CHANNELS.containsKey(id)) {
            var buffer =  PayloadHelper.read(buf,1145140);

            NetPayload.REGISTERED_CHANNELS.get(id).accept(buffer);
            cir.setReturnValue(new NetPayload.PayloadPacket(id, buffer));
        }
    }
}
