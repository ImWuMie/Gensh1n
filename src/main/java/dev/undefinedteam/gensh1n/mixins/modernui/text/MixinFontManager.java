package dev.undefinedteam.gensh1n.mixins.modernui.text;

import dev.undefinedteam.modernui.mc.text.TextLayoutEngine;
import net.minecraft.client.font.FontManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(FontManager.class)
public class MixinFontManager {

    /**
     * @author wumie
     * @reason idk
     */
    @Inject(method = "reload(Lnet/minecraft/resource/ResourceReloader$Synchronizer;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;Lnet/minecraft/util/profiler/Profiler;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;", at = @At("TAIL"))
    private void reload(ResourceReloader.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        TextLayoutEngine.getInstance().injectFontManager((FontManager) (Object) this)
            .reload(synchronizer,
                manager,
                prepareProfiler,
                applyProfiler,
                prepareExecutor,
                applyExecutor);
    }
}
