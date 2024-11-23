package dev.undefinedteam.modernui.mc.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ListObjectValue<T extends String> extends Value<List<T>> {
    private final List<T> objects = new ArrayList<>();

    public ListObjectValue(String name, List<T> defaultValue, T... values) {
        super(name, defaultValue);

        objects.addAll(defaultValue);
        for (T value : values) {
            if (!objects.contains(value)) {
                objects.add(value);
            }
        }
    }

    public ListObjectValue(String name, List<T> defaultValue, T[] values, Consumer<List<T>> consumer) {
        super(name, defaultValue, consumer);
        this.objects.addAll(Arrays.asList(values));
        defaultValue.forEach((n) -> {
            if (!objects.contains(n)) {
                objects.add(n);
            }
        });
    }

    public ListObjectValue(String name, List<T> defaultValue, T[] values, IVisible visible) {
        super(name, defaultValue, visible);
        this.objects.addAll(Arrays.asList(values));
        defaultValue.forEach((n) -> {
            if (!objects.contains(n)) {
                objects.add(n);
            }
        });
    }

    public ListObjectValue(String name, List<T> defaultValue, T[] values, IVisible visible, Consumer<List<T>> consumer) {
        super(name, defaultValue, visible, consumer);
        this.objects.addAll(Arrays.asList(values));
        defaultValue.forEach((n) -> {
            if (!objects.contains(n)) {
                objects.add(n);
            }
        });
    }

    @Override
    protected List<T> parseImpl(String str) {
      String[] s = str.split("\t");
      return (List<T>) Arrays.asList(s);
    }

    @Override
    protected boolean isValueValid(List<T> value) {
        return true;
    }

    public void set(List<? extends String> strings) {
        super.set((List<T>) strings);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < objects.size(); i++) {
            builder.append(objects.get(i)).append((i == objects.size() - 1) ? "" : "\t");
        }
        return builder.toString();
    }
}
