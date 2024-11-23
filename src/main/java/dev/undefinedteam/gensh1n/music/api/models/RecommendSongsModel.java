package dev.undefinedteam.gensh1n.music.api.models;

import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.music.api.CryptoType;
import dev.undefinedteam.gensh1n.music.api.RequestBase;
import dev.undefinedteam.gensh1n.music.api.objs.model.RecommendData;
import lombok.AllArgsConstructor;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

@AllArgsConstructor
@StringEncryption
@ControlFlowObfuscation
public class RecommendSongsModel extends RequestBase<RecommendData> {
    private final int limit;

    @Override
    public RecommendData request() {
        var tag = new JsonObject();
        tag.addProperty("limit", String.valueOf(limit));
        return post(tag);
    }

    @Override
    public String getUri() {
        return "/api/v1/discovery/recommend/songs";
    }

    @Override
    public CryptoType crypto() {
        return CryptoType.PC_EAPI;
    }

    @Override
    public RecommendData parse(String tag) {
        return api.GSON.fromJson(tag, RecommendData.class);
    }
}
