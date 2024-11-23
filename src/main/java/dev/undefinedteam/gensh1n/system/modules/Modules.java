package dev.undefinedteam.gensh1n.system.modules;

import com.google.common.collect.Ordering;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.Genshin;
import dev.undefinedteam.gensh1n.events.client.*;
import dev.undefinedteam.gensh1n.events.game.*;
import dev.undefinedteam.gensh1n.events.player.OffhandChangedEvent;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.System;
import dev.undefinedteam.gensh1n.system.Systems;
import dev.undefinedteam.gensh1n.system.modules.combat.*;
import dev.undefinedteam.gensh1n.system.modules.misc.*;
import dev.undefinedteam.gensh1n.system.modules.movement.*;
import dev.undefinedteam.gensh1n.system.modules.player.*;
import dev.undefinedteam.gensh1n.system.modules.render.*;
import dev.undefinedteam.gensh1n.system.modules.world.*;
import dev.undefinedteam.gensh1n.utils.ResLocation;
import dev.undefinedteam.gensh1n.utils.Utils;
import dev.undefinedteam.gensh1n.utils.input.Input;
import dev.undefinedteam.gensh1n.utils.input.KeyAction;
import dev.undefinedteam.gensh1n.utils.input.Keybind;
import dev.undefinedteam.gensh1n.utils.misc.ValueComparableMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static dev.undefinedteam.gensh1n.Client.EVENT_BUS;
import static dev.undefinedteam.gensh1n.Client.mc;

@StringEncryption
@ControlFlowObfuscation
public class Modules extends System<Modules> {
    public static final ModuleRegistry REGISTRY = new ModuleRegistry();

    private static final List<Category> CATEGORIES = new ArrayList<>();

    public final List<Module> modules = new ArrayList<>();
    private final Map<Class<? extends Module>, Module> moduleInstances = new Reference2ReferenceOpenHashMap<>();
    private final Map<Category, List<Module>> groups = new Reference2ReferenceOpenHashMap<>();

    private final List<Module> active = new ArrayList<>();
    private Module moduleToBind;

    public Modules() {
        super("modules");
    }

    public static Modules get() {
        return Systems.get(Modules.class);
    }

    private void initCombat() {
        add(new KillAura());
//        add(new Velocity());
        add(new InfiniteAura());
    }

    private void initPlayer() {
        add(new InvManager());
        add(new FastClick());
        add(new AutoTool());
        add(new Stealer());
        add(new Blink());
        add(new NoFall());
    }

    private void initMovement() {
        add(new AutoClip());
        add(new BoatJump());
        add(new AntiWeb());
        add(new MoveFix());
        add(new Sprint());
        add(new NoPush());
        add(new NoSlow());
    }

    private void initRender() {
        add(new ServerProtect());
        add(new NameProtect());
        add(new ItemPhysics());
        add(new FullBright());
        add(new HitSound());
        add(new DickESP());
        add(new ESP());
    }

    private void initWorld() {
        add(new PistonBreaker());
        add(new Scaffold());
//        add(new ScaffoldTest());
        add(new InstaNuker());
        //add(new ScaffoldV2());
    }

    private void initMisc() {
        add(new SuperSpammer());
//        add(new BoatSpin());
        add(new FakePlayer());
        add(new AntiStaff());
        add(new AutoPlay());
        add(new Protocol());
        add(new AutoCalc());
        add(new AntiBot());
        add(new Teams());
        add(new Debug());
    }

    @Override
    public void init() {
        initCombat();
        initPlayer();
        initMovement();
        initRender();
        initWorld();
        initMisc();

        var mods = new TModules(this);
        mods.init();
    }

    @Override
    public void load(File folder) {
        for (Module module : modules) {
            for (SettingGroup group : module.settings) {
                for (Setting<?> setting : group) setting.reset();
            }
        }

        super.load(folder);
    }

