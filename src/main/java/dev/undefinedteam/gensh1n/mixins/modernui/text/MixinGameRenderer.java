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

package dev.undefinedteam.gensh1n.mixins.modernui.text;

import dev.undefinedteam.modernui.mc.text.TextLayoutEngine;
import dev.undefinedteam.modernui.mc.text.TextRenderType;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Transition if we are rendering 3D world or 2D.
 */
@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(method = "preloadPrograms", at = @At("TAIL"))
    private void onPreloadUiShader(ResourceFactory vanillaProvider, CallbackInfo ci) {
        TextRenderType.preloadShaders();
    }

    @Inject(method = "renderWorld", at = @At("HEAD"))
    private void renderLevelStart(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci) {
        TextLayoutEngine.sCurrentInWorldRendering = true;
    }

    @Inject(method = "renderWorld", at = @At("TAIL"))
    private void renderLevelEnd(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci) {
        TextLayoutEngine.sCurrentInWorldRendering = false;
    }
}
