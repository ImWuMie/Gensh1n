package dev.undefinedteam.gensh1n.system.hud.gui;

import dev.undefinedteam.gensh1n.gui.animators.ViewAnimators;
import dev.undefinedteam.gensh1n.gui.builders.LayoutBuilder;
import dev.undefinedteam.gensh1n.gui.builders.ViewBuilder;
import dev.undefinedteam.gensh1n.gui.frags.GMainGui;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.hud.ElementInfo;
import dev.undefinedteam.gensh1n.system.hud.HudElement;
import dev.undefinedteam.gensh1n.system.hud.Huds;
import icyllis.modernui.R;
import icyllis.modernui.animation.Animator;
import icyllis.modernui.animation.AnimatorListener;
import icyllis.modernui.animation.ObjectAnimator;
import icyllis.modernui.animation.TimeInterpolator;
import icyllis.modernui.core.Context;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.graphics.drawable.ShapeDrawable;
import dev.undefinedteam.modernui.mc.MuiScreen;
import dev.undefinedteam.modernui.mc.ScreenCallback;
import dev.undefinedteam.modernui.mc.UIManager;
import dev.undefinedteam.modernui.mc.ui.ThemeControl;
import icyllis.modernui.text.TextUtils;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.view.*;
import icyllis.modernui.widget.*;

import java.awt.Color;
import java.util.Collection;

import static dev.undefinedteam.gensh1n.Client.mc;
import static dev.undefinedteam.gensh1n.gui.frags.GMainGui.*;
import static icyllis.modernui.view.View.TEXT_ALIGNMENT_VIEW_START;

public class HudEditorFragment extends Fragment implements ScreenCallback {
    public static final int BACKGROUND_COLOR = new Color(80, 80, 80, 20).getRGB();
    public static final int ADD_BUTTON_COLOR = new Color(80, 80, 80, 70).getRGB();
    public final Huds HUD = Huds.get();

    private static HudEditorFragment sInstance;

    public boolean addingElement;

    public static HudEditorFragment get() {
        if (sInstance == null) sInstance = new HudEditorFragment();
        return sInstance;
    }

    public static boolean isOpen() {
        return mc.currentScreen instanceof MuiScreen screen && screen.getFragment() instanceof HudEditorFragment;
    }

    private LinearLayout mBase;
    public HudElement current;

    public HudEditorFragment setCurrentElement(HudElement e) {
        if (!isOpen()) {
            UIManager.getInstance().open(get());
        }
        if (this.mBase != null) {
            this.mBase.postDelayed(() -> {
                this.current = e;
                if (current != null) {
                    setting(e);
                }
            }, 100);
        }
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, DataSet savedInstanceState) {
        var builder = LayoutBuilder.newLinerBuilder(requireContext());
        builder.gravity(Gravity.CENTER);
        this.mBase = builder.layout();

        mainLayout();

        int dp5 = builder.dp(5);
        builder.params().margin(dp5, dp5, dp5, dp5).v_match_parent().h_match_parent();
        return builder.build();
    }

