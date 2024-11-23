package dev.undefinedteam.gensh1n.mixins;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.events.game.*;
import dev.undefinedteam.gensh1n.events.game.SendMessageEvent;
import dev.undefinedteam.gensh1n.system.ClientConfig;
import dev.undefinedteam.gensh1n.system.commands.Commands;
import dev.undefinedteam.gensh1n.utils.chat.ChatUtils;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.EnterReconfigurationS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.undefinedteam.gensh1n.Client.mc;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {

    @Shadow
    private ClientWorld world;

    @Shadow
    public abstract void sendChatMessage(String content);

    @Unique
    private boolean ignoreChatMessage;

    @Unique
    private boolean worldNotNull;
    @Inject(method = "onGameJoin", at = @At("HEAD"))
    private void onGameJoinHead(GameJoinS2CPacket packet, CallbackInfo info) {
        worldNotNull = world != null;
    }

    @Inject(method = "onGameJoin", at = @At("TAIL"))
    private void onGameJoinTail(GameJoinS2CPacket packet, CallbackInfo info) {
        if (worldNotNull) {
            Client.EVENT_BUS.post(GameLeftEvent.INSTANCE);
        }

        Client.EVENT_BUS.post(GameJoinedEvent.INSTANCE);
    }

    // the server sends a GameJoin packet after the reconfiguration phase
    @Inject(method = "onEnterReconfiguration", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER))
    private void onEnterReconfiguration(EnterReconfigurationS2CPacket packet, CallbackInfo info) {
        Client.EVENT_BUS.post(GameLeftEvent.INSTANCE);
    }

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, CallbackInfo ci) {
        if (ignoreChatMessage) return;

        if (!message.startsWith(ClientConfig.get().commandPrefix.get())) {
            SendMessageEvent event = Client.EVENT_BUS.post(SendMessageEvent.get(message));

            if (!event.isCancelled()) {
                ignoreChatMessage = true;
                sendChatMessage(event.message);
                ignoreChatMessage = false;
            }
            ci.cancel();
            return;
        }

        if (message.startsWith(ClientConfig.get().commandPrefix.get())) {
            try {
                Commands.get().dispatch(message.substring(ClientConfig.get().commandPrefix.get().length()));
            } catch (CommandSyntaxException e) {
                ChatUtils.error(e.getMessage());
            }

            mc.inGameHud.getChatHud().addToMessageHistory(message);
            ci.cancel();
        }
    }
}
