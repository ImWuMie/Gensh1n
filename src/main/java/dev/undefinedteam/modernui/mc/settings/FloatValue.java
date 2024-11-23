package dev.undefinedteam.modernui.mc.settings;

import java.util.function.Consumer;

public class FloatValue extends Value<Float> {
    public final float min, max;
    public final float sliderMin, sliderMax;

    public FloatValue(String name, float defaultValue, float min, float max) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
        this.sliderMax = 10;
        this.sliderMin = 0;
    }

    public FloatValue(String name, float defaultValue, float min, float max, Consumer<Float> consumer) {
        super(name, defaultValue, consumer);
        this.min = min;
        this.max = max;
        this.sliderMax = 10;
        this.sliderMin = 0;
    }

    public FloatValue(String name, float defaultValue, float min, float max, IVisible visible) {
        super(name, defaultValue, visible);
        this.min = min;
        this.max = max;
        this.sliderMax = 10;
        this.sliderMin = 0;
    }

    public FloatValue(String name, float defaultValue, float min, float max, IVisible visible, Consumer<Float> consumer) {
        super(name, defaultValue, visible, consumer);
        this.min = min;
        this.max = max;
        this.sliderMax = 10;
        this.sliderMin = 0;
    }

    public FloatValue(String name, float defaultValue, float min, float max, float sliderMin, float sliderMax) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
        this.sliderMax = sliderMax;
        this.sliderMin = sliderMin;
    }

    public FloatValue(String name, float defaultValue, float min, float max, float sliderMin, float sliderMax, Consumer<Float> consumer) {
        super(name, defaultValue, consumer);
        this.min = min;
        this.max = max;
        this.sliderMax = sliderMax;
        this.sliderMin = sliderMin;
    }

    public FloatValue(String name, float defaultValue, float min, float max, float sliderMin, float sliderMax, IVisible visible) {
        super(name, defaultValue, visible);
        this.min = min;
        this.max = max;
        this.sliderMax = sliderMax;
        this.sliderMin = sliderMin;
    }

    public FloatValue(String name, float defaultValue, float min, float max, float sliderMin, float sliderMax, IVisible visible, Consumer<Float> consumer) {
        super(name, defaultValue, visible, consumer);
        this.min = min;
        this.max = max;
        this.sliderMax = sliderMax;
        this.sliderMin = sliderMin;
    }

    @Override
    protected Float parseImpl(String str) {
        try {
            return Float.parseFloat(str);
        } catch (Exception e) {
            return 0.0f;
        }
    }

    @Override
    protected boolean isValueValid(Float value) {
        return true;
    }
}
