package dev.undefinedteam.gensh1n.render;

import icyllis.arc3d.core.MathUtil;
import icyllis.arc3d.engine.GpuResource;
import icyllis.arc3d.engine.ISurface;
import icyllis.arc3d.opengl.GLAttachment;
import icyllis.arc3d.opengl.GLBackendFormat;
import icyllis.arc3d.opengl.GLDevice;
import icyllis.arc3d.opengl.GLTexture;
import icyllis.modernui.core.Core;
import icyllis.modernui.graphics.Bitmap;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import javax.annotation.Nonnull;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Objects;

import static icyllis.modernui.graphics.GLSurface.NUM_RENDER_TARGETS;
import static org.lwjgl.opengl.GL11C.GL_COLOR;
import static org.lwjgl.opengl.GL11C.GL_RGBA8;
import static org.lwjgl.opengl.GL11C.glDrawBuffer;
import static org.lwjgl.opengl.GL11C.glReadBuffer;
import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.opengl.GL30C.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL32C.glFramebufferTexture;

public class GFramebuffer implements AutoCloseable {
    public static final GFramebuffer MAIN = new GFramebuffer();

    private final FloatBuffer mClearColor = BufferUtils.createFloatBuffer(4);

    private final GLTexture[] mColorAttachments = new GLTexture[NUM_RENDER_TARGETS];
    private GLAttachment mStencilAttachment;

    private int mBackingWidth;
    private int mBackingHeight;

    private int mFramebuffer;

    public int get() {
        if (mFramebuffer == 0) {
            mFramebuffer = glGenFramebuffers();
        }
        return mFramebuffer;
    }

