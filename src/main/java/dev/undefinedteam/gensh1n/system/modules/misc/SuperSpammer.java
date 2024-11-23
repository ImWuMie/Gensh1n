package dev.undefinedteam.gensh1n.system.modules.misc;

import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.Genshin;
import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.utils.RandomUtils;
import dev.undefinedteam.gensh1n.utils.chat.ChatUtils;
import dev.undefinedteam.gensh1n.utils.time.MSTimer;
import meteordevelopment.orbit.EventHandler;
import org.apache.commons.io.FilenameUtils;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@StringEncryption
@NativeObfuscation
@ControlFlowObfuscation
public class SuperSpammer extends Module {
    private final File FOLDER = new File(Client.FOLDER, "superspammer");

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> minDelay = intN(sgGeneral, "min-delay", 3000, 0, 10000 * 60);
    private final Setting<Integer> maxDelay = intN(sgGeneral, "max-delay", 4000, 0, 10000 * 60);
    private final Setting<String> prefix = text(sgGeneral, "prefix", "[%s%n]");
    private final Setting<String> suffix = text(sgGeneral, "suffix", "[%s%n]");
    private final Setting<Boolean> autoDisable = bool(sgGeneral, "auto-disable", true);

    private final Setting<File> folderDir = dir(sgGeneral, "files", FOLDER, this::isValidFile, this::playText);

    @NativeObfuscation.Inline
    public static boolean isPlaying = false;
    @NativeObfuscation.Inline
    public static boolean isPause = false;
    @NativeObfuscation.Inline
    public static boolean noTextsFound = true;

    private final List<String> loadedText = new ArrayList<>();
    private int currentIndex = 0;
    private final MSTimer msTimer = new MSTimer();

    public SuperSpammer() {
        super(Categories.Misc, "super-spammer", "Spam txt");
        if (!FOLDER.exists()) FOLDER.mkdirs();
    }

    private long delay = minDelay.get();

    @Override
    public void onActivate() {
        delay = RandomUtils.nextInt(minDelay.get(), maxDelay.get());
    }

    @EventHandler
    public void onUpdate(TickEvent.Pre e) {
        if (msTimer.check(delay)) {
            if (isPlaying && !isPause) {
                String stStr = prefix.get();
                while (stStr.contains("%n") || stStr.contains("%s")) {
                    if (stStr.contains("%n")) {
                        stStr = stStr.replaceFirst("%n", String.valueOf(RandomUtils.nextInt(0, 9)));
                    }
                    if (stStr.contains("%s")) {
                        stStr = stStr.replaceFirst("%s", RandomUtils.randomString(1));
                    }
                }
                String enStr = suffix.get();
                while (enStr.contains("%n") || enStr.contains("%s")) {
                    if (enStr.contains("%n")) {
                        enStr = enStr.replaceFirst("%n", String.valueOf(RandomUtils.nextInt(0, 9)));
                    }
                    if (enStr.contains("%s")) {
                        enStr = enStr.replaceFirst("%s", RandomUtils.randomString(1));
                    }
                }
                if (!loadedText.isEmpty()) ChatUtils.sendPlayerMsg((stStr + loadedText.get(currentIndex) + enStr));
                if (currentIndex < loadedText.size() - 1) currentIndex += 1;
                else if (autoDisable.get()) stop();
                else currentIndex = 0;
            }
            msTimer.reset();
            delay = RandomUtils.randomDelay(minDelay.get().intValue(), maxDelay.get().intValue());
        }

        if (minDelay.get() > maxDelay.get()) {
            minDelay.set(maxDelay.get());
        }
        if (maxDelay.get() < minDelay.get()) {
            maxDelay.set(minDelay.get());
        }

        noTextsFound = loadedText.isEmpty();

        if (mc.world == null) {
            if (isActive()) toggle();
        }
    }

    private boolean isValidFile(Path file) {
        String extension = FilenameUtils.getExtension(file.toFile().getName());
        return (extension.equals("txt"));
    }

    private String getFileLabel(Path file) {
        return file
            .getFileName()
            .toString()
            .replace(".txt", "");
    }

    public String getStatus() {
        if (!this.isActive()) return "Module disabled.";
        if (loadedText.isEmpty()) return "No text loaded.";
        String playing = "Playing text. " + currentIndex + "/" + loadedText.size();
        if (isPlaying) return playing;
        return "None";
    }

    public String getInfo() {
        String playing = currentIndex + "/" + loadedText.size();
        if (isPlaying) return playing;
        return "None";
    }

    public void pause() {
        isPause = !isPause;
        info((isPause ? "Resume" : "Pause") + " play text");
    }

    private void playText(File file) {
        this.loadedText.clear();
        this.loadedText.addAll(readText(file));
        noTextsFound = loadedText.isEmpty();
        if (noTextsFound) warning("No texts found: " + getFileLabel(file.toPath()));
        else info("Loaded text: " + getFileLabel(file.toPath()));

        currentIndex = 0;
        isPause = false;
        isPlaying = !noTextsFound;
    }

    public static List<String> readText(File inputFile) {
        try {
            return Files.readAllLines(inputFile.toPath());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public String getInfoString() {
        return getInfo();
    }

    public void stop() {
        info("Stopping.");
        isPlaying = false;
        currentIndex = 0;
    }
}
