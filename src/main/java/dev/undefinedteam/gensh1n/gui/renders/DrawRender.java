package dev.undefinedteam.gensh1n.gui.renders;

import icyllis.modernui.core.Context;
import icyllis.modernui.graphics.Canvas;
import icyllis.modernui.graphics.Rect;
import icyllis.modernui.graphics.drawable.Drawable;
import icyllis.modernui.view.ViewGroup;

import java.util.function.Supplier;

public class DrawRender {
    public Rect bounds;
    public Context context;

    public void mouseMoved(float mouseX, float mouseY) {
    }

    public void mouseClicked(float mouseX, float mouseY, int button) {
    }

    public void update() {}

    public void render(Canvas canvas) {
    }

    public Drawable getDrawable(Supplier<Float> mouseX, Supplier<Float> mouseY) {
        return new Drawable() {
            @Override
            public void draw(Canvas canvas) {
                DrawRender.this.mouseMoved(mouseX.get(),mouseY.get());
                DrawRender.this.bounds = getBounds();
                DrawRender.this.render(canvas);
                invalidateSelf();
            }
        };
    }
}
