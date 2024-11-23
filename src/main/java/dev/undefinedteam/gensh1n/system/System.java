package dev.undefinedteam.gensh1n.system;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.Genshin;
import dev.undefinedteam.gensh1n.utils.StreamUtils;
import dev.undefinedteam.gensh1n.utils.misc.ISerializable;
import lombok.Getter;
import net.minecraft.util.crash.CrashException;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public abstract class System<T> implements ISerializable<T> {
    @Getter private final String name;
    @Getter private File file;

    protected boolean isFirstInit;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss", Locale.ROOT);

    public System(String name) {
        this.name = name;

        if (name != null) {
            this.file = new File(Client.FOLDER, name + ".json");
            this.isFirstInit = !file.exists();
        }
    }

    public void init() {
    }

    public void save(File folder) {
        File file = getFile();
        if (file == null) return;

        JsonObject tag = toTag();
        if (tag == null) return;

        try {
            File tempFile = File.createTempFile(Client.NAME, file.getName());

            final PrintWriter printWriter = new PrintWriter(new FileWriter(tempFile));
            printWriter.println(Client.GSON.toJson(tag));
            printWriter.close();

            if (folder != null) file = new File(folder, file.getName());

            file.getParentFile().mkdirs();
            StreamUtils.copy(tempFile, file);
            tempFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveToFile(File file) {
        if (file == null) return;

        JsonObject tag = toTag();
        if (tag == null) return;

        try {
            File tempFile = File.createTempFile(Client.NAME, file.getName());
            final PrintWriter printWriter = new PrintWriter(new FileWriter(tempFile));
            printWriter.println(Client.GSON.toJson(tag));
            printWriter.close();
            file.getParentFile().mkdirs();
            StreamUtils.copy(tempFile, file);
            tempFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        save(null);
    }

    public void load(File folder) {
        File file = getFile();
        if (file == null) return;

        try {
            if (folder != null) file = new File(folder, file.getName());

            if (file.exists()) {
                try {
                    final JsonElement jsonElement = new JsonParser().parse(new BufferedReader(new FileReader(file)));
                    fromTag(jsonElement.getAsJsonObject());
                } catch (CrashException e) {
                    String backupName = FilenameUtils.removeExtension(file.getName()) + "-" + ZonedDateTime.now().format(DATE_TIME_FORMATTER) + ".backup.json";
                    File backup = new File(file.getParentFile(), backupName);
                    StreamUtils.copy(file, backup);
                    Genshin.LOG.error("Error loading {}. Possibly corrupted?", this.name);
                    Genshin.LOG.info("Saved settings backup to '{}'.", backup);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadByFile(File file) {
        if (file == null) return;

        try {
            if (file.exists()) {
                try {
                    final JsonElement jsonElement = new JsonParser().parse(new BufferedReader(new FileReader(file)));
                    fromTag(jsonElement.getAsJsonObject());
                } catch (CrashException e) {
                    String backupName = FilenameUtils.removeExtension(file.getName()) + "-" + ZonedDateTime.now().format(DATE_TIME_FORMATTER) + ".backup.json";
                    File backup = new File(file.getParentFile(), backupName);
                    StreamUtils.copy(file, backup);
                    Genshin.LOG.error("Error loading {}. Possibly corrupted?", this.name);
                    Genshin.LOG.info("Saved settings backup to '{}'.", backup);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        load(null);
    }

    @Override
    public JsonObject toTag() {
        return null;
    }

    @Override
    public T fromTag(JsonObject tag) {
        return null;
    }
}
