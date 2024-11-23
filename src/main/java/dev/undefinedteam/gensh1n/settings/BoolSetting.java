package dev.undefinedteam.gensh1n.settings;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.gui.builders.LayoutBuilder;
import icyllis.modernui.R;
import icyllis.modernui.core.Context;
import dev.undefinedteam.modernui.mc.ui.ThemeControl;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.View;
import icyllis.modernui.widget.LinearLayout;
import icyllis.modernui.widget.SwitchButton;

import java.util.List;
import java.util.function.Consumer;

public class BoolSetting extends Setting<Boolean> {
    private static final List<String> SUGGESTIONS = ImmutableList.of("true", "false", "toggle");

    private BoolSetting(String name, String description, Boolean defaultValue, Consumer<Boolean> onChanged, Consumer<Setting<Boolean>> onModuleActivated, IVisible visible) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    }

    @Override
    protected Boolean parseImpl(String str) {
        if (str.equalsIgnoreCase("true") || str.equalsIgnoreCase("1")) return true;
        else if (str.equalsIgnoreCase("false") || str.equalsIgnoreCase("0")) return false;
        else if (str.equalsIgnoreCase("toggle")) return !get();
        return null;
    }

    @Override
    protected boolean isValueValid(Boolean value) {
        return true;
    }

    @Override
    public List<String> getSuggestions() {
        return SUGGESTIONS;
    }

    @Override
    public JsonObject save(JsonObject tag) {
        tag.addProperty("value", get());

        return tag;
    }

    @Override
    public Boolean load(JsonObject tag) {
        set(tag.get("value").getAsBoolean());

        return get();
    }

    private LinearLayout mLayout;
    private SwitchButton button;

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
            var check_button = new SwitchButton(context);
            button = check_button;
            check_button.setChecked(this.get());
            check_button.setId(R.id.button1);
            check_button.setCheckedColor(ThemeControl.THEME_COLOR);
            check_button.setOnCheckedChangeListener((__, checked) -> {
                this.set(checked);
            });

            var params = new LinearLayout.LayoutParams(mLayout.dp(40), mLayout.dp(20));
            params.gravity = Gravity.CENTER_VERTICAL;
            params.setMargins(0, dp3, 0, dp3);
            builder.add(check_button, params);
        }
        builder.params()
            .gravity(Gravity.CENTER)
            .margin(dp6, 0, dp6, 0)
            .h_match_parent()
            .v_wrap_content();
        return builder.build();
    }

    @Override
    protected View[] children(Context context) {
        return new View[]{button};
    }

    public static class Builder extends SettingBuilder<Builder, Boolean, BoolSetting> {
        public Builder() {
            super(false);
        }

        @Override
        public BoolSetting build() {
            return new BoolSetting(name, description, defaultValue, onChanged, onModuleActivated, visible);
        }
    }
}
