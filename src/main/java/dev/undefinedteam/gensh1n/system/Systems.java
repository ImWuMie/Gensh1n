package dev.undefinedteam.gensh1n.system;

import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.events.game.GameLeftEvent;
import dev.undefinedteam.gensh1n.system.commands.Commands;
import dev.undefinedteam.gensh1n.system.config.Configs;
import dev.undefinedteam.gensh1n.system.friend.Friends;
import dev.undefinedteam.gensh1n.system.hud.Huds;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Modules;
import dev.undefinedteam.gensh1n.utils.task.Tasks;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import meteordevelopment.orbit.EventHandler;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@StringEncryption
@ControlFlowObfuscation
public class Systems {
    @SuppressWarnings("rawtypes")
    private static final Map<Class<? extends System>, System<?>> systems = new Reference2ReferenceOpenHashMap<>();
    private static final List<Runnable> preLoadTasks = new ArrayList<>(1);

    public static void addPreLoadTask(Runnable task) {
        preLoadTasks.add(task);
    }

    public static void init(Tasks tasks) {
        tasks.push("system_init");
        Categories.init();
        ClientConfig config = new ClientConfig();
        System<?> configSystem = add(config);
        configSystem.init();
        configSystem.load();

        // Registers the colors from config tab. This allows rainbow colours to work for friends.
        config.settings.registerColorSettings(null);

        add(new Modules());
        add(new Huds());
        add(new Friends());
        add(new Configs());

        if (Client.isOnMinecraftEnv()) {
            add(new Commands());
        }

        load(tasks);
        Client.EVENT_BUS.subscribe(Systems.class);
        tasks.pop();
    }

    private static System<?> add(System<?> system) {
        systems.put(system.getClass(), system);
        Client.EVENT_BUS.subscribe(system);
        system.init();

        return system;
    }

    // save/load

    @EventHandler
    private static void onGameLeft(GameLeftEvent event) {
        save(null);
    }

    public static void save(Tasks task, File folder) {
        for (System<?> system : systems.values()) {
            if (task != null) task.push(system.getName() + "_save");
            system.save(folder);
            if (task != null) task.pop();
        }
    }

    public static void save(Tasks task) {
        if (task != null) task.push("system_save");
        save(task, null);
        if (task != null) task.pop();
    }

    public static void load(Tasks tasks, File folder) {
        for (Runnable task : preLoadTasks) task.run();
        for (System<?> system : systems.values()) {
            if (tasks != null) tasks.push(system.getName() + "_load");
            system.load(folder);
            if (tasks != null) tasks.pop();
        }
    }

    public static void load(Tasks task) {
        load(task, null);
    }

    @SuppressWarnings("unchecked")
    public static <T extends System<?>> T get(Class<T> klass) {
        return (T) systems.getOrDefault(klass, null);
    }
}
