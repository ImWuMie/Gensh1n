/*
 * Modern UI.
 * Copyright (C) 2019-2022 BloCamLimb. All rights reserved.
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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.undefinedteam.gensh1n.system.modules.Modules;
import dev.undefinedteam.gensh1n.system.modules.render.ESP;
import dev.undefinedteam.gensh1n.utils.render.color.Color;
import dev.undefinedteam.modernui.mc.text.TextLayoutEngine;
import dev.undefinedteam.modernui.mc.text.TextRenderType;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Handle deferred rendering and transparency sorting (painter's algorithm).
 */
@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {
    @Shadow
    @Final
    private BufferBuilderStorage bufferBuilders;

    @Unique
    private ESP esp;

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/OutlineVertexConsumerProvider;draw()V"))
    private void endTextBatch(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (TextLayoutEngine.sUseTextShadersInWorld) {
            TextRenderType firstSDFFillType = TextRenderType.getFirstSDFFillType();
            TextRenderType firstSDFStrokeType = TextRenderType.getFirstSDFStrokeType();
            if (firstSDFFillType != null) {
                bufferBuilders.getEntityVertexConsumers().draw(firstSDFFillType);
            }
            if (firstSDFStrokeType != null) {
                bufferBuilders.getEntityVertexConsumers().draw(firstSDFStrokeType);
            }
        }
    }

    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;hasOutline(Lnet/minecraft/entity/Entity;)Z"))
    private boolean shouldMobGlow(boolean original, @Local Entity entity) {
        if (!getESP().isGlow() || getESP().shouldSkip(entity)) return original;

        return getESP().getColor(entity) != null || original;
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/OutlineVertexConsumerProvider;setColor(IIII)V"))
    private void setGlowColor(OutlineVertexConsumerProvider instance, int red, int green, int blue, int alpha, Operation<Void> original, @Local LocalRef<Entity> entity) {
        if (!getESP().isGlow() || getESP().shouldSkip(entity.get())) original.call(instance, red, green, blue, alpha);
        else {
            Color color = getESP().getColor(entity.get());

            if (color == null) original.call(instance, red, green, blue, alpha);
            else instance.setColor(color.r, color.g, color.b, color.a);
        }
    }

    @Unique
    private ESP getESP() {
        if (esp == null) {
            esp = Modules.get().get(ESP.class);
        }

        return esp;
    }
}
