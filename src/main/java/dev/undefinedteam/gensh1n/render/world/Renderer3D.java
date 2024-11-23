package dev.undefinedteam.gensh1n.render.world;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.undefinedteam.gensh1n.render.*;
import dev.undefinedteam.gensh1n.utils.render.ColorUtils;
import dev.undefinedteam.gensh1n.utils.render.color.Color;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import static dev.undefinedteam.gensh1n.Client.mc;

public class Renderer3D {
    public final Mesh lines = new ShaderMesh(Shaders.POS_COLOR, Mesh.DrawMode.Lines, Mesh.Attrib.Vec3, Mesh.Attrib.Color);
    public final Mesh triangles = new ShaderMesh(Shaders.POS_COLOR, Mesh.DrawMode.Triangles, Mesh.Attrib.Vec3, Mesh.Attrib.Color);

    public final Identifier NursultanTexture = new Identifier("textures/target.png");

    private static float prevCircleStep;
    private static float circleStep;

    public void begin() {
        lines.begin();
        triangles.begin();
    }


    @Deprecated
    private void end() {
        lines.end();
        triangles.end();
    }


    public void render(MatrixStack matrices, float lineWidth) {
        lines.render(matrices, lineWidth);
        triangles.render(matrices, lineWidth);
    }

    // Lines

    public void line(double x1, double y1, double z1, double x2, double y2, double z2, Color color1, Color color2) {
        lines.line(
            lines.vec3(x1, y1, z1).color(color1).next(),
            lines.vec3(x2, y2, z2).color(color2).next()
        );
    }


    public void line(@NotNull Vec3d vec1, @NotNull Vec3d vec2, Color color) {
        line(vec1.x, vec1.y, vec1.z, vec2.x, vec2.y, vec2.z, color);
    }


    public void line(double x1, double y1, double z1, double x2, double y2, double z2, Color color) {
        line(x1, y1, z1, x2, y2, z2, color, color);
    }

    public void boxLines(double x1, double y1, double z1, double x2, double y2, double z2, Color color, int excludeDir) {
        int blb = lines.vec3(x1, y1, z1).color(color).next();
        int blf = lines.vec3(x1, y1, z2).color(color).next();
        int brb = lines.vec3(x2, y1, z1).color(color).next();
        int brf = lines.vec3(x2, y1, z2).color(color).next();
        int tlb = lines.vec3(x1, y2, z1).color(color).next();
        int tlf = lines.vec3(x1, y2, z2).color(color).next();
        int trb = lines.vec3(x2, y2, z1).color(color).next();
        int trf = lines.vec3(x2, y2, z2).color(color).next();

        if (excludeDir == 0) {
            // Bottom to top
            lines.line(blb, tlb);
            lines.line(blf, tlf);
            lines.line(brb, trb);
            lines.line(brf, trf);

            // Bottom loop
            lines.line(blb, blf);
            lines.line(brb, brf);
            lines.line(blb, brb);
            lines.line(blf, brf);

            // Top loop
            lines.line(tlb, tlf);
            lines.line(trb, trf);
            lines.line(tlb, trb);
            lines.line(tlf, trf);
        } else {
            // Bottom to top
            if (Dir.isNot(excludeDir, Dir.WEST) && Dir.isNot(excludeDir, Dir.NORTH)) lines.line(blb, tlb);
            if (Dir.isNot(excludeDir, Dir.WEST) && Dir.isNot(excludeDir, Dir.SOUTH)) lines.line(blf, tlf);
            if (Dir.isNot(excludeDir, Dir.EAST) && Dir.isNot(excludeDir, Dir.NORTH)) lines.line(brb, trb);
            if (Dir.isNot(excludeDir, Dir.EAST) && Dir.isNot(excludeDir, Dir.SOUTH)) lines.line(brf, trf);

            // Bottom loop
            if (Dir.isNot(excludeDir, Dir.WEST) && Dir.isNot(excludeDir, Dir.DOWN)) lines.line(blb, blf);
            if (Dir.isNot(excludeDir, Dir.EAST) && Dir.isNot(excludeDir, Dir.DOWN)) lines.line(brb, brf);
            if (Dir.isNot(excludeDir, Dir.NORTH) && Dir.isNot(excludeDir, Dir.DOWN)) lines.line(blb, brb);
            if (Dir.isNot(excludeDir, Dir.SOUTH) && Dir.isNot(excludeDir, Dir.DOWN)) lines.line(blf, brf);

            // Top loop
            if (Dir.isNot(excludeDir, Dir.WEST) && Dir.isNot(excludeDir, Dir.UP)) lines.line(tlb, tlf);
            if (Dir.isNot(excludeDir, Dir.EAST) && Dir.isNot(excludeDir, Dir.UP)) lines.line(trb, trf);
            if (Dir.isNot(excludeDir, Dir.NORTH) && Dir.isNot(excludeDir, Dir.UP)) lines.line(tlb, trb);
            if (Dir.isNot(excludeDir, Dir.SOUTH) && Dir.isNot(excludeDir, Dir.UP)) lines.line(tlf, trf);
        }

        lines.growIfNeeded();
    }


