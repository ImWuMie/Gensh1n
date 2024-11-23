package dev.undefinedteam.gclient.assets;

import dev.undefinedteam.gclient.GChat;
import dev.undefinedteam.gclient.data.AssetData;
import dev.undefinedteam.gclient.data.AssetsSet;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static dev.undefinedteam.gclient.GChat.GSON;

@StringEncryption
@ControlFlowObfuscation
public class AssetsManager {
    public static AssetsManager INSTANCE;
    public final File FOLDER = new File(GChat.INSTANCE.FOLDER, "assets");
    public final File CFG = new File(FOLDER, "assets.json");
    public final List<AssetData> missingData = new ArrayList<>();
    public AssetsSet assets;

    public AssetsManager() {
        INSTANCE = this;
    }

    public void init() throws IOException {
        if (!FOLDER.exists()) FOLDER.mkdirs();

        this.assets = new AssetsSet();
        missingData.clear();

        if (CFG.exists()) {
            AssetsSet assetsSet = GSON.fromJson(Files.readString(CFG.toPath(), StandardCharsets.UTF_8), AssetsSet.class);
            this.assets.addAll(assetsSet.assetData);
            for (AssetData assetDatum : assets.assetData) {
                AssetData data;
                if ((data = assetsSet.find(assetDatum.location)) != null) {
                    assetDatum.md5Hex = data.md5Hex;
                }

                File target = new File(FOLDER, assetDatum.md5Hex);
                if (!target.exists()) {
                    missingData.add(assetDatum);
                }
            }

            CompletableFuture.runAsync(() -> {
                for (AssetData assetDatum : assets.assetData) {
                    if (assetDatum != null) {
                        File target = new File(FOLDER, assetDatum.md5Hex);
                        if (target.exists()) {
                            try {
                                assetDatum.data = Files.readAllBytes(target.toPath());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }, GChat.get().executor);
        } else save();
    }

    public void add(String location, String md5, byte[] data) {
        var assets = find(location);
        if (assets == null) {
            assets = new AssetData();
            assets.location = location;
            assets.md5Hex = md5;
            assets.data = data;
            this.assets.add(assets);
        } else {
            assets.md5Hex = md5;
            assets.data = data;
        }

        missingData.remove(assets);
        saveAssets();
    }

    public void saveAssets() {
        CompletableFuture.runAsync(() -> {
            try {
                save();
                for (AssetData assetDatum : assets.assetData) {
                    if (assetDatum != null) {
                        File target = new File(FOLDER, assetDatum.md5Hex);
                        if (!target.exists()) {

                            target.createNewFile();
                            Files.write(target.toPath(), assetDatum.data);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, GChat.get().executor);
    }

    public void save() throws IOException {
        String json = GSON.toJson(this.assets);
        if (!CFG.exists()) CFG.createNewFile();
        Files.writeString(CFG.toPath(), json, StandardCharsets.UTF_8);
    }

    public AssetData find(String location) {
        return assets.find(location);
    }
}
