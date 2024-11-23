/*
 * Modern UI.
 * Copyright (C) 2019-2023 BloCamLimb. All rights reserved.
 *
 * Modern UI is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Modern UI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Modern UI. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.undefinedteam.gensh1n.mixins.modernui.ui;

import dev.undefinedteam.modernui.mc.*;

import dev.undefinedteam.modernui.mc.BlurHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraft {

    @Shadow
    @Nullable
    public Screen currentScreen;

    @Shadow
    @Final
    private Window window;

    @Shadow
    public abstract boolean isWindowFocused();

    /**
     * Forge breaks the event, see
     * <a href="https://github.com/MinecraftForge/MinecraftForge/issues/8992">this issue</a>
     */
    @Inject(method = "setScreen", at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;",
            opcode = Opcodes.PUTFIELD))
    private void onSetScreen(Screen guiScreen, CallbackInfo ci) {
        MuiModApi.dispatchOnScreenChange(currentScreen, guiScreen);
    }

    @Inject(method = "isTelemetryEnabledByApi", at = @At("HEAD"), cancellable = true)
    private void onAllowsTelemetry(CallbackInfoReturnable<Boolean> info) {
        if (ModernUIClient.sRemoveTelemetrySession) {
            info.setReturnValue(false);
        }
    }

    @Inject(method = "getFramerateLimit", at = @At("HEAD"), cancellable = true)
    private void onGetFramerateLimit(CallbackInfoReturnable<Integer> info) {
        if ((BlurHandler.sFramerateInactive != 0 ||
                BlurHandler.sFramerateMinimized != 0) &&
                !isWindowFocused()) {
            if (BlurHandler.sFramerateMinimized != 0 &&
                    BlurHandler.sFramerateMinimized < BlurHandler.sFramerateInactive &&
                    GLFW.glfwGetWindowAttrib(window.getHandle(), GLFW.GLFW_ICONIFIED) != 0) {
                info.setReturnValue(Math.min(
                        BlurHandler.sFramerateMinimized,
                        window.getFramerateLimit()
                ));
            } else if (BlurHandler.sFramerateInactive != 0) {
                info.setReturnValue(Math.min(
                        BlurHandler.sFramerateInactive,
                        window.getFramerateLimit()
                ));
            }
        }
    }

    @Inject(method = "close", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;shutdownExecutors()V"))
    private void onClose(CallbackInfo ci) {
        UIManager.destroy();
    }
}