    @Override
    public void loadByFile(File f) {
        for (Module module : modules) {
            for (SettingGroup group : module.settings) {
                for (Setting<?> setting : group) setting.reset();
            }
        }

        super.loadByFile(f);
    }

    public void sortModules() {
        for (List<Module> modules : groups.values()) {
            modules.sort(Comparator.comparing(o -> o.title));
        }
        modules.sort(Comparator.comparing(o -> o.title));
    }

    public static void registerCategory(Category category) {
        if (!Categories.REGISTERING) {
            throw new RuntimeException("Modules.registerCategory - Cannot register category outside of onRegisterCategories callback.");
        }

        CATEGORIES.add(category);
    }

    public static Iterable<Category> loopCategories() {
        return CATEGORIES;
    }

    public static int getCategoryIndex(Iterable<Category> categories, Category category) {
        int index = 0;
        for (Category loopCategory : categories) {
            if (category.equals(loopCategory)) break;
            index++;
        }
        return index;
    }

    public static int getCategoryIndex(Category category) {
        return getCategoryIndex(loopCategories(), category);
    }

    public static Category getCategoryByHash(int hash) {
        for (Category category : CATEGORIES) {
            if (category.hashCode() == hash) return category;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T get(Class<T> klass) {
        return (T) moduleInstances.get(klass);
    }

    public Module get(String name) {
        for (Module module : moduleInstances.values()) {
            if (module.name.equalsIgnoreCase(name)) {
                return module;
            } else if (module.name.equalsIgnoreCase(Utils.nameToTitle(name))) {
                return module;
            }
        }

        return null;
    }

    public boolean isActive(Class<? extends Module> klass) {
        Module module = get(klass);
        return module != null && module.isActive();
    }

    public List<Module> getModulesByCategory(Category category) {
        return groups.computeIfAbsent(category, category1 -> new ArrayList<>());
    }

    public Collection<Module> getAll() {
        return moduleInstances.values();
    }

    public List<Module> getList() {
        return modules;
    }

    public int getCount() {
        return moduleInstances.values().size();
    }

    public List<Module> getActive() {
        synchronized (active) {
            return active;
        }
    }

    public Set<Module> searchTitles(String text) {
        Map<Module, Integer> modules = new ValueComparableMap<>(Ordering.natural());

        for (Module module : this.moduleInstances.values()) {
            int score = Utils.searchLevenshteinDefault(module.title, text, false);
            modules.put(module, modules.getOrDefault(module, 0) + score);
        }

        return modules.keySet();
    }

    public Set<Module> searchSettingTitles(String text) {
        Map<Module, Integer> modules = new ValueComparableMap<>(Ordering.natural());

        for (Module module : this.moduleInstances.values()) {
            int lowest = Integer.MAX_VALUE;
            for (SettingGroup sg : module.settings) {
                for (Setting<?> setting : sg) {
                    int score = Utils.searchLevenshteinDefault(setting.title, text, false);
                    if (score < lowest) lowest = score;
                }
            }
            modules.put(module, modules.getOrDefault(module, 0) + lowest);
        }

        return modules.keySet();
    }

    void addActive(Module module) {
        synchronized (active) {
            if (!active.contains(module)) {
                active.add(module);
                Client.EVENT_BUS.post(ActiveModulesChangedEvent.INSTANCE);
            }
        }
    }

    void removeActive(Module module) {
        synchronized (active) {
            if (active.remove(module)) {
                Client.EVENT_BUS.post(ActiveModulesChangedEvent.INSTANCE);
            }
        }
    }

    // Binding

    public void setModuleToBind(Module moduleToBind) {
        this.moduleToBind = moduleToBind;
    }

    public boolean isBinding() {
        return moduleToBind != null;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onKeyBinding(KeyEvent event) {
        if (event.action == KeyAction.Press && onBinding(true, event.key)) event.cancel();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onButtonBinding(MouseButtonEvent event) {
        if (event.action == KeyAction.Press && onBinding(false, event.button)) event.cancel();
    }

    private boolean onBinding(boolean isKey, int value) {
        if (!isBinding()) return false;

        if (moduleToBind.keybind.canBindTo(isKey, value)) {
            moduleToBind.keybind.set(isKey, value);
            moduleToBind.info("Bound to (highlight)%s(default).", moduleToBind.keybind);
        } else if (value == GLFW.GLFW_KEY_ESCAPE) {
            moduleToBind.keybind.set(Keybind.none());
            moduleToBind.info("Removed bind.");
        } else return false;

        Client.EVENT_BUS.post(new ModuleBindChangedEvent(moduleToBind));
        moduleToBind = null;

        return true;
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onKey(KeyEvent event) {
        if (event.action == KeyAction.Repeat) return;
        onAction(true, event.key, event.action == KeyAction.Press);
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onMouseButton(MouseButtonEvent event) {
        if (event.action == KeyAction.Repeat) return;
        onAction(false, event.button, event.action == KeyAction.Press);
    }

    private ItemStack lastOffHand;

    @EventHandler
    private void onTick(TickEvent.Pre e) {
        if (Utils.canUpdate()) {
            var offhand = mc.player.getOffHandStack();
            if (lastOffHand == null && offhand != null) {
                lastOffHand = offhand;
                EVENT_BUS.post(new OffhandChangedEvent());
                return;
            }

            if (lastOffHand != null && offhand == null) {
                lastOffHand = null;
                EVENT_BUS.post(new OffhandChangedEvent());
                return;
            }

            if (!ItemStack.areEqual(offhand, lastOffHand)) {
                lastOffHand = offhand;
                EVENT_BUS.post(new OffhandChangedEvent());
            }
        }
    }

    private void onAction(boolean isKey, int value, boolean isPress) {
        if (mc.currentScreen == null && !Input.isKeyPressed(GLFW.GLFW_KEY_F3)) {
            for (Module module : moduleInstances.values()) {
                if (module.keybind.matches(isKey, value) && (isPress || module.toggleOnBindRelease)) {
                    module.toggle();
                }
            }
        }
    }

    // End of binding

    @EventHandler(priority = EventPriority.HIGHEST + 1)
    private void onOpenScreen(OpenScreenEvent event) {
        if (!Utils.canUpdate()) return;

        for (Module module : moduleInstances.values()) {
            if (module.toggleOnBindRelease && module.isActive()) {
                module.toggle();
                //module.sendToggledMsg(module);
                //module.sendToggledToast(module);
            }
        }
    }

    @EventHandler
    private void onGameJoined(GameJoinedEvent event) {
        synchronized (active) {
            for (Module module : modules) {
                if (module.isActive()) {
                    Client.EVENT_BUS.subscribe(module);
                    Genshin.LOG.info("sub: {}", module.title);
                    module.onSubscribe();
                    //module.onActivate();
                }
            }
        }
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        synchronized (active) {
            for (Module module : modules) {
                if (module.isActive()) {
                    Client.EVENT_BUS.unsubscribe(module);
                    Genshin.LOG.info("unsub: " + module.title);
                    module.onUnSubscribe();
                    //module.onDeactivate();
                }
            }
        }
    }

    public void disableAll() {
        synchronized (active) {
            for (Module module : modules) {
                if (module.isActive()) module.toggle();
            }
        }
    }

    @Override
    public JsonObject toTag() {
        JsonObject tag = new JsonObject();

        JsonArray modulesTag = new JsonArray();
        for (Module module : getAll()) {
            JsonObject moduleTag = module.toTag();
            if (moduleTag != null) modulesTag.add(moduleTag);
        }
        tag.add("modules", modulesTag);

        return tag;
    }

    @Override
    public Modules fromTag(JsonObject tag) {
        disableAll();

        JsonArray modulesTag = tag.getAsJsonArray("modules");
        for (JsonElement moduleTagI : modulesTag) {
            JsonObject moduleTag = moduleTagI.getAsJsonObject();
            Module module = get(moduleTag.get("name").getAsString());
            if (module != null) module.fromTag(moduleTag);
        }

        return this;
    }

    // INIT MODULES

    public void add(Module module) {
        // Check if the module's category is registered
        if (!CATEGORIES.contains(module.category)) {
            throw new RuntimeException("Modules.addModule - Module's category was not registered.");
        }

        // Remove the previous module with the same name
        AtomicReference<Module> removedModule = new AtomicReference<>();
        if (moduleInstances.values().removeIf(module1 -> {
            if (module1.name.equals(module.name)) {
                removedModule.set(module1);
                module1.settings.unregisterColorSettings();

                return true;
            }

            return false;
        })) {
            getModulesByCategory(removedModule.get().category).remove(removedModule.get());
        }

        // Add the module
        moduleInstances.put(module.getClass(), module);
        modules.add(module);
        getModulesByCategory(module.category).add(module);

        // Register color settings for the module
        module.settings.registerColorSettings(module);
    }


    public static class ModuleRegistry extends SimpleRegistry<Module> {
        public ModuleRegistry() {
            super(RegistryKey.ofRegistry(ResLocation.of("modules")), Lifecycle.stable());
        }

        @Override
        public int size() {
            return Modules.get().getAll().size();
        }

        @Override
        public Identifier getId(Module entry) {
            return null;
        }

        @Override
        public Optional<RegistryKey<Module>> getKey(Module entry) {
            return Optional.empty();
        }

        @Override
        public int getRawId(Module entry) {
            return 0;
        }

        @Override
        public Module get(RegistryKey<Module> key) {
            return null;
        }

        @Override
        public Module get(Identifier id) {
            return null;
        }

        @Override
        public Lifecycle getLifecycle() {
            return null;
        }

        @Override
        public Set<Identifier> getIds() {
            return null;
        }

        @Override
        public boolean containsId(Identifier id) {
            return false;
        }

        @Nullable
        @Override
        public Module get(int index) {
            return null;
        }

        @Override
        public @NotNull Iterator<Module> iterator() {
            return new ModuleIterator();
        }

        @Override
        public boolean contains(RegistryKey<Module> key) {
            return false;
        }

        @Override
        public Set<Map.Entry<RegistryKey<Module>, Module>> getEntrySet() {
            return null;
        }

        @Override
        public Set<RegistryKey<Module>> getKeys() {
            return null;
        }

        @Override
        public Optional<RegistryEntry.Reference<Module>> getRandom(Random random) {
            return Optional.empty();
        }

        @Override
        public Registry<Module> freeze() {
            return null;
        }

        @Override
        public RegistryEntry.Reference<Module> createEntry(Module value) {
            return null;
        }

        @Override
        public Optional<RegistryEntry.Reference<Module>> getEntry(int rawId) {
            return Optional.empty();
        }

        @Override
        public Optional<RegistryEntry.Reference<Module>> getEntry(RegistryKey<Module> key) {
            return Optional.empty();
        }

        @Override
        public Stream<RegistryEntry.Reference<Module>> streamEntries() {
            return null;
        }

        @Override
        public Optional<RegistryEntryList.Named<Module>> getEntryList(TagKey<Module> tag) {
            return Optional.empty();
        }

        @Override
        public RegistryEntryList.Named<Module> getOrCreateEntryList(TagKey<Module> tag) {
            return null;
        }

        @Override
        public Stream<Pair<TagKey<Module>, RegistryEntryList.Named<Module>>> streamTagsAndEntries() {
            return null;
        }

        @Override
        public Stream<TagKey<Module>> streamTags() {
            return null;
        }

        @Override
        public void clearTags() {
        }

        @Override
        public void populateTags(Map<TagKey<Module>, List<RegistryEntry<Module>>> tagEntries) {
        }

        private static class ModuleIterator implements Iterator<Module> {
            private final Iterator<Module> iterator = Modules.get().getAll().iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Module next() {
                return iterator.next();
            }
        }
    }
}
