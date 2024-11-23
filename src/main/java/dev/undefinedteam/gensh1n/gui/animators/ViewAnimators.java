package dev.undefinedteam.gensh1n.gui.animators;

import icyllis.modernui.util.FloatProperty;
import icyllis.modernui.view.View;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;



@StringEncryption
@ControlFlowObfuscation
public class ViewAnimators {
    public static final FloatProperty<View> ALPHA_100 = new FloatProperty<>("alpha100") {
        @Override
        public void setValue(View object, float value) {
            object.setAlpha(value / 100.0f);
        }

        @Override
        public Float get(View object) {
            return object.getAlpha() * 100.0f;
        }
    };

    public static final FloatProperty<View> ALPHA_255 = new FloatProperty<>("alpha255") {
        @Override
        public void setValue(View object, float value) {
            object.setAlpha(value / 255.0f);
        }

        @Override
        public Float get(View object) {
            return object.getAlpha() * 255.0f;
        }
    };

    public static final FloatProperty<View> SCALE_XY = new FloatProperty<>("scale_xy") {
        @Override
        public void setValue(View object, float value) {
            object.setScaleX(value);
            object.setScaleY(value);
        }

        @Override
        public Float get(View object) {
            return Math.max(object.getScaleX(),object.getScaleY());
        }
    };
}