    public void blockLines(int x, int y, int z, Color color, int excludeDir) {
        boxLines(x, y, z, x + 1, y + 1, z + 1, color, excludeDir);
    }

    // Quads


    public void quad(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, Color topLeft, Color topRight, Color bottomRight, Color bottomLeft) {
        triangles.quad(
            triangles.vec3(x1, y1, z1).color(bottomLeft).next(),
            triangles.vec3(x2, y2, z2).color(topLeft).next(),
            triangles.vec3(x3, y3, z3).color(topRight).next(),
            triangles.vec3(x4, y4, z4).color(bottomRight).next()
        );
    }


    public void quad(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, Color color) {
        quad(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4, color, color, color, color);
    }


    public void quadVertical(double x1, double y1, double z1, double x2, double y2, double z2, Color color) {
        quad(x1, y1, z1, x1, y2, z1, x2, y2, z2, x2, y1, z2, color);
    }


    public void quadHorizontal(double x1, double y, double z1, double x2, double z2, Color color) {
        quad(x1, y, z1, x1, y, z2, x2, y, z2, x2, y, z1, color);
    }


    public void gradientQuadVertical(double x1, double y1, double z1, double x2, double y2, double z2, Color topColor, Color bottomColor) {
        quad(x1, y1, z1, x1, y2, z1, x2, y2, z2, x2, y1, z2, topColor, topColor, bottomColor, bottomColor);
    }

    // Sides


    @SuppressWarnings("Duplicates")
    public void side(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, Color sideColor, Color lineColor, ShapeMode mode) {
        if (mode.lines()) {
            int i1 = lines.vec3(x1, y1, z1).color(lineColor).next();
            int i2 = lines.vec3(x2, y2, z2).color(lineColor).next();
            int i3 = lines.vec3(x3, y3, z3).color(lineColor).next();
            int i4 = lines.vec3(x4, y4, z4).color(lineColor).next();

            lines.line(i1, i2);
            lines.line(i2, i3);
            lines.line(i3, i4);
            lines.line(i4, i1);
        }

        if (mode.sides()) {
            quad(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4, sideColor);
        }
    }


    public void sideVertical(double x1, double y1, double z1, double x2, double y2, double z2, Color sideColor, Color lineColor, ShapeMode mode) {
        side(x1, y1, z1, x1, y2, z1, x2, y2, z2, x2, y1, z2, sideColor, lineColor, mode);
    }


    public void sideHorizontal(double x1, double y, double z1, double x2, double z2, Color sideColor, Color lineColor, ShapeMode mode) {
        side(x1, y, z1, x1, y, z2, x2, y, z2, x2, y, z1, sideColor, lineColor, mode);
    }

    // Boxes

    @SuppressWarnings("Duplicates")
    public void boxSides(double x1, double y1, double z1, double x2, double y2, double z2, Color color, int excludeDir) {
        int blb = triangles.vec3(x1, y1, z1).color(color).next();
        int blf = triangles.vec3(x1, y1, z2).color(color).next();
        int brb = triangles.vec3(x2, y1, z1).color(color).next();
        int brf = triangles.vec3(x2, y1, z2).color(color).next();
        int tlb = triangles.vec3(x1, y2, z1).color(color).next();
        int tlf = triangles.vec3(x1, y2, z2).color(color).next();
        int trb = triangles.vec3(x2, y2, z1).color(color).next();
        int trf = triangles.vec3(x2, y2, z2).color(color).next();

        if (excludeDir == 0) {
            // Bottom to top
            triangles.quad(blb, blf, tlf, tlb);
            triangles.quad(brb, trb, trf, brf);
            triangles.quad(blb, tlb, trb, brb);
            triangles.quad(blf, brf, trf, tlf);

            // Bottom
            triangles.quad(blb, brb, brf, blf);

            // Top
            triangles.quad(tlb, tlf, trf, trb);
        } else {
            // Bottom to top
            if (Dir.isNot(excludeDir, Dir.WEST)) triangles.quad(blb, blf, tlf, tlb);
            if (Dir.isNot(excludeDir, Dir.EAST)) triangles.quad(brb, trb, trf, brf);
            if (Dir.isNot(excludeDir, Dir.NORTH)) triangles.quad(blb, tlb, trb, brb);
            if (Dir.isNot(excludeDir, Dir.SOUTH)) triangles.quad(blf, brf, trf, tlf);

            // Bottom
            if (Dir.isNot(excludeDir, Dir.DOWN)) triangles.quad(blb, brb, brf, blf);

            // Top
            if (Dir.isNot(excludeDir, Dir.UP)) triangles.quad(tlb, tlf, trf, trb);
        }

        triangles.growIfNeeded();
    }


