package dev.undefinedteam.gensh1n;

import dev.undefinedteam.gclient.GCClient;
import dev.undefinedteam.gclient.GCUser;
import dev.undefinedteam.gclient.GChat;
import dev.undefinedteam.gensh1n.events.client.KeyEvent;
import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.events.render.Render2DAfterHotbar;
import dev.undefinedteam.gensh1n.gui.OverlayRenderer;
import dev.undefinedteam.gensh1n.gui.frags.ChatFragment;
import dev.undefinedteam.gensh1n.gui.frags.MainGuiFragment;
import dev.undefinedteam.gensh1n.music.GMusic;
import dev.undefinedteam.gensh1n.render.Fonts;
import dev.undefinedteam.gensh1n.render.Renderer;
import dev.undefinedteam.gensh1n.render.Shaders;
import dev.undefinedteam.gensh1n.render._new.NText;
import dev.undefinedteam.gensh1n.render.world.NR3D;
import dev.undefinedteam.gensh1n.system.ClientConfig;
import dev.undefinedteam.gensh1n.system.Systems;
import dev.undefinedteam.gensh1n.system.hud.gui.HudEditorFragment;
import dev.undefinedteam.gensh1n.utils.RandomUtils;
import dev.undefinedteam.gensh1n.utils.Utils;
import dev.undefinedteam.gensh1n.utils.input.KeyAction;
import dev.undefinedteam.gensh1n.utils.player.SoundManager;
import dev.undefinedteam.gensh1n.utils.render.color.Color;
import dev.undefinedteam.gensh1n.utils.render.color.RainbowColors;
import dev.undefinedteam.gensh1n.utils.task.Tasks;
import dev.undefinedteam.modernui.mc.MusicPlayer;
import icyllis.modernui.ModernUI;
import icyllis.modernui.graphics.Rect;
import dev.undefinedteam.modernui.mc.ModernUIMod;
import dev.undefinedteam.modernui.mc.MuiScreen;
import dev.undefinedteam.modernui.mc.UIManager;
import dev.undefinedteam.modernui.mc.fabric.Config;
import dev.undefinedteam.modernui.mc.fabric.ModernUIFabricClient;
import meteordevelopment.orbit.EventHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static dev.undefinedteam.gensh1n.Client.mc;

@StringEncryption
@ControlFlowObfuscation
public class Genshin extends ModernUIMod implements ClientModInitializer {
    public static final Logger LOG = LogManager.getLogger("Genshin");
    public static Genshin INSTANCE;
    public final Tasks task = new Tasks();
    private final ModernUIFabricClient ui_client = new ModernUIFabricClient(LOG, MARKER);
    public static SoundManager soundManager;

    public static Genshin getInstance() {
        return INSTANCE;
    }

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        try {
            GChat.get();
//            GChat.get().startClient(); 关闭irc
        } catch (Exception e) {
            e.printStackTrace();
        }

        task.start();
        var initTime = System.currentTimeMillis();
        LOG.info("Starting {} v{}...", Client.NAME, Client.VERSION);
        task.push(Client.NAME + "_init");

        Client.EVENT_BUS.subscribe(this);

        task.push("ui_engine");
        loadUI();
        ui_client.onInitializeClient();
        new OverlayRenderer().init();
        task.popPush("main");

        Utils.init();
        Client.init();
        soundManager = new SoundManager();
        soundManager.registerSounds();
        soundManager.CreateSoundsFolder();

        task.popPush("render");
        ClientLifecycleEvents.CLIENT_STARTED.register((mc) -> {
            Shaders.init();
            new GMusic().init();
            new NText().init();
            new NR3D().init();
            Fonts.init();
        });

        Systems.init(Genshin.INSTANCE.task);
        RainbowColors.init();


