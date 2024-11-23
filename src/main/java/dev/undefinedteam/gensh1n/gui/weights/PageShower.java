package dev.undefinedteam.gensh1n.gui.weights;

import icyllis.modernui.core.Context;
import icyllis.modernui.graphics.Canvas;
import icyllis.modernui.graphics.MathUtil;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.graphics.Rect;
import icyllis.modernui.graphics.drawable.Drawable;
import icyllis.modernui.view.View;
import icyllis.modernui.widget.LinearLayout;

import java.awt.*;
import java.util.function.Consumer;

public class PageShower extends LinearLayout {
    private int mTotalPages;
    private int mCurrentPage,mLastPage;

    private int mOffset;

    private int pointAlpha = 200;

    private boolean sub = false, plus = true;
    private boolean changed = true;

    public PageShower(Context context, int offsetDp) {
        super(context);
        this.mOffset = offsetDp;

        this.setBackground(new Drawable() {
            @Override
            public void draw(Canvas canvas) {
                Paint paint = Paint.obtain();

                Rect bounds = getBounds();
                float xCoord = bounds.x() + 4.0f;
                boolean invalidate = pointAlpha != 200;

                pointAlpha = MathUtil.clamp(pointAlpha, 100, 200);

                if (changed) {
                    if (sub) {
                        if (pointAlpha >= 100) {
                            pointAlpha -= 5;
                        } else {
                            sub = false;
                            plus = true;
                            mLastPage = mCurrentPage;
                        }
                    } else if (plus) {
                        if (pointAlpha <= 200) {
                            pointAlpha += 5;
                            plus = false;
                        }
                    }
                }

                for (int i = 0; i < mTotalPages; i++) {
                    if (i == mLastPage && mLastPage != mCurrentPage && changed) {
                        paint.setColor(new Color(255, 255, 255, pointAlpha).getRGB());
                    } else if (i == mCurrentPage && mLastPage == mCurrentPage && changed) {
                        paint.setColor(new Color(255, 255, 255, pointAlpha).getRGB());
                    } else if (i == mCurrentPage && mLastPage == mCurrentPage) {
                        paint.setColor(new Color(255, 255, 255, 100).getRGB());
                    } else paint.setColor(new Color(255, 255, 255, 100).getRGB());

                    paint.setRGBA(255,255,255,i == mCurrentPage ? 200 : 100);

                    canvas.drawCircle(xCoord, bounds.centerY(), 3.0f, paint);
                    xCoord += mOffset;
                }

                paint.recycle();

                if (invalidate) {
                    invalidateSelf();
                } else {
                    changed = false;
                }
            }
        });
    }

    public int getPageCount() {
        return mTotalPages;
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public void setOffset(int offset) {
        this.mOffset = offset;
        invalidate();
    }

    public void setPageCount(int count) {
        this.mTotalPages = count;
        invalidate();
    }

    private Consumer<Integer> mPageChangedListener;

    public void setCurrent(int page,boolean call) {
        this.mCurrentPage = MathUtil.clamp(page, 0, Math.max(mTotalPages - 1, 0));
        if (call && mPageChangedListener != null) mPageChangedListener.accept(this.mCurrentPage);
        this.changed = true;
        this.sub = true;
        invalidate();
    }

    public void setCurrent(int page) {
        setCurrent(page,true);
    }

    public void setPageChangedListener(Consumer<Integer> action) {
        this.mPageChangedListener = action;
    }
}
