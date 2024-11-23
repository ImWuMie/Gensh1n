package dev.undefinedteam.gensh1n.mixins;

import dev.undefinedteam.gensh1n.render.GL;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.ByteBuffer;

@Mixin(VertexBuffer.class)
public abstract class MixinVertexBuffer {
    @Shadow private int indexBufferId;

    @Inject(method = "uploadIndexBuffer", at = @At("RETURN"))
    private void onConfigureIndexBuffer(BufferBuilder.DrawParameters parameters, ByteBuffer vertexBuffer, CallbackInfoReturnable<VertexFormat> info) {
        if (info.getReturnValue() == null) GL.CURRENT_IBO = this.indexBufferId;
        else GL.CURRENT_IBO = ((ShapeIndexBufferAccessor) info.getReturnValue()).getId();
    }
}
