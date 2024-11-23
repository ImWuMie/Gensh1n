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

import dev.undefinedteam.modernui.mc.MuiModApi;
import dev.undefinedteam.modernui.mc.ScrollController;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mixin(EntryListWidget.class)
public abstract class MixinEntryListWidget implements ScrollController.IListener {

    @Shadow
    public abstract int getMaxScroll();

    @Shadow
    public abstract double getScrollAmount();

    @Shadow
    private double scrollAmount;

    @Shadow
    @Final
    protected int itemHeight;

    @Shadow
    @Final
    protected MinecraftClient client;

    @Unique
    @Nullable
    private ScrollController modernUI_MC$mScrollController;

    /**
     * @author BloCamLimb
     * @reason Smooth scrolling
     */
    @Overwrite
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY != 0) {
            if (modernUI_MC$mScrollController != null) {
                modernUI_MC$mScrollController.setMaxScroll(getMaxScroll());
                modernUI_MC$mScrollController.scrollBy(Math.round(-scrollY * 40));
            } else {
                setScrollAmount(getScrollAmount() - scrollY * itemHeight / 2.0D);
            }
            return true;
        }
        return false;
    }

    @Inject(method = "renderWidget", at = @At("HEAD"))
    private void preRender(DrawContext gr, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (modernUI_MC$mScrollController == null) {
            modernUI_MC$mScrollController = new ScrollController(this);
            modernUI_MC$skipAnimationTo(scrollAmount);
        }
        modernUI_MC$mScrollController.update(MuiModApi.getElapsedTime());
    }

    @Inject(method = "renderWidget", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/client/gui/widget/EntryListWidget;renderHeader(Lnet/minecraft/client/gui/DrawContext;II)V"))
    private void preRenderHeader(@Nonnull DrawContext gr, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        gr.getMatrices().push();
        gr.getMatrices().translate(0,
                ((int) (((int) getScrollAmount() - getScrollAmount()) * client.getWindow().getScaleFactor())) / client.getWindow().getScaleFactor(), 0);
    }

    @Inject(method = "renderWidget", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/client/gui/widget/EntryListWidget;renderHeader(Lnet/minecraft/client/gui/DrawContext;II)V"))
    private void postRenderHeader(@Nonnull DrawContext gr, int mouseX, int mouseY, float partialTicks,
                                  CallbackInfo ci) {
        gr.getMatrices().pop();
    }

    @Inject(method = "renderWidget", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/client/gui/widget/EntryListWidget;renderList(Lnet/minecraft/client/gui/DrawContext;IIF)V"))
    private void preRenderList(@Nonnull DrawContext gr, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        gr.getMatrices().push();
        gr.getMatrices().translate(0,
                ((int) (((int) getScrollAmount() - getScrollAmount()) * client.getWindow().getScaleFactor())) / client.getWindow().getScaleFactor(), 0);
    }

    @Inject(method = "renderWidget", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/client/gui/widget/EntryListWidget;renderList(Lnet/minecraft/client/gui/DrawContext;IIF)V"))
    private void postRenderList(@Nonnull DrawContext gr, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        gr.getMatrices().pop();
    }

    /**
     * @author BloCamLimb
     * @reason Smooth scrolling
     */
    @Overwrite
    public void setScrollAmount(double target) {
        if (modernUI_MC$mScrollController != null) {
            modernUI_MC$skipAnimationTo(target);
        } else
            scrollAmount = MathHelper.clamp(target, 0.0D, getMaxScroll());
    }

    @Override
    public void onScrollAmountUpdated(ScrollController controller, float amount) {
        scrollAmount = MathHelper.clamp(amount, 0.0D, getMaxScroll());
    }

    @Unique
    public void modernUI_MC$skipAnimationTo(double target) {
        assert modernUI_MC$mScrollController != null;
        modernUI_MC$mScrollController.setMaxScroll(getMaxScroll());
        modernUI_MC$mScrollController.scrollTo((float) target);
        modernUI_MC$mScrollController.abortAnimation();
    }
}
