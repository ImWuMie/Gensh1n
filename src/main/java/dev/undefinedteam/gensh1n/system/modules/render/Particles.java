package dev.undefinedteam.gensh1n.system.modules.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.events.render.Render3DEvent;
import dev.undefinedteam.gensh1n.settings.ColorSetting;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.system.modules.combat.KillAura;
import dev.undefinedteam.gensh1n.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author KuChaZi
 * @Date 2024/10/26 18:24
 * @ClassName: Particles
 */
@StringEncryption
@ControlFlowObfuscation
public class Particles extends Module {

    public Particles() {
        super(Categories.Render, "particles", "Make your world more beautiful");
    }

    private final SettingGroup sgDefault = settings.getDefaultGroup();
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    public Setting<Boolean> FireFlies = bool(sgDefault, "FireFlies", true);
    public Setting<Integer> ffcount = intN(sgDefault, "FFCount", 30, 20, 200);
    public Setting<Double> ffsize = doubleN(sgDefault, "FFSize", 1.0, 0.1, 2.0);

    private final Setting<Mode> mode = choice(sgGeneral, "Mode", Mode.Off);

    public Setting<Integer> count = intN(sgDefault, "Count", 100, 20, 800);

    public Setting<Double> size = doubleN(sgDefault, "Size", 1.0, 0.1, 6.0);

    private final Setting<ColorMode> lmode = choice(sgGeneral, "ColorMode", ColorMode.Sync);


    private final Setting<Physics> physics = choice(sgGeneral, "Physics", Physics.Fly);

    private final Setting<SettingColor> colorSetting = color(sgRender, "custom-color", COLOR, new SettingColor(255, 255, 255, 100), () -> lmode.get().equals(ColorMode.Custom));


    public enum ColorMode {
        Custom, Sync
    }

    public enum Mode {
        Off, SnowFlake, Stars, Hearts, Dollars, Bloom;
    }

    public enum Physics {
        Drop, Fly
    }

    private final ArrayList<ParticleBase> fireFlies = new ArrayList<>();
    private final ArrayList<ParticleBase> particles = new ArrayList<>();

    public static final Identifier star = new Identifier("textures/star.png");
    public static final Identifier heart = new Identifier("textures/heart.png");
    public static final Identifier dollar = new Identifier("textures/dollar.png");
    public static final Identifier snowflake = new Identifier("textures/snowflake.png");
    public static final Identifier capture = new Identifier("textures/capture.png");
    public static final Identifier firefly = new Identifier("textures/firefly.png");
    public static final Identifier arrow = new Identifier("textures/triangle.png");
    public static final Identifier bubble = new Identifier("textures/hitbubble.png");
    public static final Identifier default_circle = new Identifier("textures/circle.png");
    public static final Identifier CONTAINER_BACKGROUND = new Identifier("textures/container.png");

    @EventHandler
    public void onUpdate(TickEvent.Pre event) {
        fireFlies.removeIf(ParticleBase::tick);
        particles.removeIf(ParticleBase::tick);

        for (int i = fireFlies.size(); i < ffcount.get(); i++) {
            if (FireFlies.get())
                fireFlies.add(new FireFly(
                    (float) (mc.player.getX() + random(-25f, 25f)),
                    (float) (mc.player.getY() + random(2f, 15f)),
                    (float) (mc.player.getZ() + random(-25f, 25f)),
                    random(-0.2f, 0.2f),
                    random(-0.1f, 0.1f),
                    random(-0.2f, 0.2f)));
        }

        for (int j = particles.size(); j < count.get(); j++) {
            boolean drop = physics.get() == Physics.Drop;
            if (mode.get() != Mode.Off)
                particles.add(new ParticleBase(
                    (float) (mc.player.getX() + random(-48f, 48f)),
                    (float) (mc.player.getY() + random(2, 48f)),
                    (float) (mc.player.getZ() + random(-48f, 48f)),
                    drop ? 0 : random(-0.4f, 0.4f),
                    drop ? random(-0.2f, -0.05f) : random(-0.1f, 0.1f),
                    drop ? 0 : random(-0.4f, 0.4f)));
        }
    }

    public static float random(float min, float max) {
        return (float) (Math.random() * (max - min) + min);
    }

