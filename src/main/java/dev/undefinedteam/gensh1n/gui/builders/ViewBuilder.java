package dev.undefinedteam.gensh1n.gui.builders;

import icyllis.modernui.graphics.drawable.Drawable;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.LinearLayout;
import icyllis.modernui.widget.RelativeLayout;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;

public abstract class ViewBuilder<V extends View, P extends ViewGroup.LayoutParams> {
    @NativeObfuscation.Inline
    public static <V extends View> LinearViewBuilder<V> wrapLinear(V view) {
        return new LinearViewBuilder<>(view, view.getLayoutParams() == null
            ? ParamsBuilder.newLinerBuilder()
            : ParamsBuilder.newLinerBuilder(new LinearLayout.LayoutParams(view.getLayoutParams())));
    }
    @NativeObfuscation.Inline
    public static <V extends View> RelativeViewBuilder<V> wrapRelative(V view) {
        return new RelativeViewBuilder<>(view, view.getLayoutParams() == null
            ? ParamsBuilder.newRelativeBuilder()
            : ParamsBuilder.newRelativeBuilder(new RelativeLayout.LayoutParams(view.getLayoutParams())));
    }

    protected final V view;
    protected final ParamsBuilder<P> params;

    private ViewBuilder(V view, ParamsBuilder<P> params) {
        this.view = view;
        this.params = params;
    }

    public V view() {
        return view;
    }

    public <S extends ViewBuilder<V, P>> S bg(Drawable drawable) {
        this.view.setBackground(drawable);
        return (S) this;
    }

    public <S extends ViewBuilder<V, P>> S id(int id) {
        this.view.setId(id);
        return (S) this;
    }

    public abstract V build();

    public int dp(float i) {
        return this.view.dp(i);
    }

    public static class LinearViewBuilder<V extends View> extends ViewBuilder<V, LinearLayout.LayoutParams> {

        private LinearViewBuilder(V view, ParamsBuilder.LinearParamsBuilder params) {
            super(view, params);
        }

        public ParamsBuilder.LinearParamsBuilder params() {
            return (ParamsBuilder.LinearParamsBuilder) this.params;
        }

        public ParamsBuilder.RelativeParamsBuilder relative_params() {
            return ParamsBuilder.newRelativeBuilder(new RelativeLayout.LayoutParams(this.params.build()));
        }

        @Override
        public V build() {
            this.view.setLayoutParams(this.params.build());
            return this.view;
        }

        public <S extends ParamsBuilder<RelativeLayout.LayoutParams>> V build(S params) {
            this.view.setLayoutParams(params.build());
            return this.view;
        }
    }

    public static class RelativeViewBuilder<V extends View> extends ViewBuilder<V, RelativeLayout.LayoutParams> {

        private RelativeViewBuilder(V view, ParamsBuilder.RelativeParamsBuilder params) {
            super(view, params);
        }

        public ParamsBuilder.RelativeParamsBuilder params() {
            return (ParamsBuilder.RelativeParamsBuilder) this.params;
        }

        @Override
        public V build() {
            this.view.setLayoutParams(this.params.build());
            return this.view;
        }
    }
}