        task.popPush("add_shutdown");
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, Client.NAME + "_shutdown"));
        task.pop();

        task.pop();
        LOG.info("Done. {}ms.", System.currentTimeMillis() - initTime);
        task.done();
        task.show("Client Init", LOG);
        task.reset();
    }

    private void loadUI() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            ModernUIMod.sDevelopment = true;
            ModernUI.LOGGER.debug(ModernUI.MARKER, "Auto detected in Fabric development environment");
        } else if (ModernUI.class.getSigners() == null) {
            ModernUI.LOGGER.warn(ModernUI.MARKER, "Signature is missing");
        }

        sUntranslatedItemsLoaded = FabricLoader.getInstance().isModLoaded("untranslateditems");
        Config.initCommonConfig();
        Config.initClientConfig();
        Config.initTextConfig();
        Config.reloadAnyClient();
    }

    @EventHandler
    private void onKey(KeyEvent e) {
        if (e.isCancelled()) return;

        if (mc.currentScreen == null || mc.currentScreen instanceof TitleScreen || mc.currentScreen instanceof MultiplayerScreen) {
            if (e.action == KeyAction.Press) {
                switch (e.key) {
                    case GLFW.GLFW_KEY_RIGHT_SHIFT -> UIManager.getInstance().open(MainGuiFragment.get());
                    case GLFW.GLFW_KEY_INSERT -> {
                        if (Utils.canUpdate())
                            UIManager.getInstance().open(HudEditorFragment.get());
                    }
                }
            }
        }
    }

    private final Rect spectrumRect = new Rect(0, 0, 0, 0);

    private final int LYRIC_BG_COLOR = new java.awt.Color(231, 231, 231, 200).getRGB();

    @EventHandler
    private void onRender2DHotbar(Render2DAfterHotbar e) {
        final var renderer = Renderer.MAIN;

        var sd = MainGuiFragment.get().mSpectrumDrawable;
        var player = MusicPlayer.getInstance();

        if (sd != null && player != null && player.isPlaying() && ClientConfig.get().musicInfo.get()) {
            var renderX = (float) this.spectrumRect.left;
            var renderY = (float) this.spectrumRect.bottom;
            var margin = 10.0f;
            var imageRadius = 70.0f;
            // ImageRender
            var currentImage = GMusic.INSTANCE.currentMImage;
            if (currentImage != null) {
                renderX += margin;
                renderY -= (margin + imageRadius);
                final var canvas = renderer._renderer();
                var paint = renderer._paint();
                paint.setRGBA(255, 255, 255, 210);
                canvas.drawImage(
                    currentImage,
                    0.0f, 0.0f, currentImage.asTextureView().getWidth(), currentImage.asTextureView().getHeight(),
                    renderX,
                    renderY,
                    renderX + imageRadius,
                    renderY + imageRadius,
                    paint
                );
            }

            var currentPlay = GMusic.INSTANCE.current;
            if (currentPlay != null) {
                // Text Render
                final var matrices = e.drawContext.getMatrices();
                var font = NText.regular;
                matrices.push();
                var name = currentPlay.name;
                var nameHeight = font.getHeight(name);
                var authorHeight = font.getHeight(currentPlay.author());
                var timeHeight = font.getHeight(player.trackTimeStr());
                var totalHeight = nameHeight + authorHeight + timeHeight + 10.0f;
                var halfHeight = totalHeight / 2;
                renderY = this.spectrumRect.bottom - margin - imageRadius / 2 - halfHeight;
                renderX += imageRadius + 10;
                font.drawWithShadow(name, renderX, renderY, Color.WHITE.getPacked());
                renderY += nameHeight + 5.0f;
                font.drawWithShadow(currentPlay.author(), renderX, renderY, Color.WHITE.getPacked());
                renderY += authorHeight + 5.0f;
                font.drawWithShadow(player.trackTimeStr(), renderX, renderY, Color.WHITE.getPacked());
                matrices.pop();
            }
        }

        if ((Utils.canUpdate()) && sd != null && player != null && ClientConfig.get().renderSpectrum.get()) {
            if (mc.currentScreen instanceof MuiScreen screen) {
                if (screen.getFragment() instanceof MainGuiFragment) return;
            }

            final var window = mc.getWindow();
            this.spectrumRect.set(0, 0, window.getFramebufferWidth(), window.getFramebufferHeight());
            sd.drawWithOut(renderer, this.spectrumRect);
        }
    }

    private void reload() {
        task.start();
        var initTime = System.currentTimeMillis();
        LOG.info("Reloading {}...", Client.NAME);
        task.push(Client.NAME + "_reload");

        Config.reloadCommon();
        Config.reloadAnyClient();

        task.pop();
        LOG.info("Done. {}ms.", System.currentTimeMillis() - initTime);
        task.done();
        task.show("Client Reload", LOG);
        task.reset();
    }

    @EventHandler
    private void onTick(TickEvent.Pre e) {
//        GChat.get().reconnectCheck();
    }

    private void shutdown() {
        task.start();
        var initTime = System.currentTimeMillis();
        LOG.info("Saving {}...", Client.NAME);
        task.push(Client.NAME + "_save");

        Systems.save(task);
        GMusic.INSTANCE.shutdown();

        task.pop();
        LOG.info("Done. {}ms.", System.currentTimeMillis() - initTime);
        task.done();
        task.show("Client Save", LOG);
        task.reset();
    }

    @Override
    protected void checkFirstLoadTypeface() {
    }
}
