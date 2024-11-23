package dev.undefinedteam.gensh1n.utils.time;

public class TickTimer {
    int tick = 0;

    public void update() {
        tick++;
    }

    public void reset() {
        tick = 0;
    }

    public boolean hasTimePassed(int ticks) {
        return tick >= ticks;
    }
}
