package dev.undefinedteam.gensh1n.utils.time;

import org.apache.commons.lang3.RandomUtils;

public class NSTimer {
    private long lastMS;

    private long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }

    public boolean check(float milliseconds) {
        return getTime() >= milliseconds;
    }

    public boolean hasTimeElapsed(long time, boolean reset) {
        if (System.currentTimeMillis() - lastMS > time) {
            if (reset) reset();
            return true;
        }

        return false;
    }

    public boolean hasReached(double milliseconds) {
        return (double) (this.getCurrentMS() - this.lastMS) >= milliseconds;
    }

    public static long randomDelay(final int minDelay, final int maxDelay) {
        return RandomUtils.nextInt(minDelay, maxDelay);
    }

    public void reset() {
        this.lastMS = this.getCurrentMS();
    }

    public boolean delay(float milliSec) {
        return (float) (this.getTime() - this.lastMS) >= milliSec;
    }

    public long getTime() {
        return System.nanoTime() / 1000000L;
    }
}
