package dev.undefinedteam.gensh1n.system;

import dev.undefinedteam.gensh1n.settings.*;
import dev.undefinedteam.gensh1n.settings.EntitySettings;
import dev.undefinedteam.gensh1n.utils.render.color.Color;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface SettingAdapter {
    default <T extends Enum<?>> EnumSetting<T> choice(SettingGroup group, String name, String desc, T defVal, IVisible visible) {
        return (EnumSetting<T>) group.add(new EnumSetting.Builder<T>().name(name).description(desc).defaultValue(defVal).visible(visible).build());
    }

    default DoubleSetting doubleN(SettingGroup group, String name, String desc, double defVal, double min, double max, IVisible visible) {
        return (DoubleSetting) group.add(new DoubleSetting.Builder().name(name).defaultValue(defVal).description(desc).visible(visible).range(min, max).build());
    }

    default EntitySettings entities(SettingGroup group) {
        return new EntitySettings(group);
    }

    default ColorSettings colors(SettingGroup group, String name, IVisible visible) {
        return new ColorSettings(name, group, visible);
    }

    default ColorSettings colors(SettingGroup group, String name) {
        return colors(group, name, null);
    }

    default BoolSetting bool(SettingGroup group, String name, String desc, boolean defVal, IVisible visible) {
        return (BoolSetting) group.add(new BoolSetting.Builder().name(name).description(desc).visible(visible).defaultValue(defVal).build());
    }

    default IntSetting intN(SettingGroup group, String name, String description, int defVal, int min, int max, IVisible visible) {
        return (IntSetting) group.add(new IntSetting.Builder().name(name).defaultValue(defVal).description(description).visible(visible).range(min, max).build());
    }

    default <T extends Enum<?>> EnumSetting<T> choice(SettingGroup group, String name, String desc, T defVal) {
        return this.choice(group, name, desc, defVal, null);
    }

    default DoubleSetting doubleN(SettingGroup group, String name, String desc, double defVal, double min, double max) {
        return this.doubleN(group, name, desc, defVal, min, max, null);
    }

    default BoolSetting bool(SettingGroup group, String name, String desc, boolean defVal) {
        return this.bool(group, name, desc, defVal, null);
    }

    default BoolSetting bool(SettingGroup group, String name, boolean defVal, String desc) {
        return this.bool(group, name, desc, defVal, null);
    }

    default IntSetting intN(SettingGroup group, String name, String desc, int defVal, int min, int max) {
        return this.intN(group, name, desc, defVal, min, max, null);
    }

    default <T extends Enum<?>> Setting<T> choice(SettingGroup group, String name, T defVal, IVisible visible) {
        return this.choice(group, name, "", defVal, visible);
    }

    default DoubleSetting doubleN(SettingGroup group, String name, double defVal, double min, double max, IVisible visible) {
        return this.doubleN(group, name, "", defVal, min, max, visible);
    }

    default BoolSetting bool(SettingGroup group, String name, boolean defVal, IVisible visible) {
        return this.bool(group, name, "", defVal, visible);
    }

    default IntSetting intN(SettingGroup group, String name, int defVal, int min, int max, IVisible visible) {
        return this.intN(group, name, "", defVal, min, max, visible);
    }

    default <T extends Enum<?>> EnumSetting<T> choice(SettingGroup group, String name, T defVal) {
        return this.choice(group, name, "", defVal);
    }

    default DoubleSetting doubleN(SettingGroup group, String name, double defVal, double min, double max) {
        return this.doubleN(group, name, "", defVal, min, max);
    }

    default BoolSetting bool(SettingGroup group, String name, boolean defVal) {
        return this.bool(group, name, "", defVal);
    }

    default IntSetting intN(SettingGroup group, String name, int defVal, int min, int max) {
        return this.intN(group, name, "", defVal, min, max);
    }

    default StringSetting text(SettingGroup group, String name, String description, String defaultValue, IVisible visible) {
        return (StringSetting) group.add(new StringSetting.Builder().name(name).description(description).defaultValue(defaultValue).visible(visible).build());
    }

    default StringSetting text(SettingGroup group, String name, String description, String defaultValue) {
        return text(group, name, description, defaultValue, null);
    }

    default StringSetting text(SettingGroup group, String name, String defaultValue, IVisible visible) {
        return text(group, name, "", defaultValue, visible);
    }

    default StringSetting text(SettingGroup group, String name, String defaultValue) {
        return text(group, name, "", defaultValue, null);
    }

    default StringListSetting texts(SettingGroup group, String name, String description, String[] defaultValue, IVisible visible) {
        return (StringListSetting) group.add(new StringListSetting.Builder().name(name).description(description).defaultValue(defaultValue).visible(visible).build());
    }

    default StringListSetting texts(SettingGroup group, String name, String description, String[] defaultValue) {
        return texts(group, name, description, defaultValue, null);
    }

    default StringListSetting texts(SettingGroup group, String name, String[] defaultValue, IVisible visible) {
        return texts(group, name, "", defaultValue, visible);
    }

    default StringListSetting texts(SettingGroup group, String name, String[] defaultValue) {
        return texts(group, name, "", defaultValue, null);
    }

    default StringListSetting texts(SettingGroup group, String name, Collection<String> defaultValue) {
        return texts(group, name, "", defaultValue.toArray(String[]::new), null);
    }

    default ColorSetting color(SettingGroup group, String name, String description, Color defVal, IVisible visible) {
        return (ColorSetting) group.add(new ColorSetting.Builder().name(name).description(description).defaultValue(defVal).visible(visible).build());
    }

    default ColorSetting color(SettingGroup group, String name, Color defVal, IVisible visible) {
        return color(group, name, "", defVal, visible);
    }

    default ColorSetting color(SettingGroup group, String name, String description, Color defVal) {
        return color(group, name, description, defVal, null);
    }

    default ColorSetting color(SettingGroup group, String name, Color defVal) {
        return color(group, name, "", defVal);
    }

    default FolderFileSetting dir(SettingGroup group, String name, String description, File folder, Predicate<Path> valid, Consumer<File> onSelect, IVisible visible) {
        return (FolderFileSetting) group.add(new FolderFileSetting.Builder().name(name).defaultValue(folder).onSelect(onSelect).valid(valid).visible(visible).build());
    }

    default FolderFileSetting dir(SettingGroup group, String name, String description, File folder, Predicate<Path> valid, IVisible visible) {
        return dir(group, name, description, folder, valid, null, visible);
    }

    default FolderFileSetting dir(SettingGroup group, String name, String description, File folder, Predicate<Path> valid, Consumer<File> onSelect) {
        return dir(group, name, description, folder, valid, onSelect, null);
    }

    default FolderFileSetting dir(SettingGroup group, String name, String description, File folder, Predicate<Path> valid) {
        return dir(group, name, description, folder, valid, null, null);
    }

    default FolderFileSetting dir(SettingGroup group, String name, File folder, Predicate<Path> valid, Consumer<File> onSelect, IVisible visible) {
        return dir(group, name, "", folder, valid, onSelect, visible);
    }

    default FolderFileSetting dir(SettingGroup group, String name, File folder, Predicate<Path> valid, Consumer<File> onSelect) {
        return dir(group, name, folder, valid, onSelect, null);
    }

    default FolderFileSetting dir(SettingGroup group, String name, File folder, Predicate<Path> valid, IVisible visible) {
        return dir(group, name, folder, valid, null, visible);
    }

    default FolderFileSetting dir(SettingGroup group, String name, File folder, Predicate<Path> valid) {
        return dir(group, name, folder, valid, null, null);
    }
}
