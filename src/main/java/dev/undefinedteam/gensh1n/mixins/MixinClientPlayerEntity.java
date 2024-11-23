package dev.undefinedteam.gensh1n.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.events.player.SlowdownEvent;
import dev.undefinedteam.gensh1n.events.player.SprintEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity {
    @Shadow
    @Final
    protected MinecraftClient client;

    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getAbilities()Lnet/minecraft/entity/player/PlayerAbilities;", shift = At.Shift.AFTER))
    private void onSprint(CallbackInfo ci) {
        if ((Object) this == client.player) {
            var event = SprintEvent.get(isSprinting());
            Client.EVENT_BUS.post(event);
            setSprinting(event.isSprint);
        }
    }

    @Inject(method = "sendMovementPackets", at = @At(value = "HEAD"))
    private void onSendMovePacketHead(CallbackInfo ci) {
        if (Client.ROT.isApplyToPlayer()) {
            Client.ROT.targetRotation.rot.toPlayer(client.player);
        }
    }

    @Inject(method = "sendMovementPackets", at = @At(value = "RETURN"))
    private void onSendMovePacket(CallbackInfo ci) {
        if (Client.ROT.shouldOverride()) {
            Client.ROT.targetRotation.passTicks--;
        }
    }

    @ModifyExpressionValue(method = {"sendMovementPackets", "tick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getYaw()F"))
    private float hookSilentRotationYaw(float original) {
        return Client.ROT.shouldOverride() ? Client.ROT.targetRotation.yaw() : original;
    }

    @ModifyExpressionValue(method = {"sendMovementPackets", "tick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getPitch()F"))
    private float hookSilentRotationPitch(float original) {
        return Client.ROT.shouldOverride() ? Client.ROT.targetRotation.pitch() : original;
    }

    @ModifyExpressionValue(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    private boolean redirectUsingItem(boolean isUsingItem) {
        //if (StarryClient.getInstance().getModuleManager().getByClass(NoSlow.class).isEnabled()) return false;
        SlowdownEvent event = new SlowdownEvent();
        Client.EVENT_BUS.post(event);
        if (event.isCancelled()) return false;
        return isUsingItem;
    }
}
