package dev.undefinedteam.modernui.mc.settings;

import java.util.function.Consumer;

public class IntValue extends Value<Integer> {
    public final int min, max;
    public final int sliderMin, sliderMax;

    public IntValue(String name, int defaultValue, int min, int max) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
        this.sliderMax = 10;
        this.sliderMin = 0;
    }

    public IntValue(String name, int defaultValue, int min, int max, Consumer<Integer> consumer) {
        super(name, defaultValue, consumer);
        this.min = min;
        this.max = max;
        this.sliderMax = 10;
        this.sliderMin = 0;
    }

    public IntValue(String name, int defaultValue, int min, int max, IVisible visible) {
        super(name, defaultValue, visible);
        this.min = min;
        this.max = max;
        this.sliderMax = 10;
        this.sliderMin = 0;
    }

    public IntValue(String name, int defaultValue, int min, int max, IVisible visible, Consumer<Integer> consumer) {
        super(name, defaultValue, visible, consumer);
        this.min = min;
        this.max = max;
        this.sliderMax = 10;
        this.sliderMin = 0;
    }

    public IntValue(String name, int defaultValue, int min, int max, int sliderMin, int sliderMax) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
        this.sliderMax = sliderMax;
        this.sliderMin = sliderMin;
    }

    public IntValue(String name, int defaultValue, int min, int max, int sliderMin, int sliderMax, Consumer<Integer> consumer) {
        super(name, defaultValue, consumer);
        this.min = min;
        this.max = max;
        this.sliderMax = sliderMax;
        this.sliderMin = sliderMin;
    }

    public IntValue(String name, int defaultValue, int min, int max, int sliderMin, int sliderMax, IVisible visible) {
        super(name, defaultValue, visible);
        this.min = min;
        this.max = max;
        this.sliderMax = sliderMax;
        this.sliderMin = sliderMin;
    }

    public IntValue(String name, int defaultValue, int min, int max, int sliderMin, int sliderMax, IVisible visible, Consumer<Integer> consumer) {
        super(name, defaultValue, visible, consumer);
        this.min = min;
        this.max = max;
        this.sliderMax = sliderMax;
        this.sliderMin = sliderMin;
    }

    @Override
    protected Integer parseImpl(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    protected boolean isValueValid(Integer value) {
        return true;
    }
}
