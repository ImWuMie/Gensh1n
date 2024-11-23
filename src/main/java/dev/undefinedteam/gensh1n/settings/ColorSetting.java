package dev.undefinedteam.gensh1n.settings;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.gui.builders.LayoutBuilder;
import dev.undefinedteam.gensh1n.gui.builders.ViewBuilder;
import dev.undefinedteam.gensh1n.utils.render.color.Color;
import dev.undefinedteam.gensh1n.utils.render.color.SettingColor;
import icyllis.modernui.R;
import icyllis.modernui.animation.ObjectAnimator;
import icyllis.modernui.animation.TimeInterpolator;
import icyllis.modernui.core.Context;
import icyllis.modernui.graphics.MathUtil;
import icyllis.modernui.graphics.drawable.ShapeDrawable;
import icyllis.modernui.graphics.drawable.StateListDrawable;
import dev.undefinedteam.modernui.mc.ui.ThemeControl;
import icyllis.modernui.text.InputFilter;
import icyllis.modernui.text.method.DigitsInputFilter;
import icyllis.modernui.util.StateSet;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.*;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static dev.undefinedteam.gensh1n.gui.frags.GMainGui.*;
import static icyllis.modernui.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ColorSetting extends Setting<SettingColor> {
    private static final List<String> SUGGESTIONS = ImmutableList.of("0 0 0 255", "225 25 25 255", "25 225 25 255", "25 25 225 255", "255 255 255 255");

    public ColorSetting(String name, String description, SettingColor defaultValue, Consumer<SettingColor> onChanged, Consumer<Setting<SettingColor>> onModuleActivated, IVisible visible) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    }

    @Override
    protected SettingColor parseImpl(String str) {
        try {
            String[] strs = str.split(" ");
            return new SettingColor(Integer.parseInt(strs[0]), Integer.parseInt(strs[1]), Integer.parseInt(strs[2]), Integer.parseInt(strs[3]));
        } catch (IndexOutOfBoundsException | NumberFormatException ignored) {
            return null;
        }
    }

    private boolean boxExpanded;

    private LinearLayout mLayout, mBar, mPicker;
    private ImageButton mImg;

    private float clickX, clickY;

    @Override
    public View createView(Context context, LinearLayout view) {
        var base = LayoutBuilder.newLinerBuilder(context);
        mLayout = base.layout();
        base.vOrientation().hGravity(Gravity.CENTER_HORIZONTAL);

        var bar = LayoutBuilder.newLinerBuilder(context);
        this.mBar = bar.layout();

        final int dp3 = mLayout.dp(3);
        final int dp6 = mLayout.dp(6);
        bar.hOrientation()
            .hGravity(Gravity.START);
        bar.add(createTitle(context).build());
        var picker = LayoutBuilder.newLinerBuilder(context);
        picker.vOrientation().vGravity(Gravity.CENTER_VERTICAL);
        this.mPicker = picker.layout();

        ShapeDrawable preview_draw = new ShapeDrawable();
        {
            var layout = LayoutBuilder.newLinerBuilder(context);
            layout.hOrientation().vGravity(Gravity.CENTER_VERTICAL);

            var preview = ViewBuilder.wrapLinear(new View(context));

            {
                int dp2 = base.dp(2);
                preview_draw.setShape(ShapeDrawable.RECTANGLE);
                preview_draw.setColor(get().getPacked());
                preview_draw.setPadding(dp2, dp2, dp2, dp2);
                preview_draw.setCornerRadius(base.dp(5));
                preview.bg(preview_draw);
            }

            preview.params().width(base.dp(18)).height(base.dp(18));
            layout.add(preview.build());

            {
                var expand = ViewBuilder.wrapLinear(new ImageButton(context));

                this.mImg = expand.view();

                expand.view().setImage(ARROW_RIGHT_IMAGE);
                expand.params().margin(base.dp(4), 0, 0, 0);
                expand.view().setRotation(boxExpanded ? 90 : 0);

                var arrow_o2f_Animator = ObjectAnimator.ofFloat(expand.view(),
                    View.ROTATION, 90, 0);
                var arrow_f2o_Animator = ObjectAnimator.ofFloat(expand.view(),
                    View.ROTATION, 0, 90);
                arrow_o2f_Animator.setInterpolator(TimeInterpolator.DECELERATE_QUINTIC);
                arrow_f2o_Animator.setInterpolator(TimeInterpolator.DECELERATE_QUINTIC);

                expand.view().setOnClickListener((__) -> {
                    if (boxExpanded) {
                        arrow_o2f_Animator.start();
                        base.layout().post(() -> base.layout().removeView(mPicker));
                    }

                    boxExpanded = !boxExpanded;

                    if (boxExpanded) {
                        if (arrow_o2f_Animator.isRunning()) {
                            arrow_o2f_Animator.cancel();
                        }
                        base.layout().post(() -> base.layout().addView(mPicker));
                        arrow_f2o_Animator.start();
                    }

                    expand.view().setRotation(boxExpanded ? 90 : 0);
                });

                {
                    StateListDrawable background = new StateListDrawable();
                    ShapeDrawable drawable = new ShapeDrawable();
                    drawable.setShape(ShapeDrawable.RECTANGLE);
                    drawable.setColor(BACKGROUND_COLOR);
                    drawable.setPadding(base.dp(3), base.dp(3), base.dp(3), base.dp(3));
                    drawable.setCornerRadius(base.dp(66));
                    background.addState(StateSet.get(StateSet.VIEW_STATE_HOVERED), drawable);
                    //background.addState(new int[]{R.attr.state_checked},drawable);
                    background.setEnterFadeDuration(250);
                    background.setExitFadeDuration(250);
                    expand.bg(background);
                }

                layout.add(expand.build());
            }
            layout.params().gravity(Gravity.CENTER_VERTICAL).margin(0, 0, base.dp(5), 0);
            bar.add(layout.build());
        }

        base.add(bar.build());

        {
            picker.hOrientation().vGravity(Gravity.CENTER_VERTICAL);

            String[] values = {"R:", "G:", "B:", "A:"};
            int id = 0;
            for (String t : values) {
                var src = LayoutBuilder.newLinerBuilder(context);
                var title = ViewBuilder.wrapLinear(new TextView(context));
                title.view().setText(t);
                title.view().setTextColor(FONT_COLOR);
                title.view().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                title.view().setTextSize(12);
                title.params()
                    .gravity(Gravity.CENTER_VERTICAL)
                    .h_wrap_content().v_wrap_content();

                src.add(title.build());
                final int fid = id;
                var slider = colorSeekBar(context,
                    () -> switch (fid) {
                        case 0 -> this.get().r;
                        case 1 -> this.get().g;
                        case 2 -> this.get().b;
                        case 3 -> this.get().a;
                        default -> throw new IllegalStateException("Unexpected value: " + fid);
                    }, (v) -> {
                    switch (fid) {
                        case 0 -> this.get().r = v;
                        case 1 -> this.get().g = v;
                        case 2 -> this.get().b = v;
                        case 3 -> this.get().a = v;
                    }

                    preview_draw.setColor(get().getPacked());
                });
                src.add(slider);
                src.params().margin(base.dp(5), 0, 0, 0);
                mPicker.addView(src.build());
                id++;
            }

            if (boxExpanded)
                base.add(mPicker);
        }
        bar.params()
            .h_match_parent()
            .v_wrap_content();

        base.params()
            .margin(dp6, 0, dp6, 0)
            .h_match_parent()
            .v_wrap_content();
        return base.build();
    }

    @Override
    protected ViewGroup layouts(Context context) {
        return mLayout;
    }

    @Override
    protected View[] children(Context context) {
        return new View[]{mBar, mImg, mPicker};
    }

    @Override
    public void resetImpl() {
        if (value == null) value = new SettingColor(defaultValue);
        else value.set(defaultValue);
    }

    @Override
    protected boolean isValueValid(SettingColor value) {
        value.validate();

        return true;
    }

    @Override
    public List<String> getSuggestions() {
        return SUGGESTIONS;
    }

    @Override
    protected JsonObject save(JsonObject tag) {
        tag.add("value", get().toTag());
        tag.addProperty("expand", this.boxExpanded);

        return tag;
    }

    @Override
    public SettingColor load(JsonObject tag) {
        get().fromTag(tag.getAsJsonObject("value"));

        if (tag.has("expand")) this.boxExpanded = tag.get("expand").getAsBoolean();
        return get();
    }

    private LinearLayout colorSeekBar(Context context, Supplier<Integer> get, Consumer<Integer> set) {
        var base = LayoutBuilder.newLinerBuilder(context);

        var view = ViewBuilder.wrapLinear(new SeekBar(context));
        var slider = view.view();

        var textbox = new EditText(context);

        {
            slider = new SeekBar(context);
            slider.setClickable(true);

            {
                int steps = 255;
                slider.setMax(steps);
                int curProgress = get.get();
                slider.setProgress(curProgress);

                slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        int newValue = seekBar.getProgress();
                        set.accept(newValue);
                        replaceText(textbox, String.valueOf(newValue));
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        int newValue = seekBar.getProgress();
                        if (newValue != get.get()) {
                            set.accept(newValue);
                            replaceText(textbox, String.valueOf(newValue));
                        }
                    }
                });
            }

            var params = new LinearLayout.LayoutParams(slider.dp(100), WRAP_CONTENT);
            params.gravity = Gravity.CENTER_VERTICAL;
            slider.setLayoutParams(params);
            base.add(slider);
        }

        {
            textbox.setId(R.id.input);
            textbox.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textbox.setTextSize(12);
            textbox.setTextColor(FONT_COLOR);
            textbox.setMinWidth(mLayout.dp(20));

            textbox.setFilters(DigitsInputFilter.getInstance(null, false, true),
                new InputFilter.LengthFilter(3));

            textbox.setText(String.valueOf(get.get()));
            SeekBar finalSlider = slider;
            textbox.setOnFocusChangeListener((__, hasFocus) -> {
                if (!hasFocus) {
                    int newValue = MathUtil.clamp(Integer.parseInt(textbox.getText().toString()),
                        0, 255);

                    replaceText(textbox, String.valueOf(newValue));
                    if (newValue != get.get()) {
                        set.accept(newValue);
                        finalSlider.setProgress(newValue, true);
                    }
                }
            });

            ThemeControl.addBackground(textbox);

            var params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            params.gravity = Gravity.CENTER_VERTICAL;
            textbox.setLayoutParams(params);
            base.add(textbox);
        }

        return base.build();
    }

    public static class Builder extends SettingBuilder<Builder, SettingColor, ColorSetting> {
        public Builder() {
            super(new SettingColor());
        }

        @Override
        public ColorSetting build() {
            return new ColorSetting(name, description, defaultValue, onChanged, onModuleActivated, visible);
        }

        @Override
        public Builder defaultValue(SettingColor defaultValue) {
            this.defaultValue.set(defaultValue);
            return this;
        }

        public Builder defaultValue(Color defaultValue) {
            this.defaultValue.set(defaultValue);
            return this;
        }
    }
}