    public void mainLayout() {
        var base = LayoutBuilder.newLinerBuilder(requireContext());
        this.mBase.removeAllViews();
        base.vOrientation().gravity(Gravity.CENTER);
        current = null;
        var button = ViewBuilder.wrapLinear(new Button(requireContext()));

        {
            button.view().setEnabled(HUD.active);
            button.view().setAlpha((HUD.active ? 255 : 80) / 255.0f);
            button.view().setText("+");
            button.view().setTextStyle(Paint.BOLD);
            button.view().setTextColor(GREEN);

            int dp5 = base.dp(5);
            {
                ShapeDrawable drawable = new ShapeDrawable();
                drawable.setShape(ShapeDrawable.RECTANGLE);
                drawable.setColor(ADD_BUTTON_COLOR);
                drawable.setStroke(base.dp(1), EDGE_SIDES_COLOR);
                drawable.setPadding(dp5, dp5, dp5, dp5);
                drawable.setCornerRadius(base.dp(5));
                button.bg(drawable);
            }

            button.view().setOnClickListener((__) -> {
                addingElement = true;
                add(HUD.types());
            });
            button.params().width(base.dp(100)).height(base.dp(40));

            base.add(button.build());
        }

        {
            var status = LayoutBuilder.newLinerBuilder(requireContext());

            status.hOrientation().gravity(Gravity.CENTER);

            var title = ViewBuilder.wrapLinear(new TextView(requireContext()));
            var switchButton = ViewBuilder.wrapLinear(new SwitchButton(requireContext()));

            title.view().setText("HUD");
            title.view().setTextColor(GMainGui.FONT_COLOR);
            title.view().setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
            title.view().setTextSize(14);
            title.params().margin(0, 0, base.dp(7), 0);

            switchButton.view().setChecked(HUD.active);
            switchButton.view().setId(R.id.button1);
            switchButton.view().setCheckedColor(ThemeControl.THEME_COLOR);
            switchButton.view().setOnCheckedChangeListener((__, checked) -> {
                HUD.active = checked;

                button.view().setEnabled(HUD.active);
                button.view().setAlpha((HUD.active ? 255 : 80) / 255.0f);
            });

            switchButton.params().gravity(Gravity.CENTER_VERTICAL).width(base.dp(30)).height(base.dp(15));
            status.add(title.build()).add(switchButton.build());
            base.add(status.build());
        }

        base.params().width(base.dp(150)).v_wrap_content();
        this.mBase.addView(base.build());
    }

    public void add(Collection<ElementInfo> infos) {
        var base = LayoutBuilder.newLinerBuilder(requireContext());
        this.mBase.removeAllViews();
        base.vOrientation().gravity(Gravity.CENTER);

        {
            var title = ViewBuilder.wrapLinear(new TextView(requireContext()));
            title.view().setText("Add an element.");
            title.params()
                .margin(0, base.dp(2), 0, base.dp(3))
                .h_match_parent().v_wrap_content();

            title.view().setTextColor(FONT_COLOR);
            title.view().setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            base.add(title.build());
        }

        {
            var scroll = ViewBuilder.wrapLinear(new ScrollView(requireContext()));
            int maxHeight = base.dp(400);
            int elementHeight = base.dp(40);

            var elements = LayoutBuilder.newLinerBuilder(requireContext());
            elements.vOrientation().gravity(Gravity.CENTER);
            for (ElementInfo info : infos) {
                var src = LayoutBuilder.newLinerBuilder(requireContext());
                src.hOrientation().gravity(Gravity.START);

                var widget = LayoutBuilder.newLinerBuilder(requireContext());
                widget
                    .vOrientation()
                    .vGravity(Gravity.CENTER_VERTICAL);

                var title = ViewBuilder.wrapLinear(new TextView(requireContext()));
                title.view().setText(info.title);
                title.view().setTextColor(FONT_COLOR);
                title.view().setTextSize(14);
                title.params()
                    .margin(base.dp(2), base.dp(1), base.dp(2), base.dp(1))
                    .h_match_parent();

                var desc = ViewBuilder.wrapLinear(new TextView(requireContext()));
                desc.view().setText(info.description);
                desc.view().setTextColor(L_FONT_COLOR);
                desc.view().setTextSize(10);
                desc.view().setEllipsize(TextUtils.TruncateAt.END);
                desc.params()
                    .margin(base.dp(2), 0, base.dp(2), 0)
                    .h_match_parent();

                widget.add(title.build()).add(desc.build());
                widget.params().params().weight = 1;
                src.add(widget.build());

                var button = ViewBuilder.wrapLinear(new Button(requireContext()));
                button.view().setText("+");
                button.view().setTextColor(GREEN);
                button.view().setTextStyle(Paint.BOLD);
                button.view().setTextSize(14);

                int dp3 = base.dp(3);
                {
                    ShapeDrawable drawable = new ShapeDrawable();
                    drawable.setShape(ShapeDrawable.RECTANGLE);
                    drawable.setColor(ADD_BUTTON_COLOR);
                    drawable.setStroke(base.dp(1), EDGE_SIDES_COLOR);
                    drawable.setPadding(dp3, dp3, dp3, dp3);
                    drawable.setCornerRadius(base.dp(5));
                    button.bg(drawable);
                }
                button.view().setOnClickListener((__) -> {
                    HUD.addElement(info);
                });

                button.params().gravity(Gravity.CENTER_VERTICAL);

                {
                    int dp2 = base.dp(3);
                    ShapeDrawable drawable = new ShapeDrawable();
                    drawable.setShape(ShapeDrawable.RECTANGLE);
                    drawable.setPadding(dp2, dp2, dp2, dp2);
                    drawable.setColor(new Color(0, 0, 0, 50).getRGB());
                    drawable.setCornerRadius(base.dp(7));
                    src.bg(drawable);
                }

                src.params()
                    .margin(base.dp(5), base.dp(5), base.dp(5), base.dp(5))
                    .height(elementHeight).h_match_parent();

                src.add(button.build());
                elements.add(src.build());
            }

            elements.params().h_match_parent();

            scroll.view().addView(elements.build());
            scroll.params().h_match_parent();
            if (infos.size() * elementHeight > maxHeight) {
                scroll.params().height(maxHeight);
            }

            base.add(scroll.build());
        }

        {
            var apply = ViewBuilder.wrapLinear(new Button(requireContext()));
            {
                apply.view().setEnabled(HUD.active);
                apply.view().setAlpha((HUD.active ? 255 : 80) / 255.0f);
                apply.view().setText("Apply");
                apply.view().setTextSize(12);
                apply.view().setTextColor(GREEN);

                int dp5 = base.dp(5);
                {
                    ShapeDrawable drawable = new ShapeDrawable();
                    drawable.setShape(ShapeDrawable.RECTANGLE);
                    drawable.setColor(ADD_BUTTON_COLOR);
                    drawable.setStroke(base.dp(1), EDGE_SIDES_COLOR);
                    drawable.setPadding(dp5, dp5, dp5, dp5);
                    drawable.setCornerRadius(base.dp(5));
                    apply.bg(drawable);
                }

                apply.view().setOnClickListener((__) -> {
                    addingElement = false;
                    mainLayout();
                });
                apply.params().margin(0, dp5, 0, dp5).width(base.dp(100)).v_wrap_content();
                base.add(apply.build());
            }
        }

        {
            ShapeDrawable drawable = new ShapeDrawable();
            drawable.setShape(ShapeDrawable.RECTANGLE);
            drawable.setColor(BACKGROUND_COLOR);
            drawable.setStroke(base.dp(1), EDGE_SIDES_COLOR);
            drawable.setCornerRadius(base.dp(10));
            base.bg(drawable);
        }

        base.params().width(base.dp(300)).v_wrap_content();

        this.mBase.addView(base.build());
    }

