package dev.undefinedteam.gensh1n.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.events.player.PlayerSafeWalkEvent;
import dev.undefinedteam.gensh1n.events.player.PlayerTravelEvent;
import dev.undefinedteam.gensh1n.system.modules.Modules;
import dev.undefinedteam.gensh1n.system.modules.movement.NoSlow;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity  {
    @ModifyReturnValue(method = "clipAtLedge", at = @At("RETURN"))
    private boolean hookSafeWalk(boolean original) {
        final var event = Client.EVENT_BUS.post(new PlayerSafeWalkEvent());
        return original || event.isSafeWalk;
    }

    @ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getYaw()F"))
    private float hookFixRotation(float original) {
        if ((Object) this != MinecraftClient.getInstance().player) {
            return original;
        }

        if (Client.ROT.shouldOverride()) {
            var rot = Client.ROT.targetRotation;

            if (rot.movefix) {
                return rot.yaw();
            } else return original;
        }
        return original;
    }

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void onTravelhookPre(Vec3d movementInput, CallbackInfo ci) {
        if(Client.mc.player == null)
            return;

        final PlayerTravelEvent event = new PlayerTravelEvent(movementInput, true);
        Client.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            Client.mc.player.move(MovementType.SELF, Client.mc.player.getVelocity());
            ci.cancel();
        }
    }


    @Inject(method = "travel", at = @At("RETURN"), cancellable = true)
    private void onTravelhookPost(Vec3d movementInput, CallbackInfo ci) {
        if(Client.mc.player == null)
            return;
        final PlayerTravelEvent event = new PlayerTravelEvent(movementInput, false);
        Client.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            Client.mc.player.move(MovementType.SELF, Client.mc.player.getVelocity());
            ci.cancel();
        }
    }


    @WrapWithCondition(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V", ordinal = 0))
    private boolean hookSlowVelocity(PlayerEntity instance, Vec3d vec3d) {
        return (Object) this != MinecraftClient.getInstance().player || !Modules.get().get(NoSlow.class).attackNoSlow.get();
    }

    @WrapWithCondition(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setSprinting(Z)V", ordinal = 0))
    private boolean hookSlowVelocity(PlayerEntity instance, boolean b) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            return !Modules.get().get(NoSlow.class).attackNoSlow.get() || b;
        }

        return true;
    }
}
