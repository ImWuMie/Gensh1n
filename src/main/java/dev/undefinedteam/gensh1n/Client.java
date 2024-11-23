package dev.undefinedteam.gensh1n;

import com.google.gson.Gson;
import dev.undefinedteam.gclient.GChat;
import dev.undefinedteam.gensh1n.hotbar.SilentHotbar;
import dev.undefinedteam.gensh1n.music.GMusic;
import dev.undefinedteam.gensh1n.render.Fonts;
import dev.undefinedteam.gensh1n.render.GL;
import dev.undefinedteam.gensh1n.render._new.NText;
import dev.undefinedteam.gensh1n.rotate.RotationManager;
import dev.undefinedteam.gensh1n.system.TextReplacements;
import dev.undefinedteam.gensh1n.utils.json.GsonUtils;
import dev.undefinedteam.gensh1n.utils.network.NetPayload;
import dev.undefinedteam.gensh1n.utils.predict.ExtrapolationUtils;
import meteordevelopment.orbit.EventBus;
import meteordevelopment.orbit.IEventBus;
import net.minecraft.client.MinecraftClient;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.io.File;
import java.lang.invoke.MethodHandles;


@StringEncryption
@NativeObfuscation
@ControlFlowObfuscation
public class Client {
    @NativeObfuscation.Inline
    public static final String NAME = "Genshin";
    @NativeObfuscation.Inline
    public static final String LC_NAME = NAME.toLowerCase();
    @NativeObfuscation.Inline
    public static final String VERSION = "1.0.1";
    public static final int VERSION_ID = 11;
    @NativeObfuscation.Inline
    public static final String DEV = "wumie";
    @NativeObfuscation.Inline
    public static final String ASSETS_LOCATION = "gensh1n";
    public static final Gson GSON = GsonUtils.newBuilder().create();
    public static final MinecraftClient mc = MinecraftClient.getInstance();
    public static final File FOLDER = new File("_" + NAME + "_");
    public static final IEventBus EVENT_BUS = new EventBus();
    public static final String NAME_F = "G";
    public static final String SINGLE_SPECIAL_NAME = "\uD835\uDD72";
    public static final String FULL_UPPER1_SPECIAL_NAME = "\uD835\uDD72";
    public static final String FULL_UPPER_SPECIAL_NAME = "\uD835\uDD72\uD835\uDD70\uD835\uDD79\uD835\uDD7E\uD835\uDD73\uD835\uDD74\uD835\uDD79";
    public static final String FULL_SPECIAL_NAME = "\uD835\uDD72\uD835\uDD8A\uD835\uDD93\uD835\uDD98\uD835\uDD8D\uD835\uDD8E\uD835\uDD93";

    public static RotationManager ROT;
    public static SilentHotbar HOTBAR;

    public static boolean isOnMinecraftEnv() {
        return mc != null;
    }

    static {
        if (!FOLDER.exists()) {
            FOLDER.mkdirs();
        }

        EVENT_BUS.registerLambdaFactory("" /*"dev.undefinedteam"*/, (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
    }


    @NativeObfuscation(virtualize = NativeObfuscation.VirtualMachine.TIGER_BLACK)
    public static void init() {
        EVENT_BUS.subscribe(ExtrapolationUtils.class);
        EVENT_BUS.subscribe(NetPayload.class);
        EVENT_BUS.subscribe(TextReplacements.class);

        ROT = new RotationManager();
        HOTBAR = new SilentHotbar();

        EVENT_BUS.subscribe(ROT);
        EVENT_BUS.subscribe(HOTBAR);

        GL.init();
    }
}
