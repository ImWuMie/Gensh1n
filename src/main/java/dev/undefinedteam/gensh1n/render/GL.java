package dev.undefinedteam.gensh1n.render;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import dev.undefinedteam.gensh1n.mixins.IBufferRenderer;
import dev.undefinedteam.gensh1n.utils.Utils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static dev.undefinedteam.gensh1n.Client.mc;
import static org.lwjgl.opengl.GL32C.*;

@StringEncryption
@ControlFlowObfuscation
public class GL {
    private static final FloatBuffer MAT = BufferUtils.createFloatBuffer(4 * 4);
    private static final GLState rootState = new GLState();
    private static GLState currentState;

    private static boolean changeBufferRenderer = true;

    public static int CURRENT_IBO;
    private static int prevIbo;

    public static void init() {
        if (FabricLoader.getInstance().isModLoaded("canvas")) changeBufferRenderer = false;
    }

    // Generation

    public static int genVertexArray() {
        return GlStateManager._glGenVertexArrays();
    }

    public static int genBuffer() {
        return GlStateManager._glGenBuffers();
    }

    public static int genTexture() {
        return GlStateManager._genTexture();
    }

    public static int genFramebuffer() {
        return GlStateManager.glGenFramebuffers();
    }

    // Deletion

    public static void deleteBuffer(int buffer) {
        GlStateManager._glDeleteBuffers(buffer);
    }

    public static void deleteVertexArray(int vao) {
        GlStateManager._glDeleteVertexArrays(vao);
    }

    public static void deleteShader(int shader) {
        GlStateManager.glDeleteShader(shader);
    }

    public static void scissorStart(int x, int y, int width, int height) {
        setState(GL_SCISSOR_TEST, true);
        glScissor(x, Utils.getWindowHeight() - y - height, width, height);
    }

    public static void scissorStart(double x, double y, double width, double height) {
        setState(GL_SCISSOR_TEST, true);
        var sY = MathHelper.floor(y);
        var sHeight = MathHelper.floor(height);
        glScissor(MathHelper.floor(x), Utils.getWindowHeight() - sY - sHeight, MathHelper.floor(width), sHeight);
    }

    public static void scissor(int x, int y, int width, int height) {
        glScissor(x, Utils.getWindowHeight() - y - height, width, height);
    }

    public static void scissor(double x, double y, double width, double height) {
        var sY = MathHelper.floor(y);
        var sHeight = MathHelper.floor(height);
        glScissor(MathHelper.floor(x), Utils.getWindowHeight() - sY - sHeight, MathHelper.floor(width), sHeight);
    }

    public static void scissorEnd() {
        scissor(0, 0, Utils.getWindowWidth(), Utils.getWindowHeight());
        setState(GL_SCISSOR_TEST, false);
    }

    public static void deleteTexture(int id) {
        GlStateManager._deleteTexture(id);
    }

    public static void deleteFramebuffer(int fbo) {
        GlStateManager._glDeleteFramebuffers(fbo);
    }

    public static void deleteProgram(int program) {
        GlStateManager.glDeleteProgram(program);
    }

    // Binding

    public static void bindVertexArray(int vao) {
        GlStateManager._glBindVertexArray(vao);
        if (changeBufferRenderer) BufferRenderer.resetCurrentVertexBuffer();
    }

    public static void bindVertexBuffer(int vbo) {
        GlStateManager._glBindBuffer(GL_ARRAY_BUFFER, vbo);
    }

