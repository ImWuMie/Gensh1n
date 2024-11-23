package dev.undefinedteam.modernui.mc.settings;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.function.Consumer;

public class BoolValue extends Value<Boolean> {
    private static final List<String> SUGGESTIONS = ImmutableList.of("true", "false", "toggle");

    public BoolValue(String name, boolean defaultValue) {
        super(name, defaultValue);
    }

    public BoolValue(String name, boolean defaultValue, Consumer<Boolean> consumer) {
        super(name, defaultValue, consumer);
    }

    public BoolValue(String name, boolean defaultValue, IVisible visible) {
        super(name, defaultValue, visible);
    }

    public BoolValue(String name, boolean defaultValue, IVisible visible, Consumer<Boolean> consumer) {
        super(name, defaultValue, visible, consumer);
    }

    @Override
    protected Boolean parseImpl(String str) {
        return str.equalsIgnoreCase("true");
    }

    @Override
    protected boolean isValueValid(Boolean value) {
        return true;
    }

    @Override
    public List<String> getSuggestions() {
        return SUGGESTIONS;
    }
}