    public void blockSides(int x, int y, int z, Color color, int excludeDir) {
        boxSides(x, y, z, x + 1, y + 1, z + 1, color, excludeDir);
    }


    public void box(double x1, double y1, double z1, double x2, double y2, double z2, Color sideColor, Color lineColor, ShapeMode mode, int excludeDir) {
        if (mode.lines()) boxLines(x1, y1, z1, x2, y2, z2, lineColor, excludeDir);
        if (mode.sides()) boxSides(x1, y1, z1, x2, y2, z2, sideColor, excludeDir);
    }


    public void box(BlockPos pos, Color sideColor, Color lineColor, ShapeMode mode, int excludeDir) {
        if (mode.lines())
            boxLines(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1, lineColor, excludeDir);
        if (mode.sides())
            boxSides(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1, sideColor, excludeDir);
    }


    public void box(Box box, Color sideColor, Color lineColor, ShapeMode mode, int excludeDir) {
        if (mode.lines()) boxLines(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, lineColor, excludeDir);
        if (mode.sides()) boxSides(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, sideColor, excludeDir);
    }

    public void circle(MatrixStack matrices, double x, double y, double z, double radius, Color color) {
        circle(matrices, x, z, x, z, y, radius, color);
    }

    public void circle(MatrixStack matrices, double x1, double z1, double x2, double z2, double y, double radius, Color color) {
        matrices.push();
        for (int i = 5; i <= 360; i++) {
            double MPI = Math.PI;
            double x = x1 - Math.sin((double) i * MPI / (double) 180.0F) * radius;
            double z = z1 + Math.cos((double) i * MPI / (double) 180.0F) * radius;
            double xx = x2 - Math.sin((double) (i - 5) * MPI / (double) 180.0F) * radius;
            double zz = z2 + Math.cos((double) (i - 5) * MPI / (double) 180.0F) * radius;

            line(x, y, z, xx, y, zz, color);
        }
        matrices.pop();
    }


    public static void updateJello() {
        prevCircleStep = circleStep;
        circleStep += 0.15f;
    }

    public void drawJello(MatrixStack matrix, Entity target, Color color, float delta) {
        double cs = prevCircleStep + (circleStep - prevCircleStep) * delta;
        double prevSinAnim = absSinAnimation(cs - 0.45f);
        double sinAnim = absSinAnimation(cs);
        double x = target.prevX + (target.getX() - target.prevX) * delta - mc.getEntityRenderDispatcher().camera.getPos().getX();
        double y = target.prevY + (target.getY() - target.prevY) * delta - mc.getEntityRenderDispatcher().camera.getPos().getY() + prevSinAnim * target.getHeight();
        double z = target.prevZ + (target.getZ() - target.prevZ) * delta - mc.getEntityRenderDispatcher().camera.getPos().getZ();
        double nextY = target.prevY + (target.getY() - target.prevY) * delta - mc.getEntityRenderDispatcher().camera.getPos().getY() + sinAnim * target.getHeight();

        matrix.push();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        float cos;
        float sin;
        for (int i = 0; i <= 360; i++) {
            cos = (float) (x + Math.cos(i * 6.28 / 360) * ((target.getBoundingBox().maxX - target.getBoundingBox().minX) + (target.getBoundingBox().maxZ - target.getBoundingBox().minZ)) * 0.5f);
            sin = (float) (z + Math.sin(i * 6.28 / 360) * ((target.getBoundingBox().maxX - target.getBoundingBox().minX) + (target.getBoundingBox().maxZ - target.getBoundingBox().minZ)) * 0.5f);
            bufferBuilder.vertex(matrix.peek().getPositionMatrix(), cos, (float) nextY, sin).color(color.getPacked())
                .next();
            bufferBuilder.vertex(matrix.peek().getPositionMatrix(), cos, (float) y, sin).color(injectAlpha(color, 0).getPacked())
                .next();
        }

        tessellator.draw();
//        var builtBuffer = bufferBuilder.end();
//        BufferRenderer.drawWithGlobalProgram(builtBuffer);
//        builtBuffer.close();
        RenderSystem.enableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        matrix.pop();
    }


