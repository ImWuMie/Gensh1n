package dev.undefinedteam.gensh1n.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.undefinedteam.gensh1n.Genshin;
import dev.undefinedteam.gensh1n.utils.ResLocation;
import dev.undefinedteam.gensh1n.utils.render.color.Color;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.apache.commons.io.IOUtils;
import org.joml.Matrix4f;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static dev.undefinedteam.gensh1n.Client.mc;
import static org.lwjgl.opengl.GL32C.*;

public class Shader {
    private final int id;
    private final Object2IntMap<String> uniformLocations = new Object2IntOpenHashMap<>();

    public Shader(String vert, String frag) {
        this(vert, frag, true, true, true);
    }

    public Shader(String vertSource, String fragSource, boolean error, boolean pathVert, boolean pathFrag) {
        int vert = GL.createShader(GL_VERTEX_SHADER);
        GL.shaderSource(vert, pathVert ? read(vertSource) : vertSource);

        String vertError = GL.compileShader(vert);
        if (vertError != null) {
            if (error) {
                Genshin.LOG.error("Failed to compile vertex shader (" + vertSource + "): " + vertError);
            }
            throw new RuntimeException("Failed to compile vertex shader (" + vertSource + "): " + vertError);
        }

        int frag = GL.createShader(GL_FRAGMENT_SHADER);
        GL.shaderSource(frag, pathFrag ? read(fragSource) : fragSource);

        String fragError = GL.compileShader(frag);
        if (fragError != null) {
            if (error) {
                Genshin.LOG.error("Failed to compile fragment shader (" + fragSource + "): " + fragError);
            }
            throw new RuntimeException("Failed to compile fragment shader (" + fragSource + "): " + fragError);
        }

        id = GL.createProgram();

        String programError = GL.linkProgram(id, vert, frag);
        if (programError != null) {
            if (error) {
                Genshin.LOG.error("Failed to link program: " + programError);
            }
            throw new RuntimeException("Failed to link program: " + programError);
        }

        GL.deleteShader(vert);
        GL.deleteShader(frag);
    }

    private static String read(String path) {
        try {
            return IOUtils.toString(mc.getResourceManager().getResource(ResLocation.of("shaders/" + path)).get().getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read shader '" + path + "'", e);
        }
    }

    public void bind() {
        GL.useProgram(id);
    }

    private int getLocation(String name) {
        if (uniformLocations.containsKey(name)) return uniformLocations.getInt(name);

        int location = GL.getUniformLocation(id, name);
        uniformLocations.put(name, location);
        return location;
    }

    public void set(String name, boolean v) {
        GL.uniformInt(getLocation(name), v ? GL_TRUE : GL_FALSE);
    }

    public void set(String name, int v) {
        GL.uniformInt(getLocation(name), v);
    }

    public void set(String name, double v) {
        GL.uniformFloat(getLocation(name), (float) v);
    }

    public void set(String name, double v1, double v2) {
        GL.uniformFloat2(getLocation(name), (float) v1, (float) v2);
    }

    public void set(String name, float a, float b, float c, float d) {
        GL.uniformFloat4(getLocation(name), d, b, c, d);
    }

    public void set(String name, Color color) {
        GL.uniformFloat4(getLocation(name), (float) color.r / 255, (float) color.g / 255, (float) color.b / 255, (float) color.a / 255);
    }

    public void set(String name, Matrix4f mat) {
        GL.uniformMatrix(getLocation(name), mat);
    }

    public void setDefaults() {
        set("u_Proj", RenderSystem.getProjectionMatrix());
        set("u_ModelView", RenderSystem.getModelViewStack().peek().getPositionMatrix());
    }

    public void unbind() {
        GL.useProgram(0);
    }
}
