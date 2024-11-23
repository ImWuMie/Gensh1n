package dev.undefinedteam.gensh1n.mixins;

import dev.undefinedteam.gensh1n.system.modules.Modules;
import dev.undefinedteam.gensh1n.system.modules.render.FullBright;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LightmapTextureManager.class)
public abstract class MixinLightmapTextureManager {
    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/NativeImage;setColor(III)V"))
    private void update(Args args) {
        if (((FullBright) Modules.get().get(FullBright.class)).isActive()) {
            args.set(2, 0xFFFFFFFF);
        }
    }
}
