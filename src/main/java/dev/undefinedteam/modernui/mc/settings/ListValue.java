package dev.undefinedteam.modernui.mc.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ListValue extends Value<String> {
    private final List<String> suggestions = new ArrayList<>();

    private int index;
    public ListValue(String name, String defaultValue, String... values) {
        super(name, defaultValue);

        suggestions.add(defaultValue);
        for (String value : values) {
            if (!suggestions.contains(value)) {
                suggestions.add(value);
            }
        }

        this.index = this.suggestions.indexOf(get());
    }

    public ListValue(String name, String defaultValue, String[] values, Consumer<String> consumer) {
        super(name, defaultValue, consumer);
        this.suggestions.addAll(Arrays.asList(values));
        if (!suggestions.contains(defaultValue)) {
            suggestions.add(defaultValue);
        }
        this.index = this.suggestions.indexOf(get());
    }

    public ListValue(String name, String defaultValue, String[] values, IVisible visible) {
        super(name, defaultValue, visible);
        this.suggestions.addAll(Arrays.asList(values));
        if (!suggestions.contains(defaultValue)) {
            suggestions.add(defaultValue);
        }
        this.index = this.suggestions.indexOf(get());
    }

    public ListValue(String name, String defaultValue, String[] values, IVisible visible, Consumer<String> consumer) {
        super(name, defaultValue, visible, consumer);
        this.suggestions.addAll(Arrays.asList(values));
        if (!suggestions.contains(defaultValue)) {
            suggestions.add(defaultValue);
        }
        this.index = this.suggestions.indexOf(get());
    }

    @Override
    protected String parseImpl(String str) {
        for (String possibleValue : suggestions) {
            if (str.equalsIgnoreCase(possibleValue)) return possibleValue;
        }

        return null;
    }

    @Override
    protected boolean isValueValid(String value) {
        return true;
    }

    @Override
    public List<String> getSuggestions() {
        return suggestions;
    }

    @Override
    public boolean set(String value) {
        if (suggestions.contains(value)) {
            this.index = suggestions.indexOf(value);
            return super.set(value);
        }
        return false;
    }

    public void step() {
        if (this.index < this.suggestions.size() - 1) {
            ++this.index;
        } else {
            this.index = 0;
        }

        set(!suggestions.get(index).equals(get()) ? suggestions.get(index) : get());
    }
}
