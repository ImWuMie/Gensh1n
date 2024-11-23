package dev.undefinedteam.gensh1n.gui.builders;

import icyllis.arc3d.core.Rect2fc;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.LinearLayout;
import icyllis.modernui.widget.RelativeLayout;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import static icyllis.modernui.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static icyllis.modernui.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public abstract class ParamsBuilder<P extends ViewGroup.LayoutParams> {
    @NativeObfuscation.Inline
    public static LinearParamsBuilder newLinerBuilder() {
        return new LinearParamsBuilder(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
    }
    @NativeObfuscation.Inline
    public static RelativeParamsBuilder newRelativeBuilder() {
        return new RelativeParamsBuilder(new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
    }
    @NativeObfuscation.Inline
    public static LinearParamsBuilder newLinerBuilder(LinearLayout.LayoutParams params) {
        return new LinearParamsBuilder(params);
    }
    @NativeObfuscation.Inline
    public static RelativeParamsBuilder newRelativeBuilder(RelativeLayout.LayoutParams params) {
        return new RelativeParamsBuilder(params);
    }

    protected P params;

    private ParamsBuilder(P params) {
        this.params = params;
    }

    public <S extends ParamsBuilder<P>> S set(P params) {
        this.params = params;
        return (S) this;
    }

    public <S extends ParamsBuilder<P>> S h_match_parent() {
        return this.width(MATCH_PARENT);
    }

    public <S extends ParamsBuilder<P>> S v_match_parent() {
        return this.height(MATCH_PARENT);
    }

    public <S extends ParamsBuilder<P>> S h_wrap_content() {
        return this.width(WRAP_CONTENT);
    }

    public <S extends ParamsBuilder<P>> S v_wrap_content() {
        return this.height(WRAP_CONTENT);
    }

    public <S extends ParamsBuilder<P>> S height(int height) {
        this.params.height = height;
        return (S) this;
    }

    public <S extends ParamsBuilder<P>> S width(int width) {
        this.params.width = width;
        return (S) this;
    }

    public P params() {
        return this.params;
    }

    public abstract P build();

    public abstract static class MarginParamsBuilder<P extends ViewGroup.MarginLayoutParams> extends ParamsBuilder<P> {

        private MarginParamsBuilder(P params) {
            super(params);
        }

        public <S extends MarginParamsBuilder<P>> S margin(int left, int top, int right, int bottom) {
            this.params.setMargins(left, top, right, bottom);
            return (S) this;
        }

        public <S extends MarginParamsBuilder<P>> S relativeMargin(int start, int top, int end, int bottom) {
            this.params.setMarginsRelative(start, top, end, bottom);
            return (S) this;
        }
    }

    public static class LinearParamsBuilder extends MarginParamsBuilder<LinearLayout.LayoutParams> {

        private LinearParamsBuilder(LinearLayout.LayoutParams params) {
            super(params);
        }

        public LinearParamsBuilder gravity(int gravityMask) {
            this.params.gravity = gravityMask;
            return this;
        }

        public LinearParamsBuilder weight(int w) {
            this.params.weight = w;
            return this;
        }

        @Override
        public LinearLayout.LayoutParams build() {
            return params;
        }
    }

    public static class RelativeParamsBuilder extends MarginParamsBuilder<RelativeLayout.LayoutParams> {
        private boolean parent = false;


        private RelativeParamsBuilder(RelativeLayout.LayoutParams params) {
            super(params);
        }

        public RelativeParamsBuilder rule(int... rules) {
            for (int rule : rules) {
                this.params.addRule(rule);
            }
            return this;
        }

        @Override
        public RelativeLayout.LayoutParams build() {
            return params;
        }

        public RelativeParamsBuilder parent_mode() {
            this.parent = true;
            return this;
        }

        public RelativeParamsBuilder child_mode() {
            this.parent = false;
            return this;
        }

        public RelativeParamsBuilder center() {
            return rule(RelativeLayout.CENTER_IN_PARENT);
        }

        public RelativeParamsBuilder vCenter() {
            return rule(RelativeLayout.CENTER_VERTICAL);
        }

        public RelativeParamsBuilder hCenter() {
            return rule(RelativeLayout.CENTER_HORIZONTAL);
        }

        public RelativeParamsBuilder left() {
            return rule(parent ? RelativeLayout.ALIGN_PARENT_LEFT : RelativeLayout.ALIGN_LEFT);
        }

        public RelativeParamsBuilder top() {
            return rule(parent ? RelativeLayout.ALIGN_PARENT_TOP : RelativeLayout.ALIGN_TOP);
        }

        public RelativeParamsBuilder right() {
            return rule(parent ? RelativeLayout.ALIGN_PARENT_RIGHT : RelativeLayout.ALIGN_RIGHT);
        }

        public RelativeParamsBuilder bottom() {
            return rule(parent ? RelativeLayout.ALIGN_PARENT_BOTTOM : RelativeLayout.ALIGN_BOTTOM);
        }

        public RelativeParamsBuilder start() {
            return rule(parent ? RelativeLayout.ALIGN_PARENT_START : RelativeLayout.ALIGN_START);
        }

        public RelativeParamsBuilder end() {
            return rule(parent ? RelativeLayout.ALIGN_PARENT_END : RelativeLayout.ALIGN_END);
        }
    }
}