    public void setting(HudElement element) {
        var main = LayoutBuilder.newLinerBuilder(requireContext());
        this.mBase.removeAllViews();
        main.vOrientation().gravity(Gravity.CENTER);

        {
            var title = ViewBuilder.wrapLinear(new TextView(requireContext()));
            title.view().setText(element.getTitle());
            title.params()
                .margin(0, main.dp(2), 0, main.dp(3))
                .h_match_parent().v_wrap_content();

            title.view().setTextColor(FONT_COLOR);
            title.view().setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            main.add(title.build());
        }

        var scroll = ViewBuilder.wrapLinear(new ScrollView(requireContext()));
        {
            var base = LayoutBuilder.newLinerBuilder(requireContext());
            base.vOrientation();
            var settings_ = element.settings;
            if (settings_.sizeGroups() == 0) {
                base.gravity(Gravity.CENTER);
                base.params().v_match_parent().h_match_parent();

                var empty = getEmpty(base, requireContext());
                empty.params().margin(base.dp(10), base.dp(10), base.dp(10), base.dp(10)).v_wrap_content().h_wrap_content();
                base.add(empty.build());
            } else {
                base.hGravity(Gravity.CENTER_HORIZONTAL);

                for (SettingGroup group : settings_.groups) {
                    var src = LayoutBuilder.newLinerBuilder(requireContext());
                    src.vOrientation()
                        .hGravity(Gravity.CENTER);

                    var settings = LayoutBuilder.newLinerBuilder(requireContext());
                    settings.vOrientation().hGravity(Gravity.CENTER_HORIZONTAL);

                    {
                        var relativeTop = LayoutBuilder.newRelativeBuilder(requireContext());
                        {
                            var top = LayoutBuilder.newLinerBuilder(requireContext());
                            top.hOrientation()
                                .vGravity(Gravity.CENTER_VERTICAL);

                            var location = ViewBuilder.wrapLinear(new ImageView(requireContext()));
                            var name = ViewBuilder.wrapLinear(new TextView(requireContext()));

                            location.view().setImage(LOCATION_IMAGE);
                            location.params().margin(0, base.dp(2), base.dp(7), base.dp(2));

                            name.view().setTextColor(FONT_COLOR);
                            name.view().setTextSize(10);
                            name.view().setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            name.view().setText(group.name);

                            top.add(location.build()).add(name.build());

                            var params0 = top.relative_params()
                                .parent_mode().vCenter().left()
                                .margin(base.dp(7), base.dp(3), 0, base.dp(5))
                                .h_wrap_content().height(base.dp(20));

                            relativeTop.add(top.build(params0));
                        }

                        var expandAnimator = ObjectAnimator.ofFloat(settings.layout(),
                            ViewAnimators.ALPHA_255, 0, 255);
                        var expandAnimator1 = ObjectAnimator.ofFloat(settings.layout(),
                            ViewAnimators.ALPHA_255, 255, 0);

                        expandAnimator.setInterpolator(TimeInterpolator.ACCELERATE_DECELERATE);
                        expandAnimator1.setInterpolator(TimeInterpolator.ACCELERATE_DECELERATE);

                        {
                            var expand = ViewBuilder.wrapLinear(new ImageView(requireContext()));
                            expand.view().setImage(ARROW_RIGHT_IMAGE);
                            expand.params().margin(0, base.dp(2), base.dp(5), base.dp(2));
                            expand.view().setRotation(group.sectionExpanded ? 90 : 0);

                            var arrow_o2f_Animator = ObjectAnimator.ofFloat(expand.view(),
                                View.ROTATION, 90, 0);
                            var arrow_f2o_Animator = ObjectAnimator.ofFloat(expand.view(),
                                View.ROTATION, 0, 90);
                            arrow_o2f_Animator.setInterpolator(TimeInterpolator.DECELERATE_QUINTIC);
                            arrow_f2o_Animator.setInterpolator(TimeInterpolator.DECELERATE_QUINTIC);


                            relativeTop.layout().setOnClickListener((v) -> {
                                if (group.sectionExpanded) {
                                    expandAnimator1.addListener(new AnimatorListener() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            src.layout().removeView(settings.layout());
                                        }
                                    });

                                    expandAnimator1.start();
                                    arrow_o2f_Animator.start();
                                }

                                group.sectionExpanded = !group.sectionExpanded;

                                if (group.sectionExpanded) {
                                    if (expandAnimator1.isRunning()) {
                                        expandAnimator1.cancel();
                                    }
                                    if (arrow_o2f_Animator.isRunning()) {
                                        arrow_o2f_Animator.cancel();
                                    }
                                    src.layout().addView(settings.layout());
                                    expandAnimator.start();
                                    arrow_f2o_Animator.start();
                                }

                                expand.view().setRotation(group.sectionExpanded ? 90 : 0);
                            });

                            var params = expand.relative_params()
                                .parent_mode().vCenter().right()
                                .h_wrap_content().height(20);
                            relativeTop.add(expand.build(params));
                        }

                        relativeTop.params()
                            .h_match_parent().v_wrap_content();
                        src.add(relativeTop.build());
                    }

                    if (group.settings.isEmpty()) {
                        var empty = getEmpty(src, requireContext());
                        empty.params().margin(base.dp(5), base.dp(5), base.dp(5), base.dp(5)).v_wrap_content().h_wrap_content();
                        settings.add(empty.build());
                    } else {
                        int id = 0;
                        for (Setting<?> setting : group.settings) {
                            View view = setting.createView(requireContext(), settings.layout());
                            if (view != null) {
                                view.setId(id + (114 * 514));
                                settings.add(view);
                                view.post(() -> setting.checkVisible0(requireContext(), view));
                                id++;
                            }
                        }
                    }

                    settings.params()
                        .margin(base.dp(5), base.dp(5), base.dp(3), base.dp(5))
                        .h_match_parent().v_wrap_content();

                    if (group.sectionExpanded)
                        src.add(settings.build());

                    src.params()
                        .margin(base.dp(5), base.dp(5), base.dp(5), base.dp(5))
                        .v_wrap_content().h_match_parent();

                    {
                        int dp2 = base.dp(2);
                        ShapeDrawable drawable = new ShapeDrawable();
                        drawable.setShape(ShapeDrawable.RECTANGLE);
                        drawable.setPadding(dp2, dp2, dp2, dp2);
                        drawable.setColor(new Color(0, 0, 0, 50).getRGB());
                        drawable.setCornerRadius(base.dp(7));
                        src.bg(drawable);
                    }

                    base.add(src.build());
                }

                base.params()
                    .margin(base.dp(5), base.dp(3), base.dp(3), base.dp(5))
                    .h_match_parent();
            }

