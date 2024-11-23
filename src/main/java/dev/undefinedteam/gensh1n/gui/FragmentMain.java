package dev.undefinedteam.gensh1n.gui;

import dev.undefinedteam.gclient.GCClient;
import dev.undefinedteam.gclient.GChat;
import dev.undefinedteam.gensh1n.gui.frags.MainGuiFragment;
import dev.undefinedteam.gensh1n.music.GMusic;
import dev.undefinedteam.gensh1n.render.Fonts;
import dev.undefinedteam.gensh1n.system.Systems;
import dev.undefinedteam.gensh1n.utils.task.Tasks;
import icyllis.modernui.ModernUI;
import icyllis.modernui.audio.AudioManager;
import icyllis.modernui.fragment.Fragment;
import dev.undefinedteam.modernui.mc.fabric.Config;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.IOException;

public class FragmentMain extends Fragment {
    public static void main(String[] args) throws IOException {
        System.setProperty("java.awt.headless", "true");
        Configurator.setRootLevel(Level.DEBUG);

        Tasks tasks = new Tasks();
        new GMusic().init();
        Systems.init(tasks);
        Systems.load(tasks);

        Fonts.init();

        Config.initClientConfig();
        Config.initCommonConfig();
        Config.initTextConfig();

        new GChat().startClient();
        new Thread(() -> {
            while (true) {
                try {
                    GCClient.INSTANCE.tick();
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
        AudioManager.getInstance().initialize();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                GChat.INSTANCE.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        tasks.done();
        tasks.show("Demo", LogManager.getLogger());
        try (ModernUI app = new ModernUI()) {
            app.run(MainGuiFragment.get());
        }
        GMusic.INSTANCE.shutdown();
        AudioManager.getInstance().close();
        System.gc();
    }
}
