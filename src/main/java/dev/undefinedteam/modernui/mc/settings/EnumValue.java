package dev.undefinedteam.modernui.mc.settings;

import com.google.common.collect.Lists;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EnumValue<T extends Enum<?>> extends Value<T> {
    private T[] values;

    private List<String> suggestions;


    private int index;

    public EnumValue(String name, T defaultValue) {
        super(name, defaultValue);
        loadSuggests(defaultValue);
    }

    public EnumValue(String name, T defaultValue, Consumer<T> consumer) {
        super(name, defaultValue, consumer);
        loadSuggests(defaultValue);
    }

    public EnumValue(String name, T defaultValue, IVisible visible) {
        super(name, defaultValue, visible);
        loadSuggests(defaultValue);
    }

    public EnumValue(String name, T defaultValue, IVisible visible, Consumer<T> consumer) {
        super(name, defaultValue, visible, consumer);
        loadSuggests(defaultValue);
    }

    @Override
    protected T parseImpl(String str) {
        for (T possibleValue : values) {
            if (str.equals(possibleValue.toString())) return possibleValue;
        }

        return null;
    }

    public void set(String name) {
        this.set(parseImpl(name));
    }

    @Override
    protected boolean isValueValid(T value) {
        return true;
    }

    @Override
    public List<String> getSuggestions() {
        return suggestions;
    }

    public List<T> getAllValue() {
        return Lists.newArrayList(values);
    }

    private void loadSuggests(T v) {
        try {
            values = (T[]) v.getClass().getMethod("values").invoke(null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        suggestions = new ArrayList<>(values.length);
        for (T value : values) suggestions.add(value.toString());

        this.index = this.suggestions.indexOf(get().toString());
    }

    @Override
    public boolean set(T value) {
        this.index = suggestions.indexOf(value.toString());
        return super.set(value);
    }

    public void step() {
        if (this.index < this.values.length - 1) {
            ++this.index;
        } else {
            this.index = 0;
        }

        set(values[index]);
    }
}
