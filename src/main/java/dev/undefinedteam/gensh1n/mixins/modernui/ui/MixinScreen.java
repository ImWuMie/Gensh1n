package dev.undefinedteam.gensh1n.mixins.modernui.ui;

import dev.undefinedteam.modernui.mc.BlurHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nonnull;

@Mixin(Screen.class)
public class MixinScreen {

    //private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/block/spruce_planks.png");

    @Redirect(
            method = "renderInGameBackground",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;fillGradient(IIIIII)V"
            )
    )
    private void renderBackgroundInWorld(@Nonnull DrawContext gr, int x1, int y1,
                                         int x2, int y2, int color1, int color2) {
        BlurHandler.INSTANCE.drawScreenBackground(gr, x1, y1, x2, y2);
    }
}
