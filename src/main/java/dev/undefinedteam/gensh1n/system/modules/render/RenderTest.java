package dev.undefinedteam.gensh1n.system.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.events.render.Render3DEvent;
import dev.undefinedteam.gensh1n.render.ShapeMode;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class RenderTest extends Module {
    public RenderTest() {
        super(Categories.Render, "render-test", "");
    }

    private final SettingGroup sg = settings.getDefaultGroup();
//    private final Setting<Boolean> glow = bool(sg, "glow-entity", true);
//    private final Setting<Double> radius = doubleN(sg, "test-radius", 3.0, 0.1, 10, glow::get);
//    private final Setting<Double> inGlow = doubleN(sg, "inGlow-radius", 3.0, 0.1, 10, glow::get);
//    private final Setting<Double> outGlow = doubleN(sg, "outGlow-radius", 3.0, 0.1, 10, glow::get);
//
//    private final Setting<Boolean> jello = bool(sg, "glow-entity", true);
//    private final Setting<Boolean> box = bool(sg, "glow-entity", true);
//    private final Setting<Boolean> line = bool(sg, "glow-entity", true);
//
//    private float prevCircleStep;
//    private float circleStep;
//
//
//    @EventHandler
//    private void onRender3D(TickEvent.Pre e) {
//        updateJello();
//    }
//
//    public void updateJello() {
//        prevCircleStep = circleStep;
//        circleStep += 0.15f;
//    }
//
//    @EventHandler
//    private void onRender3D(Render3DEvent event) {
//        if (mc.targetedEntity != null) {
//            var entity = mc.targetedEntity;
//            double x = MathHelper.lerp(event.tickDelta, entity.lastRenderX, entity.getX()) - entity.getX();
//            double y = MathHelper.lerp(event.tickDelta, entity.lastRenderY, entity.getY()) - entity.getY();
//            double z = MathHelper.lerp(event.tickDelta, entity.lastRenderZ, entity.getZ()) - entity.getZ();
//
//            double px = MathHelper.lerp(event.tickDelta, mc.player.lastRenderX, mc.player.getX()) - mc.player.getX();
//            double py = MathHelper.lerp(event.tickDelta, mc.player.lastRenderY, mc.player.getY()) - mc.player.getY();
//            double pz = MathHelper.lerp(event.tickDelta, mc.player.lastRenderZ, mc.player.getZ()) - mc.player.getZ();
//
//            if (jello.get())
//                drawJello(event.matrices, entity, Color.WHITE, event.tickDelta);
//            if (box.get())
//                event.renderer.box(entity.getBoundingBox(), new Color(255, 255, 255, 80), Color.WHITE, ShapeMode.Both, 0);
//            if (line.get())
//                event.renderer.line(x,y,z,px,py,pz, Color.WHITE);
//            if (glow.get())
//                drawGlowCircle(event.matrices, entity.getX() + x, entity.getY() + y, entity.getZ() + z, radius.get().floatValue());
//        }
//    }
//
//    public void drawGlowCircle(MatrixStack matrices, double posX, double posY, double posZ, float radius) {
//        drawGlowCircle(matrices, posX, posY, posZ, radius, outGlow.get().floatValue(), inGlow.get().floatValue());
//    }
//
//    public void drawGlowCircle(MatrixStack matrices, double posX, double posY, double posZ, float radius, float glowRadius, float inGlowRadius) {
//        double x = posX - mc.getEntityRenderDispatcher().camera.getPos().getX();
//        double y = posY - mc.getEntityRenderDispatcher().camera.getPos().getY();
//        double z = posZ - mc.getEntityRenderDispatcher().camera.getPos().getZ();
//        matrices.push();
//        matrices.translate(x, y, z);
//        Tessellator tessellator = RenderSystem.renderThreadTesselator();
//        float start = radius + glowRadius; // r + gR
//        float middle = (start + radius) / 2; // (2r+gR)/2
//        float k = Math.min(glowRadius, 1.0f) / Math.min(radius, 1.0f);
//        RenderSystem.enableBlend();
//        RenderSystem.defaultBlendFunc();
//        RenderSystem.disableCull();
//        RenderSystem.enableDepthTest();
//        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
//        BuiltBuffer builtBuffer;
//        BufferBuilder bufferBuilder;
//
//        // Out circle
//        /*
//        __
//        --
//         */
//        bufferBuilder = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
//        for (int i = 0; i <= 360; i += 5) {
//            int clr = Color.WHITE.getPacked();
//            double v = Math.sin(Math.toRadians(i));
//            double u = Math.cos(Math.toRadians(i));
//            bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float) u * start, (float) 0, (float) v * start).color(injectAlpha(new Color(clr), 0).getPacked());
//            bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float) u * middle, (float) 0, (float) v * middle).color(injectAlpha(new Color(clr), (int) (150 * k)).getPacked());
//        }
//        builtBuffer = bufferBuilder.end();
//        BufferRenderer.drawWithGlobalProgram(builtBuffer);
//        builtBuffer.close();
//        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
//        bufferBuilder = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
//        // In circle
//        /*
//            --
//            __
//         */
//        for (int i = 0; i <= 360; i += 5) {
//            int clr = Color.WHITE.getPacked();
//            double v = Math.sin(Math.toRadians(i));
//            double u = Math.cos(Math.toRadians(i));
//            bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float) u * middle, (float) 0, (float) v * middle).color(injectAlpha(new Color(clr), (int) (150 * k)).getPacked());
//            bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float) u * start, (float) 0, (float) v * start).color(injectAlpha(new Color(clr), 0).getPacked());
//        }
//        builtBuffer = bufferBuilder.end();
//        BufferRenderer.drawWithGlobalProgram(builtBuffer);
//        builtBuffer.close();
//        // circle
//        /*
//            __
//            --
//            __
//         */
//        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
//        bufferBuilder = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
//        for (int i = 0; i <= 360; i += 5) {
//            int clr = Color.WHITE.getPacked();
//            double v = Math.sin(Math.toRadians(i));
//            double u = Math.cos(Math.toRadians(i));
//            bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float) u * middle, (float) 0, (float) v * middle).color(injectAlpha(new Color(clr), (int) (255 * k)).getPacked());
//            bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float) u * (middle - inGlowRadius), (float) 0, (float) v * (middle - inGlowRadius)).color(injectAlpha(new Color(clr), 0).getPacked());
//        }
//        builtBuffer = bufferBuilder.end();
//        BufferRenderer.drawWithGlobalProgram(builtBuffer);
//        builtBuffer.close();
//        RenderSystem.enableCull();
//        RenderSystem.disableDepthTest();
//        RenderSystem.disableBlend();
//        matrices.translate(-x, -y, -z);
//        matrices.pop();
//    }
//
//    public void drawJello(MatrixStack matrix, Entity target, Color color, float delta) {
//        double cs = prevCircleStep + (circleStep - prevCircleStep) * delta;
//        double prevSinAnim = absSinAnimation(cs - 0.45f);
//        double sinAnim = absSinAnimation(cs);
//        double x = target.prevX + (target.getX() - target.prevX) * delta - mc.getEntityRenderDispatcher().camera.getPos().getX();
//        double y = target.prevY + (target.getY() - target.prevY) * delta - mc.getEntityRenderDispatcher().camera.getPos().getY() + prevSinAnim * target.getHeight();
//        double z = target.prevZ + (target.getZ() - target.prevZ) * delta - mc.getEntityRenderDispatcher().camera.getPos().getZ();
//        double nextY = target.prevY + (target.getY() - target.prevY) * delta - mc.getEntityRenderDispatcher().camera.getPos().getY() + sinAnim * target.getHeight();
//
//        matrix.push();
//        RenderSystem.enableBlend();
//        RenderSystem.defaultBlendFunc();
//        RenderSystem.disableCull();
//        RenderSystem.enableDepthTest();
//        Tessellator tessellator = RenderSystem.renderThreadTesselator();
//        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
//        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
//
//        float cos;
//        float sin;
//        for (int i = 0; i <= 30; i++) {
//            cos = (float) (x + Math.cos(i * 6.28 / 30) * ((target.getBoundingBox().maxX - target.getBoundingBox().minX) + (target.getBoundingBox().maxZ - target.getBoundingBox().minZ)) * 0.5f);
//            sin = (float) (z + Math.sin(i * 6.28 / 30) * ((target.getBoundingBox().maxX - target.getBoundingBox().minX) + (target.getBoundingBox().maxZ - target.getBoundingBox().minZ)) * 0.5f);
//            bufferBuilder.vertex(matrix.peek().getPositionMatrix(), cos, (float) nextY, sin).color(color.getPacked());
//            bufferBuilder.vertex(matrix.peek().getPositionMatrix(), cos, (float) y, sin).color(injectAlpha(color, 0).getPacked());
//        }
//
//        var builtBuffer = bufferBuilder.end();
//        BufferRenderer.drawWithGlobalProgram(builtBuffer);
//        builtBuffer.close();
//        RenderSystem.enableCull();
//        RenderSystem.disableDepthTest();
//        RenderSystem.disableBlend();
//        matrix.pop();
//    }
//
//    private double absSinAnimation(double input) {
//        return Math.abs(1 + Math.sin(input)) / 2;
//    }
//
//    public Color injectAlpha(Color color, int alpha) {
//        return new Color(color.r, color.g, color.b, MathHelper.clamp(alpha, 0, 255));
//    }
}
