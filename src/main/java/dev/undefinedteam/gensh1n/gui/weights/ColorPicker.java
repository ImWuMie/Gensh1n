package dev.undefinedteam.gensh1n.gui.weights;

import dev.undefinedteam.gensh1n.gui.builders.LayoutBuilder;
import dev.undefinedteam.gensh1n.gui.builders.ParamsBuilder;
import dev.undefinedteam.gensh1n.utils.render.color.Color;
import dev.undefinedteam.gensh1n.utils.render.color.SettingColor;
import icyllis.modernui.core.Context;
import dev.undefinedteam.modernui.mc.ui.ThemeControl;
import icyllis.modernui.text.InputFilter;
import icyllis.modernui.text.method.DigitsInputFilter;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.widget.EditText;
import icyllis.modernui.widget.LinearLayout;
import net.minecraft.util.math.MathHelper;

import java.util.function.BiConsumer;

public class ColorPicker extends LinearLayout {
    private EditText mRed;
    private EditText mGreen;
    private EditText mBlue;
    private EditText mAlpha;

    private EditText mHex;

    private final OnFocusChangeListener mColorChanged, mHexChanged, mUpdateHex;

    private BiConsumer<ColorPicker, SettingColor> colorChangedListener;

    public void setColorChangedListener(BiConsumer<ColorPicker, SettingColor> listener) {
        this.colorChangedListener = listener;
    }

