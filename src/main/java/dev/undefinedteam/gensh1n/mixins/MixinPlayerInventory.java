package dev.undefinedteam.gensh1n.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.undefinedteam.gensh1n.Client;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerInventory.class)
public class MixinPlayerInventory {

    @Shadow
    @Final
    public PlayerEntity player;

    @ModifyExpressionValue(method = {"dropSelectedItem", "getBlockBreakingSpeed", "getMainHandStack"}, at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerInventory;selectedSlot:I"))
    private int hookOverrideOriginalSlot(int original) {
        return ((PlayerInventory) (Object) this).player == MinecraftClient.getInstance().player ? Client.HOTBAR.hasSelect() ? Client.HOTBAR.getSlot() : original : original;
    }
}
