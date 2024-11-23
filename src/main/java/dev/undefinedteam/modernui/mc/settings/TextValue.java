package dev.undefinedteam.modernui.mc.settings;

import java.util.function.Consumer;

public class TextValue extends Value<String> {
    public TextValue(String name, String defaultValue) {
        super(name, defaultValue);
    }

    public TextValue(String name, String defaultValue, Consumer<String> consumer) {
        super(name, defaultValue, consumer);
    }

    public TextValue(String name, String defaultValue, IVisible visible) {
        super(name, defaultValue, visible);
    }

    public TextValue(String name, String defaultValue, IVisible visible, Consumer<String> consumer) {
        super(name, defaultValue, visible, consumer);
    }

    @Override
    protected String parseImpl(String str) {
        return str;
    }

    @Override
    protected boolean isValueValid(String value) {
        return true;
    }
}
