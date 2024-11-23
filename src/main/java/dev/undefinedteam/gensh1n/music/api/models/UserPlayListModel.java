package dev.undefinedteam.gensh1n.music.api.models;

import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.music.api.CryptoType;
import dev.undefinedteam.gensh1n.music.api.RequestBase;
import dev.undefinedteam.gensh1n.music.api.objs.model.UserPlayListData;
import lombok.AllArgsConstructor;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

@AllArgsConstructor
@StringEncryption
@ControlFlowObfuscation
public class UserPlayListModel extends RequestBase<UserPlayListData> {
    private final long uid;
    private final int offset;
    private final int limit;

    @Override
    public UserPlayListData request() {
        var tag = new JsonObject();
        tag.addProperty("uid", String.valueOf(uid));
        tag.addProperty("offset", String.valueOf(offset));
        tag.addProperty("limit", String.valueOf(limit));
        return post(tag);
    }

    @Override
    public String getUri() {
        return "/api/user/playlist";
    }

    @Override
    public CryptoType crypto() {
        return CryptoType.PC_EAPI;
    }

    @Override
    public UserPlayListData parse(String tag) {
        return api.GSON.fromJson(tag, UserPlayListData.class);
    }
}
