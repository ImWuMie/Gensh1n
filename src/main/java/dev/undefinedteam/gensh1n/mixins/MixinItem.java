package dev.undefinedteam.gensh1n.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.undefinedteam.gensh1n.Client;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Item.class)
public class MixinItem {
    @ModifyExpressionValue(method = "raycast", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/entity/player/PlayerEntity;getYaw()F"))
    private static float hookFixRotation(float original, World world, PlayerEntity player, RaycastContext.FluidHandling fluidHandling) {
        if (Client.ROT.shouldOverride()) {
            var rotation = Client.ROT.targetRotation;

            if (player == MinecraftClient.getInstance().player) {
                return rotation.yaw();
            }
        }

        return original;
    }

    @ModifyExpressionValue(method = "raycast", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/entity/player/PlayerEntity;getPitch()F"))
    private static float hookFixRotationP(float original, World world, PlayerEntity player, RaycastContext.FluidHandling fluidHandling) {
        if (Client.ROT.shouldOverride()) {
            var rotation = Client.ROT.targetRotation;

            if (player == MinecraftClient.getInstance().player) {
                return rotation.pitch();
            }
        }

        return original;
    }
}
