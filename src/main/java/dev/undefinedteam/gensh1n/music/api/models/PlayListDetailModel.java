package dev.undefinedteam.gensh1n.music.api.models;

import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.music.api.CryptoType;
import dev.undefinedteam.gensh1n.music.api.RequestBase;
import dev.undefinedteam.gensh1n.music.api.objs.model.PlayListDetailData;
import lombok.AllArgsConstructor;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

@AllArgsConstructor
@StringEncryption
@ControlFlowObfuscation
public class PlayListDetailModel extends RequestBase<PlayListDetailData> {
    private final String id;

    @Override
    public PlayListDetailData request() {
        var tag = new JsonObject();
        tag.addProperty("id", id);
        tag.addProperty("n", 100000);
        tag.addProperty("s", 8);
        return post(tag);
    }

    @Override
    public String getUri() {
        return "/api/v6/playlist/detail";
    }

    @Override
    public CryptoType crypto() {
        return CryptoType.PC_EAPI;
    }

    @Override
    public PlayListDetailData parse(String tag) {
        return api.GSON.fromJson(tag, PlayListDetailData.class);
    }
}
