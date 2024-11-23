package dev.undefinedteam.gensh1n.mixins;

import dev.undefinedteam.gensh1n.system.TextReplacements;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.OrderedText;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TextRenderer.class)
public abstract class MixinTextRenderer {

    @ModifyArg(method = "drawInternal(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;IIZ)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawLayer(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)F"), index = 0)
    private String injectNameProtectA(String text) {
        return TextReplacements.replace(text);
    }

    @Redirect(method = "drawLayer(Lnet/minecraft/text/OrderedText;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)F", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/OrderedText;accept(Lnet/minecraft/text/CharacterVisitor;)Z"))
    private boolean injectNameProtectB(OrderedText orderedText, CharacterVisitor visitor) {
        if (TextReplacements.shouldReplace()) {
            final OrderedText wrapped = new TextReplacements.ReplacementOrderedText(orderedText);
            return wrapped.accept(visitor);
        }

        return orderedText.accept(visitor);
    }

    @ModifyArg(method = "getWidth(Ljava/lang/String;)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextHandler;getWidth(Ljava/lang/String;)F"), index = 0)
    private @org.jetbrains.annotations.Nullable String injectNameProtectWidthA(@Nullable String text) {
        if (text != null && TextReplacements.shouldReplace()) {
            return TextReplacements.replace(text);
        }

        return text;
    }

    @ModifyArg(method = "getWidth(Lnet/minecraft/text/OrderedText;)I",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextHandler;getWidth(Lnet/minecraft/text/OrderedText;)F"),
        index = 0)
    private OrderedText injectNameProtectWidthB(OrderedText text) {
        if (TextReplacements.shouldReplace()) {
            return new TextReplacements.ReplacementOrderedText(text);
        }

        return text;
    }

}
