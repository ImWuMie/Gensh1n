package dev.undefinedteam.gclient.data;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtils {
    private static final Gson GSON = newBuilderNoPretty().create();

    public static GsonBuilder custom() {
        return new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                return fieldAttributes.getAnnotation(GsonIgnore.class) != null;
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return aClass.getAnnotation(GsonIgnore.class) != null;
            }
        });
    }

    public static GsonBuilder newBuilder() {
        return custom().setPrettyPrinting();
    }

    public static GsonBuilder newBuilderNoPretty() {
        return custom();
    }
}