            scroll.view().addView(base.build());
        }

        scroll.params().margin(main.dp(5), main.dp(5), main.dp(5), main.dp(5))
            .h_match_parent().height(mBase.dp(350));

        main.add(scroll.build());
        {
            var apply = ViewBuilder.wrapLinear(new Button(requireContext()));
            {
                apply.view().setEnabled(HUD.active);
                apply.view().setAlpha((HUD.active ? 255 : 80) / 255.0f);
                apply.view().setText("Apply");
                apply.view().setTextSize(12);
                apply.view().setTextColor(GREEN);

                int dp5 = main.dp(5);
                {
                    ShapeDrawable drawable = new ShapeDrawable();
                    drawable.setShape(ShapeDrawable.RECTANGLE);
                    drawable.setColor(ADD_BUTTON_COLOR);
                    drawable.setStroke(main.dp(1), EDGE_SIDES_COLOR);
                    drawable.setPadding(dp5, dp5, dp5, dp5);
                    drawable.setCornerRadius(main.dp(5));
                    apply.bg(drawable);
                }

                apply.view().setOnClickListener((__) -> {
                    current = null;
                    mainLayout();
                });
                apply.params().margin(0, dp5, 0, dp5).width(main.dp(100)).v_wrap_content();
                main.add(apply.build());
            }
        }

        {
            ShapeDrawable drawable = new ShapeDrawable();
            drawable.setShape(ShapeDrawable.RECTANGLE);
            drawable.setColor(BACKGROUND_COLOR);
            drawable.setStroke(main.dp(1), EDGE_SIDES_COLOR);
            drawable.setCornerRadius(main.dp(10));
            main.bg(drawable);
        }

        main.params().width(main.dp(400)).v_wrap_content();

        this.mBase.addView(main.build());
    }

    @Override
    public boolean shouldBlurBackground() {
        return current != null;
    }

    @Override
    public boolean hasDefaultBackground() {
        return mc.world == null;
    }

    private LayoutBuilder.LinearLayoutBuilder<LinearLayout> getEmpty(LayoutBuilder.LinearLayoutBuilder<LinearLayout> base, Context context) {
        var empty = LayoutBuilder.newLinerBuilder(context);
        empty.vOrientation().hGravity(Gravity.CENTER).vGravity(Gravity.CENTER);

        var img = ViewBuilder.wrapLinear(new ImageView(context));
        var txt = ViewBuilder.wrapLinear(new TextView(context));

        img.view().setImage(EMPTY_IMAGE);
        img.params().margin(base.dp(5), base.dp(5), base.dp(5), base.dp(10));
        txt.view().setText("哎哟我去 啥都没有 \uD83D\uDE05\uD83D\uDE05\uD83D\uDE05");
        txt.view().setTextColor(FONT_COLOR);

        empty.add(img.build(), 0)
            .add(txt.build(), 1);
        return empty;
    }
}
