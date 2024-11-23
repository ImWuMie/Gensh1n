package dev.undefinedteam.gclient;

public class Timer {
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

    public long getMs(long time) {
        return time / 1000000L;
    }

    public long getPassedTimeMs() {
        return getMs(System.nanoTime() - previousTime);
    }
}
