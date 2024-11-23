package dev.undefinedteam.gensh1n.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.events.game.ReceiveMessageEvent;
import dev.undefinedteam.gensh1n.events.render.Render2DBeforeChat;
import dev.undefinedteam.gensh1n.mixin_interface.IChatHud;
import dev.undefinedteam.gensh1n.mixin_interface.IChatHudLine;
import dev.undefinedteam.gensh1n.render.GFramebuffer;
import dev.undefinedteam.gensh1n.render.GL;
import dev.undefinedteam.gensh1n.render.MSAA;
import dev.undefinedteam.gensh1n.render.Renderer;
import dev.undefinedteam.gensh1n.render._new.NText;
import dev.undefinedteam.gensh1n.utils.Utils;
import icyllis.arc3d.opengl.GLTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static org.lwjgl.opengl.GL30C.*;

@Mixin(ChatHud.class)
public abstract class MixinChatHud implements IChatHud {
    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    @Final
    private List<ChatHudLine.Visible> visibleMessages;
    @Shadow
    @Final
    private List<ChatHudLine> messages;
    @Unique
    private int nextId;
    @Unique
    private boolean skipOnAddMessage;

    @Shadow
    protected abstract void addMessage(Text message, @Nullable MessageSignatureData signature, @Nullable MessageIndicator indicator);

    @Shadow
    public abstract void addMessage(Text message);

    @Override
    public void gensh1n$add(Text message, int id) {
        nextId = id;
        addMessage(message);
        nextId = 0;
    }

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V", ordinal = 0, shift = At.Shift.AFTER))
    private void onAddMessageAfterNewChatHudLineVisible(Text message, MessageSignatureData signature, int ticks, MessageIndicator indicator, boolean refresh, CallbackInfo info) {
        ((IChatHudLine) (Object) visibleMessages.getFirst()).gensh1n$setId(nextId);
    }

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V", ordinal = 1, shift = At.Shift.AFTER))
    private void onAddMessageAfterNewChatHudLine(Text message, MessageSignatureData signature, int ticks, MessageIndicator indicator, boolean refresh, CallbackInfo info) {
        ((IChatHudLine) (Object) messages.getFirst()).gensh1n$setId(nextId);
    }

    @Inject(at = @At("HEAD"), method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", cancellable = true)
    private void onAddMessage(Text message, MessageSignatureData signatureData, MessageIndicator indicator, CallbackInfo ci) {
        if (skipOnAddMessage) return;

        ReceiveMessageEvent event = Client.EVENT_BUS.post(ReceiveMessageEvent.get(message, indicator, nextId));

        if (event.isCancelled()) ci.cancel();
        else {
            visibleMessages.removeIf(msg -> ((IChatHudLine) (Object) msg).gensh1n$getId() == nextId && nextId != 0);
            messages.removeIf(msg -> ((IChatHudLine) (Object) msg).gensh1n$getId() == nextId && nextId != 0);

            if (event.isModified()) {
                ci.cancel();

                skipOnAddMessage = true;
                addMessage(event.getMessage(), signatureData, event.getIndicator());
                skipOnAddMessage = false;
            }
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderBefore(DrawContext context, int currentTick, int mouseX, int mouseY, CallbackInfo ci) {
        client.getProfiler().push(Client.LC_NAME + "_r2d_chat_before");
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
            var event = Client.EVENT_BUS.post(Render2DBeforeChat.get(context, mWidth, mHeight, Utils.getTickDelta()));
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
        client.getProfiler().pop();
    }
}
