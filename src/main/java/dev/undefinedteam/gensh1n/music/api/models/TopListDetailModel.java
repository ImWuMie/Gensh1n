package dev.undefinedteam.gensh1n.music.api.models;

import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.music.api.CryptoType;
import dev.undefinedteam.gensh1n.music.api.RequestBase;
import dev.undefinedteam.gensh1n.music.api.objs.model.TopListData;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;


@StringEncryption
@ControlFlowObfuscation
public class TopListDetailModel extends RequestBase<TopListData> {
    @Override
    public TopListData request() {
        return post(new JsonObject());
    }

    @Override
    public String getUri() {
        return "/api/toplist/detail/v2";
    }

    @Override
    public CryptoType crypto() {
        return CryptoType.PC_EAPI;
    }

    @Override
    public TopListData parse(String tag) {
        return api.GSON.fromJson(tag, TopListData.class);
    }
}
