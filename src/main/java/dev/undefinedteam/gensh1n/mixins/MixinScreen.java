package dev.undefinedteam.gensh1n.mixins;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.undefinedteam.gensh1n.system.ClientConfig;
import dev.undefinedteam.gensh1n.system.commands.Commands;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Screen.class)
public abstract class MixinScreen {
    @Inject(method = "handleTextClick", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;)V", ordinal = 1, remap = false), cancellable = true)
    private void onRunCommand(Style style, CallbackInfoReturnable<Boolean> cir) {
        if (style.getClickEvent().getValue().startsWith(ClientConfig.get().commandPrefix.get()) && ClientConfig.get().calledByScreen.get()) {
            try {
                Commands.get().dispatch(style.getClickEvent().getValue().substring(ClientConfig.get().commandPrefix.get().length()));
                cir.setReturnValue(true);
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
        }
    }
}
