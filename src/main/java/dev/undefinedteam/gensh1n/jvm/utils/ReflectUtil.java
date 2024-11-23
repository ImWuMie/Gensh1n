package dev.undefinedteam.gensh1n.jvm.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
public class ReflectUtil {
    public static <T> void setAll(T old, T t) throws NoSuchFieldException, IllegalAccessException {
        for (Field oldField : old.getClass().getDeclaredFields()) {
            oldField.setAccessible(true);
            Field declaredField = t.getClass().getDeclaredField(oldField.getName());
            oldField.set(old, declaredField.get(t));
        }
    }

    public static Field getField(Class<?> klass, String name) throws NoSuchFieldException {
        Field declaredField = klass.getDeclaredField(name);
        declaredField.setAccessible(true);
        return declaredField;
    }

    public static Method getMethod(Class<?> klass, String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        Method declaredMethod = klass.getDeclaredMethod(name, parameterTypes);
        declaredMethod.setAccessible(true);
        return declaredMethod;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object o, String name) throws IllegalAccessException, NoSuchFieldException {
        return (T) getField(o.getClass(), name).get(o);
    }

    public static boolean classHas(String name) {
        try {
            Class.forName(name);
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }
}