    @EventHandler
    public void onRender3D(Render3DEvent event) {
        MatrixStack stack = new MatrixStack();
        if (FireFlies.get()) {
            stack.push();
            RenderSystem.setShaderTexture(0, firefly);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(false);
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            fireFlies.forEach(p -> p.render(bufferBuilder));
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
            RenderSystem.depthMask(true);
            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();
            stack.pop();
        }

        if (mode.get() != Mode.Off) {
            stack.push();
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(false);
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            particles.forEach(p -> p.render(bufferBuilder));
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
            RenderSystem.depthMask(true);
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();
            stack.pop();
        }
    }

    public static Color rainbow(int speed, int index, float saturation, float brightness, float opacity) {
        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
        float hue = angle / 360f;
        Color color = new Color(Color.HSBtoRGB(hue, saturation, brightness));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, (int) (opacity * 255))));
    }

    public static class Trail {
        private final Vec3d from;
        private final Vec3d to;
        private final Color color;
        private int ticks, prevTicks;

        public Trail(Vec3d from, Vec3d to, Color color) {
            this.from = from;
            this.to = to;
            this.ticks = 10;
            this.color = color;
        }

        public Vec3d interpolate(float pt) {
            double x = from.x + ((to.x - from.x) * pt) - Client.mc.getEntityRenderDispatcher().camera.getPos().getX();
            double y = from.y + ((to.y - from.y) * pt) - Client.mc.getEntityRenderDispatcher().camera.getPos().getY();
            double z = from.z + ((to.z - from.z) * pt) - Client.mc.getEntityRenderDispatcher().camera.getPos().getZ();
            return new Vec3d(x, y, z);
        }

        public double animation(float pt) {
            return (this.prevTicks + (this.ticks - this.prevTicks) * pt) / 10.;
        }

        public boolean update() {
            this.prevTicks = this.ticks;
            return this.ticks-- <= 0;
        }

        public Color color() {
            return color;
        }
    }

    public class FireFly extends ParticleBase {
        private final List<Trail> trails = new ArrayList<>();


        public FireFly(float posX, float posY, float posZ, float motionX, float motionY, float motionZ) {
            super(posX, posY, posZ, motionX, motionY, motionZ);
        }

        @Override
        public boolean tick() {

            if (mc.player.squaredDistanceTo(posX, posY, posZ) > 100) age -= 4;
            else if (!mc.world.getBlockState(new BlockPos((int) posX, (int) posY, (int) posZ)).isAir()) age -= 8;
            else age--;

            if (age < 0)
                return true;

            trails.removeIf(Trail::update);

            prevposX = posX;
            prevposY = posY;
            prevposZ = posZ;

            posX += motionX;
            posY += motionY;
            posZ += motionZ;

            Color color1 = rainbow(6, (age * 10), 1f, 1, 1);

            // 颜色Setting有点搞不明白所以先固定一个
            trails.add(new Trail(new Vec3d(prevposX, prevposY, prevposZ), new Vec3d(posX, posY, posZ), lmode.get() == ColorMode.Sync ? color1 : colorSetting.get().awt()));

            motionX *= 0.99f;
            motionY *= 0.99f;
            motionZ *= 0.99f;

            return false;
        }

        @Override
        public void render(BufferBuilder bufferBuilder) {
            RenderSystem.setShaderTexture(0, firefly);
            if (!trails.isEmpty()) {
                Camera camera = mc.gameRenderer.getCamera();
                for (Trail ctx : trails) {
                    Vec3d pos = ctx.interpolate(1f);
                    MatrixStack matrices = new MatrixStack();
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));
                    matrices.translate(pos.x, pos.y, pos.z);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
                    Matrix4f matrix = matrices.peek().getPositionMatrix();

                    bufferBuilder.vertex(matrix, 0, -ffsize.get().intValue(), 0).texture(0f, 1f).color(injectAlpha(ctx.color(), (int) (255 * ((float) age / (float) maxAge) * ctx.animation(mc.getTickDelta()))).getRGB()).next();
                    bufferBuilder.vertex(matrix, -ffsize.get().intValue(), -ffsize.get().intValue(), 0).texture(1f, 1f).color(injectAlpha(ctx.color(), (int) (255 * ((float) age / (float) maxAge) * ctx.animation(mc.getTickDelta()))).getRGB()).next();
                    bufferBuilder.vertex(matrix, -ffsize.get().intValue(), 0, 0).texture(1f, 0).color(injectAlpha(ctx.color(), (int) (255 * ((float) age / (float) maxAge) * ctx.animation(mc.getTickDelta()))).getRGB()).next();
                    bufferBuilder.vertex(matrix, 0, 0, 0).texture(0, 0).color(injectAlpha(ctx.color(), (int) (255 * ((float) age / (float) maxAge) * ctx.animation(mc.getTickDelta()))).getRGB()).next();
                }
            }
        }
    }

    public static Color injectAlpha(final Color color, final int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), MathHelper.clamp(alpha, 0, 255));
    }

    public class ParticleBase {

        protected float prevposX, prevposY, prevposZ, posX, posY, posZ, motionX, motionY, motionZ;
        protected int age, maxAge;

        public ParticleBase(float posX, float posY, float posZ, float motionX, float motionY, float motionZ) {
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            prevposX = posX;
            prevposY = posY;
            prevposZ = posZ;
            this.motionX = motionX;
            this.motionY = motionY;
            this.motionZ = motionZ;
            age = (int) random(100, 300);
            maxAge = age;
        }

        public boolean tick() {
            if (mc.player.squaredDistanceTo(posX, posY, posZ) > 4096) age -= 8;
            else age--;

            if (age < 0)
                return true;

            prevposX = posX;
            prevposY = posY;
            prevposZ = posZ;

            posX += motionX;
            posY += motionY;
            posZ += motionZ;

            motionX *= 0.9f;
            if (physics.get() == Physics.Fly)
                motionY *= 0.9f;
            motionZ *= 0.9f;

            motionY -= 0.001f;

            return false;
        }

        public void render(BufferBuilder bufferBuilder) {
            switch (mode.get()) {
                case Bloom -> RenderSystem.setShaderTexture(0, firefly);
                case SnowFlake -> RenderSystem.setShaderTexture(0, snowflake);
                case Dollars -> RenderSystem.setShaderTexture(0, dollar);
                case Hearts -> RenderSystem.setShaderTexture(0, heart);
                case Stars -> RenderSystem.setShaderTexture(0, star);
            }
            Color color2 = rainbow(6, (age * 10), 1f, 1, 1);
            Camera camera = mc.gameRenderer.getCamera();

            // 颜色Setting有点搞不明白所以先固定一个
            Color color1 = lmode.get() == ColorMode.Sync ? color2 : colorSetting.get().awt();

            Vec3d pos = interpolatePos(prevposX, prevposY, prevposZ, posX, posY, posZ);

            MatrixStack matrices = new MatrixStack();
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));
            matrices.translate(pos.x, pos.y, pos.z);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));

            Matrix4f matrix1 = matrices.peek().getPositionMatrix();

            bufferBuilder.vertex(matrix1, 0, -size.get().intValue(), 0).texture(0f, 1f).color(injectAlpha(color1, (int) (255 * ((float) age / (float) maxAge))).getRGB()).next();
            bufferBuilder.vertex(matrix1, -size.get().intValue(), -size.get().intValue(), 0).texture(1f, 1f).color(injectAlpha(color1, (int) (255 * ((float) age / (float) maxAge))).getRGB()).next();
            bufferBuilder.vertex(matrix1, -size.get().intValue(), 0, 0).texture(1f, 0).color(injectAlpha(color1, (int) (255 * ((float) age / (float) maxAge))).getRGB()).next();
            bufferBuilder.vertex(matrix1, 0, 0, 0).texture(0, 0).color(injectAlpha(color1, (int) (255 * ((float) age / (float) maxAge))).getRGB()).next();
        }
    }

    public static Vec3d interpolatePos(float prevposX, float prevposY, float prevposZ, float posX, float posY, float posZ) {
        double x = prevposX + ((posX - prevposX) * Client.mc.getTickDelta()) - Client.mc.getEntityRenderDispatcher().camera.getPos().getX();
        double y = prevposY + ((posY - prevposY) * Client.mc.getTickDelta()) - Client.mc.getEntityRenderDispatcher().camera.getPos().getY();
        double z = prevposZ + ((posZ - prevposZ) * Client.mc.getTickDelta()) - Client.mc.getEntityRenderDispatcher().camera.getPos().getZ();
        return new Vec3d(x, y, z);
    }
}
