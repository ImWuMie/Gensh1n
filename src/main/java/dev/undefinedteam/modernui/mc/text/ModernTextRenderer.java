package dev.undefinedteam.modernui.mc.text;

import icyllis.modernui.graphics.MathUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import javax.annotation.Nonnull;

public final class ModernTextRenderer {

    public static final Vector3f SHADOW_OFFSET = new Vector3f(0.0F, 0.0F, 0.03F);
    public static final Vector3f OUTLINE_OFFSET = new Vector3f(0.0F, 0.0F, 0.01F);

    /**
     * Config values
     */
    public static volatile boolean sAllowShadow = true;
    public static volatile float sShadowOffset = 1.0f;
    public static volatile float sOutlineOffset = 0.5f;
    public static volatile boolean sComputeDeviceFontSize = true;
    public static volatile boolean sAllowSDFTextIn2D = true;

    private final TextLayoutEngine mEngine;
    private final float size;

    public ModernTextRenderer(TextLayoutEngine engine, float size) {
        mEngine = engine;
        this.size = size;
    }


    public float getWidth(String text, boolean shadow) {
        // 保证不减1
        return mEngine.lookupVanillaLayout(text, size).getTotalAdvance() + (shadow ? 0.125f * this.size : 0) + 1;
    }

    public float getHeight(String text, boolean shadow) {
        return mEngine.lookupVanillaLayout(text, size).getHeight() + (shadow ? 0.125f * this.size : 0) + 1;
    }

    public float drawText(@Nonnull String text, float x, float y, int color, boolean dropShadow,
                          @Nonnull Matrix4f matrix, @Nonnull VertexConsumerProvider source, TextRenderer.TextLayerType displayMode,
                          int colorBackground, int packedLight) {
        if (text.isEmpty()) {
            return x;
        }

        // ensure alpha, color can be ARGB, or can be RGB
        // we check if alpha <= 1, then make alpha = 255 (fully opaque)
        /*if ((color & 0xfe000000) == 0) {
            color |= 0xff000000;
        }*/

        int a = color >>> 24;
        //if (a <= 2) a = 255;
        int r = color >> 16 & 0xff;
        int g = color >> 8 & 0xff;
        int b = color & 0xff;

        int mode = chooseMode(matrix, displayMode);
        boolean polygonOffset = displayMode == TextRenderer.TextLayerType.POLYGON_OFFSET;
        TextLayout layout = mEngine.lookupVanillaLayout(text, size);
        if (layout.hasColorEmoji() && source instanceof VertexConsumerProvider.Immediate) {
            // performance impact
            ((VertexConsumerProvider.Immediate) source).draw(TexturedRenderLayers.getSign());
        }
        if (dropShadow && sAllowShadow) {
            float offset = 1.0f; //0.125f * this.size;
            layout.drawText(matrix, source, x + offset, y + offset, r >> 2, g >> 2, b >> 2, a, true,
                mode, polygonOffset, colorBackground, packedLight);
            matrix = new Matrix4f(matrix); // if not drop shadow, we don't need to copy the matrix
            matrix.translate(SHADOW_OFFSET);
        }

        x += layout.drawText(matrix, source, x, y, r, g, b, a, false,
            mode, polygonOffset, colorBackground, packedLight);
        return x;
    }

    public float drawText(@Nonnull StringVisitable text, float x, float y, int color, boolean dropShadow,
                          @Nonnull Matrix4f matrix, @Nonnull VertexConsumerProvider source,
                          TextRenderer.TextLayerType displayMode,
                          int colorBackground, int packedLight) {
        if (text == ScreenTexts.EMPTY || text == StringVisitable.EMPTY) {
            return x;
        }

        // ensure alpha, color can be ARGB, or can be RGB
        // we check if alpha <= 1, then make alpha = 255 (fully opaque)
        /*if ((color & 0xfe000000) == 0) {
            color |= 0xff000000;
        }*/

        int a = color >>> 24;
        if (a <= 2) a = 255;
        int r = color >> 16 & 0xff;
        int g = color >> 8 & 0xff;
        int b = color & 0xff;

        int mode = chooseMode(matrix, displayMode);
        boolean polygonOffset = displayMode == TextRenderer.TextLayerType.POLYGON_OFFSET;
        TextLayout layout = mEngine.lookupFormattedLayout(text, size);
        if (layout.hasColorEmoji() && source instanceof VertexConsumerProvider.Immediate) {
            // performance impact
            ((VertexConsumerProvider.Immediate) source).draw(TexturedRenderLayers.getSign());
        }
        if (dropShadow && sAllowShadow) {
            float offset = 2.0f; //0.125f * this.size;
            layout.drawText(matrix, source, x + offset, y + offset, r >> 2, g >> 2, b >> 2, a, true,
                mode, polygonOffset, colorBackground, packedLight);
            matrix = new Matrix4f(matrix); // if not drop shadow, we don't need to copy the matrix
            matrix.translate(SHADOW_OFFSET);
        }

        x += layout.drawText(matrix, source, x, y, r, g, b, a, false,
            mode, polygonOffset, colorBackground, packedLight);
        return x;
    }

