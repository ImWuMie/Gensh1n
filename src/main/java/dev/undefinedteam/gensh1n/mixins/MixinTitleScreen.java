package dev.undefinedteam.gensh1n.mixins;

import dev.undefinedteam.gensh1n.render.MagicBlur;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {
    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
    }

    @Inject(method = "mouseClicked", at = @At("TAIL"))
    private void c(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
    }


    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
    }
}
