package dev.undefinedteam.gensh1n.music.api.models;

import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.music.api.CryptoType;
import dev.undefinedteam.gensh1n.music.api.RequestBase;
import dev.undefinedteam.gensh1n.music.api.objs.model.LyricData;
import lombok.AllArgsConstructor;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

@AllArgsConstructor
@StringEncryption
@ControlFlowObfuscation
public class LyricNewModel extends RequestBase<LyricData> {
    private final String id;

    @Override
    public LyricData request() {
        var tag = new JsonObject();
        tag.addProperty("id", id);
        tag.addProperty("cp", false);
        tag.addProperty("tv", 0);
        tag.addProperty("lv", 0);
        tag.addProperty("rv", 0);
        tag.addProperty("kv", 0);
        tag.addProperty("yv", 0);
        tag.addProperty("ytv", 0);
        tag.addProperty("yrv", 0);
        return post(tag);
    }

    @Override
    public String getUri() {
        return "/api/song/lyric/v1";
    }

    @Override
    public CryptoType crypto() {
        return CryptoType.PC_EAPI;
    }

    @Override
    public LyricData parse(String tag) {
        return api.GSON.fromJson(tag, LyricData.class);
    }
}
