package dev.undefinedteam.gensh1n.gui.overlay;

import dev.undefinedteam.gensh1n.gui.animators.ViewAnimators;
import icyllis.modernui.ModernUI;
import icyllis.modernui.animation.Animator;
import icyllis.modernui.animation.AnimatorListener;
import icyllis.modernui.animation.ObjectAnimator;
import icyllis.modernui.animation.TimeInterpolator;
import icyllis.modernui.app.Activity;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.view.WindowManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class DialogManager {
    public static DialogManager INSTANCE;

    static final Marker MARKER = MarkerManager.getMarker("Dialog");
    private final WindowManager mWindowManager;

    private ViewGroup mLayout;

    private ObjectAnimator showAnimator;
    private boolean show;

    private DialogManager(Activity activity) {
        mWindowManager = activity.getWindowManager();
        INSTANCE = this;
    }

    public static DialogManager get() {
        if (INSTANCE != null) return INSTANCE;

        INSTANCE = new DialogManager(ModernUI.getInstance());
        INSTANCE.reset();
        return INSTANCE;
    }

    public void reset() {
        this.setLayout(null);
    }

    public DialogManager setLayout(ViewGroup viewGroup) {
        this.mLayout = viewGroup;

        if (viewGroup != null) {
            this.showAnimator = ObjectAnimator.ofFloat(viewGroup, ViewAnimators.ALPHA_255,  0,255);

            this.showAnimator.setInterpolator(TimeInterpolator.ACCELERATE_DECELERATE);

            this.showAnimator.addListener(new AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    DialogManager.this.mLayout.setAlpha(0.0f);
                }
            });
        }
        return this;
    }

    public void show() {
        if (show) hidden();

        if (this.mLayout != null && showAnimator != null) {
            show = true;

            var params = new WindowManager.LayoutParams();
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.CENTER;
            params.type = WindowManager.LayoutParams.TYPE_BASE_APPLICATION;

            showAnimator.start();
            mWindowManager.addView(this.mLayout, params);
        }
    }

    public void hidden() {
        if (showAnimator != null && showAnimator.isRunning())
            this.showAnimator.cancel();

        if (show) {
            mWindowManager.removeView(DialogManager.this.mLayout);
            DialogManager.this.mLayout = null;
            show = false;
        }
    }

    public boolean isShow() {
        return this.show;
    }
}
