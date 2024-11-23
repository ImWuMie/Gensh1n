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

public class DoubleSetting extends Setting<Double> {
    public final double min, max;
    public final int decimalPlaces;
    public final boolean noSlider;

    private DoubleSetting(String name, String description, double defaultValue, Consumer<Double> onChanged, Consumer<Setting<Double>> onModuleActivated, IVisible visible, double min, double max, int decimalPlaces, boolean noSlider) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);

        this.min = min;
        this.max = max;
        this.decimalPlaces = decimalPlaces;
        this.noSlider = noSlider;
    }

    @Override
    protected Double parseImpl(String str) {
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    @Override
    protected boolean isValueValid(Double value) {
        return value >= min && value <= max;
    }

    public static final int DENOMINATOR = 100 * 10;

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
                        new InputFilter.LengthFilter(64));

                    mTextbox.setText(String.valueOf(get()));
                    mTextbox.setOnFocusChangeListener((view, hasFocus) -> {
                        if (!hasFocus) {
                            EditText v = (EditText) view;
                            double newValue = MathUtil.clamp(Double.parseDouble(v.getText().toString()),
                                min, max);
                            replaceText(v, String.valueOf(newValue));
                            if (newValue != get()) {
                                set(newValue);
                                int curProgress = (int) ((newValue - min) * DENOMINATOR);
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
                        int steps = (int) ((max - min) * DENOMINATOR);
                        mSlider.setMax(steps);
                        int curProgress = (int) ((get() - min) * DENOMINATOR);
                        mSlider.setProgress(curProgress);

                        mSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                double newValue = (double) seekBar.getProgress() / DENOMINATOR + min;
                                replaceText(mTextbox, String.valueOf(newValue));
                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                double newValue = (double) seekBar.getProgress() / DENOMINATOR + min;
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
        return new View[] {mSeekLayout,mSlider,mTextbox};
    }

    @Override
    protected JsonObject save(JsonObject tag) {
        tag.addProperty("value", get());

        return tag;
    }

    @Override
    public Double load(JsonObject tag) {
        set(tag.get("value").getAsDouble());

        return get();
    }

    public static class Builder extends SettingBuilder<Builder, Double, DoubleSetting> {
        public double min = 0, max = 10;
        public int decimalPlaces = 3;
        public boolean noSlider = false;

        public Builder() {
            super(0.0D);
        }

        public Builder defaultValue(double defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder min(double min) {
            this.min = min;
            return this;
        }

        public Builder max(double max) {
            this.max = max;
            return this;
        }

        public Builder range(double min, double max) {
            this.min = Math.min(min, max);
            this.max = Math.max(min, max);
            return this;
        }


        public Builder decimalPlaces(int decimalPlaces) {
            this.decimalPlaces = decimalPlaces;
            return this;
        }

        public Builder noSlider() {
            noSlider = true;
            return this;
        }

        public DoubleSetting build() {
            return new DoubleSetting(name, description, defaultValue, onChanged, onModuleActivated, visible, min, max, decimalPlaces, noSlider);
        }
    }
}
