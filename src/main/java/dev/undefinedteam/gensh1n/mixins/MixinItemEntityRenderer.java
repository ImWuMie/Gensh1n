package dev.undefinedteam.gensh1n.mixins;

import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.events.player.RenderItemEntityEvent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @Author KuChaZi
 * @Date 2024/10/26 11:33
 * @ClassName: MixinItemEntityRenderer skid
 */
@Mixin(ItemEntityRenderer.class)
public abstract class MixinItemEntityRenderer {
    @Shadow
    @Final private Random random;
    @Shadow @Final
    private ItemRenderer itemRenderer;

    @Inject(method = "render(Lnet/minecraft/entity/ItemEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
    private void render(ItemEntity itemEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        RenderItemEntityEvent event = Client.EVENT_BUS.post(RenderItemEntityEvent.get(itemEntity, f, g, matrixStack, vertexConsumerProvider, i, random, itemRenderer));
        if (event.isCancelled()) ci.cancel();
    }
}
