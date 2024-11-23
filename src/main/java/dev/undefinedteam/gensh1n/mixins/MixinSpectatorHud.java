package dev.undefinedteam.gensh1n.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.events.render.Render2DAfterHotbar;
import dev.undefinedteam.gensh1n.events.render.Render2DBeforeHotbar;
import dev.undefinedteam.gensh1n.render.GFramebuffer;
import dev.undefinedteam.gensh1n.render.GL;
import dev.undefinedteam.gensh1n.render.Renderer;
import dev.undefinedteam.gensh1n.render._new.NText;
import dev.undefinedteam.gensh1n.utils.Utils;
import icyllis.arc3d.opengl.GLTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.SpectatorHud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.opengl.GL30C.*;

@Mixin(SpectatorHud.class)
public class MixinSpectatorHud {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "renderSpectatorMenu(Lnet/minecraft/client/gui/DrawContext;)V", at = @At("HEAD"))
    private void onRenderHotbar(DrawContext context, CallbackInfo ci) {
        client.getProfiler().push(Client.LC_NAME + "_r2d_before_hotbar2");
        //MSAA.use(() -> {
        Utils.unscaledProjection();

        var window = client.getWindow();
        var mWidth = window.getFramebufferWidth();
        var mHeight = window.getFramebufferHeight();
//        var framebuffer = GFramebuffer.MAIN;
//        Renderer.BLIT_SCREEN.begin();
//        framebuffer.bindDraw();
//        framebuffer.makeBuffers(mWidth, mHeight, false);
//        framebuffer.clearColorBuffer();
//        framebuffer.clearStencilBuffer();

        {
            GL.saveRootState();
            NText.begin(context);
            Renderer.MAIN.begin();
            Render2DBeforeHotbar event = Client.EVENT_BUS.post(Render2DBeforeHotbar.get(context, mWidth, mHeight, Utils.getTickDelta()));
            Renderer.MAIN.render();
            NText.draw();
            event.postTasks.forEach(Runnable::run);
            GL.restoreRootState();
        }

//        GLTexture layer = framebuffer.getAttachedTexture(GL_COLOR_ATTACHMENT0);
//        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, client.getFramebuffer().fbo);
//        Renderer.BLIT_SCREEN._renderer().drawLayer(layer, mWidth, mHeight, 1, true);
//        Renderer.BLIT_SCREEN.render();

        Utils.scaledProjection();
        RenderSystem.applyModelViewMatrix();
        //});
        client.getProfiler().pop();
    }

    @Inject(method = "renderSpectatorMenu(Lnet/minecraft/client/gui/DrawContext;)V", at = @At("RETURN"))
    private void onRenderHotbarA(DrawContext context, CallbackInfo ci) {
        client.getProfiler().push(Client.LC_NAME + "_r2d_after_hotbar2");
        //MSAA.use(() -> {
        Utils.unscaledProjection();
        var window = client.getWindow();
        var mWidth = window.getFramebufferWidth();
        var mHeight = window.getFramebufferHeight();
//        var framebuffer = GFramebuffer.MAIN;
//        Renderer.BLIT_SCREEN.begin();
//        framebuffer.bindDraw();
//        framebuffer.makeBuffers(mWidth, mHeight, false);
//        framebuffer.clearColorBuffer();
//        framebuffer.clearStencilBuffer();

        {
            GL.saveRootState();
            NText.begin(context);
            Renderer.MAIN.begin();
            Render2DAfterHotbar event = Client.EVENT_BUS.post(Render2DAfterHotbar.get(context, mWidth, mHeight, Utils.getTickDelta()));
            Renderer.MAIN.render();
            NText.draw();
            event.postTasks.forEach(Runnable::run);
            GL.restoreRootState();
        }

//        GLTexture layer = framebuffer.getAttachedTexture(GL_COLOR_ATTACHMENT0);
//        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, client.getFramebuffer().fbo);
//        Renderer.BLIT_SCREEN._renderer().drawLayer(layer, mWidth, mHeight, 1, true);
//        Renderer.BLIT_SCREEN.render();
        Utils.scaledProjection();
        RenderSystem.applyModelViewMatrix();
        //});
        client.getProfiler().pop();
    }
}
