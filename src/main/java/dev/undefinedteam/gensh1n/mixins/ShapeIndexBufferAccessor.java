package dev.undefinedteam.gensh1n.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderSystem.ShapeIndexBuffer.class)
public interface ShapeIndexBufferAccessor {
    @Accessor("id")
    int getId();
}
