package dev.undefinedteam.gensh1n.mixins;

import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.events.client.KeyEvent;
import dev.undefinedteam.gensh1n.utils.input.Input;
import dev.undefinedteam.gensh1n.utils.input.KeyAction;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class MixinKeyboard {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    public void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo info) {
        if (key != GLFW.GLFW_KEY_UNKNOWN) {
            Input.setKeyState(key, action != GLFW.GLFW_RELEASE);
            if (Client.EVENT_BUS.post(new KeyEvent(key, modifiers, KeyAction.get(action))).isCancelled())
                info.cancel();
        }
    }
}
