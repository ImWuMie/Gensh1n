package dev.undefinedteam.gensh1n.mixins;

import dev.undefinedteam.gensh1n.Client;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class MixinKeyboardInput extends Input {

    @Shadow
    @Final
    private GameOptions settings;

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/input/KeyboardInput;sneaking:Z", shift = At.Shift.AFTER), allow = 1)
    private void injectMovementInputEvent(boolean slowDown, float f, CallbackInfo ci) {
        this.fixStrafeMovement();
    }

    @Unique
    private void fixStrafeMovement() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        var rotationManager = Client.ROT;

        if (!rotationManager.shouldOverride()) return;

        var rotation = rotationManager.targetRotation;

        if (!rotation.movefix) return;

        float z = this.movementForward;
        float x = this.movementSideways;

        float deltaYaw = player.getYaw() - rotation.yaw();

        float sideways = x * MathHelper.cos(deltaYaw * 0.017453292f) - z * MathHelper.sin(deltaYaw * 0.017453292f);
        float forward = z * MathHelper.cos(deltaYaw * 0.017453292f) + x * MathHelper.sin(deltaYaw * 0.017453292f);

        this.movementSideways = Math.round(sideways);
        this.movementForward = Math.round(forward);
    }
}
