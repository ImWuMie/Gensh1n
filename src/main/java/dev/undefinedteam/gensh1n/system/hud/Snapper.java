package dev.undefinedteam.gensh1n.system.hud;

public class Snapper {
    private final Container container;

    private Element snappedTo;

    private Direction mainDir;
    private int mainPos;

    private boolean secondary;
    private int secondaryPos;

    public Snapper(Container container) {
        this.container = container;
    }

    public void move(Element element, int deltaX, int deltaY) {
        if (container.getSnappingRange() == 0) {
            element.move(deltaX, deltaY);
            return;
        }

        if (snappedTo == null) moveUnsnapped(element, deltaX, deltaY);
        else moveSnapped(element, deltaX, deltaY);
    }

    public void unsnap() {
        snappedTo = null;
    }

    private void moveUnsnapped(Element element, int deltaX, int deltaY) {
        element.move(deltaX, deltaY);

        // Main Right
        if (deltaX > 0) {
            Element closest = null;
            double closestDist = Integer.MAX_VALUE;

            for (Element e : container.getElements()) {
                if (container.shouldNotSnapTo(e)) continue;

                double dist = e.getElementX() - element.getElementX2();
                if (dist > 0 && dist <= container.getSnappingRange() && (closest == null || dist < closestDist) && isNextToHorizontally(element, e)) {
                    closest = e;
                    closestDist = dist;
                }
            }

            if (closest != null) {
                element.setElementPos(closest.getElementX() - element.getElementWidth(), element.getElementY());
                snapMain(closest, Direction.Right);
            }
        }
        // Main Left
        else if (deltaX < 0) {
            Element closest = null;
            double closestDist = Integer.MAX_VALUE;

            for (Element e : container.getElements()) {
                if (container.shouldNotSnapTo(e)) continue;

                double dist = element.getElementX() - e.getElementX2();
                if (dist > 0 && dist <= container.getSnappingRange() && (closest == null || dist < closestDist) && isNextToHorizontally(element, e)) {
                    closest = e;
                    closestDist = dist;
                }
            }

            if (closest != null) {
                element.setElementPos(closest.getElementX2(), element.getElementY());
                snapMain(closest, Direction.Left);
            }
        }
        // Main Top
        else if (deltaY > 0) {
            Element closest = null;
            double closestDist = Integer.MAX_VALUE;

            for (Element e : container.getElements()) {
                if (container.shouldNotSnapTo(e)) continue;

                double dist = e.getElementY() - element.getElementY2();
                if (dist > 0 && dist <= container.getSnappingRange() && (closest == null || dist < closestDist) && isNextToVertically(element, e)) {
                    closest = e;
                    closestDist = dist;
                }
            }

            if (closest != null) {
                element.setElementPos(element.getElementX(), closest.getElementY() - element.getElementHeight());
                snapMain(closest, Direction.Top);
            }
        }
        // Main Bottom
        else if (deltaY < 0) {
            Element closest = null;
            double closestDist = Integer.MAX_VALUE;

            for (Element e : container.getElements()) {
                if (container.shouldNotSnapTo(e)) continue;

                double dist = element.getElementY() - e.getElementY2();
                if (dist > 0 && dist <= container.getSnappingRange() && (closest == null || dist < closestDist) && isNextToVertically(element, e)) {
                    closest = e;
                    closestDist = dist;
                }
            }

            if (closest != null) {
                element.setElementPos(element.getElementX(), closest.getElementY2());
                snapMain(closest, Direction.Bottom);
            }
        }
    }

    private void moveSnapped(Element element, int deltaX, int deltaY) {
        switch (mainDir) {
            case Right, Left -> {
                if (secondary) secondaryPos += deltaY;
                else element.move(0, deltaY);
                mainPos += deltaX;

                if (!isNextToHorizontally(element, snappedTo)) unsnap();
                else if (!secondary) {
                    // Secondary Bottom
                    if (deltaY > 0) {
                        double dist = snappedTo.getElementY2() - element.getElementY2();
                        if (dist > 0 && dist < container.getSnappingRange()) {
                            element.setElementPos(element.getElementX(), snappedTo.getElementY2() - element.getElementHeight());
                            snapSecondary();
                        }
                    }
                    // Secondary Top
                    else if (deltaY < 0) {
                        double dist = snappedTo.getElementY() - element.getElementY();
                        if (dist < 0 && dist > -container.getSnappingRange()) {
                            element.setElementPos(element.getElementX(), snappedTo.getElementY());
                            snapSecondary();
                        }
                    }
                }
            }
            case Top, Bottom -> {
                if (secondary) secondaryPos += deltaX;
                else element.move(deltaX, 0);
                mainPos += deltaY;

                if (!isNextToVertically(element, snappedTo)) unsnap();
                else if (!secondary) {
                    // Secondary Right
                    if (deltaX > 0) {
                        double dist = snappedTo.getElementX2() - element.getElementX2();
                        if (dist > 0 && dist < container.getSnappingRange()) {
                            element.setElementPos(snappedTo.getElementX2() - element.getElementWidth(), element.getElementY());
                            snapSecondary();
                        }
                    }
                    // Secondary Left
                    else if (deltaX < 0) {
                        double dist = element.getElementX() - snappedTo.getElementX();
                        if (dist > 0 && dist < container.getSnappingRange()) {
                            element.setElementPos(snappedTo.getElementX(), element.getElementY());
                            snapSecondary();
                        }
                    }
                }
            }
        }

        if (Math.abs(mainPos) > container.getSnappingRange() * 5) unsnap();
        else if (Math.abs(secondaryPos) > container.getSnappingRange() * 5) secondary = false;
    }

    private void snapMain(Element element, Direction dir) {
        snappedTo = element;
        mainDir = dir;
        mainPos = 0;

        secondary = false;
    }

    private void snapSecondary() {
        secondary = true;
        secondaryPos = 0;
    }

    private boolean isBetween(double value, double min, double max) {
        return value > min && value < max;
    }

    private boolean isNextToHorizontally(Element e1, Element e2) {
        double y1 = e1.getElementY();
        double h1 = e1.getElementHeight();
        double y2 = e2.getElementY();
        double h2 = e2.getElementHeight();
        return isBetween(y1, y2, y2 + h2) || isBetween(y1 + h1, y2, y2 + h2) || isBetween(y2, y1, y1 + h1) || isBetween(y2 + h2, y1, y1 + h1);
    }

    private boolean isNextToVertically(Element e1, Element e2) {
        double x1 = e1.getElementX();
        double w1 = e1.getElementWidth();
        double x2 = e2.getElementX();
        double w2 = e2.getElementWidth();
        return isBetween(x1, x2, x2 + w2) || isBetween(x1 + w1, x2, x2 + w2) || isBetween(x2, x1, x1 + w1) || isBetween(x2 + w2, x1, x1 + w1);
    }

    public interface Container {
        Iterable<Element> getElements();

        boolean shouldNotSnapTo(Element element);

        int getSnappingRange();
    }

    public interface Element {
        double getElementX();

        double getElementY();

        default double getElementX2() {
            return getElementX() + getElementWidth();
        }

        default double getElementY2() {
            return getElementY() + getElementHeight();
        }

        double getElementWidth();

        double getElementHeight();

        void setElementPos(double x, double y);

        void move(double deltaX, double deltaY);
    }

    private enum Direction {
        Right,
        Left,
        Top,
        Bottom
    }
}