    public void drawNursultan(MatrixStack matrices, LivingEntity entity) {
        if (entity == null) return;

        matrices.push();

//        RenderSystem.disableLighting();
        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.defaultBlendFunc();

        float playerViewY = mc.getEntityRenderDispatcher().camera.getYaw();
        float playerViewX = mc.getEntityRenderDispatcher().camera.getPitch();

        double camX = mc.getEntityRenderDispatcher().camera.getPos().x;
        double camY = mc.getEntityRenderDispatcher().camera.getPos().y;
        double camZ = mc.getEntityRenderDispatcher().camera.getPos().z;

        double ex = MathHelper.lerp(mc.getTickDelta(), entity.lastRenderX, entity.getX()) - camX;
        double ey = MathHelper.lerp(mc.getTickDelta(), entity.lastRenderY + 1, entity.getY() + 1) - camY;
        double ez = MathHelper.lerp(mc.getTickDelta(), entity.lastRenderZ, entity.getZ()) - camZ;

        matrices.translate(ex, ey, ez);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-playerViewY));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(playerViewX));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) (Math.sin(System.currentTimeMillis() / 600.0) * 360)));

        float scale = 0.025f;
        matrices.scale(-scale, -scale, scale);

        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.setShaderTexture(0, NursultanTexture);

        drawTextureLocationZoom(matrices, -24.0, -24.0, 48.0, 48.0, color(1), color(4), color(16), color(64));

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();

        matrices.pop();
    }

    public void drawTextureLocationZoom(MatrixStack matrices, double x, double y, double width, double height, Color c, Color c1, Color c2, Color c3) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        GL.bindTexture(NursultanTexture);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        drawModalRectWithCustomSizedTexture((int) x, (int) y, 0f, 0f, (int) width, (int) height, (float) width, (float) height, c, c1, c2, c3);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }


    public void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight, Color c, Color c1, Color c2, Color c3)
    {
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldrenderer = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
//        worldrenderer.begin(7, GameRenderer.getPositionTexColorProgram());

        worldrenderer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        worldrenderer.vertex(x, (y + height), 0.0D).texture( (u * f), ((v + (float)height) * f1)).color(c3.r,c3.g,c3.b,c3.a).next();
        worldrenderer.vertex(x + width, (y + height), 0.0D).texture(((u + (float)width) * f), ((v + (float)height) * f1)).color(c2.r,c2.g,c2.b,c2.a).next();
        worldrenderer.vertex((x + width), y, 0.0D).texture(((u + (float)width) * f), (v * f1)).color(c1.r,c1.g,c1.b,c1.a).next();
        worldrenderer.vertex(x, y, 0.0D).texture((u * f), (v * f1)).color(c.r,c.g,c.b,c.a).next();
        tessellator.draw();
    }


    public void drawModalRectWithCustomSizedTexture1(MatrixStack matrices, int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight, Color c, Color c1, Color c2, Color c3) {
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        Matrix4f model = matrices.peek().getPositionMatrix();

        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(model, x, y + height, 0.0F).texture(u * f, (v + height) * f1).color(c3.r, c3.g, c3.b, c3.a).next();
        buffer.vertex(model, x + width, y + height, 0.0F).texture((u + width) * f, (v + height) * f1).color(c2.r, c2.g, c2.b, c2.a).next();
        buffer.vertex(model, x + width, y, 0.0F).texture((u + width) * f, v * f1).color(c1.r, c1.g, c1.b, c1.a).next();
        buffer.vertex(model, x, y, 0.0F).texture(u * f, v * f1).color(c.r, c.g, c.b, c.a).next();
        tessellator.draw();
    }

    public Color color(int tick) {
        return new Color(ColorUtils.rainbow(10, tick * 8, 0.5f, 1f, 1f));
    }

    private static double absSinAnimation(double input) {
        return Math.abs(1 + Math.sin(input)) / 2;
    }

    private static Color injectAlpha(Color color, int alpha) {
        return new Color(color.r, color.g, color.b, MathHelper.clamp(alpha, 0, 255));
    }
}
