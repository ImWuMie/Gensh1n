package dev.undefinedteam.gensh1n.render._new;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.undefinedteam.gensh1n.render.GL;
import dev.undefinedteam.modernui.mc.text.ModernTextRenderer;
import dev.undefinedteam.modernui.mc.text.TextLayoutEngine;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;


@StringEncryption
@ControlFlowObfuscation
public class NTextRenderer {
    private MatrixStack mMatrix;
    private final VertexConsumerProvider.Immediate mConsumers;
    private final ModernTextRenderer mRenderer;
    private boolean invalidate = false;

    public NTextRenderer(int bufferSize, float size) {
        this.mMatrix = new MatrixStack();
        this.mMatrix.loadIdentity();
        var allocator = new BufferBuilder(bufferSize);
        this.mConsumers = VertexConsumerProvider.immediate(allocator);
        TextLayoutEngine mEngine = TextLayoutEngine.getInstance();
        this.mRenderer = new ModernTextRenderer(mEngine, size);
    }

    public void begin(MatrixStack stack) {
        this.mMatrix = stack;
        GL.pushState();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public float draw(String text, float x, float y, int color, boolean shadow) {
        return this._draw0(text, x, y, color, shadow, this.mMatrix.peek().getPositionMatrix(), this.mConsumers, TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
    }

    public float draw(String text, double x, double y, int color, boolean shadow) {
        return this.draw(text, (float) x, (float) y, color, shadow);
    }

    public float draw(String text, float x, float y, int color) {
        return this.draw(text, x, y, color, false);
    }

    public float draw(String text, double x, double y, int color) {
        return this.draw(text, (float) x, (float) y, color, false);
    }

    public float draw(OrderedText text, float x, float y, int color, boolean shadow) {
        return this._draw0(text, x, y, color, shadow, this.mMatrix.peek().getPositionMatrix(), this.mConsumers, TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
    }

    public float draw(OrderedText text, double x, double y, int color, boolean shadow) {
        return this.draw(text, (float) x, (float) y, color, shadow);
    }

    public float draw(OrderedText text, float x, float y, int color) {
        return this.draw(text, x, y, color, false);
    }

    public float draw(OrderedText text, double x, double y, int color) {
        return this.draw(text, (float) x, (float) y, color, false);
    }

    public float drawWithShadow(String text, float x, float y, int color) {
        return this.draw(text, x, y, color, true);
    }

    public float drawWithShadow(String text, double x, double y, int color) {
        return this.draw(text, x, y, color, true);
    }

    public void drawWithOutline(String text, float x, float y, int color, int outlineColor) {
        this._drawOutline0(text, x, y, color, outlineColor, this.mMatrix.peek().getPositionMatrix(), this.mConsumers, LightmapTextureManager.MAX_LIGHT_COORDINATE);
    }

    public void drawWithOutline(String text, double x, double y, int color, int outlineColor) {
        this.drawWithOutline(text, (float) x, (float) y, color, outlineColor);
    }

    public float getWidth(String text) {
        return mRenderer.getWidth(text,false);
    }

    public float getHeight(String text) {
        return mRenderer.getHeight(text,false);
    }

    public float getWidth(String text,boolean shadow) {
        return mRenderer.getWidth(text,shadow);
    }

    public float getHeight(String text,boolean shadow) {
        return mRenderer.getHeight(text,shadow);
    }

    public float _draw0(String text,
                        float x, float y,
                        int color, boolean shadow,
                        Matrix4f matrix, VertexConsumerProvider consumers, TextRenderer.TextLayerType type,
                        int bgColor, int light) {
        float resultX;
        if ((resultX = this.mRenderer.drawText(text, x, y, color, shadow, matrix, consumers, type, bgColor, light)) != x) {
            invalidate = true;
        }
        return resultX;
    }

    public float _draw0(Text text,
                        float x, float y,
                        int color, boolean shadow,
                        Matrix4f matrix, VertexConsumerProvider consumers, TextRenderer.TextLayerType type,
                        int bgColor, int light) {
        float resultX;
        if ((resultX = this.mRenderer.drawText(text, x, y, color, shadow, matrix, consumers, type, bgColor, light)) != x) {
            invalidate = true;
        }
        return resultX;
    }

    public float _draw0(OrderedText text,
                        float x, float y,
                        int color, boolean shadow,
                        Matrix4f matrix, VertexConsumerProvider consumers, TextRenderer.TextLayerType type,
                        int bgColor, int light) {
        float resultX;
        if ((resultX = this.mRenderer.drawText(text, x, y, color, shadow, matrix, consumers, type, bgColor, light)) != x) {
            invalidate = true;
        }
        return resultX;
    }

    public void _drawOutline0(OrderedText text,
                              float x, float y,
                              int color, int outlineColor,
                              Matrix4f matrix, VertexConsumerProvider consumers,
                              int light) {
        this.mRenderer.drawText8xOutline(text, x, y, color, outlineColor, matrix, consumers, light);
        invalidate = true;
    }

    public void _drawOutline0(String text,
                              float x, float y,
                              int color, int outlineColor,
                              Matrix4f matrix, VertexConsumerProvider consumers,
                              int light) {
        this._drawOutline0(OrderedText.styledForwardsVisitedString(text, Style.EMPTY), x, y, color, outlineColor, matrix, consumers, light);
    }

    public void _render() {
        if (invalidate) {
            this.mConsumers.draw();
        }
        this.invalidate = false;
    }

    public void end() {
        _render();
        RenderSystem.enableDepthTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        GL.popState();
    }
}
