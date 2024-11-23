package dev.undefinedteam.gensh1n.gui.overlay;

import dev.undefinedteam.gensh1n.render.Renderer;
import dev.undefinedteam.gensh1n.system.ClientConfig;
import icyllis.modernui.animation.AnimationUtils;
import icyllis.modernui.annotation.NonNull;
import icyllis.modernui.audio.FFT;
import icyllis.modernui.core.Core;
import icyllis.modernui.graphics.Canvas;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.graphics.Rect;
import icyllis.modernui.graphics.drawable.Drawable;

import java.awt.*;




public class MusicSpectrum extends Drawable {
    private static final int AMPLITUDE_LENGTH = 60;

    private final float[] mAmplitudes = new float[AMPLITUDE_LENGTH];

    private volatile int mActualAmplitudeLength;

    private long mLastAnimationTime;

    private final Runnable mAnimationRunnable = this::invalidateSelf;

    private final int mBandWidth;
    private final int mBandGap;
    private final int mBandHeight;

    private final int color;

    public MusicSpectrum(int bandWidth, int bandGap, int bandHeight, Color color) {
        mBandWidth = bandWidth;
        mBandGap = bandGap;
        mBandHeight = bandHeight;
        this.color = color.getRGB();
    }

    @Override
    public void draw(Canvas canvas) {

        var b = getBounds();
        //int contentCenter = (mBandWidth * AMPLITUDE_LENGTH + mBandGap * (AMPLITUDE_LENGTH - 1)) / 2;

        float x = b.x();
        float bottom = b.bottom - mBandWidth;

        var paint = Paint.obtain();

        long time = AnimationUtils.currentAnimationTimeMillis();
        long delta = time - mLastAnimationTime;
        mLastAnimationTime = time;

        final float[] amplitudes = mAmplitudes;
        final int len = mActualAmplitudeLength;

        boolean invalidate = false;
        for (int i = 0; i < len; i++) {
            if (amplitudes[i] > 0) {
                invalidate = true;
                break;
            }
        }

        for (int i = 0; i < len; i++) {
            // 2.5e-5f * BPM
            amplitudes[i] = Math.max(0,
                amplitudes[i] - delta * 2.5e-5f * 180.0f * amplitudes[i]
            );
        }

        for (int i = 0; i < AMPLITUDE_LENGTH; i++) {
            paint.setColor(ClientConfig.get().spectrumColor.get().getPacked());
            canvas.drawRect(x, bottom - amplitudes[i] * mBandHeight, x + mBandWidth, bottom, paint);
            x += mBandWidth + mBandGap;
        }

        paint.recycle();

        if (invalidate) {
            invalidateSelf();
        }
    }

    public void drawWithOut(Renderer renderer,Rect b) {
        //int contentCenter = (mBandWidth * AMPLITUDE_LENGTH + mBandGap * (AMPLITUDE_LENGTH - 1)) / 2;
        float x = b.x();
        float bottom = b.bottom - 1.0f;

        var paint = renderer._paint();

        long time = AnimationUtils.currentAnimationTimeMillis();
        long delta = time - mLastAnimationTime;
        mLastAnimationTime = time;

        final float[] amplitudes = mAmplitudes;
        final int len = mActualAmplitudeLength;

        for (int i = 0; i < len; i++) {
            // 2.5e-5f * BPM
            amplitudes[i] = Math.max(0,
                amplitudes[i] - delta * 2.5e-5f * 180.0f * amplitudes[i]
            );
        }

        for (int i = 0; i < AMPLITUDE_LENGTH; i++) {
            paint.setColor(ClientConfig.get().spectrumColor.get().getPacked());
            renderer._renderer().drawRect(x, bottom - amplitudes[i] * mBandHeight, x + mBandWidth, bottom, paint);
            x += mBandWidth + mBandGap;
        }
    }

    public void updateAmplitudes(FFT fft) {
        final float[] amplitudes = mAmplitudes;
        final int len = Math.min(fft.getAverageSize() - 5, AMPLITUDE_LENGTH);

        for (int i = 0; i < len; i++) {
            float value = fft.getAverage((i % len) + 5) / fft.getBandSize();
            amplitudes[i] = Math.max(amplitudes[i], value);
        }
        mActualAmplitudeLength = len;

        final long now = Core.timeMillis();
        scheduleSelf(mAnimationRunnable, now);
    }

    @Override
    public int getIntrinsicWidth() {
        return mBandWidth * AMPLITUDE_LENGTH + mBandGap * (AMPLITUDE_LENGTH - 1);
    }

    @Override
    public int getIntrinsicHeight() {
        return mBandHeight;
    }

    @Override
    public boolean getPadding(@NonNull Rect padding) {
        int pad = mBandWidth;
        padding.set(pad, pad, pad, pad);
        return true;
    }
}
