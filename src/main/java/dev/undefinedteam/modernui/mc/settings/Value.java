package dev.undefinedteam.modernui.mc.settings;

import dev.undefinedteam.gensh1n.utils.Utils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class Value<T> {
    public String name, title;

    @Getter
    private String[] desc;
    private final IVisible visible;

    public double valueRenderX,valueRenderY;

    protected final T defaultValue;
    protected T value;

    public T getDefaultValue() {
        return defaultValue;
    }

    private final Consumer<T> onChanged;

    private static final List<String> NO_SUGGESTIONS = new ArrayList<>(0);

    public Value(String name, T defaultValue) {
        this(name, defaultValue, null, null);
    }

    public Value(String name, T defaultValue, Consumer<T> consumer) {
        this(name, defaultValue, null, consumer);
    }

    public Value(String name, T defaultValue, IVisible visible) {
        this(name, defaultValue, visible, null);
    }

    public Value(String name, T defaultValue, IVisible visible, Consumer<T> consumer) {
        this.name = name;
        this.title = Utils.nameToTitle(name);
        this.visible = visible;
        this.defaultValue = defaultValue;
        this.onChanged = consumer;
        this.value = defaultValue;
    }

    public <S extends Value<T>> S desc(String desc) {
        this.desc = new String[] {desc};
        return (S) this;
    }

    public <S extends Value<T>> S desc(String... s) {
        this.desc = s;
        return (S) this;
    }

    public <S extends Value<T>> S define(String name,T value) {
        this.name = name;
        this.title = Utils.nameToTitle(name);
        this.set(value);
        return (S) this;
    }
    public T get() {
        return value;
    }

    public boolean set(T value) {
        if (!isValueValid(value)) return false;
        this.value = value;
        onChanged();
        return true;
    }

    public boolean isVisible() {
        return (visible == null || visible.isVisible());
    }

    public void onChanged() {
        if (onChanged != null) onChanged.accept(value);
    }

    public boolean parse(String str) {
        T newValue = parseImpl(str);

        if (newValue != null) {
            if (isValueValid(newValue)) {
                value = newValue;
                onChanged();
            }
        }

        return newValue != null;
    }

    public List<String> getSuggestions() {
        return NO_SUGGESTIONS;
    }

    protected abstract T parseImpl(String str);

    protected abstract boolean isValueValid(T value);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Value<?> setting = (Value<?>) o;
        return Objects.equals(name, setting.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
