package dev.undefinedteam.gensh1n.settings;

import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.gui.builders.LayoutBuilder;
import dev.undefinedteam.gensh1n.gui.builders.ViewBuilder;
import dev.undefinedteam.gensh1n.gui.frags.GMainGui;
import dev.undefinedteam.gensh1n.gui.frags.music.MusicFragment;
import dev.undefinedteam.modernui.mc.ui.ThemeControl;
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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.undefinedteam.gensh1n.gui.frags.GMainGui.*;

public class FolderFileSetting extends Setting<File> {
    private Consumer<File> selectAction;
    private Predicate<Path> validTest;

    private boolean sectionExpand = false;

    private FolderFileSetting(String name, String description, File defaultValue, Consumer<File> onSelect, Consumer<File> onChanged, Consumer<Setting<File>> onModuleActivated, IVisible visible, Predicate<Path> valid) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);
        this.selectAction = onSelect;
    }

    public FolderFileSetting onSelect(Consumer<File> onSelect) {
        this.selectAction = onSelect;
        return this;
    }

    public FolderFileSetting setValid(Predicate<Path> supplier) {
        this.validTest = supplier;
        return this;
    }

    private LinearLayout mLayout;
    private LinearLayout mBar, mChild;
    private ImageButton mImg;
    private TextView mText;

    private File currentSelect;

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
        bar.params()
            .h_match_parent()
            .v_wrap_content();

        var children = LayoutBuilder.newLinerBuilder(context);
        this.mChild = children.layout();

        var layout = LayoutBuilder.newLinerBuilder(context);
        layout.hOrientation().vGravity(Gravity.CENTER_VERTICAL);

        var text = ViewBuilder.wrapLinear(new TextView(context));
        this.mText = text.view();
        text.view().setText(currentSelect == null ? "None" : currentSelect.getName());
        text.view().setTextColor(black ? 0xFF000000 : 0xFFFFFFFF);
        layout.add(text.build());

        {
            var expand = ViewBuilder.wrapLinear(new ImageButton(context));
            this.mImg = expand.view();
            mImg.setId(98988);

            expand.view().setImage(ARROW_RIGHT_IMAGE);
            expand.params().margin(base.dp(4), 0, 0, 0);
            expand.view().setRotation(sectionExpand ? 90 : 0);

            var arrow_o2f_Animator = ObjectAnimator.ofFloat(expand.view(),
                View.ROTATION, 90, 0);
            var arrow_f2o_Animator = ObjectAnimator.ofFloat(expand.view(),
                View.ROTATION, 0, 90);
            arrow_o2f_Animator.setInterpolator(TimeInterpolator.DECELERATE_QUINTIC);
            arrow_f2o_Animator.setInterpolator(TimeInterpolator.DECELERATE_QUINTIC);

            if (sectionExpand) {
                base.add(mChild);
                loadFiles(context);
            }

            expand.view().setOnClickListener((__) -> {
                if (sectionExpand) {
                    arrow_o2f_Animator.start();
                    base.layout().post(() -> base.layout().removeView(mChild));
                }

                sectionExpand = !sectionExpand;

                if (sectionExpand) {
                    if (arrow_o2f_Animator.isRunning()) {
                        arrow_o2f_Animator.cancel();
                    }
                    base.layout().post(() ->
                        {
                            if (base.layout().findViewById(778) == null) {
                                base.layout().addView(mChild);
                                loadFiles(context);
                            }
                        }
                    );
                    arrow_f2o_Animator.start();
                }

                expand.view().setRotation(sectionExpand ? 90 : 0);
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

        bar.add(layout.build());
        base.add(bar.build());
        base.params()
            .gravity(Gravity.CENTER)
            .margin(dp6, 0, dp6, 0)
            .h_match_parent()
            .v_wrap_content();
        return base.build();
    }

    public void loadFiles(Context context) {
        mChild.removeAllViews();
        var child = LayoutBuilder.wrapLinerLayout(mChild);
        child.params().margin(child.dp(7), 0, 0, 0).h_match_parent().height(child.dp(250));
        var scroll = ViewBuilder.wrapLinear(new ScrollView(context));
        scroll.params().v_match_parent().h_match_parent();

        child.add(scroll.build());
        var base = LayoutBuilder.newLinerBuilder(context);
        base.vOrientation().hGravity(Gravity.CENTER_HORIZONTAL).params().h_match_parent().v_wrap_content();
        try {
            var list = Files.list(defaultValue.toPath()).toList();
             if (list.isEmpty()) {
                 base.add(getEmpty(base, context).build());
             } else {
                 for (Path path : list) {
                     if (validTest == null || validTest.test(path)) {
                         var layout = buildLayout(context, path);
                         base.add(layout.build());
                     }
                 }
             }
        } catch (Exception e) {
            e.printStackTrace();
        }
        scroll.view().addView(base.build());
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

    private LayoutBuilder.LinearLayoutBuilder<LinearLayout> buildLayout(Context context, Path path) {
        var base = LayoutBuilder.newLinerBuilder(context);
        base.hOrientation().hGravity(Gravity.START);
        base.params().h_match_parent().v_wrap_content();

        var title = ViewBuilder.wrapLinear(new TextView(context));
        title.view().setText(path.getFileName().toString());
        title.view().setTextColor(blackColor ? MusicFragment.FONT_COLOR : GMainGui.FONT_COLOR);
        title.view().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        title.view().setTextSize(16);
        title.params()
            .weight(1)
            .gravity(Gravity.CENTER_VERTICAL)
            .h_wrap_content().v_wrap_content();

        var button = ViewBuilder.wrapLinear(new Button(context));
        button.view().setText("Select");
        button.view().setSingleLine();
        ThemeControl.addBackground(button.view());
        this.currentSelect = path.toFile();
        button.view().setOnClickListener(__ -> this.selectAction.accept(path.toFile()));
        int dp5 = base.dp(5);
        button.params().margin(dp5, base.dp(2), dp5, base.dp(2)).h_wrap_content().v_wrap_content();
        base.add(title.build()).add(button.build());
        base.params().margin(base.dp(2), base.dp(3), base.dp(2), base.dp(3));
        return base;
    }

    private String getFileLabel(Path file) {
        return file
            .getFileName()
            .toString()
            .replace(".txt", "");
    }

    @Override
    protected ViewGroup layouts(Context context) {
        return mLayout;
    }

    @Override
    protected View[] children(Context context) {
        return new View[]{mBar, mChild, mText};
    }

    @Override
    protected File parseImpl(String str) {
        return null;
    }

    @Override
    protected boolean isValueValid(File value) {
        return true;
    }

    @Override
    protected JsonObject save(JsonObject tag) {
        tag.addProperty("expand", this.sectionExpand);
        return tag;
    }

    @Override
    protected File load(JsonObject tag) {
        this.sectionExpand = tag.get("expand").getAsBoolean();
        return get();
    }

    public static class Builder extends SettingBuilder<Builder, File, FolderFileSetting> {

        private Consumer<File> selectAction;
        private Predicate<Path> validTest;

        public Builder() {
            super(null);
        }

        public Builder onSelect(Consumer<File> onSelect) {
            this.selectAction = onSelect;
            return this;
        }

        public Builder valid(Predicate<Path> onSelect) {
            this.validTest = onSelect;
            return this;
        }

        @Override
        public FolderFileSetting build() {
            return new FolderFileSetting(name, description, defaultValue, selectAction, onChanged, onModuleActivated, visible, validTest);
        }
    }
}
