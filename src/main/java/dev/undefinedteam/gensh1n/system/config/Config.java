package dev.undefinedteam.gensh1n.system.config;

import dev.undefinedteam.gensh1n.system.modules.Modules;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class Config {
    public final String name;

    public Config(String name) {
        this.name = name;
    }

    public void load() {
        Modules.get().loadByFile(getFile());
    }

    public void delete() {
        try {
            FileUtils.deleteDirectory(getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getFile() {
        return new File(Configs.FOLDER, name + ".cfg");
    }

    public void save() {
        Modules.get().saveToFile(getFile());
    }
}
