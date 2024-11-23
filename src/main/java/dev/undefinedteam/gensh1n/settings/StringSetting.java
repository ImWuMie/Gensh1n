package dev.undefinedteam.gensh1n.settings;

import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.gui.builders.ParamsBuilder;
import dev.undefinedteam.gensh1n.gui.builders.ViewBuilder;
import dev.undefinedteam.gensh1n.gui.frags.GMainGui;
import dev.undefinedteam.gensh1n.gui.builders.LayoutBuilder;
import icyllis.modernui.R;
import icyllis.modernui.core.Context;
import dev.undefinedteam.modernui.mc.ui.ThemeControl;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.EditText;
import icyllis.modernui.widget.HorizontalScrollView;
import icyllis.modernui.widget.LinearLayout;

import java.util.function.Consumer;

public class StringSetting extends Setting<String> {
    public final boolean wide;

    public StringSetting(String name, String description, String defaultValue, Consumer<String> onChanged, Consumer<Setting<String>> onModuleActivated, IVisible visible, boolean wide) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);

        this.wide = wide;
    }

    private LinearLayout mLayout;
    private EditText mTextbox;

    @Override
    public View createView(Context context, LinearLayout view) {
        var builder = LayoutBuilder.newLinerBuilder(context);

        mLayout = builder.layout();
        final int dp3 = mLayout.dp(3);
        final int dp6 = mLayout.dp(6);
        builder
            .hOrientation()
            .hGravity(Gravity.START);
        builder.add(createTitle(context).build());
        {
            var scroll = ViewBuilder.wrapLinear(new HorizontalScrollView(context));

            mTextbox = new EditText(context);
            mTextbox.setTextColor(GMainGui.FONT_COLOR);
            mTextbox.setId(R.id.button1);
            mTextbox.setText(get());
            mTextbox.setSingleLine();
            mTextbox.setMinWidth(mLayout.dp(250));
            mTextbox.setVerticalScrollBarEnabled(false);
            mTextbox.setOnFocusChangeListener((__, hasFocus) -> {
                if (!hasFocus) {
                    if (!mTextbox.getText().toString().equals(get())) {
                        this.set(mTextbox.getText().toString());
                    }
                }
            });
            ThemeControl.addBackground(mTextbox);
            var params = ParamsBuilder.newLinerBuilder();
            params.height(25);
            scroll.view().addView(mTextbox,params.build());
            scroll.params().gravity(Gravity.CENTER_VERTICAL)
                .margin(0, dp3, 0, dp3).width(mLayout.dp(250)).height(mLayout.dp(25));
            builder.add(scroll.build());
        }
        builder.params()
            .gravity(Gravity.CENTER)
            .margin(dp6, 0, dp6, 0)
            .h_match_parent()
            .v_wrap_content();
        return builder.build();
    }

    @Override
    protected ViewGroup layouts(Context context) {
        return mLayout;
    }

    @Override
    protected View[] children(Context context) {
        return new View[]{mTextbox};
    }

    @Override
    protected String parseImpl(String str) {
        return str;
    }

    @Override
    protected boolean isValueValid(String value) {
        return true;
    }

    @Override
    public JsonObject save(JsonObject tag) {
        tag.addProperty("value", get());

        return tag;
    }

    @Override
    public String load(JsonObject tag) {
        set(tag.get("value").getAsString());

        return get();
    }

    public static class Builder extends SettingBuilder<Builder, String, StringSetting> {
        private boolean wide;

        public Builder() {
            super(null);
        }

        public Builder wide() {
            wide = true;
            return this;
        }

        @Override
        public StringSetting build() {
            return new StringSetting(name, description, defaultValue, onChanged, onModuleActivated, visible, wide);
        }
    }
}
