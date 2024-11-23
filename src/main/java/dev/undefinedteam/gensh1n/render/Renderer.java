package dev.undefinedteam.gensh1n.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.undefinedteam.gensh1n.utils.render.color.Color;
import icyllis.arc3d.core.Matrix4;
import icyllis.arc3d.engine.Engine;
import icyllis.arc3d.opengl.GLDevice;
import icyllis.modernui.core.Core;
import icyllis.modernui.core.Window;
import icyllis.modernui.graphics.GLSurface;
import icyllis.modernui.graphics.GLSurfaceCanvas;
import icyllis.modernui.graphics.Paint;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import static dev.undefinedteam.gensh1n.Client.mc;
import static icyllis.arc3d.opengl.GLCore.DEFAULT_TEXTURE;
import static icyllis.modernui.ModernUI.LOGGER;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL13C.*;
import static org.lwjgl.opengl.GL14C.*;
import static org.lwjgl.opengl.GL20C.*;
import static org.lwjgl.opengl.GL30C.*;


@StringEncryption
@NativeObfuscation
@ControlFlowObfuscation
public class Renderer {
//    public static final Renderer BLIT_SCREEN = new Renderer(null);
    public static final Renderer MAIN = new Renderer();

//    private final GLSurface framebuffer;
    private final GLSurfaceCanvas renderer;
    private final GLDevice device;
    private final Matrix4 mProjectionMatrix = new Matrix4();
    private final Paint defPaint = Paint.obtain();

    public Renderer() {
//        this(new GLSurface());
        this(null);
    }

    public Renderer(GLSurface surface) {
        this.device = (GLDevice) Core.requireDirectContext().getDevice();
        this.renderer = new GLSurfaceCanvas(this.device);
//        this.framebuffer = surface;
    }

    private Paint paint = Paint.obtain();

    public Renderer begin() {
        return this.begin(null);
    }

    public Renderer begin(Paint paint) {
        this.paint = paint == null ? defPaint : paint;
        int width = mc.getWindow().getFramebufferWidth();
        int height = mc.getWindow().getFramebufferHeight();
        device.markContextDirty(Engine.GLBackendState.kPixelStore);
        renderer.setProjection(mProjectionMatrix.setOrthographic(width, height, 0, Window.LAST_SYSTEM_WINDOW * 2 + 1, true));
        return this;
    }

    public Renderer drawRound(float x, float y, float width, float height, float radius, Color color) {
        color(color).renderer.drawRoundRect(x, y, x + width, y + height, radius, paint);
        return this;
    }

    public Renderer drawRound(float x, float y, float width, float height, float radius, int color) {
        color(color).renderer.drawRoundRect(x, y, x + width, y + height, radius, paint);
        return this;
    }

    public Renderer drawRect(float x, float y, float width, float height, Color color) {
        color(color).renderer.drawRect(x, y, x + width, y + height, paint);
        return this;
    }

    public Renderer drawRect(double x, double y, double width, double height, Color color) {
        color(color).renderer.drawRect((float) x, (float) y, (float) (x + width), (float) (y + height), paint);
        return this;
    }

    public Renderer drawRect(double x, double y, double width, double height, int color) {
        color(color).renderer.drawRect((float) x, (float) y, (float) (x + width), (float) (y + height), paint);
        return this;
    }

    public Renderer drawLine(float x, float y, float x1, float y1, Color color) {
        color(color).renderer.drawLine(x, y, x1, y1, paint);
        return this;
    }

    public Renderer drawLine(float x, float y, float x1, float y1, int color) {
        color(color).renderer.drawLine(x, y, x1, y1, paint);
        return this;
    }

    public Renderer drawLine(double x, double y, double x1, double y1, int color) {
        color(color).renderer.drawLine((float) x, (float) y, (float) x1, (float) y1, paint);
        return this;
    }

    public double factor() {
        return mc.getWindow().getScaleFactor();
    }

    public Renderer color(Color color) {
        paint.setRGBA(color.r, color.g, color.b, color.a);
        return this;
    }

    public Renderer color(int color) {
        paint.setColor(color);
        return this;
    }

    public GLSurfaceCanvas _renderer() {
        return this.renderer;
    }

    public Paint _paint() {
        return this.paint;
    }

    public Renderer render() {
        int width = mc.getWindow().getFramebufferWidth();
        int height = mc.getWindow().getFramebufferHeight();

        GL.pushState();
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.activeTexture(GL_TEXTURE0);
        RenderSystem.disableDepthTest();
        glEnable(GL_LINE_SMOOTH);
        //glDisable(GL_DEPTH_TEST);
        // Minecraft.mainRenderTarget has no transparency (=== 1)
        // UI layer has a transparent background, with premultiplied alpha
        RenderSystem.blendFuncSeparate(GL_ONE, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        glBlendFuncSeparate(GL_ONE, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        int oldVertexArray = glGetInteger(GL_VERTEX_ARRAY_BINDING);
        int oldProgram = glGetInteger(GL_CURRENT_PROGRAM);

        {

            // 其实应该需要 模板测试的framebuffer
            glEnable(GL_STENCIL_TEST);
//            var mBlit = renderer.executeRenderPass(framebuffer);
            renderer.executeRenderPass(null);
            glDisable(GL_STENCIL_TEST);
//            glBindFramebuffer(GL_DRAW_FRAMEBUFFER, GFramebuffer.MAIN.get());
//            if (framebuffer != null && mBlit && framebuffer.getBackingWidth() > 0) {
//                GLTexture layer = framebuffer.getAttachedTexture(GL_COLOR_ATTACHMENT0);
//                // draw off-screen target to Minecraft mainTarget (not the default framebuffer)
//                {
//                    renderer.drawLayer(layer, width, height, 1, true);
//                    renderer.executeRenderPass(null);
//                }
//            }
        }

        paint.recycle();
        renderer.reset(width, height);
        glDisable(GL_LINE_SMOOTH);
        glBindVertexArray(oldVertexArray);
        glUseProgram(oldProgram);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        // force changing Blaze3D state
        RenderSystem.bindTexture(DEFAULT_TEXTURE);
        GL.popState();
        return this;
    }
}
