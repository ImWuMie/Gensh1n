package dev.undefinedteam.gensh1n.mixins;

import dev.undefinedteam.gensh1n.system.TextReplacements;
import dev.undefinedteam.gensh1n.system.modules.render.NameProtect;
import net.minecraft.client.font.TextHandler;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.TextVisitFactory;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(TextHandler.class)
public class MixinTextHandler {

    @Shadow
    @Final
    public TextHandler.WidthRetriever widthRetriever;

    @Inject(method = "getWidth(Lnet/minecraft/text/StringVisitable;)F", at = @At("HEAD"), cancellable = true)
    private void injectNameProtectWidthB(StringVisitable text, CallbackInfoReturnable<Float> cir) {
        if (!TextReplacements.shouldReplace()) {
            return;
        }

        MutableFloat mutableFloat = new MutableFloat();
        text.visit((style, asString) -> {
            TextVisitFactory.visitFormatted(TextReplacements.replace(asString), style, (unused, stylex, codePoint) -> {
                mutableFloat.add(widthRetriever.getWidth(codePoint, stylex));
                return true;
            });

            return Optional.empty();
        }, Style.EMPTY);

        cir.setReturnValue(mutableFloat.floatValue());
    }

}
