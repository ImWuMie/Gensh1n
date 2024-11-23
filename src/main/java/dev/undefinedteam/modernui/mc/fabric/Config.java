/*
 * Modern UI.
 * Copyright (C) 2019-2024 BloCamLimb. All rights reserved.
 *
 * Modern UI is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Modern UI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Modern UI. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.undefinedteam.modernui.mc.fabric;

import com.google.gson.*;
import dev.undefinedteam.modernui.mc.*;
import dev.undefinedteam.modernui.mc.settings.*;
import dev.undefinedteam.modernui.mc.text.ModernTextRenderer;
import dev.undefinedteam.modernui.mc.text.TextLayoutEngine;
import dev.undefinedteam.modernui.mc.text.TextLayoutProcessor;
import dev.undefinedteam.modernui.mc.text.TextRenderType;
import icyllis.modernui.ModernUI;
import icyllis.modernui.core.Core;
import icyllis.modernui.core.Handler;
import icyllis.modernui.graphics.Color;
import icyllis.modernui.graphics.font.GlyphManager;
import icyllis.modernui.graphics.text.LineBreakConfig;
import icyllis.modernui.resources.Resources;
import icyllis.modernui.util.DisplayMetrics;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewConfiguration;
import lombok.SneakyThrows;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.VideoMode;
import net.minecraft.client.util.Window;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Platform;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.*;

import static icyllis.modernui.ModernUI.LOGGER;
import static icyllis.modernui.ModernUI.MARKER;

@ApiStatus.Internal
public final class Config {

    public static Client CLIENT = new Client();

    public static Common COMMON = new Common();
    public static Text TEXT = new Text();

    @SneakyThrows
    public static void initClientConfig() {
        CLIENT = new Client();
        CLIENT.load(new File(dev.undefinedteam.gensh1n.Client.FOLDER, "ui.clients.json"));
    }

    @SneakyThrows
    public static void initCommonConfig() {
        COMMON = new Common();
        COMMON.load(new File(dev.undefinedteam.gensh1n.Client.FOLDER, "ui.commons.json"));
    }

    @SneakyThrows
    public static void initTextConfig() {
        TEXT = new Text();
        TEXT.load(new File(dev.undefinedteam.gensh1n.Client.FOLDER, "ui.texts.json"));
    }

    @SneakyThrows
    public static void saveClientConfig() {
        CLIENT.save(new File(dev.undefinedteam.gensh1n.Client.FOLDER, "ui.clients.json"));
    }

    @SneakyThrows
    public static void saveCommonConfig() {
        COMMON.save(new File(dev.undefinedteam.gensh1n.Client.FOLDER, "ui.commons.json"));
    }

    @SneakyThrows
    public static void saveTextConfig() {
        TEXT.save(new File(dev.undefinedteam.gensh1n.Client.FOLDER, "ui.texts.json"));
    }

    /*public static void reload() {
        final IConfigSpec<?> spec = config.getSpec();
        *//* else if (spec == SERVER_SPEC) {
            SERVER.reload();
            LOGGER.debug(MARKER, "Server config reloaded with {}", event.getClass().getSimpleName());
        }*//*
    }*/

    public static void reloadCommon() {
        COMMON.reload();
        LOGGER.info(MARKER, "Modern UI common config loaded/reloaded");
    }

    public static void reloadAnyClient() {
        CLIENT.reload();
        LOGGER.info(MARKER, "Modern UI client config loaded/reloaded");
        TEXT.reload();
        LOGGER.info(MARKER, "Modern UI text config loaded/reloaded");
    }

    /*private static class C extends ModConfig {

        private static final Toml _TOML = new Toml();

        public C(Type type, ForgeConfigSpec spec, ModContainer container, String name) {
            super(type, spec, container, ModernUI.NAME_CPT + "/" + name + ".toml");
        }

        @Override
        public ConfigFileTypeHandler getHandler() {
            return _TOML;
        }
    }

    private static class Toml extends ConfigFileTypeHandler {

        private Toml() {
        }

        // reroute it to the global config directory
        // see ServerLifecycleHooks, ModConfig.Type.SERVER
        private static Path reroute(@Nonnull Path configBasePath) {
            //noinspection SpellCheckingInspection
            if (configBasePath.endsWith("serverconfig")) {
                return FMLPaths.CONFIGDIR.get();
            }
            return configBasePath;
        }

        @Override
        public Function<ModConfig, CommentedFileConfig> reader(Path configBasePath) {
            return super.reader(reroute(configBasePath));
        }

        @Override
        public void unload(Path configBasePath, ModConfig config) {
            super.unload(reroute(configBasePath), config);
        }
    }*/

    public static class Client {

        public static final int ANIM_DURATION_MIN = 0;
        public static final int ANIM_DURATION_MAX = 800;
        public static final int BLUR_RADIUS_MIN = 2;
        public static final int BLUR_RADIUS_MAX = 18;
        public static final float FONT_SCALE_MIN = 0.5f;
        public static final float FONT_SCALE_MAX = 2.0f;
        public static final int TOOLTIP_BORDER_COLOR_ANIM_MIN = 0;
        public static final int TOOLTIP_BORDER_COLOR_ANIM_MAX = 5000;
        public static final float TOOLTIP_BORDER_WIDTH_MIN = 0.5f;
        public static final float TOOLTIP_BORDER_WIDTH_MAX = 2.5f;
        public static final float TOOLTIP_CORNER_RADIUS_MIN = 0;
        public static final float TOOLTIP_CORNER_RADIUS_MAX = 8;
        public static final float TOOLTIP_SHADOW_RADIUS_MIN = 0;
        public static final float TOOLTIP_SHADOW_RADIUS_MAX = 32;

        public final BoolValue mBlurEffect;
        public final BoolValue mBlurWithBackground;
        public final IntValue mBackgroundDuration;
        public final IntValue mBlurRadius;
        public final ListObjectValue<String> mBackgroundColor;
        public final BoolValue mDing;
        //public final BoolValue mZoom;
        //private final BoolValue hudBars;
        public final BoolValue mForceRtl;
        public final DoubleValue mFontScale;
        public final IntValue mFramerateInactive;
        public final IntValue mFramerateMinimized;
        public final DoubleValue mMasterVolumeInactive;
        public final DoubleValue mMasterVolumeMinimized;

        public final IntValue mScrollbarSize;
        public final IntValue mTouchSlop;
        public final IntValue mMinScrollbarTouchTarget;
        public final IntValue mMinimumFlingVelocity;
        public final IntValue mMaximumFlingVelocity;
        public final IntValue mOverscrollDistance;
        public final IntValue mOverflingDistance;
        public final DoubleValue mVerticalScrollFactor;
        public final DoubleValue mHorizontalScrollFactor;

        private final ListObjectValue<String> mBlurBlacklist;

        public final BoolValue mAntiAliasing;
        public final BoolValue mAutoHinting;
        //public final BoolValue mLinearSampling;
        public final TextValue mFirstFontFamily;
        public final ListObjectValue<String> mFallbackFontFamilyList;
        public final ListObjectValue<String> mFontRegistrationList;
        public final BoolValue mUseColorEmoji;
        public final BoolValue mEmojiShortcodes;

        /*public final BoolValue mSkipGLCapsError;
        public final BoolValue mShowGLCapsError;*/

        private void save(File cfg) throws IOException {
            if (!cfg.exists()) {
                cfg.createNewFile();
            }

            final JsonObject jsonObject = new JsonObject();

            List<Value> found = Arrays.stream(Client.class.getDeclaredFields()).map((field -> {
                if (Value.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    try {
                        if (field.get(this) != null) {
                            return (Value) field.get(this);
                        }
                    } catch (IllegalAccessException e) {
                    }
                }
                return null;
            })).toList();

            for (final Value value : found) {
                if (value != null) {
                    jsonObject.add(value.name, new JsonPrimitive(value.toString()));
                }
            }

            final PrintWriter printWriter = new PrintWriter(new FileWriter(cfg));
            printWriter.println(dev.undefinedteam.gensh1n.Client.GSON.toJson(jsonObject));
            printWriter.close();
        }

        private void load(File cfg) throws IOException {
            if (!cfg.exists()) {
                save(cfg);
                return;
            }

            final JsonElement jsonElement = new JsonParser().parse(new BufferedReader(new FileReader(cfg)));

            if (jsonElement instanceof JsonNull)
                return;

            List<Value> found = Arrays.stream(Client.class.getDeclaredFields()).map((field -> {
                if (Value.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    try {
                        if (field.get(this) != null) {
                            return (Value) field.get(this);
                        }
                    } catch (IllegalAccessException e) {
                    }
                }
                return null;
            })).toList();
            for (Map.Entry<String, JsonElement> entry : jsonElement.getAsJsonObject().entrySet()) {
                found.forEach(v -> {
                    if (v != null) {
                        if (v.name.equals(entry.getKey())) {
                            v.parse(entry.getValue().getAsString());
                        }
                    }
                });
            }
        }

        private Client() {
            mBackgroundDuration = new IntValue("animationDuration", 200, ANIM_DURATION_MIN, ANIM_DURATION_MAX).desc(
                "The duration of GUI background color and blur radius animation in milliseconds. (0 = OFF)");
            mBackgroundColor = new ListObjectValue<>("backgroundColor", new ArrayList<>(), "#99000000").desc(
                "The GUI background color in #RRGGBB or #AARRGGBB format. Default value: #66000000",
                "Can be one to four values representing top left, top right, bottom right and bottom left" +
                    " color.",
                "Multiple values produce a gradient effect, whereas one value produce a solid color.",
                "When values is less than 4, the rest of the corner color will be replaced by the last " +
                    "value.");

            mBlurEffect = new BoolValue("blurEffect", true).desc(
                    "Add blur effect to GUI background when opened, it is incompatible with OptiFine's FXAA " +
                        "shader and some mods.",
                    "Disable this if you run into a problem or are on low-end PCs")
                .define("blurEffect", true);
            mBlurWithBackground = new BoolValue("blurWithBackground", true).desc(
                    "This option means that blur effect only applies for GUI screens with a background",
                    "This is only meaningful when blur effect is enabled.")
                .define("blurWithBackground", true);
            mBlurRadius = new IntValue("blurRadius", 7, BLUR_RADIUS_MIN, BLUR_RADIUS_MAX).desc(
                "The strength for two-pass gaussian convolution blur effect.",
                "samples/pixel = ((radius * 2) + 1) * 2, sigma = radius / 2.");
            mBlurBlacklist = new ListObjectValue<>("blurBlacklist", new ArrayList<>(), ChatScreen.class.getName()).desc(
                "A list of GUI screen superclasses that won't activate blur effect when opened.");
            mFramerateInactive = new IntValue("framerateInactive", 30, 0, 255).desc(
                "Framerate limit on window inactive (out of focus or minimized), 0 = no change.");

            mFramerateMinimized = new IntValue("framerateMinimized", 0, 0, 255).desc(
                "Framerate limit on window minimized, 0 = same as framerate inactive.",
                "This value will be no greater than framerate inactive.");
            mMasterVolumeInactive = new DoubleValue("masterVolumeInactive", 0.5, 0, 1).desc(
                "Master volume multiplier on window inactive (out of focus or minimized), 1 = no change.");
            mMasterVolumeMinimized = new DoubleValue("masterVolumeMinimized", 0.25, 0, 1).desc(
                "Master volume multiplier on window minimized, 1 = same as master volume inactive.",
                "This value will be no greater than master volume inactive.");


            mDing = new BoolValue("ding", true).desc("Play a sound effect when the game is loaded.");
            mEmojiShortcodes = new BoolValue("emojiShortcodes", true).desc(
                    "Allow Slack or Discord shortcodes to replace Unicode Emoji Sequences in chat.")
                .define("emojiShortcodes", true);

            mForceRtl = new BoolValue("forceRtl", false).desc("Force layout direction to RTL, otherwise, the current Locale setting.")
                .define("forceRtl", false);
            mFontScale = new DoubleValue("fontScale", 1.0f, FONT_SCALE_MIN, FONT_SCALE_MAX).desc("The global font scale used with sp units.");
            mScrollbarSize = new IntValue("scrollbarSize", ViewConfiguration.SCROLL_BAR_SIZE, 0, 1024).desc("Default scrollbar size in dips.");
            mTouchSlop = new IntValue("touchSlop", ViewConfiguration.TOUCH_SLOP, 0, 1024).desc("Distance a touch can wander before we think the user is scrolling in dips.");
            mMinScrollbarTouchTarget = new IntValue("minScrollbarTouchTarget", ViewConfiguration.MIN_SCROLLBAR_TOUCH_TARGET, 0, 1024).desc("Minimum size of the touch target for a scrollbar in dips.");
            mMinimumFlingVelocity = new IntValue("minimumFlingVelocity", ViewConfiguration.MINIMUM_FLING_VELOCITY, 0, 32767).desc("Minimum velocity to initiate a fling in dips per second.");
            mMaximumFlingVelocity = new IntValue("maximumFlingVelocity", ViewConfiguration.MAXIMUM_FLING_VELOCITY, 0, 32767).desc("Maximum velocity to initiate a fling in dips per second.");
            mOverscrollDistance = new IntValue("overscrollDistance", ViewConfiguration.OVERSCROLL_DISTANCE, 0, 1024).desc("Max distance in dips to overscroll for edge effects.");
            mOverflingDistance = new IntValue("overflingDistance", ViewConfiguration.OVERFLING_DISTANCE, 0, 1024).desc("Max distance in dips to overfling for edge effects.");
            mVerticalScrollFactor = new DoubleValue("verticalScrollFactor", ViewConfiguration.VERTICAL_SCROLL_FACTOR, 0, 1024).desc("Amount to scroll in response to a vertical scroll event, in dips" +
                    " per axis value.");
            mHorizontalScrollFactor = new DoubleValue("horizontalScrollFactor", ViewConfiguration.HORIZONTAL_SCROLL_FACTOR, 0, 1024).desc("Amount to scroll in response to a horizontal scroll event, in " +
                    "dips per axis value.");

            mAntiAliasing = new BoolValue("antiAliasing", true).desc(
                    "Control the anti-aliasing of raw glyph rasterization.")
                .define("antiAliasing", true);
            mAutoHinting = new BoolValue("autoHinting", false).desc(
                    "Control the FreeType font hinting of raw glyph metrics.",
                    "Enable if on low-res monitor; disable for linear texts.")
                .define("autoHinting", false);
            mFirstFontFamily = new TextValue("firstFontFamily", "Microsoft YaHei").desc(
                    "The first font family to use. See fallbackFontFamilyList")
                .define("firstFontFamily", "Microsoft YaHei");
            List<String> list1 = new ArrayList<>();
            list1.add("Noto Sans");
            list1.add("Segoe UI Variable");
            list1.add("Segoe UI");
            list1.add("San Francisco");
            list1.add("Open Sans");
            list1.add("SimHei");
            list1.add("STHeiti");
            list1.add("Segoe UI Symbol");
            list1.add("mui-i18n-compat");
            mFallbackFontFamilyList = new ListObjectValue<>("fallbackFontFamilyList",list1).desc(
                    "A set of fallback font families to determine the typeface to use.",
                    "The order is first > fallbacks. TrueType & OpenType are supported.",
                    "Each element can be one of the following two cases:",
                    "1) Name of registered font family, for instance: Segoe UI",
                    "2) Path of font files on your PC, for instance: /usr/shared/fonts/x.otf",
                    "Registered font families include:",
                    "1) OS builtin fonts.",
                    "2) Font files in fontRegistrationList.",
                    "3) Font files in '/resourcepacks' directory.",
                    "4) Font files under 'modernui:font' in resource packs.",
                    "Note that for TTC/OTC font, you should register it and select one of font families.",
                    "Otherwise, only the first font family from the TrueType/OpenType Collection will be used.",
                    "This is only read once when the game is loaded, you can reload via in-game GUI.");
            mFontRegistrationList = new ListObjectValue<>("fontRegistrationList", new ArrayList<>()).desc(
                    "A set of additional font files (or directories) to register.",
                    "For TrueType/OpenType Collections, all contained font families will be registered.",
                    "Registered fonts can be referenced in Modern UI and MinecraftClient (Modern Text Engine).",
                    "For example, \"E:/Fonts\" means all font files in that directory will be registered.",
                    "System requires random access to these files, you should not remove them while running.",
                    "This is only read once when the game is loaded, i.e. registration.");
            mUseColorEmoji = new BoolValue("useColorEmoji", true).desc(
                    "Whether to use Google Noto Color Emoji, otherwise grayscale emoji (faster).",
                    "See Unicode 15.0 specification for details on how this affects text layout.")
                .define("useColorEmoji", true);
        }

        public void saveAndReloadAsync() {
            Config.saveClientConfig();
            reload();
        }

        private void reload() {
            BlurHandler.sBlurEffect = mBlurEffect.get();
            BlurHandler.sBlurWithBackground = mBlurWithBackground.get();
            BlurHandler.sBackgroundDuration = mBackgroundDuration.get();
            BlurHandler.sBlurRadius = mBlurRadius.get();

            BlurHandler.sFramerateInactive = mFramerateInactive.get();
            BlurHandler.sFramerateMinimized = Math.min(
                mFramerateMinimized.get(),
                BlurHandler.sFramerateInactive
            );
            BlurHandler.sMasterVolumeInactive = mMasterVolumeInactive.get().floatValue();
            BlurHandler.sMasterVolumeMinimized = Math.min(
                mMasterVolumeMinimized.get().floatValue(),
                BlurHandler.sMasterVolumeInactive
            );

            List<? extends String> inColors = mBackgroundColor.get();
            int[] resultColors = new int[4];
            int color = 0x99000000;
            for (int i = 0; i < 4; i++) {
                if (inColors != null && i < inColors.size()) {
                    String s = inColors.get(i);
                    try {
                        color = Color.parseColor(s);
                    } catch (Exception e) {
                        LOGGER.error(MARKER, "Wrong color format for screen background, index: {}", i, e);
                    }
                }
                resultColors[i] = color;
            }
            BlurHandler.sBackgroundColor = resultColors;

            BlurHandler.INSTANCE.loadBlacklist(mBlurBlacklist.get());

            // TODO: TTTT
            //ModernUIClient.sInventoryPause = mInventoryPause.get();
            //ModernUIClient.sRemoveTelemetrySession = mRemoveTelemetry.get();

            UIManager.sDingEnabled = mDing.get();

            //TestHUD.sBars = hudBars.get();
            Handler handler = Core.getUiHandlerAsync();
            if (handler != null) {
                handler.post(() -> {
                    UIManager.getInstance().updateLayoutDir(mForceRtl.get());
                    ModernUIClient.sFontScale = (mFontScale.get().floatValue());
                    var ctx = ModernUI.getInstance();
                    if (ctx != null) {
                        Resources res = ctx.getResources();
                        DisplayMetrics metrics = new DisplayMetrics();
                        metrics.setTo(res.getDisplayMetrics());
                        metrics.scaledDensity = ModernUIClient.sFontScale * metrics.density;
                        res.updateMetrics(metrics);
                    }
                });
            }

            boolean reloadStrike = false;
            if (GlyphManager.sAntiAliasing != mAntiAliasing.get()) {
                GlyphManager.sAntiAliasing = mAntiAliasing.get();
                reloadStrike = true;
            }
            if (GlyphManager.sFractionalMetrics == mAutoHinting.get()) {
                GlyphManager.sFractionalMetrics = !mAutoHinting.get();
                reloadStrike = true;
            }
            /*if (GLFontAtlas.sLinearSampling != mLinearSampling.get()) {
                GLFontAtlas.sLinearSampling = mLinearSampling.get();
                reload = true;
            }*/
            ModernUIClient.sUseColorEmoji = mUseColorEmoji.get();
            ModernUIClient.sEmojiShortcodes = mEmojiShortcodes.get();
            ModernUIClient.sFirstFontFamily = mFirstFontFamily.get();
            ModernUIClient.sFallbackFontFamilyList = mFallbackFontFamilyList.get();
            ModernUIClient.sFontRegistrationList = mFontRegistrationList.get();
            if (reloadStrike) {
                MinecraftClient.getInstance().submit(
                    () -> FontResourceManager.getInstance().reloadAll());
            }

            // scan and preload typeface in background thread
            //ModernUI.getSelectedTypeface();
        }

        public enum WindowMode {
            NORMAL,
            FULLSCREEN,
            FULLSCREEN_BORDERLESS,
            MAXIMIZED,
            MAXIMIZED_BORDERLESS,
            WINDOWED,
            WINDOWED_BORDERLESS;

            public void apply() {
                if (this == NORMAL) {
                    return;
                }
                Window window = MinecraftClient.getInstance().getWindow();
                switch (this) {
                    case FULLSCREEN -> {
                        if (!window.isFullscreen()) {
                            window.toggleFullscreen();
                        }
                    }
                    case FULLSCREEN_BORDERLESS -> {
                        if (window.isFullscreen()) {
                            window.toggleFullscreen();
                        }
                        GLFW.glfwRestoreWindow(window.getHandle());
                        GLFW.glfwSetWindowAttrib(window.getHandle(),
                            GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);
                        Monitor monitor = window.getMonitor();
                        if (monitor != null) {
                            VideoMode videoMode = monitor.getCurrentVideoMode();
                            int x = monitor.getViewportX();
                            int y = monitor.getViewportY();
                            int width = videoMode.getWidth();
                            int height = videoMode.getHeight();
                            GLFW.glfwSetWindowMonitor(window.getHandle(), MemoryUtil.NULL,
                                x, y, width, height, GLFW.GLFW_DONT_CARE);
                        } else {
                            GLFW.glfwMaximizeWindow(window.getHandle());
                        }
                    }
                    case MAXIMIZED -> {
                        if (window.isFullscreen()) {
                            window.toggleFullscreen();
                        }
                        GLFW.glfwRestoreWindow(window.getHandle());
                        GLFW.glfwSetWindowAttrib(window.getHandle(),
                            GLFW.GLFW_DECORATED, GLFW.GLFW_TRUE);
                        GLFW.glfwMaximizeWindow(window.getHandle());
                    }
                    case MAXIMIZED_BORDERLESS -> {
                        if (window.isFullscreen()) {
                            window.toggleFullscreen();
                        }
                        GLFW.glfwRestoreWindow(window.getHandle());
                        GLFW.glfwSetWindowAttrib(window.getHandle(),
                            GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);
                        GLFW.glfwMaximizeWindow(window.getHandle());
                    }
                    case WINDOWED -> {
                        if (window.isFullscreen()) {
                            window.toggleFullscreen();
                        }
                        GLFW.glfwSetWindowAttrib(window.getHandle(),
                            GLFW.GLFW_DECORATED, GLFW.GLFW_TRUE);
                        GLFW.glfwRestoreWindow(window.getHandle());
                    }
                    case WINDOWED_BORDERLESS -> {
                        if (window.isFullscreen()) {
                            window.toggleFullscreen();
                        }
                        GLFW.glfwSetWindowAttrib(window.getHandle(),
                            GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);
                        GLFW.glfwRestoreWindow(window.getHandle());
                    }
                }
            }

            @Nonnull
            @Override
            public String toString() {
                return I18n.translate("modernui.windowMode." + name().toLowerCase(Locale.ROOT));
            }
        }
    }

    /**
     * Common config exists on physical client and physical server once game loaded.
     * They are independent and do not sync with each other.
     */
    public static class Common {

        public final BoolValue developerMode;
        public final IntValue oneTimeEvents;

        //public final BoolValue autoShutdown;

        //public final ListObjectValue<String shutdownTimes;

        private void save(File cfg) throws IOException {
            if (!cfg.exists()) {
                cfg.createNewFile();
            }

            final JsonObject jsonObject = new JsonObject();

            List<Value> found = Arrays.stream(Common.class.getDeclaredFields()).map((field -> {
                if (Value.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    try {
                        if (field.get(this) != null) {
                            return (Value) field.get(this);
                        }
                    } catch (IllegalAccessException e) {
                    }
                }
                return null;
            })).toList();

            for (final Value value : found) {
                if (value != null) {
                    jsonObject.add(value.name, new JsonPrimitive(value.toString()));
                }
            }

            final PrintWriter printWriter = new PrintWriter(new FileWriter(cfg));
            printWriter.println(dev.undefinedteam.gensh1n.Client.GSON.toJson(jsonObject));
            printWriter.close();
        }

        private void load(File cfg) throws IOException {
            if (!cfg.exists()) {
                save(cfg);
                return;
            }

            final JsonElement jsonElement = new JsonParser().parse(new BufferedReader(new FileReader(cfg)));

            if (jsonElement instanceof JsonNull)
                return;

            List<Value> found = Arrays.stream(Common.class.getDeclaredFields()).map((field -> {
                if (Value.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    try {
                        if (field.get(this) != null) {
                            return (Value) field.get(this);
                        }
                    } catch (IllegalAccessException e) {
                    }
                }
                return null;
            })).toList();
            for (Map.Entry<String, JsonElement> entry : jsonElement.getAsJsonObject().entrySet()) {
                found.forEach(v -> {
                    if (v != null) {
                        if (v.name.equals(entry.getKey())) {
                            v.parse(entry.getValue().getAsString());
                        }
                    }
                });
            }
        }


        private Common() {
            developerMode = new BoolValue("enableDeveloperMode", false).desc("Whether to enable developer mode.")
                .define("enableDeveloperMode", false);
            oneTimeEvents = new IntValue("oneTimeEvents", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);

            /*builder.comment("Auto Shutdown Config")
                    .push("autoShutdown");

            autoShutdown = builder.comment(
                            "Enable auto-shutdown for server.")
                    .define("enable", false);
            shutdownTimes = builder.comment(
                            "The time points of when server will auto-shutdown. Format: HH:mm.")
                    .defineList("times", () -> {
                        List<String> list = new ArrayList<>();
                        list.add("04:00");
                        list.add("16:00");
                        return list;
                    }, s -> true);

            builder.pop();*/
        }

        public void saveAndReloadAsync() {
            Config.saveCommonConfig();
            reload();
        }

        private void reload() {
            ModernUIMod.sDeveloperMode = developerMode.get();
            //ServerHandler.INSTANCE.determineShutdownTime();
        }
    }

    public static class Text {

        public static final float BASE_FONT_SIZE_MIN = 6.5f;
        public static final float BASE_FONT_SIZE_MAX = 9.5f;
        public static final float BASELINE_MIN = 4;
        public static final float BASELINE_MAX = 10;
        public static final float SHADOW_OFFSET_MIN = 0.2f;
        public static final float SHADOW_OFFSET_MAX = 2;
        public static final float OUTLINE_OFFSET_MIN = 0.2f;
        public static final float OUTLINE_OFFSET_MAX = 2;
        public static final int LIFESPAN_MIN = 2;
        public static final int LIFESPAN_MAX = 15;
        /*public static final int REHASH_MIN = 0;
        public static final int REHASH_MAX = 2000;*/

        //final BoolValue globalRenderer;
        public final BoolValue mAllowShadow;
        public final BoolValue mFixedResolution;
        public final DoubleValue mShadowOffset;
        public final DoubleValue mOutlineOffset;
        //public final BoolValue mSuperSampling;
        //public final BoolValue mAlignPixels;
        public final IntValue mCacheLifespan;
        //public final IntValue mRehashThreshold;
        public final EnumValue<TextDirection> mTextDirection;
        //public final BoolValue mBitmapReplacement;
        //public final BoolValue mUseDistanceField;
        //public final BoolValue mUseVanillaFont;
        public final BoolValue mUseTextShadersInWorld;
        public final EnumValue<DefaultFontBehavior> mDefaultFontBehavior;
        public final ListObjectValue<String> mDefaultFontRuleSet;
        public final BoolValue mUseComponentCache;
        public final BoolValue mAllowAsyncLayout;
        public final EnumValue<LineBreakStyle> mLineBreakStyle;
        public final EnumValue<LineBreakWordStyle> mLineBreakWordStyle;
        public final BoolValue mSmartSDFShaders;
        public final BoolValue mComputeDeviceFontSize;
        public final BoolValue mAllowSDFTextIn2D;

        //private final BoolValue antiAliasing;
        //private final BoolValue highPrecision;
        //private final BoolValue enableMipmap;
        //private final IntValue mipmapLevel;
        //private final IntValue resolutionLevel;
        //private final IntValue defaultFontSize;

        private void save(File cfg) throws IOException {
            if (!cfg.exists()) {
                cfg.createNewFile();
            }

            final JsonObject jsonObject = new JsonObject();

            List<Value> found = Arrays.stream(Text.class.getDeclaredFields()).map((field -> {
                if (Value.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    try {
                        if (field.get(this) != null) {
                            return (Value) field.get(this);
                        }
                    } catch (IllegalAccessException e) {
                    }
                }
                return null;
            })).toList();

            for (final Value value : found) {
                if (value != null) {
                    jsonObject.add(value.name, new JsonPrimitive(value.toString()));
                }
            }

            final PrintWriter printWriter = new PrintWriter(new FileWriter(cfg));
            printWriter.println(dev.undefinedteam.gensh1n.Client.GSON.toJson(jsonObject));
            printWriter.close();
        }

        private void load(File cfg) throws IOException {
            if (!cfg.exists()) {
                save(cfg);
                return;
            }

            final JsonElement jsonElement = new JsonParser().parse(new BufferedReader(new FileReader(cfg)));

            if (jsonElement instanceof JsonNull)
                return;

            List<Value> found = Arrays.stream(Text.class.getDeclaredFields()).map((field -> {
                if (Value.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    try {
                        if (field.get(this) != null) {
                            return (Value) field.get(this);
                        }
                    } catch (IllegalAccessException e) {
                    }
                }
                return null;
            })).toList();
            for (Map.Entry<String, JsonElement> entry : jsonElement.getAsJsonObject().entrySet()) {
                found.forEach(v -> {
                    if (v != null) {
                        if (v.name.equals(entry.getKey())) {
                            v.parse(entry.getValue().getAsString());
                        }
                    }
                });
            }
        }

        private Text() {
            /*globalRenderer = builder.comment(
                    "Apply Modern UI font renderer (including text layouts) to the entire game rather than only " +
                            "Modern UI itself.")
                    .define("globalRenderer", true);*/
            mAllowShadow = new BoolValue("allowShadow", true).desc(
                "Allow text renderer to drop shadow, setting to false can improve performance."
            ).define("allowShadow", true);
            mFixedResolution = new BoolValue("baseFontSize-shadow", true).desc(
                    "Fix resolution level at 2. When the GUI scale increases, the resolution level remains.",
                    "Then GUI scale should be even numbers (2, 4, 6...), based on MinecraftClient GUI system.",
                    "If your fonts are not bitmap fonts, then you should keep this setting false.")
                .define("fixedResolution", false);
            mShadowOffset = new DoubleValue("shadowOffset", 0.8, SHADOW_OFFSET_MIN, SHADOW_OFFSET_MAX).desc(
                "Control the text shadow offset for vanilla text rendering, in GUI scaled pixels.");
            mOutlineOffset = new DoubleValue("outlineOffset", 0.5, OUTLINE_OFFSET_MIN, OUTLINE_OFFSET_MAX).desc(
                "Control the text outline offset for vanilla text rendering, in GUI scaled pixels.");
            /*mSuperSampling = builder.comment(
                            "Super sampling can make the text more smooth with large font size or in the 3D world.",
                            "But it makes the glyph edge too blurry and difficult to read.")
                    .define("superSampling", false);*/
            /*mAlignPixels = builder.comment(
                            "Enable to make each glyph pixel-aligned in text layout in screen-space.",
                            "Text rendering may be better with bitmap fonts / fixed resolution / linear sampling.")
                    .define("alignPixels", false);*/
            mCacheLifespan = new IntValue("cacheLifespan", 6, LIFESPAN_MIN, LIFESPAN_MAX).desc(
                    "Set the recycle time of layout cache in seconds, using least recently used algorithm.");
            /*mRehashThreshold = builder.comment("Set the rehash threshold of layout cache")
                    .defineInRange("rehashThreshold", 100, REHASH_MIN, REHASH_MAX);*/
            mTextDirection = new EnumValue<>("textDirection", TextDirection.FIRST_STRONG).desc(
                    "The bidirectional text heuristic algorithm.",
                    "This will affect which BiDi algorithm to use during text layout.");
            /*mBitmapReplacement = builder.comment(
                            "Whether to use bitmap replacement for non-Emoji character sequences. Restart is required.")
                    .define("bitmapReplacement", false);*/
            /*mUseVanillaFont = builder.comment(
                            "Whether to use MinecraftClient default bitmap font for basic Latin letters.")
                    .define("useVanillaFont", false);*/
            mUseTextShadersInWorld = new BoolValue("useTextShadersInWorld", true).desc(
                    "Whether to use Modern UI text rendering pipeline in 3D world.",
                    "Disabling this means that SDF text and rendering optimization are no longer effective.",
                    "But text rendering can be compatible with OptiFine Shaders and Iris Shaders.",
                    "This does not affect text rendering in GUI.",
                    "This option only applies to TrueType fonts.");
            /*mUseDistanceField = builder.comment(
                            "Enable to use distance field for text rendering in 3D world.",
                            "It improves performance with deferred rendering and sharpens when doing 3D transform.")
                    .define("useDistanceField", true);*/
            mDefaultFontBehavior = new EnumValue<>("defaultFontBehavior", DefaultFontBehavior.ONLY_EXCLUDE).desc(
                    "For \"minecraft:default\" font, should we keep some glyph providers of them?",
                    "Ignore All: Only use Modern UI typeface list.",
                    "Keep ASCII: Include minecraft:font/ascii.png, minecraft:font/accented.png, " +
                        "minecraft:font/nonlatin_european.png",
                    "Keep Other: Include providers other than ASCII and Unicode font.",
                    "Keep All: Include all except Unicode font.",
                    "Only Include: Only include providers that specified by defaultFontRuleSet.",
                    "Only Exclude: Only exclude providers that specified by defaultFontRuleSet.");
            List<String> rules = new ArrayList<>();
            // three vanilla fonts
            rules.add("^minecraft:font\\/(nonlatin_european|accented|ascii|" +
                // four added by CFPA MinecraftClient-Mod-Language-Package
                "element_ideographs|cjk_punctuations|ellipsis|2em_dash)\\.png$");
            // the vanilla space
            rules.add("^minecraft:include\\/space \\/ minecraft:space$");
            mDefaultFontRuleSet = new ListObjectValue<>("defaultFontRuleSet",rules).desc(
                    "Used when defaultFontBehavior is either ONLY_INCLUDE or ONLY_EXCLUDE.",
                    "This specifies a set of regular expressions to match the glyph provider name.",
                    "For bitmap providers, this is the texture path without 'textures/'.",
                    "For TTF providers, this is the TTF file path without 'font/'.",
                    "For space providers, this is \"font_name / minecraft:space\",",
                    "where font_name is font definition path without 'font/'.");
            mUseComponentCache = new BoolValue("useComponentCache", !ModernUIMod.isUntranslatedItemsLoaded()).desc(                 "Whether to use text component object as hash key to lookup in layout cache.",
                    "If you find that Modern UI text rendering is not compatible with some mods,",
                    "you can disable this option for compatibility, but this will decrease performance a bit.",
                    "Modern UI will use another cache strategy if this is disabled.")
                .define("useComponentCache", !ModernUIMod.isUntranslatedItemsLoaded());
            mAllowAsyncLayout = new BoolValue("allowAsyncLayout", true).desc(
                    "Allow text layout to be computed from background threads, which may lead to " +
                        "inconsistency issues.",
                    "Otherwise, block the current thread and wait for main thread.")
                .define("allowAsyncLayout", true);
            mLineBreakStyle = new EnumValue<>("lineBreakStyle", LineBreakStyle.AUTO).desc(
                    "See CSS line-break property, https://developer.mozilla.org/en-US/docs/Web/CSS/line-break");
            mLineBreakWordStyle = new EnumValue<>("lineBreakWordStyle", LineBreakWordStyle.AUTO);
            mSmartSDFShaders = new BoolValue("smartSDFShaders", !ModernUIMod.isOptiFineLoaded()).desc(
                    "When enabled, Modern UI will compute texel density in device-space to determine whether " +
                        "to use SDF text or bilinear sampling.",
                    "This feature requires GLSL 400 or has no effect.",
                    "This generally decreases performance but provides better rendering quality.",
                    "This option only applies to TrueType fonts. May not be compatible with OptiFine.");
            // OK, this doesn't work well with OptiFine
            mComputeDeviceFontSize = new BoolValue("computeDeviceFontSize", true).desc(
                    "When rendering in 2D, this option allows Modern UI to exactly compute font size in " +
                        "device-space from the current coordinate transform matrix.",
                    "This provides perfect text rendering for scaling-down texts in vanilla, but may increase" +
                        " GPU memory usage.",
                    "When disabled, Modern UI will use SDF text rendering if appropriate.",
                    "This option only applies to TrueType fonts.");
            mAllowSDFTextIn2D = new BoolValue("allowSDFTextIn2D", true).desc(
                    "When enabled, Modern UI will use SDF text rendering if appropriate.",
                    "Otherwise, it uses nearest-neighbor or bilinear sampling based on texel density.",
                    "This option only applies to TrueType fonts.");
            /*antiAliasing = builder.comment(
                    "Enable font anti-aliasing.")
                    .define("antiAliasing", true);
            highPrecision = builder.comment(
                    "Enable high precision rendering, this is very useful especially when the font is very small.")
                    .define("highPrecision", true);
            enableMipmap = builder.comment(
                    "Enable mipmap for font textures, this makes font will not be blurred when scaling down.")
                    .define("enableMipmap", true);
            mipmapLevel = builder.comment(
                    "The mipmap level for font textures.")
                    .defineInRange("mipmapLevel", 4, 0, 4);*/
            /*resolutionLevel = builder.comment(
                    "The resolution level of font, higher levels would better work with high resolution monitors.",
                    "Reference: 1 (Standard, 1.5K Fullscreen), 2 (High, 2K~3K Fullscreen), 3 (Ultra, 4K Fullscreen)",
                    "This should match your GUI scale. Scale -> Level: [1,2] -> 1; [3,4] -> 2; [5,) -> 3")
                    .defineInRange("resolutionLevel", 2, 1, 3);*/
            /*defaultFontSize = builder.comment(
                    "The default font size for texts with no size specified. (deprecated, to be removed)")
                    .defineInRange("defaultFontSize", 16, 12, 20);*/
        }

        public void saveAndReloadAsync() {
            Config.saveTextConfig();
            reload();
        }

        void reload() {
            boolean reload = false;
            boolean reloadStrike = false;
            ModernTextRenderer.sAllowShadow = mAllowShadow.get();
            if (TextLayoutEngine.sFixedResolution != mFixedResolution.get()) {
                TextLayoutEngine.sFixedResolution = mFixedResolution.get();
                reload = true;
            }
            ModernTextRenderer.sShadowOffset = mShadowOffset.get().floatValue();
            ModernTextRenderer.sOutlineOffset = mOutlineOffset.get().floatValue();
            /*if (TextLayoutProcessor.sAlignPixels != mAlignPixels.get()) {
                TextLayoutProcessor.sAlignPixels = mAlignPixels.get();
                reload = true;
            }*/
            TextLayoutEngine.sCacheLifespan = mCacheLifespan.get();
            /*TextLayoutEngine.sRehashThreshold = mRehashThreshold.get();*/
            if (TextLayoutEngine.sTextDirection != mTextDirection.get().key) {
                TextLayoutEngine.sTextDirection = mTextDirection.get().key;
                reload = true;
            }
            if (TextLayoutEngine.sDefaultFontBehavior != mDefaultFontBehavior.get().key) {
                TextLayoutEngine.sDefaultFontBehavior = mDefaultFontBehavior.get().key;
                reload = true;
            }
            List<? extends String> defaultFontRuleSet = mDefaultFontRuleSet.get();
            if (!Objects.equals(TextLayoutEngine.sDefaultFontRuleSet, defaultFontRuleSet)) {
                TextLayoutEngine.sDefaultFontRuleSet = defaultFontRuleSet;
                reload = true;
            }
            TextLayoutEngine.sRawUseTextShadersInWorld = mUseTextShadersInWorld.get();
            TextLayoutEngine.sUseComponentCache = mUseComponentCache.get();
            TextLayoutEngine.sAllowAsyncLayout = mAllowAsyncLayout.get();
            if (TextLayoutProcessor.sLbStyle != mLineBreakStyle.get().key) {
                TextLayoutProcessor.sLbStyle = mLineBreakStyle.get().key;
                reload = true;
            }
            if (TextLayoutProcessor.sLbWordStyle != mLineBreakWordStyle.get().key) {
                TextLayoutProcessor.sLbWordStyle = mLineBreakWordStyle.get().key;
                reload = true;
            }

            final boolean smartShaders = mSmartSDFShaders.get();
            MinecraftClient.getInstance().submit(() -> TextRenderType.toggleSDFShaders(smartShaders));

            ModernTextRenderer.sComputeDeviceFontSize = mComputeDeviceFontSize.get();
            ModernTextRenderer.sAllowSDFTextIn2D = mAllowSDFTextIn2D.get();

            if (reloadStrike) {
                MinecraftClient.getInstance().submit(
                    () -> FontResourceManager.getInstance().reloadAll());
            } else if (reload) {
                MinecraftClient.getInstance().submit(
                    () -> {
                        try {
                            TextLayoutEngine.getInstance().reload();
                        } catch (Exception ignored) {
                        }
                    });
            }
            /*GlyphManagerForge.sPreferredFont = preferredFont.get();
            GlyphManagerForge.sAntiAliasing = antiAliasing.get();
            GlyphManagerForge.sHighPrecision = highPrecision.get();
            GlyphManagerForge.sEnableMipmap = enableMipmap.get();
            GlyphManagerForge.sMipmapLevel = mipmapLevel.get();*/
            //GlyphManager.sResolutionLevel = resolutionLevel.get();
            //TextLayoutEngine.sDefaultFontSize = defaultFontSize.get();
        }

        public enum TextDirection {
            FIRST_STRONG(View.TEXT_DIRECTION_FIRST_STRONG, "FirstStrong"),
            ANY_RTL(View.TEXT_DIRECTION_ANY_RTL, "AnyRTL-LTR"),
            LTR(View.TEXT_DIRECTION_LTR, "LTR"),
            RTL(View.TEXT_DIRECTION_RTL, "RTL"),
            LOCALE(View.TEXT_DIRECTION_LOCALE, "Locale"),
            FIRST_STRONG_LTR(View.TEXT_DIRECTION_FIRST_STRONG_LTR, "FirstStrong-LTR"),
            FIRST_STRONG_RTL(View.TEXT_DIRECTION_FIRST_STRONG_RTL, "FirstStrong-RTL");

            private final int key;
            private final String text;

            TextDirection(int key, String text) {
                this.key = key;
                this.text = text;
            }

            @Override
            public String toString() {
                return text;
            }
        }

        public enum DefaultFontBehavior {
            IGNORE_ALL(TextLayoutEngine.DEFAULT_FONT_BEHAVIOR_IGNORE_ALL),
            KEEP_ASCII(TextLayoutEngine.DEFAULT_FONT_BEHAVIOR_KEEP_ASCII),
            KEEP_OTHER(TextLayoutEngine.DEFAULT_FONT_BEHAVIOR_KEEP_OTHER),
            KEEP_ALL(TextLayoutEngine.DEFAULT_FONT_BEHAVIOR_KEEP_ALL),
            ONLY_INCLUDE(TextLayoutEngine.DEFAULT_FONT_BEHAVIOR_ONLY_INCLUDE),
            ONLY_EXCLUDE(TextLayoutEngine.DEFAULT_FONT_BEHAVIOR_ONLY_EXCLUDE);

            private final int key;

            DefaultFontBehavior(int key) {
                this.key = key;
            }

            @Nonnull
            @Override
            public String toString() {
                return I18n.translate("modernui.defaultFontBehavior." + name().toLowerCase(Locale.ROOT));
            }
        }

        public enum LineBreakStyle {
            AUTO(LineBreakConfig.LINE_BREAK_STYLE_NONE, "Auto"),
            LOOSE(LineBreakConfig.LINE_BREAK_STYLE_LOOSE, "Loose"),
            NORMAL(LineBreakConfig.LINE_BREAK_STYLE_NORMAL, "Normal"),
            STRICT(LineBreakConfig.LINE_BREAK_STYLE_STRICT, "Strict");

            private final int key;
            private final String text;

            LineBreakStyle(int key, String text) {
                this.key = key;
                this.text = text;
            }

            @Override
            public String toString() {
                return text;
            }
        }

        public enum LineBreakWordStyle {
            AUTO(LineBreakConfig.LINE_BREAK_WORD_STYLE_NONE, "Auto"),
            PHRASE(LineBreakConfig.LINE_BREAK_WORD_STYLE_PHRASE, "Phrase-based");

            private final int key;
            private final String text;

            LineBreakWordStyle(int key, String text) {
                this.key = key;
                this.text = text;
            }

            @Override
            public String toString() {
                return text;
            }
        }
    }

    // server config is available when integrated server or dedicated server started
    // if on dedicated server, all config data will sync to remote client via network
    /*public static class Server {

        private Server() {

        }

        private void reload() {

        }
    }*/
}