    public ColorPicker(Context context, SettingColor value) {
        super(context);

        mUpdateHex = (v, hasFocus) -> {
            if (!hasFocus) {
                try {
                    var string = mHex.getText().toString();
                    int color = 0xFFFFFFFF;
                    try {
                        color = icyllis.modernui.graphics.Color.parseColor(string);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                    mHex.setTextColor(0xFF000000 | color);
                } catch (Exception e) {
                    mHex.setTextColor(0xFFFF0000);
                }
            }
        };

        mHexChanged = (v, hasFocus) -> {
            if (!hasFocus) {
                int finalColor;
                try {
                    var string = mHex.getText().toString();
                    int color = 0xFFFFFFFF;
                    try {
                        color = icyllis.modernui.graphics.Color.parseColor(string);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                    finalColor = 0xFF000000 | color;
                } catch (Exception e) {
                    finalColor = 0xFFFF0000;
                }

                Color c = new Color(finalColor);
                this.mRed.setText(c.r + "");
                this.mGreen.setText(c.g + "");
                this.mBlue.setText(c.b + "");
                this.mAlpha.setText(c.a + "");

                int red = Integer.parseInt(this.mRed.getText().toString());
                int green = Integer.parseInt(this.mGreen.getText().toString());
                int blue = Integer.parseInt(this.mBlue.getText().toString());
                int alpha = Integer.parseInt(this.mAlpha.getText().toString());

                this.mAlpha.setTextColor(new Color(red, green, blue, MathHelper.clamp(alpha, 10, 255)).getPacked());

                if (colorChangedListener != null) {
                    colorChangedListener.accept(ColorPicker.this, getColor());
                }
            }
        };

        mColorChanged = (v, hasFocus) -> {
            if (!hasFocus) {
                int red = Integer.parseInt(this.mRed.getText().toString());
                int green = Integer.parseInt(this.mGreen.getText().toString());
                int blue = Integer.parseInt(this.mBlue.getText().toString());
                int alpha = Integer.parseInt(this.mAlpha.getText().toString());
                Color color = new Color(red, green, blue, alpha);
                this.mHex.setText(toHex(color));
                if (colorChangedListener != null) {
                    colorChangedListener.accept(ColorPicker.this, getColor());
                }
            }
        };

        this.mRed = createField(0, value.r);
        this.mGreen = createField(1, value.g);
        this.mBlue = createField(2, value.b);

        this.mRed.setTextColor(Color.RED.getPacked());
        this.mGreen.setTextColor(Color.GREEN.getPacked());
        this.mBlue.setTextColor(Color.BLUE.getPacked());

        this.mAlpha = createField(3, value.a);
        {
            int red = Integer.parseInt(this.mRed.getText().toString());
            int green = Integer.parseInt(this.mGreen.getText().toString());
            int blue = Integer.parseInt(this.mBlue.getText().toString());
            int alpha = Integer.parseInt(this.mAlpha.getText().toString());

            this.mAlpha.setTextColor(new Color(red, green, blue, MathHelper.clamp(alpha, 10, 255)).getPacked());
        }

        this.mHex = createField(4, value);
        this.mColorChanged.onFocusChange(mHex,false);

        this.mHex.setOnFocusChangeListener((v, is) -> {
            this.mHexChanged.onFocusChange(v, is);
            this.mUpdateHex.onFocusChange(v, is);
        });

        this.mRed.setOnFocusChangeListener((v, is) -> {
            this.mColorChanged.onFocusChange(v, is);
            this.mUpdateHex.onFocusChange(v, is);
        });

        this.mGreen.setOnFocusChangeListener((v, is) -> {
            this.mColorChanged.onFocusChange(v, is);
            this.mUpdateHex.onFocusChange(v, is);
        });

        this.mBlue.setOnFocusChangeListener((v, is) -> {
            this.mColorChanged.onFocusChange(v, is);
            this.mUpdateHex.onFocusChange(v, is);
        });

        this.mAlpha.setOnFocusChangeListener((v, is) -> {
            this.mColorChanged.onFocusChange(v, is);
            this.mUpdateHex.onFocusChange(v, is);
        });

        this.setOrientation(HORIZONTAL);
        this.setVerticalGravity(Gravity.CENTER_VERTICAL);
        this.setGravity(Gravity.START);

        var rgbaBuilder = LayoutBuilder.newLinerBuilder(context);
        rgbaBuilder
            .hOrientation()
            .vGravity(Gravity.CENTER_VERTICAL);

        var paramsBuilder = ParamsBuilder.newLinerBuilder();
        paramsBuilder
            .margin(dp(4), 0, 0, 0)
            .v_match_parent();
        {
            var params = paramsBuilder.build();
            this.mRed.setLayoutParams(params);
            this.mGreen.setLayoutParams(params);
            this.mBlue.setLayoutParams(params);
            this.mAlpha.setLayoutParams(params);

            rgbaBuilder.add(mRed, 0);
            rgbaBuilder.add(mGreen, 1);
            rgbaBuilder.add(mBlue, 2);
            rgbaBuilder.add(mAlpha, 3);
        }

        rgbaBuilder.params()
            .margin(dp(5), 0, 0, 0)
            .v_match_parent();

        paramsBuilder = ParamsBuilder.newLinerBuilder();
        paramsBuilder
            .weight(1)
            .margin(dp(4), 0, 0, 0)
            .v_match_parent();
        {
            this.mHex.setLayoutParams(paramsBuilder.build());
        }

        this.addView(this.mHex, 0);
        this.addView(rgbaBuilder.build(), 0);
    }

    private EditText createField(int idx, int color) {
        var field = new EditText(getContext());
        field.setId(idx + 117);
        field.setSingleLine();
        field.setText(color + "");
        field.setFilters(DigitsInputFilter.getInstance(null, false, true), new InputFilter.LengthFilter(3));
        field.setTextSize(16);
        ThemeControl.addBackground(field);
        return field;
    }

    private EditText createField(int idx, Color color) {
        var field = new EditText(getContext());
        field.setId(idx + 1118);
        field.setSingleLine();
        field.setText(toHex(color));
        field.setFilters(new InputFilter.LengthFilter(10));
        field.setTextSize(16);
        ThemeControl.addBackground(field);
        return field;
    }

    private String toHex(Color color) {
        return ("#" + Integer.toHexString(color.getPacked())).toUpperCase();
    }

    public SettingColor getColor() {
        int red = Integer.parseInt(this.mRed.getText().toString());
        int green = Integer.parseInt(this.mGreen.getText().toString());
        int blue = Integer.parseInt(this.mBlue.getText().toString());
        int alpha = Integer.parseInt(this.mAlpha.getText().toString());
        return new SettingColor(red, green, blue, alpha);
    }

    public void setColors(Color color) {
        mRed.setText(color.r + "");
        mGreen.setText(color.g + "");
        mBlue.setText(color.b + "");
        mAlpha.setText(color.a + "");
        mColorChanged.onFocusChange(mHex, false);
        mUpdateHex.onFocusChange(mHex, false);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.mRed.setEnabled(enabled);
        this.mGreen.setEnabled(enabled);
        this.mBlue.setEnabled(enabled);
        this.mAlpha.setEnabled(enabled);
        this.mHex.setEnabled(enabled);
    }

    @Override
    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
        this.mRed.setAlpha(alpha);
        this.mGreen.setAlpha(alpha);
        this.mBlue.setAlpha(alpha);
        this.mAlpha.setAlpha(alpha);
        this.mHex.setAlpha(alpha);
    }
}
