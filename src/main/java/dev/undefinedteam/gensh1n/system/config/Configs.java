package dev.undefinedteam.gensh1n.system.config;

import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.settings.FolderFileSetting;
import dev.undefinedteam.gensh1n.system.System;
import dev.undefinedteam.gensh1n.system.Systems;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Configs extends System<Configs> implements Iterable<Config> {
    public static final File FOLDER = new File(Client.FOLDER, "configs");

    public Configs() {
        super("configs");
    }

    private final List<Config> configs = new ArrayList<>();

    public static Configs get() {
        return Systems.get(Configs.class);
    }

    @Override
    public void init() {
        if (!FOLDER.exists()) FOLDER.mkdirs();

        configs.clear();

        try (var lists = Files.list(FOLDER.toPath())) {
            lists.forEach(f -> {
                if (isValidFile(f)) {
                    Config cfg = new Config(getFileLabel(f));
                    configs.add(cfg);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFileLabel(Path file) {
        return file
            .getFileName()
            .toString()
            .replace(".cfg", "");
    }

    private boolean isValidFile(Path file) {
        String extension = FilenameUtils.getExtension(file.toFile().getName());
        return (extension.equals("cfg"));
    }

    public void add(Config cfg) {
        if (!configs.contains(cfg)) configs.add(cfg);
        cfg.save();
        save();
    }

    public void remove(Config cfg) {
        if (configs.remove(cfg)) cfg.delete();
        save();
    }

    public Config get(String name) {
        for (Config profile : this) {
            if (profile.name.equalsIgnoreCase(name)) {
                return profile;
            }
        }

        return null;
    }

    public List<Config> getAll() {
        init();
        return configs;
    }

    @Override
    public File getFile() {
        return new File(FOLDER, "configs.json");
    }

    public boolean isEmpty() {
        return configs.isEmpty();
    }

    @Override
    public @NotNull Iterator<Config> iterator() {
        return configs.iterator();
    }
}