    public float drawText(@Nonnull OrderedText text, float x, float y, int color, boolean dropShadow,
                          @Nonnull Matrix4f matrix, @Nonnull VertexConsumerProvider source,
                          TextRenderer.TextLayerType displayMode,
                          int colorBackground, int packedLight) {
        if (text == OrderedText.EMPTY) {
            return x;
        }

        // ensure alpha, color can be ARGB, or can be RGB
        // we check if alpha <= 1, then make alpha = 255 (fully opaque)
        /*if ((color & 0xfe000000) == 0) {
            color |= 0xff000000;
        }*/

        int a = color >>> 24;
        if (a <= 2) a = 255;
        int r = color >> 16 & 0xff;
        int g = color >> 8 & 0xff;
        int b = color & 0xff;

        int mode = chooseMode(matrix, displayMode);
        boolean polygonOffset = displayMode == TextRenderer.TextLayerType.POLYGON_OFFSET;
        TextLayout layout = mEngine.lookupFormattedLayout(text, size);
        if (layout.hasColorEmoji() && source instanceof VertexConsumerProvider.Immediate) {
            // performance impact
            ((VertexConsumerProvider.Immediate) source).draw(TexturedRenderLayers.getSign());
        }
        if (dropShadow && sAllowShadow) {
            float offset = 2.0f; //0.125f * this.size;
            layout.drawText(matrix, source, x + offset, y + offset, r >> 2, g >> 2, b >> 2, a, true,
                mode, polygonOffset, colorBackground, packedLight);
            matrix = new Matrix4f(matrix); // if not drop shadow, we don't need to copy the matrix
            matrix.translate(SHADOW_OFFSET);
        }

        x += layout.drawText(matrix, source, x, y, r, g, b, a, false,
            mode, polygonOffset, colorBackground, packedLight);
        return x;
    }

    public int chooseMode(Matrix4f ctm, TextRenderer.TextLayerType displayMode) {
        if (displayMode == TextRenderer.TextLayerType.SEE_THROUGH) {
            return TextRenderType.MODE_SEE_THROUGH;
        } else if (TextLayoutEngine.sCurrentInWorldRendering) {
            return TextRenderType.MODE_SDF_FILL;
        } else {
            if ((ctm.properties() & Matrix4f.PROPERTY_TRANSLATION) == 0 &&
                (sComputeDeviceFontSize || sAllowSDFTextIn2D)) {
                // JOML can report fake values, compute again
                if (MathUtil.isApproxZero(ctm.m01()) &&
                    MathUtil.isApproxZero(ctm.m02()) &&
                    MathUtil.isApproxZero(ctm.m03()) &&
                    MathUtil.isApproxZero(ctm.m10()) &&
                    MathUtil.isApproxZero(ctm.m12()) &&
                    MathUtil.isApproxZero(ctm.m13()) &&
                    MathUtil.isApproxZero(ctm.m20()) &&
                    MathUtil.isApproxZero(ctm.m21()) &&
                    MathUtil.isApproxZero(ctm.m23()) &&
                    MathUtil.isApproxEqual(ctm.m33(), 1)) {
                    if (MathUtil.isApproxEqual(ctm.m00(), 1) &&
                        MathUtil.isApproxEqual(ctm.m11(), 1)) {
                        // pure translation
                        return TextRenderType.MODE_NORMAL;
                    } else if (sComputeDeviceFontSize && MathUtil.isApproxEqual(ctm.m00(), ctm.m11())) {
                        float upperLimit = Math.max(1.0f,
                            (float) TextLayoutEngine.MIN_PIXEL_DENSITY_FOR_SDF / mEngine.getResLevel());
                        if (ctm.m00() < upperLimit) {
                            // uniform scale smaller and not too large
                            return TextRenderType.MODE_UNIFORM_SCALE;
                        }
                    }
                }
                if (sAllowSDFTextIn2D) {
                    return TextRenderType.MODE_SDF_FILL;
                }
            }
            // pure translation
            return TextRenderType.MODE_NORMAL;
        }
    }

    public void drawText8xOutline(@Nonnull OrderedText text, float x, float y,
                                  int color, int outlineColor, @Nonnull Matrix4f matrix,
                                  @Nonnull VertexConsumerProvider source, int packedLight) {
        if (text == OrderedText.EMPTY) {
            return;
        }

        boolean isBlack = (color & 0xFFFFFF) == 0;
        if (isBlack) {
            color = outlineColor;
        }
        int a = color >>> 24;
        if (a <= 2) a = 255;
        int r = color >> 16 & 0xff;
        int g = color >> 8 & 0xff;
        int b = color & 0xff;

        TextLayout layout = mEngine.lookupFormattedLayout(text, size);
        if (layout.hasColorEmoji() && source instanceof VertexConsumerProvider.Immediate) {
            // performance impact
            ((VertexConsumerProvider.Immediate) source).draw(TexturedRenderLayers.getSign());
        }

        layout.drawText(matrix, source, x, y, r, g, b, a, false,
            TextRenderType.MODE_SDF_FILL, false, 0, packedLight);

        // disable outline if either text color is BLACK or SDF shader is unavailable
        if (isBlack ||
            (TextLayoutEngine.sCurrentInWorldRendering && !TextLayoutEngine.sUseTextShadersInWorld)) {
            return;
        }
        matrix = new Matrix4f(matrix);

        a = outlineColor >>> 24;
        if (a <= 2) a = 255;
        r = outlineColor >> 16 & 0xff;
        g = outlineColor >> 8 & 0xff;
        b = outlineColor & 0xff;

        matrix.translate(OUTLINE_OFFSET);
        layout.drawTextOutline(matrix, source, x, y, r, g, b, a, packedLight);
    }
}
