package dev.undefinedteam.gensh1n.gui.builders;

import icyllis.modernui.core.Context;
import icyllis.modernui.graphics.drawable.Drawable;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.LinearLayout;
import icyllis.modernui.widget.RadioGroup;
import icyllis.modernui.widget.RelativeLayout;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;

import java.util.function.Consumer;

import static icyllis.modernui.view.ViewGroup.LayoutParams.*;

public abstract class LayoutBuilder<L extends ViewGroup, P extends ViewGroup.LayoutParams> {
    @NativeObfuscation.Inline
    public static LinearLayoutBuilder<LinearLayout> newLinerBuilder(Context context) {
        return new LinearLayoutBuilder<>(new LinearLayout(context), new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
    }
    @NativeObfuscation.Inline
    public static RadioGroupBuilder newRadioGroupBuilder(Context context) {
        return new RadioGroupBuilder(new RadioGroup(context), new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
    }
    @NativeObfuscation.Inline
    public static RelativeLayoutBuilder newRelativeBuilder(Context context) {
        return new RelativeLayoutBuilder(new RelativeLayout(context), new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
    }
    @NativeObfuscation.Inline
    public static LinearLayoutBuilder<LinearLayout> wrapLinerLayout(LinearLayout layout) {
        return new LinearLayoutBuilder<>(layout, new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
    }
    @NativeObfuscation.Inline
    public static RadioGroupBuilder wrapRadioGroup(RadioGroup group) {
        return new RadioGroupBuilder(group, new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
    }
    @NativeObfuscation.Inline
    public static RelativeLayoutBuilder wrapRelativeLayout(RelativeLayout layout) {
        return new RelativeLayoutBuilder(layout, new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
    }

    protected final L layout;
    protected final ParamsBuilder<P> params;

    private LayoutBuilder(L layout, ParamsBuilder<P> params) {
        this.layout = layout;
        this.params = params;
    }

    public <S extends LayoutBuilder<L, P>> S id(int id) {
        this.layout.setId(id);
        return (S) this;
    }

    public <S extends LayoutBuilder<L, P>> S add(View view) {
        layout.addView(view);
        return (S) this;
    }

    public <S extends LayoutBuilder<L, P>> S add(View view, int index) {
        layout.addView(view, index);
        return (S) this;
    }

    public <S extends LayoutBuilder<L, P>> S add(View view, P params) {
        layout.addView(view, params);
        return (S) this;
    }

    public <S extends LayoutBuilder<L, P>> S add(View view, int index, P params) {
        layout.addView(view, index, params);
        return (S) this;
    }

    public <S extends LayoutBuilder<L, P>> S add(View view, int width, int height) {
        layout.addView(view, width, height);
        return (S) this;
    }

    public L layout() {
        return this.layout;
    }

    public int dp(float value) {
        return this.layout.dp(value);
    }

    public abstract L build();

    public <S extends LayoutBuilder<L, P>> S tooltip(CharSequence text) {
        layout.setTooltipText(text);
        return (S) this;
    }

    public static class RadioGroupBuilder extends LinearLayoutBuilder<RadioGroup> {

        private RadioGroupBuilder(RadioGroup layout, LinearLayout.LayoutParams params) {
            super(layout, params);
        }

        public void onCheck(Consumer<Integer> action) {
            this.layout.setOnCheckedChangeListener((group, id) -> {
                action.accept(id);
            });
        }
    }


    public static class LinearLayoutBuilder<T extends LinearLayout> extends LayoutBuilder<T, LinearLayout.LayoutParams> {


        private LinearLayoutBuilder(T layout, LinearLayout.LayoutParams params) {
            super(layout, ParamsBuilder.newLinerBuilder(params));
        }

        public LinearLayoutBuilder vOrientation() {
            layout.setOrientation(LinearLayout.VERTICAL);
            return this;
        }

        public LinearLayoutBuilder hOrientation() {
            layout.setOrientation(LinearLayout.HORIZONTAL);
            return this;
        }

        public LinearLayoutBuilder gravity(int gravity) {
            layout.setGravity(gravity);
            return this;
        }

        public LinearLayoutBuilder hGravity(int gravity) {
            layout.setHorizontalGravity(gravity);
            return this;
        }

        public LinearLayoutBuilder vGravity(int gravity) {
            layout.setVerticalGravity(gravity);
            return this;
        }

        public LinearLayoutBuilder bg(Drawable drawable) {
            layout.setBackground(drawable);
            return this;
        }

        public LinearLayoutBuilder fg(Drawable drawable) {
            layout.setForeground(drawable);
            return this;
        }

        public LinearLayoutBuilder padding(int left, int top, int right, int bottom) {
            layout.setPadding(left, top, right, bottom);
            return this;
        }

        public ParamsBuilder.LinearParamsBuilder params() {
            return (ParamsBuilder.LinearParamsBuilder) this.params;
        }

        public ParamsBuilder.RelativeParamsBuilder relative_params() {
            return ParamsBuilder.newRelativeBuilder(new RelativeLayout.LayoutParams(this.params.build()));
        }

        public T build() {
            this.layout.setLayoutParams(params.build());
            return this.layout;
        }

        public <S extends ParamsBuilder<RelativeLayout.LayoutParams>> T build(S params) {
            this.layout.setLayoutParams(params.build());
            return this.layout;
        }
    }

    public static class RelativeLayoutBuilder extends LayoutBuilder<RelativeLayout, RelativeLayout.LayoutParams> {


        private RelativeLayoutBuilder(RelativeLayout layout, RelativeLayout.LayoutParams params) {
            super(layout, ParamsBuilder.newRelativeBuilder(params));
        }

        public RelativeLayoutBuilder gravity(int gravity) {
            layout.setGravity(gravity);
            return this;
        }

        public RelativeLayoutBuilder hGravity(int gravity) {
            layout.setHorizontalGravity(gravity);
            return this;
        }

        public RelativeLayoutBuilder vGravity(int gravity) {
            layout.setVerticalGravity(gravity);
            return this;
        }

        public RelativeLayoutBuilder bg(Drawable drawable) {
            layout.setBackground(drawable);
            return this;
        }

        public ParamsBuilder.RelativeParamsBuilder params() {
            return (ParamsBuilder.RelativeParamsBuilder) this.params;
        }

        public RelativeLayout build() {
            this.layout.setLayoutParams(params.build());
            return this.layout;
        }
    }
}
