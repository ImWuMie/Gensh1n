package dev.undefinedteam.gensh1n.gui.overlay;

import dev.undefinedteam.gensh1n.render.Renderer;
import icyllis.arc3d.core.Color;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.graphics.Rect;
import icyllis.modernui.graphics.RectF;

public class ROverlayGui {
    public String name;

    public ROverlayGui(String name) {
        this.name = name;
    }

    protected final RectF box = new RectF(0, 0, 0, 0);

    protected int width, height;

    protected void setSize(float bWidth, float bHeight) {
        this.box.set(this.box.left, this.box.top, this.box.left + bWidth, this.box.top + bHeight);
    }

    protected void setPos(float bX, float bY) {
        this.box.set(bX, bY, bX + this.box.width(), bY + this.box.width());
    }

    public void render(Renderer renderer,float tickDelta) {
    }


    public void tick() {
    }

    public void debugRender(Renderer renderer,float tickDelta) {
        Paint paint = renderer._paint();
        paint.setColor(Color.RED);
        paint.setStroke(true);
        paint.setStrokeWidth(1.5f);
        renderer._renderer().drawRoundRect(this.box, 3.0f,renderer._paint());
    }

    public void setup(int displayWidth, int displayHeight) {
        this.width = displayWidth;
        this.height = displayHeight;
    }
}
