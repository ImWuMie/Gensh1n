package dev.undefinedteam.gensh1n.settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.gui.builders.ViewBuilder;
import dev.undefinedteam.gensh1n.gui.frags.GMainGui;
import dev.undefinedteam.gensh1n.gui.builders.LayoutBuilder;
import icyllis.modernui.R;
import icyllis.modernui.animation.ObjectAnimator;
import icyllis.modernui.animation.TimeInterpolator;
import icyllis.modernui.annotation.NonNull;
import icyllis.modernui.core.Context;
import icyllis.modernui.graphics.drawable.ShapeDrawable;
import icyllis.modernui.graphics.drawable.StateListDrawable;
import icyllis.modernui.text.Editable;
import icyllis.modernui.util.StateSet;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static dev.undefinedteam.gensh1n.gui.frags.ChatFragment.EDIT_COLOR;
import static dev.undefinedteam.gensh1n.gui.frags.GMainGui.*;

public class StringListSetting extends Setting<List<String>> {
    public static final int INFO_FONT_COLOR = new Color(255, 255, 255, 170).getRGB();

    public StringListSetting(String name, String description, List<String> defaultValue, Consumer<List<String>> onChanged, Consumer<Setting<List<String>>> onModuleActivated, IVisible visible) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    }

    @Override
    protected List<String> parseImpl(String str) {
        return Arrays.asList(str.split(","));
    }

    @Override
    protected boolean isValueValid(List<String> value) {
        return true;
    }

    private boolean boxExpanded;

    private LinearLayout mLayout, mBar;
    private ImageButton mImg;
    private EditText mTextBox;

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
        var hScroll = ViewBuilder.wrapLinear(new HorizontalScrollView(context));
        var vScroll = ViewBuilder.wrapLinear(new ScrollView(context));

        var info = ViewBuilder.wrapLinear(new TextView(context));
        {
            var layout = LayoutBuilder.newLinerBuilder(context);
            info.view().setText(this.get().size() + " strings");
            info.view().setTextColor(INFO_FONT_COLOR);
            info.view().setTextSize(12);
            info.view().setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            layout.add(info.build());

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
                        base.layout().post(() -> base.layout().removeView(vScroll.view()));
                    }

                    boxExpanded = !boxExpanded;

                    if (boxExpanded) {
                        if (arrow_o2f_Animator.isRunning()) {
                            arrow_o2f_Animator.cancel();
                        }
                        base.layout().post(() -> base.layout().addView(vScroll.view()));
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
            var textBox = ViewBuilder.wrapLinear(new EditText(context));
            this.mTextBox = textBox.view();

            textBox.view().setTextColor(GMainGui.FONT_COLOR);
            textBox.view().setId(R.id.button1);
            textBox.view().setGravity(Gravity.START | Gravity.TOP);
            textBox.view().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            textBox.view().setText(String.join("\n", get()));
            textBox.view().setOnFocusChangeListener((__, hasFocus) -> {
                if (!hasFocus) {
                    EditText v = (EditText) __;
                    ArrayList<String> result = new ArrayList<>();
                    for (String s : v.getText().toString().split("\n")) {
                        if (!s.isBlank()) {
                            String strip = s.strip();
                            if (!strip.isEmpty()) {
                                result.add(strip);
                            }
                        }
                    }
                    replaceText(v, String.join("\n", result));
                    if (!Objects.equals(get(), result)) {
                        set(result);
                        info.view().setText(this.get().size() + " strings");
                    }
                }
            });

            {
                int dp5 = base.dp(5);
                ShapeDrawable drawable = new ShapeDrawable();
                drawable.setShape(ShapeDrawable.RECTANGLE);
                drawable.setColor(EDIT_COLOR);
                drawable.setStroke(base.dp(1), EDGE_SIDES_COLOR);
                drawable.setPadding(dp5, dp5, dp5, dp5);
                drawable.setCornerRadius(base.dp(5));
                textBox.bg(drawable);
            }
            textBox.params().v_wrap_content();
            textBox.view().setMinHeight(base.dp(75));
            textBox.view().setMinimumWidth(base.dp(550));
            textBox.params().margin(base.dp(10), 0, 0, 0);

            hScroll.view().addView(textBox.build());
            hScroll.params().h_match_parent().v_wrap_content();

            vScroll.params().margin(0, dp3, 0, dp3).h_match_parent().height(base.dp(75));
            vScroll.view().setLayoutParams(vScroll.params().build());
            vScroll.view().addView(hScroll.build());

            if (boxExpanded)
                base.add(vScroll.view());
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
        return new View[]{mBar, mImg, mTextBox};
    }

    @Override
    public JsonObject save(JsonObject tag) {
        JsonArray valueTag = new JsonArray();
        for (int i = 0; i < this.value.size(); i++) {
            valueTag.add(get().get(i));
        }
        tag.add("value", valueTag);
        tag.addProperty("expand", this.boxExpanded);

        return tag;
    }

    @Override
    public List<String> load(JsonObject tag) {
        get().clear();

        JsonArray valueTag = tag.getAsJsonArray("value");
        for (JsonElement tagI : valueTag) {
            get().add(tagI.getAsString());
        }

        if (tag.has("expand")) this.boxExpanded = tag.get("expand").getAsBoolean();

        return get();
    }

    @Override
    public void resetImpl() {
        value = new ArrayList<>(defaultValue);
    }

    public static class Builder extends SettingBuilder<Builder, List<String>, StringListSetting> {
        public Builder() {
            super(new ArrayList<>(0));
        }

        public Builder defaultValue(String... defaults) {
            return defaultValue(defaults != null ? Arrays.asList(defaults) : new ArrayList<>());
        }

        @Override
        public StringListSetting build() {
            return new StringListSetting(name, description, defaultValue, onChanged, onModuleActivated, visible);
        }
    }
}
