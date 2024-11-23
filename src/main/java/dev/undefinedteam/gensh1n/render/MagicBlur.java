package dev.undefinedteam.gensh1n.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.undefinedteam.gensh1n.utils.render.ColorUtils;
import dev.undefinedteam.gensh1n.utils.render.color.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.NativeImage;

import static dev.undefinedteam.gensh1n.Client.mc;

//TODO: 太高雅了我写不来
@Deprecated
public class MagicBlur {
    private final int mAlpha = 255;
    private int mBlurWidth, mBlurHeight;
    private int mBlurX, mBlurY;

    private int colorTopLeft, colorTopRight, colorBottomLeft, colorBottomRight;

    private int tRed, tGreen, tBlue;
    private int lasttRed, lasttGreen, lasttBlue;

    private int bRed, bGreen, bBlue;
    private int lastbRed, lastbGreen, lastbBlue;

    private boolean mEnableFactor;

    public MagicBlur() {
    }

    public MagicBlur factorMode() {
        this.mEnableFactor = true;
        return this;
    }

    public MagicBlur xy(int x, int y) {
        this.mBlurX = x;
        this.mBlurY = y;
        return this;
    }

    public MagicBlur size(int width, int height) {
        this.mBlurWidth = width;
        this.mBlurHeight = height;
        return this;
    }

    public boolean a = false;

    private void readPixels() {
        var factor = mEnableFactor ? (int) mc.getWindow().getScaleFactor() : 1;
        try (NativeImage image = new NativeImage(mc.getFramebuffer().textureWidth, mc.getFramebuffer().textureHeight, false)) {
            RenderSystem.bindTexture(mc.getFramebuffer().getColorAttachment());
            image.loadFromTextureImage(0, true);
            image.mirrorVertically();

            int _x = Math.min(this.mBlurX * factor, image.getWidth() - 1);
            int _y = Math.min(this.mBlurY * factor, image.getHeight() - 1);
            int _x1 = Math.min((this.mBlurX + this.mBlurWidth) * factor, image.getWidth() - 1);
            int _y1 = Math.min((this.mBlurY + this.mBlurHeight) * factor, image.getWidth() - 1);

            colorTopLeft = image.getColor(_x, _y);
            colorTopRight = image.getColor(_x1, _y);
            if (image.getHeight() - 1 >= (this.mBlurY + this.mBlurHeight) * factor)
                colorBottomLeft = image.getColor(_x, _y1);
            else return;

            colorBottomRight = image.getColor(_x1, _y1);
        }
    }

    public void tick() {
        lasttRed = tRed;
        lasttGreen = tGreen;
        lasttBlue = tBlue;

        lastbRed = bRed;
        lastbGreen = bGreen;
        lastbBlue = bBlue;


        Color top = ColorUtils.blend(new Color(colorTopLeft), new Color(colorTopRight));
        Color bottom = ColorUtils.blend(new Color(colorBottomLeft), new Color(colorBottomRight));

        bRed += (int) (((bottom.r - bRed) / (5)) + 0.1);
        bGreen += (int) (((bottom.g - bGreen) / (5)) + 0.1);
        bBlue += (int) (((bottom.b - bBlue) / (5)) + 0.1);

        tRed += (int) (((top.r - tRed) / (5)) + 0.1);
        tGreen += (int) (((top.g - tGreen) / (5)) + 0.1);
        tBlue += (int) (((top.b - tBlue) / (5)) + 0.1);

        tRed = Math.min(tRed, 255);
        tGreen = Math.min(tGreen, 255);
        tBlue = Math.min(tBlue, 255);
        tRed = Math.max(tRed, 0);
        tGreen = Math.max(tGreen, 0);
        tBlue = Math.max(tBlue, 0);

        bRed = Math.min(bRed, 255);
        bGreen = Math.min(bGreen, 255);
        bBlue = Math.min(bBlue, 255);
        bRed = Math.max(bRed, 0);
        bGreen = Math.max(bGreen, 0);
        bBlue = Math.max(bBlue, 0);
    }

    public void render(DrawContext context, float delta) {
        readPixels();

        int tR = smoothAnimation(tRed, lasttRed, delta);
        int tG = smoothAnimation(tGreen, lasttGreen, delta);
        int tB = smoothAnimation(tBlue, lasttBlue, delta);

        int bR = smoothAnimation(bRed, lastbRed, delta);
        int bG = smoothAnimation(bGreen, lastbGreen, delta);
        int bB = smoothAnimation(bBlue, lastbBlue, delta);

        context.fillGradient(this.mBlurX, this.mBlurY, this.mBlurX + this.mBlurWidth, this.mBlurY + this.mBlurHeight, new Color(tR, tG, tB, this.mAlpha).getPacked(), new Color(bR, bG, bB, this.mAlpha).getPacked());
    }

    private int smoothAnimation(double current, double last, float delta) {
        return (int) (current * delta + (last * (1.0f - delta)));
    }
}
