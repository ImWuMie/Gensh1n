package dev.undefinedteam.gensh1n.gui.frags.music;

import dev.undefinedteam.gensh1n.music.GMusic;
import icyllis.modernui.ModernUI;
import icyllis.modernui.audio.AudioManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

public class TestFrag {
    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        Configurator.setRootLevel(Level.DEBUG);

        new GMusic().init();

        AudioManager.getInstance().initialize();
        try (ModernUI app = new ModernUI()) {
            app.run(new MusicFragment());
        }
        AudioManager.getInstance().close();
        System.gc();
    }
}
