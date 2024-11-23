package dev.undefinedteam.modernui.mc.settings;

import java.util.function.Consumer;

public class DoubleValue extends Value<Double> {
    public final double min, max;
    public final double sliderMin, sliderMax;

    public DoubleValue(String name, double defaultValue, double min, double max) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
        this.sliderMax = 10;
        this.sliderMin = 0;
    }

    public DoubleValue(String name, double defaultValue, double min, double max, Consumer<Double> consumer) {
        super(name, defaultValue, consumer);
        this.min = min;
        this.max = max;
        this.sliderMax = 10;
        this.sliderMin = 0;
    }

    public DoubleValue(String name, double defaultValue, double min, double max, IVisible visible) {
        super(name, defaultValue, visible);
        this.min = min;
        this.max = max;
        this.sliderMax = 10;
        this.sliderMin = 0;
    }

    public DoubleValue(String name, double defaultValue, double min, double max, IVisible visible, Consumer<Double> consumer) {
        super(name, defaultValue, visible, consumer);
        this.min = min;
        this.max = max;
        this.sliderMax = 10;
        this.sliderMin = 0;
    }

    public DoubleValue(String name, double defaultValue, double min, double max, double sliderMin, double sliderMax) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
        this.sliderMax = sliderMax;
        this.sliderMin = sliderMin;
    }

    public DoubleValue(String name, double defaultValue, double min, double max, double sliderMin, double sliderMax, Consumer<Double> consumer) {
        super(name, defaultValue, consumer);
        this.min = min;
        this.max = max;
        this.sliderMax = sliderMax;
        this.sliderMin = sliderMin;
    }

    public DoubleValue(String name, double defaultValue, double min, double max, double sliderMin, double sliderMax, IVisible visible) {
        super(name, defaultValue, visible);
        this.min = min;
        this.max = max;
        this.sliderMax = sliderMax;
        this.sliderMin = sliderMin;
    }

    public DoubleValue(String name, double defaultValue, double min, double max, double sliderMin, double sliderMax, IVisible visible, Consumer<Double> consumer) {
        super(name, defaultValue, visible, consumer);
        this.min = min;
        this.max = max;
        this.sliderMax = sliderMax;
        this.sliderMin = sliderMin;
    }

    @Override
    protected Double parseImpl(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0.0;
        }
    }

    @Override
    protected boolean isValueValid(Double value) {
        return true;
    }
}
