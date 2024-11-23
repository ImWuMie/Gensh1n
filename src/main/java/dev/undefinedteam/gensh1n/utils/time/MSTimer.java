package dev.undefinedteam.gensh1n.utils.time;

public class MSTimer {
    private long previousTime = -1L;

    public boolean check(float milliseconds) {
        return (float) this.getTime() >= milliseconds;
    }

    public boolean check(double milliseconds) {
        return (double) this.getTime() >= milliseconds;
    }

    public long getTime() {
        return this.getCurrentTime() - this.previousTime;
    }

    public void reset() {
        this.previousTime = this.getCurrentTime();
    }

    public long getCurrentTime() {
        return System.currentTimeMillis();
    }
}
