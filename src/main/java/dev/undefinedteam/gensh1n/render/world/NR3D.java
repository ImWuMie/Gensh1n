package dev.undefinedteam.gensh1n.render.world;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import org.jetbrains.annotations.Nullable;

public class NR3D {
    public static NR3D INSTANCE;

    public NR3D() {
        INSTANCE = this;
    }

//    //Use Tessellator.getInstance()
//    private static final int R3D_BUFFER_CAPACITY = 2097152;
//    @Nullable private BufferBuilder m3DVertexConsumer;

    public static Renderer3D REGULAR;

    public void init() {
//        this.m3DVertexConsumer = new BufferBuilder(R3D_BUFFER_CAPACITY);

        REGULAR = new Renderer3D();
    }

//    public BufferBuilder buffer() {
//        return this.m3DVertexConsumer;
//    }
//
//    public void end() {
//        BufferRenderer.drawWithGlobalProgram(this.m3DVertexConsumer.end());
//    }
}
