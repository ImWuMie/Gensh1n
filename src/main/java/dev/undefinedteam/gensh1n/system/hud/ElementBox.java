package dev.undefinedteam.gensh1n.system.hud;

import dev.undefinedteam.gensh1n.utils.Utils;

public class ElementBox {
    private final HudElement element;

    public XAnchor xAnchor = XAnchor.Left;
    public YAnchor yAnchor = YAnchor.Top;

    public double x, y;
    double width, height;

    public ElementBox(HudElement element) {
        this.element = element;
    }

    public void setSize(double width, double height) {
        if (width >= 0) this.width = Math.ceil(width);
        if (height >= 0) this.height = Math.ceil(height);
    }

    public void setPos(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setXAnchor(XAnchor anchor) {
        if (xAnchor != anchor) {
            double renderX = getRenderX();

            switch (anchor) {
                case Left -> x = renderX;
                case Center -> x = renderX + width / 2 - (double) Utils.getWindowWidth() / 2;
                case Right -> x = renderX + width - Utils.getWindowWidth();
            }

            xAnchor = anchor;
        }
    }

    public void setYAnchor(YAnchor anchor) {
        if (yAnchor != anchor) {
            double renderY = getRenderY();

            switch (anchor) {
                case Top -> y = renderY;
                case Center -> y = renderY + height / 2 - (double) Utils.getWindowHeight() / 2;
                case Bottom -> y = renderY + height - Utils.getWindowHeight();
            }

            yAnchor = anchor;
        }
    }

    public void updateAnchors() {
        setXAnchor(getXAnchor(getRenderX()));
        setYAnchor(getYAnchor(getRenderY()));
    }

    public void move(double deltaX, double deltaY) {
        x += deltaX;
        y += deltaY;

        if (element.autoAnchors) updateAnchors();

        int border = 1;

        // Clamp X
        if (xAnchor == XAnchor.Left && x < border) x = border;
        else if (xAnchor == XAnchor.Right && x > border) x = border;

        // Clamp Y
        if (yAnchor == YAnchor.Top && y < border) y = border;
        else if (yAnchor == YAnchor.Bottom && y > border) y = border;
    }

    public XAnchor getXAnchor(double x) {
        double splitLeft = Utils.getWindowWidth() / 3.0;
        double splitRight = splitLeft * 2;

        boolean left = x <= splitLeft;
        boolean right = x + width >= splitRight;

        if ((left && right) || (!left && !right)) return XAnchor.Center;
        return left ? XAnchor.Left : XAnchor.Right;
    }

    public YAnchor getYAnchor(double y) {
        double splitTop = Utils.getWindowHeight() / 3.0;
        double splitBottom = splitTop * 2;

        boolean top = y <= splitTop;
        boolean bottom = y + height >= splitBottom;

        if ((top && bottom) || (!top && !bottom)) return YAnchor.Center;
        return top ? YAnchor.Top : YAnchor.Bottom;
    }

    public double getRenderX() {
        return switch (xAnchor) {
            case Left -> x;
            case Center -> (double) Utils.getWindowWidth() / 2 - width / 2 + x;
            case Right -> Utils.getWindowWidth() - width + x;
        };
    }

    public double getRenderY() {
        return switch (yAnchor) {
            case Top -> y;
            case Center -> (double) Utils.getWindowHeight() / 2 - height / 2 + y;
            case Bottom -> Utils.getWindowHeight() - height + y;
        };
    }

    public double alignX(double selfWidth, double width, Alignment alignment) {
        XAnchor anchor = xAnchor;

        if (alignment == Alignment.Left) anchor = XAnchor.Left;
        else if (alignment == Alignment.Center) anchor = XAnchor.Center;
        else if (alignment == Alignment.Right) anchor = XAnchor.Right;

        return switch (anchor) {
            case Left -> 0;
            case Center -> selfWidth / 2.0 - width / 2.0;
            case Right -> selfWidth - width;
        };
    }
}
