package dev.undefinedteam.gensh1n.gui.renders;

import dev.undefinedteam.gensh1n.utils.RandomUtils;
import dev.undefinedteam.gensh1n.utils.Utils;
import icyllis.arc3d.core.Point;
import icyllis.modernui.graphics.Canvas;
import icyllis.modernui.graphics.Paint;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ParticlesRender extends DrawRender {
    private final List<Particles> particles = new CopyOnWriteArrayList<>();
    private boolean first = true;

    @Override
    public void mouseMoved(float mouseX, float mouseY) {
        particles.forEach(p -> p.mouseMove(mouseX, mouseY));
    }

    @Override
    public void update() {
        particles.forEach(p -> p.update(this.bounds.x(), this.bounds.y(), this.bounds.width(), this.bounds.height()));
    }

    @Override
    public void render(Canvas canvas) {
        if (first) {
            for (int i = 0; i < 100; i++) {
                Particles p = new Particles();
                p.x = RandomUtils.nextFloat(this.bounds.x(), this.bounds.width());
                p.y = RandomUtils.nextFloat(this.bounds.y(), this.bounds.height());
                particles.add(p);
            }
            first = false;
        }

        update();
        particles.forEach(p -> p.render(canvas));
    }

    public static class Particles {
        public float rotation;
        public double velX, velY;
        public double x, y;

        public double speed, s0 = 0.01, next;
        private static final float _radius = 5.0f;
        private int alpha = 80;

        public void mouseMove(float mouseX, float mouseY) {
            float distance = Point.distanceTo(mouseX, mouseY, (float) x, (float) y);
            if (distance < 50) {
                this.next = ((50 - distance) + RandomUtils.nextFloat(0.1, 0.7));
                this.rotation = (float) Math.toDegrees(Math.atan2(mouseY - y, mouseX - x));
                //this.rotation += 20;
                this.s0 = 0.0003;
            } else {
                this.next = (RandomUtils.nextFloat(0.1, 0.7));
                this.rotation = (float) Math.toDegrees(Math.atan2(1, 1));
                this.s0 = 0.01;
            }
        }

        public void update(float minX, float minY, float maxX, float maxY) {
            this.speed = Utils.smooth_s(this.speed, this.next, s0);

            double rotationRadians = Math.toRadians(rotation % 360);

            this.speed = MathHelper.clamp(this.speed,-10,10);

            this.velX = this.speed * (float) Math.cos(rotationRadians);
            this.velY = this.speed * (float) Math.sin(rotationRadians);

            this.velX *= 0.98f;
            this.velY *= 0.98f;
            this.x += this.velX;
            this.y += this.velY;

            if (this.x + _radius / 2.0f > maxX - 1 || this.y + _radius / 2.0f > maxY - 1 || this.x - _radius / 2.0f < minX + 1 || this.y - _radius / 2.0f < minY + 1) {
                if (alpha != 0) {
                    alpha--;
                } else {
                    this.x = RandomUtils.nextFloat(minX, maxX);
                    this.y = RandomUtils.nextFloat(minY, maxY);
                }
            } else if (alpha != 80) {
                alpha++;
            }
        }

        public void render(Canvas canvas) {
            Paint paint = Paint.obtain();
            paint.setColor(new Color(255, 255, 255, alpha).getRGB());
            canvas.drawCircle((float) x, (float) y, _radius, paint);
            paint.recycle();
        }
    }
}
