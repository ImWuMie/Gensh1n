package dev.undefinedteam.gensh1n.mixins;

import dev.undefinedteam.gensh1n.system.modules.Modules;
import dev.undefinedteam.gensh1n.system.modules.misc.Protocol;
import dev.undefinedteam.gensh1n.utils.network.NetPayload;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.impl.networking.client.ClientPlayNetworkAddon;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPlayNetworkAddon.class})
public abstract class MixinClientPlayNetworkAddon {
    @Inject(
        method = {"onServerReady"},
        at = {@At("HEAD")},
        cancellable = true,
        remap = false
    )
    public void onServerReady(CallbackInfo ci) {
        ci.cancel();
    }
}