    public static void bindIndexBuffer(int ibo) {
        if (ibo != 0) prevIbo = CURRENT_IBO;
        GlStateManager._glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo != 0 ? ibo : prevIbo);
    }

    public static void bindFramebuffer(int fbo) {
        GlStateManager._glBindFramebuffer(GL_FRAMEBUFFER, fbo);
    }

    // Buffers

    public static void bufferData(int target, ByteBuffer data, int usage) {
        GlStateManager._glBufferData(target, data, usage);
    }

    public static void drawElements(int mode, int first, int type) {
        GlStateManager._drawElements(mode, first, type, 0);
    }

    // Vertex attributes

    public static void enableVertexAttribute(int i) {
        GlStateManager._enableVertexAttribArray(i);
    }

    public static void vertexAttribute(int index, int size, int type, boolean normalized, int stride, long pointer) {
        GlStateManager._vertexAttribPointer(index, size, type, normalized, stride, pointer);
    }

    // Shaders

    public static int createShader(int type) {
        return GlStateManager.glCreateShader(type);
    }

    public static void shaderSource(int shader, String source) {
        GlStateManager.glShaderSource(shader, ImmutableList.of(source));
    }

    public static String compileShader(int shader) {
        GlStateManager.glCompileShader(shader);

        if (GlStateManager.glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            return GlStateManager.glGetShaderInfoLog(shader, 512);
        }

        return null;
    }

    public static int createProgram() {
        return GlStateManager.glCreateProgram();
    }

    public static String linkProgram(int program, int vertShader, int fragShader) {
        GlStateManager.glAttachShader(program, vertShader);
        GlStateManager.glAttachShader(program, fragShader);
        GlStateManager.glLinkProgram(program);

        if (GlStateManager.glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            return GlStateManager.glGetProgramInfoLog(program, 512);
        }

        return null;
    }

    public static void useProgram(int program) {
        GlStateManager._glUseProgram(program);
    }

    public static void viewport(int x, int y, int width, int height) {
        GlStateManager._viewport(x, y, width, height);
    }

    // Uniforms

    public static int getUniformLocation(int program, String name) {
        return GlStateManager._glGetUniformLocation(program, name);
    }

    public static void uniformInt(int location, int v) {
        GlStateManager._glUniform1i(location, v);
    }

    public static void uniformFloat(int location, float v) {
        glUniform1f(location, v);
    }

    public static void uniformFloat2(int location, float v1, float v2) {
        glUniform2f(location, v1, v2);
    }

    public static void uniformFloat3(int location, float v1, float v2, float v3) {
        glUniform3f(location, v1, v2, v3);
    }

    public static void uniformFloat4(int location, float v1, float v2, float v3, float v4) {
        glUniform4f(location, v1, v2, v3, v4);
    }

    public static void uniformFloat3Array(int location, float[] v) {
        glUniform3fv(location, v);
    }

    public static void uniformMatrix(int location, Matrix4f v) {
        v.get(MAT);
        GlStateManager._glUniformMatrix4(location, false, MAT);
    }

    // Textures

    public static void pixelStore(int name, int param) {
        GlStateManager._pixelStore(name, param);
    }

    public static void textureParam(int target, int name, int param) {
        GlStateManager._texParameter(target, name, param);
    }

    public static void textureImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, ByteBuffer pixels) {
        glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels);
    }

    public static void defaultPixelStore() {
        pixelStore(GL_UNPACK_SWAP_BYTES, GL_FALSE);
        pixelStore(GL_UNPACK_LSB_FIRST, GL_FALSE);
        pixelStore(GL_UNPACK_ROW_LENGTH, 0);
        pixelStore(GL_UNPACK_IMAGE_HEIGHT, 0);
        pixelStore(GL_UNPACK_SKIP_ROWS, 0);
        pixelStore(GL_UNPACK_SKIP_PIXELS, 0);
        pixelStore(GL_UNPACK_SKIP_IMAGES, 0);
        pixelStore(GL_UNPACK_ALIGNMENT, 4);
    }

    public static void generateMipmap(int target) {
        glGenerateMipmap(target);
    }

    // Framebuffers

    public static void framebufferTexture2D(int target, int attachment, int textureTarget, int texture, int level) {
        GlStateManager._glFramebufferTexture2D(target, attachment, textureTarget, texture, level);
    }

    public static void clear(int mask) {
        GlStateManager._clearColor(0, 0, 0, 1);
        GlStateManager._clear(mask, false);
    }

    // State

    public static void saveRootState() {
        currentState = null;
        rootState.depthSaved = glIsEnabled(GL_DEPTH_TEST);
        rootState.blendSaved = glIsEnabled(GL_BLEND);
        rootState.cullSaved = glIsEnabled(GL_CULL_FACE);
        rootState.scissorSaved = glIsEnabled(GL_SCISSOR_TEST);
    }

    public static void restoreRootState() {
        currentState = null;
        setState(GL_DEPTH_TEST, rootState.depthSaved);
        setState(GL_BLEND, rootState.blendSaved);
        setState(GL_CULL_FACE, rootState.cullSaved);
        setState(GL_SCISSOR_TEST, rootState.scissorSaved);
    }

    public static void pushState() {
        if (currentState == null) {
            currentState = new GLState();
            rootState.child = currentState;
            currentState.parent = rootState;
        } else {
            currentState.child = new GLState();
            currentState.child.parent = currentState;
            currentState = currentState.child;
        }

        currentState.depthSaved = glIsEnabled(GL_DEPTH_TEST);
        currentState.blendSaved = glIsEnabled(GL_BLEND);
        currentState.cullSaved = glIsEnabled(GL_CULL_FACE);
        currentState.scissorSaved = glIsEnabled(GL_SCISSOR_TEST);
    }

    public static void popState() {
        if (currentState != null) {
            setState(GL_DEPTH_TEST, currentState.depthSaved);
            setState(GL_BLEND, currentState.blendSaved);
            setState(GL_CULL_FACE, currentState.cullSaved);
            setState(GL_SCISSOR_TEST, currentState.scissorSaved);
            currentState = currentState.parent;
            currentState.child = null;
        }
    }

    public static void setState(int state, boolean value) {
        switch (state) {
            case GL_DEPTH_TEST -> {
                if (value) GlStateManager._enableDepthTest();
                else GlStateManager._disableDepthTest();
            }
            case GL_BLEND -> {
                if (value) GlStateManager._enableBlend();
                else GlStateManager._disableBlend();
            }
            case GL_CULL_FACE -> {
                if (value) GlStateManager._enableCull();
                else GlStateManager._disableCull();
            }
            case GL_SCISSOR_TEST -> {
                if (value) GlStateManager._enableScissorTest();
                else GlStateManager._disableScissorTest();
            }
            default -> {
                if (value) glEnable(state);
                else glDisable(state);
            }
        }
    }

    public static void enableDepth() {
        GlStateManager._enableDepthTest();
    }

    public static void disableDepth() {
        GlStateManager._disableDepthTest();
    }

    public static void enableBlend() {
        GlStateManager._enableBlend();
        GlStateManager._blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void disableBlend() {
        GlStateManager._disableBlend();
    }

    public static void enableCull() {
        GlStateManager._enableCull();
    }

    public static void disableCull() {
        GlStateManager._disableCull();
    }

    public static void enableScissorTest() {
        GlStateManager._enableScissorTest();
    }

    public static void disableScissorTest() {
        GlStateManager._disableScissorTest();
    }

    public static void enableLineSmooth(float lineWidth) {
        glLineWidth(lineWidth);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
    }

    public static void disableLineSmooth() {
        glDisable(GL_LINE_SMOOTH);
    }

    public static void bindTexture(Identifier id) {
        GlStateManager._activeTexture(GL_TEXTURE0);
        mc.getTextureManager().bindTexture(id);
    }

    public static void bindTexture(int i, int slot) {
        GlStateManager._activeTexture(GL_TEXTURE0 + slot);
        GlStateManager._bindTexture(i);
    }

    public static void bindTexture(int i) {
        bindTexture(i, 0);
    }

    public static void resetTextureSlot() {
        GlStateManager._activeTexture(GL_TEXTURE0);
    }
}
