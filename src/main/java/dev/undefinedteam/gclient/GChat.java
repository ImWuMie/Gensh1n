package dev.undefinedteam.gclient;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.undefinedteam.gclient.assets.AssetsManager;
import dev.undefinedteam.gclient.data.GsonUtils;
import dev.undefinedteam.gclient.text.Style;
import dev.undefinedteam.gclient.text.Text;
import dev.undefinedteam.gensh1n.system.ClientConfig;
import dev.undefinedteam.gensh1n.utils.chat.ChatUtils;
import dev.undefinedteam.gensh1n.utils.network.NetPayload;
import org.apache.commons.codec.binary.Base64;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.Executor;


@StringEncryption
@NativeObfuscation
@ControlFlowObfuscation
public class GChat {
    @NativeObfuscation.Inline
    public static final int VERSION_ID = 10;
    public static GChat INSTANCE;
    public final File FOLDER = new File("_Genshin_", "gchat");
    public final File CFG = new File(FOLDER, "user.json");
    @NativeObfuscation.Inline
    public static final String SPECIAL_NAME = "GChat";
    @NativeObfuscation.Inline
    public static final Text LOG_PREFIX = Text.of(SPECIAL_NAME).setStyle(Style.EMPTY.withFormatting(Formatting.GOLD)).append(Text.of(">>").setStyle(Style.EMPTY.withFormatting(Formatting.BOLD, Formatting.GRAY)));//Formatting.GOLD + SPECIAL_NAME + Formatting.BOLD + Formatting.GRAY + ">>";
    public static final Gson GSON = GsonUtils.newBuilder().create();

    public boolean success = true;

    public String username, passwd;

    public Timer timer = new Timer();
    private final AsyncWorkerThread worker = new AsyncWorkerThread();
    public final Executor executor = worker::submit;

    public GChat() {
        INSTANCE = this;
        worker.start();
        if (!FOLDER.exists()) FOLDER.mkdirs();

        try {
            if (CFG.exists()) {
                JsonObject object = new JsonParser().parse(new BufferedReader(new FileReader(CFG))).getAsJsonObject();
                this.username = object.get("name").getAsString();
                this.passwd = new String(Base64.decodeBase64(object.get("d").getAsString()), StandardCharsets.UTF_8);
            }

            GCClient.get().init();
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }
    }

    public static GChat get() {
        if (INSTANCE == null) {
            INSTANCE = new GChat();
        }
        return INSTANCE;
    }


    public void startClient() throws IOException {
        if (success) {
            GCClient.get().connect();
        }
    }


    public void reconnectCheck() {
        if (success) {
            if (GCClient.INSTANCE.session() == null && ClientConfig.get() != null && timer.check(ClientConfig.get().reconnectDelay.get())) {
                GCClient.get().connect();
                NetPayload.pre(() -> ChatUtils.info("尝试重新连接GChat"));
                timer.reset();
            }
        }
    }

    public void save() throws IOException {
        AssetsManager.INSTANCE.save();
        saveUser();
    }

    public void saveUser() throws IOException {
        JsonObject data = new JsonObject();
        data.addProperty("name", this.username);
        data.addProperty("d", Base64.encodeBase64String(this.passwd.getBytes(StandardCharsets.UTF_8)));
        if (!CFG.exists()) CFG.createNewFile();

        Files.writeString(CFG.toPath(), GSON.toJson(data), StandardCharsets.UTF_8);
    }
}
