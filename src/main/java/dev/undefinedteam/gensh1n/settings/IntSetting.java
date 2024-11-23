package dev.undefinedteam.gensh1n.settings;

import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.gui.frags.GMainGui;
import dev.undefinedteam.gensh1n.gui.builders.LayoutBuilder;
import icyllis.modernui.R;
import icyllis.modernui.core.Context;
import icyllis.modernui.graphics.MathUtil;
import dev.undefinedteam.modernui.mc.ui.ThemeControl;
import icyllis.modernui.text.InputFilter;
import icyllis.modernui.text.method.DigitsInputFilter;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.EditText;
import icyllis.modernui.widget.LinearLayout;
import icyllis.modernui.widget.SeekBar;

import java.util.function.Consumer;

import static icyllis.modernui.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class IntSetting extends Setting<Integer> {
    public final int min, max;

    private IntSetting(String name, String description, int defaultValue, Consumer<Integer> onChanged, Consumer<Setting<Integer>> onModuleActivated, IVisible visible, int min, int max) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);

        this.min = min;
        this.max = max;
    }


    private LinearLayout mLayout;
    private LinearLayout mSeekLayout;
    private SeekBar mSlider;
    private EditText mTextbox;

    @Override
    public View createView(Context context, LinearLayout g) {
        var builder = LayoutBuilder.newLinerBuilder(context);
        mLayout = builder.layout();

        final int dp6 = mLayout.dp(6);

        {
            builder
                .hOrientation()
                .vGravity(Gravity.CENTER_VERTICAL);

            builder.add(createTitle(context).build());
            {
                var seekBuilder = LayoutBuilder.newLinerBuilder(context);
                mSeekLayout = seekBuilder.layout();
                seekBuilder
                    .hOrientation()
                    .gravity(Gravity.START)
                    .vGravity(Gravity.CENTER_VERTICAL);

                {
                    mTextbox = new EditText(context);
                    mTextbox.setId(R.id.input);
                    mTextbox.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                    mTextbox.setTextSize(14);
                    mTextbox.setTextColor(GMainGui.FONT_COLOR);
                    mTextbox.setMinWidth(mLayout.dp(20));

                    mTextbox.setFilters(DigitsInputFilter.getInstance(null, min < 0, true),
                        new InputFilter.LengthFilter(Integer.toString(Integer.MAX_VALUE).length() + 1));

                    mTextbox.setText(String.valueOf(get()));
                    mTextbox.setOnFocusChangeListener((view, hasFocus) -> {
                        if (!hasFocus) {
                            EditText v = (EditText) view;
                            int newValue = MathUtil.clamp(Integer.parseInt(v.getText().toString()),
                                min, max);
                            replaceText(v, String.valueOf(newValue));
                            if (newValue != get()) {
                                set(newValue);
                                int curProgress = newValue - min;
                                mSlider.setProgress(curProgress, true);
                            }
                        }
                    });

                    ThemeControl.addBackground(mTextbox);

                    var params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 1);
                    params.gravity = Gravity.CENTER_VERTICAL;
                    mTextbox.setLayoutParams(params);
                    seekBuilder.add(mTextbox);

                }

                {
                    mSlider = new SeekBar(context);
                    mSlider.setClickable(true);

                    {
                        int steps = max - min;
                        mSlider.setMax(steps);
                        int curProgress = get() - min;
                        mSlider.setProgress(curProgress);

                        mSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                int newValue = seekBar.getProgress() + min;
                                replaceText(mTextbox, String.valueOf(newValue));
                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                int newValue = seekBar.getProgress() + min;
                                if (newValue != get()) {
                                    set(newValue);
                                    replaceText(mTextbox, String.valueOf(newValue));
                                }
                            }
                        });
                    }

                    var params = new LinearLayout.LayoutParams(mSlider.dp(200), WRAP_CONTENT);
                    params.setMargins(mLayout.dp(3), mLayout.dp(2), 0, mLayout.dp(2));
                    params.gravity = Gravity.CENTER_VERTICAL;
                    mSlider.setLayoutParams(params);
                    seekBuilder.add(mSlider);
                }

                seekBuilder.params()
                    .gravity(Gravity.END)
                    .margin(dp6, 0, 0, 0)
                    .v_match_parent();
                builder.add(seekBuilder.build());
            }
        }

        builder.params()
            .gravity(Gravity.CENTER)
            .margin(dp6, 0, dp6, 0)
            .h_match_parent().v_wrap_content();
        return builder.build();
    }

    @Override
    protected ViewGroup layouts(Context context) {
        return mLayout;
    }

    @Override
    protected View[] children(Context context) {
        return new View[]{mSeekLayout, mTextbox, mSlider};
    }

    @Override
    protected Integer parseImpl(String str) {
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    @Override
    protected boolean isValueValid(Integer value) {
        return value >= min && value <= max;
    }

    @Override
    public JsonObject save(JsonObject tag) {
        tag.addProperty("value", get());

        return tag;
    }

    @Override
    public Integer load(JsonObject tag) {
        set(tag.get("value").getAsInt());

        return get();
    }

    public static class Builder extends SettingBuilder<Builder, Integer, IntSetting> {
        private int min = Integer.MIN_VALUE, max = Integer.MAX_VALUE;

        public Builder() {
            super(0);
        }

        public Builder min(int min) {
            this.min = min;
            return this;
        }

        public Builder max(int max) {
            this.max = max;
            return this;
        }

        public Builder range(int min, int max) {
            this.min = Math.min(min, max);
            this.max = Math.max(min, max);
            return this;
        }

        @Override
        public IntSetting build() {
            return new IntSetting(name, description, defaultValue, onChanged, onModuleActivated, visible, min, max);
        }
    }
}