    /**
     * Binds this framebuffer object to both draw and read target.
     */
    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, get());
    }

    public void bindDraw() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, get());
    }

    public void bindRead() {
        glBindFramebuffer(GL_READ_FRAMEBUFFER, get());
    }

    /**
     * Clear the current color buffer set by {@link #setDrawBuffer(int)} to the color (0,0,0,0).
     * Clear the current depth buffer to 1.0f, and stencil buffer to 0.
     */
    public void clearColorBuffer() {
        // here drawbuffer is zero, because setDrawBuffer only set the buffer with index 0
        glClearBufferfv(GL_COLOR, 0, mClearColor);
    }

    public void clearStencilBuffer() {
        // for depth or stencil, the drawbuffer must be 0
        glClearBufferfi(GL_DEPTH_STENCIL, 0, 1.0f, 0);
    }

    /**
     * Set the color buffer for <code>layout(location = 0) out vec4 fragColor</code>.
     * That means the color buffer index is 0.
     * <p>
     * Note that only GL_COLOR_ATTACHMENT[x] or GL_NONE is accepted by a framebuffer
     * object. Values such as GL_FRONT_LEFT, GL_BACK are only accepted by the default
     * framebuffer (reserved by the window).
     *
     * @param buffer enum buffer
     */
    public void setDrawBuffer(int buffer) {
        glDrawBuffer(buffer);
    }

    public void setReadBuffer(int buffer) {
        glReadBuffer(buffer);
    }

    public int getBackingWidth() {
        return mBackingWidth;
    }

    public int getBackingHeight() {
        return mBackingHeight;
    }

    /**
     * Returns the attached texture with the given attachment point.
     *
     * @param attachment specify an attachment point
     * @return the raw ptr to texture
     * @throws NullPointerException attachment is not a texture or detached
     */
    @Nonnull
    public GLTexture getAttachedTexture(int attachment) {
        return Objects.requireNonNull(
            mColorAttachments[attachment - GL_COLOR_ATTACHMENT0]
        );
    }

    @SuppressWarnings("all")
    public void makeBuffers(int width, int height, boolean exact) {
        if (width <= 0 || height <= 0) {
            return;
        }
        if (exact) {
            if (mBackingWidth == width && mBackingHeight == height) {
                return;
            }
        } else {
            if (mBackingWidth >= width && mBackingHeight >= height) {
                return;
            }
        }
        mBackingWidth = width;
        mBackingHeight = height;
        var dContext = Core.requireDirectContext();
        for (int i = 0; i < NUM_RENDER_TARGETS; i++) {
            if (mColorAttachments[i] != null) {
                mColorAttachments[i].unref();
            }
            mColorAttachments[i] = (GLTexture) dContext
                .getResourceProvider()
                .createTexture(width, height,
                    GLBackendFormat.make(GL_RGBA8),
                    1,
                    ISurface.FLAG_BUDGETED,
                    null
                );
            Objects.requireNonNull(mColorAttachments[i], "Failed to create G-buffer " + i);
            glFramebufferTexture(
                GL_FRAMEBUFFER,
                GL_COLOR_ATTACHMENT0 + i,
                mColorAttachments[i].getHandle(),
                0
            );
        }
        if (mStencilAttachment != null) {
            mStencilAttachment.unref();
        }
        mStencilAttachment = GLAttachment.makeStencil(
            (GLDevice) dContext.getDevice(),
            width, height,
            1, GL_STENCIL_INDEX8
        );
        Objects.requireNonNull(mStencilAttachment, "Failed to create depth/stencil");
        glFramebufferRenderbuffer(
            GL_FRAMEBUFFER,
            GL_STENCIL_ATTACHMENT,
            GL_RENDERBUFFER,
            mStencilAttachment.getRenderbufferID()
        );
        int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (status != GL_FRAMEBUFFER_COMPLETE) {
            throw new IllegalStateException("Framebuffer is not complete: " + status);
        }
    }

    @Override
    public void close() {
        if (mFramebuffer != 0) {
            glDeleteFramebuffers(mFramebuffer);
        }
        mFramebuffer = 0;
        for (int i = 0; i < NUM_RENDER_TARGETS; i++) {
            mColorAttachments[i] = GpuResource.move(mColorAttachments[i]);
        }
        mStencilAttachment = GpuResource.move(mStencilAttachment);
    }

    @SuppressWarnings("resource")
    public Bitmap takeScreenshot() {
        bindRead();
        final int width = getBackingWidth();
        final int height = getBackingHeight();
        final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Format.RGBA_8888);
        glPixelStorei(GL_PACK_ROW_LENGTH, 0);
        glPixelStorei(GL_PACK_SKIP_ROWS, 0);
        glPixelStorei(GL_PACK_SKIP_PIXELS, 0);
        glPixelStorei(GL_PACK_ALIGNMENT, 1);
        // SYNC GPU TODO (use transfer buffer?)
        glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, bitmap.getAddress());
        Bitmap.flipVertically(bitmap);
        unpremulAlpha(bitmap);
        return bitmap;
    }

    @SuppressWarnings("IntegerMultiplicationImplicitCastToLong")
    private void unpremulAlpha(Bitmap bitmap) {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final int rowStride = bitmap.getRowStride();
        long addr = bitmap.getAddress();
        final boolean big = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                long base = addr + (j << 2);
                int col = MemoryUtil.memGetInt(base);
                if (big) {
                    col = Integer.reverseBytes(col);
                }
                int alpha = col >>> 24;
                if (alpha != 0) {
                    float a = alpha / 255.0f;
                    int r = MathUtil.clamp((int) ((col & 0xFF) / a + 0.5f), 0, 0xFF);
                    int g = MathUtil.clamp((int) (((col >> 8) & 0xFF) / a + 0.5f), 0, 0xFF);
                    int b = MathUtil.clamp((int) (((col >> 16) & 0xFF) / a + 0.5f), 0, 0xFF);
                    col = (r) | (g << 8) | (b << 16) | (col & 0xFF000000);
                    if (big) {
                        col = Integer.reverseBytes(col);
                    }
                    MemoryUtil.memPutInt(base, col);
                }
            }
            addr += rowStride;
        }
    }
}
