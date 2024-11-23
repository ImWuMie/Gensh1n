package dev.undefinedteam.gensh1n.settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.gui.builders.ViewBuilder;
import dev.undefinedteam.gensh1n.gui.frags.GMainGui;
import dev.undefinedteam.gensh1n.gui.builders.LayoutBuilder;
import dev.undefinedteam.gensh1n.gui.frags.music.MusicFragment;
import icyllis.modernui.animation.ObjectAnimator;
import icyllis.modernui.animation.TimeInterpolator;
import icyllis.modernui.core.Context;
import icyllis.modernui.graphics.drawable.ShapeDrawable;
import icyllis.modernui.graphics.drawable.StateListDrawable;
import icyllis.modernui.util.StateSet;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static dev.undefinedteam.gensh1n.gui.frags.GMainGui.ARROW_RIGHT_IMAGE;
import static dev.undefinedteam.gensh1n.gui.frags.GMainGui.BACKGROUND_COLOR;
import static icyllis.modernui.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class EnumSetting<T extends Enum<?>> extends Setting<T> {
    private final T[] values;

    private final List<String> suggestions;

    public final Map<T, SettingGroup> childSettings = new HashMap<>();

    public EnumSetting(String name, String description, T defaultValue, Consumer<T> onChanged, Consumer<Setting<T>> onModuleActivated, IVisible visible) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);

        values = (T[]) defaultValue.getClass().getEnumConstants();
        suggestions = new ArrayList<>(values.length);
        for (T value : values) suggestions.add(value.toString());
    }

    public void registerChild(T val, SettingGroup settings) {
        this.childSettings.put(val, settings);
    }

    public boolean childHas(String name) {
        return this.childSettings.keySet().stream().map(c -> c.toString()).anyMatch(name::equals);
    }

    public T childGet(String name) {
        for (T t : childSettings.keySet()) {
            if (t.toString().equals(name)) return t;
        }
        return null;
    }

    @Override
    protected T parseImpl(String str) {
        for (T possibleValue : values) {
            if (str.equalsIgnoreCase(possibleValue.toString())) return possibleValue;
        }

        return null;
    }

    private LinearLayout mLayout;

    private LinearLayout mBar, mChild, mCLLayout;
    private ImageButton mImg;

    private Spinner mDropdown;

    @Override
    public View createView(Context context, LinearLayout view) {
        final boolean black = this.blackColor;
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

        var children = LayoutBuilder.newLinerBuilder(context);
        this.mChild = children.layout();

        {
            var layout = LayoutBuilder.newLinerBuilder(context);
            layout.hOrientation().vGravity(Gravity.CENTER_VERTICAL);
            this.mCLLayout = layout.layout();

            mDropdown = new Spinner(context);
            mDropdown.setGravity(Gravity.END);
            mDropdown.setMinimumWidth(mLayout.dp(40));
            {
                ShapeDrawable drawable = new ShapeDrawable();
                drawable.setCornerRadius(mLayout.dp(4));
                drawable.setColor(new java.awt.Color(80, 80, 80, 170).getRGB());
                drawable.setInnerRadius(mLayout.dp(2));
                drawable.setShape(ShapeDrawable.RECTANGLE);
                mDropdown.setPopupBackgroundDrawable(drawable);
            }
            mDropdown.setAdapter(new ArrayAdapter<>(context, values) {
                @NotNull
                @Override
                public View getView(int position, @Nullable View convertView, @NotNull ViewGroup parent) {
                    return createViewInner(position, convertView);
                }

                @Nonnull
                private View createViewInner(int position, @javax.annotation.Nullable View convertView) {
                    final TextView tv;

                    if (convertView == null) {
                        tv = new TextView(context);
                    } else {
                        tv = (TextView) convertView;
                    }

                    final T item = getItem(position);
                    if (item instanceof CharSequence) {
                        tv.setText((CharSequence) item);
                    } else tv.setText(String.valueOf(item));

                    tv.setTextSize(14);
                    tv.setTextColor(black ? MusicFragment.FONT_COLOR : GMainGui.FONT_COLOR);
                    tv.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                    final int dp4 = tv.dp(4);
                    tv.setPadding(dp4, dp4, dp4, dp4);

                    return tv;
                }
            });
            mDropdown.setSelection(get().ordinal());
            mDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    T newValue = values[position];
                    if (get() != newValue) {
                        set(newValue);
                        if (currentChild() == null) {
                            mCLLayout.removeView(mImg);
                        } else {
                            if (mCLLayout.findViewById(98988) == null)
                                mCLLayout.addView(mImg, 1);
                        }
                        updateChild(context);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            var params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            params.gravity = Gravity.CENTER_VERTICAL;
            layout.add(mDropdown, params);


            if (currentChild() != null) {
                AtomicBoolean boxExpanded = new AtomicBoolean(currentChild().sectionExpanded);

                var expand = ViewBuilder.wrapLinear(new ImageButton(context));
                this.mImg = expand.view();
                mImg.setId(98988);

                expand.view().setImage(ARROW_RIGHT_IMAGE);
                expand.params().margin(base.dp(4), 0, 0, 0);
                expand.view().setRotation(boxExpanded.get() ? 90 : 0);

                var arrow_o2f_Animator = ObjectAnimator.ofFloat(expand.view(),
                    View.ROTATION, 90, 0);
                var arrow_f2o_Animator = ObjectAnimator.ofFloat(expand.view(),
                    View.ROTATION, 0, 90);
                arrow_o2f_Animator.setInterpolator(TimeInterpolator.DECELERATE_QUINTIC);
                arrow_f2o_Animator.setInterpolator(TimeInterpolator.DECELERATE_QUINTIC);

                expand.view().setOnClickListener((__) -> {
                    if (boxExpanded.get()) {
                        arrow_o2f_Animator.start();
                        base.layout().post(() -> base.layout().removeView(mChild));
                    }

                    boxExpanded.set(!boxExpanded.get());
                    currentChild().sectionExpanded = !currentChild().sectionExpanded;

                    if (boxExpanded.get()) {
                        if (arrow_o2f_Animator.isRunning()) {
                            arrow_o2f_Animator.cancel();
                        }
                        base.layout().post(() ->
                            {
                                if (base.layout().findViewById(778) == null) {
                                    base.layout().addView(mChild);
                                    updateChild(context);
                                }
                            }
                        );
                        arrow_f2o_Animator.start();
                    }

                    expand.view().setRotation(boxExpanded.get() ? 90 : 0);
                });

                {
                    StateListDrawable background = new StateListDrawable();
                    ShapeDrawable drawable = new ShapeDrawable();
                    drawable.setShape(ShapeDrawable.RECTANGLE);
                    drawable.setColor(BACKGROUND_COLOR);
                    drawable.setPadding(dp3, dp3, dp3, dp3);
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
            base.add(bar.build());
        }

        {
            var childLayout = LayoutBuilder.newLinerBuilder(context);
            this.mChild = childLayout.layout();
            this.mChild.setId(778);
            childLayout.params().h_match_parent().v_wrap_content();
            updateChild(context);

            base.add(childLayout.build());
        }

        bar.params()
            .h_match_parent()
            .v_wrap_content();
        base.params()
            .gravity(Gravity.CENTER)
            .margin(dp6, 0, dp6, 0)
            .h_match_parent()
            .v_wrap_content();
        return base.build();
    }

    private void updateChild(Context context) {
        var base = LayoutBuilder.newLinerBuilder(context);
        var group = currentChild();

        if (group != null) {
            int id = 0;
            for (Setting<?> setting : group.settings) {
                View view = setting.createView(context, base.layout());
                if (view != null) {
                    view.setId(id + (114 * 514));
                    base.add(view);
                    view.post(() -> setting.checkVisible0(context, view));
                    id++;
                }
            }
            base.params().margin(base.dp(3), 0, 0, 0)
                .h_match_parent().v_wrap_content();
        }
        this.mChild.removeAllViews();
        this.mChild.addView(base.build());
    }

    private SettingGroup currentChild() {
        return childSettings.getOrDefault(get(), null);
    }

    @Override
    protected ViewGroup layouts(Context context) {
        return mLayout;
    }

    @Override
    protected View[] children(Context context) {
        return new View[]{mBar,mChild, mDropdown};
    }

    @Override
    protected boolean isValueValid(T value) {
        return true;
    }

    @Override
    public List<String> getSuggestions() {
        return suggestions;
    }

    @Override
    public JsonObject save(JsonObject tag) {
        tag.addProperty("value", get().toString());
        var childArray = new JsonArray();
        for (Map.Entry<T, SettingGroup> e : this.childSettings.entrySet()) {
            var childObj = new JsonObject();
            childObj.addProperty("name", e.getKey().toString());
            childObj.add("data", e.getValue().toTag());
            childArray.add(childObj);
        }
        tag.add("child_setting", childArray);

        return tag;
    }

    @Override
    public T load(JsonObject tag) {
        parse(tag.get("value").getAsString());
        if (tag.has("child_setting")) {
            var childArray = tag.getAsJsonArray("child_setting");
            for (JsonElement jsonElement : childArray) {
                var childObj = jsonElement.getAsJsonObject();
                var name = childObj.get("name").getAsString();
                if (childHas(name)) {
                    var val = childGet(name);
                    this.childSettings.get(val).fromTag(childObj.getAsJsonObject("data"));
                }
            }

        }

        return get();
    }

    public static class Builder<T extends Enum<?>> extends SettingBuilder<Builder<T>, T, EnumSetting<T>> {
        public Builder() {
            super(null);
        }

        @Override
        public EnumSetting<T> build() {
            return new EnumSetting<>(name, description, defaultValue, onChanged, onModuleActivated, visible);
        }
    }
}
