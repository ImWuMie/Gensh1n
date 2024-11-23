package dev.undefinedteam.gensh1n.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.system.modules.Modules;
import dev.undefinedteam.gensh1n.system.modules.movement.NoPush;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @ModifyExpressionValue(method = "jump", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getYaw()F"))
    private float hookFixRotation(float original) {
        var rotationManager = Client.ROT;
        if (rotationManager.shouldOverride()) {
            var rotation = rotationManager.targetRotation;

            if ((Object) this != MinecraftClient.getInstance().player) {
                return original;
            }

            if (rotation.movefix) {

//                float yaw = rotation.yaw() * 0.017453292F;
                return rotation.yaw();
//                return new Vec3d(-MathHelper.sin(yaw) * 0.2F, 0.0, MathHelper.cos(yaw) * 0.2F);
            } else return original;
        } else return original;
    }

    @Inject(method = "pushAwayFrom", at = @At("HEAD"), cancellable = true)
    private void hookNoPush(CallbackInfo callbackInfo) {
        if (Modules.get().isActive(NoPush.class)) {
            callbackInfo.cancel();
        }
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getYaw()F"), slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getYaw()F", ordinal = 1)))
    private float hookBodyRotationsA(float original) {
        if ((Object) this != MinecraftClient.getInstance().player) {
            return original;
        }

        return Client.ROT.shouldOverride() ? Client.ROT.targetRotation.yaw() : original;
    }

    @ModifyExpressionValue(method = "turnHead", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getYaw()F"))
    private float hookBodyRotationsB(float original) {
        if ((Object) this != MinecraftClient.getInstance().player) {
            return original;
        }

        return Client.ROT.shouldOverride() ? Client.ROT.targetRotation.yaw() : original;
    }

    @ModifyExpressionValue(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getPitch()F"))
    private float hookModifyFallFlyingPitch(float original) {
        if ((Object) this != MinecraftClient.getInstance().player) {
            return original;
        }

        var rotationManager = Client.ROT;
        if (rotationManager.shouldOverride()) {
            var rotation = rotationManager.targetRotation;

            if (!rotation.movefix) {
                return original;
            }

            return rotation.pitch();
        }
        return original;
    }

    @ModifyExpressionValue(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getRotationVector()Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d hookModifyFallFlyingRotationVector(Vec3d original) {
        if ((Object) this != MinecraftClient.getInstance().player) {
            return original;
        }

        var rotationManager = Client.ROT;
        if (rotationManager.shouldOverride()) {
            var rotation = rotationManager.targetRotation;

            if (!rotation.movefix) {
                return original;
            }

            return rotationManager.getRotationVec(rotation);
        }

        return original;
    }
}
