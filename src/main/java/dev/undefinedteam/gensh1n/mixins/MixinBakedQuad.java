package dev.undefinedteam.gensh1n.mixins;

import dev.undefinedteam.gensh1n.utils.render.IBakedQuad;
import net.minecraft.client.render.model.BakedQuad;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @Author KuChaZi
 * @Date 2024/10/26 11:29
 * @ClassName: MixinBakedQuad skid
 */
@Mixin(BakedQuad.class)
public abstract class MixinBakedQuad implements IBakedQuad {
    @Shadow
    @Final
    protected int[] vertexData;

    @Override
    public float genshin$getX(int vertexI) {
        return Float.intBitsToFloat(vertexData[vertexI * 8]);
    }

    @Override
    public float genshin$getY(int vertexI) {
        return Float.intBitsToFloat(vertexData[vertexI * 8 + 1]);
    }

    @Override
    public float genshin$getZ(int vertexI) {
        return Float.intBitsToFloat(vertexData[vertexI * 8 + 2]);
    }
}
