package dev.undefinedteam.gensh1n.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.undefinedteam.gensh1n.system.modules.Modules;
import dev.undefinedteam.gensh1n.system.modules.misc.Protocol;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientLoginNetworkHandler.class)
public class MixinClientLoginNetworkHandler {
    @ModifyExpressionValue(method = "onSuccess", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/ClientBrandRetriever;getClientModName()Ljava/lang/String;"))
    private String getClientModName(String original) {
        return Modules.get().get(Protocol.class).isHeypixel() ? "forge" : original;
    }
}
