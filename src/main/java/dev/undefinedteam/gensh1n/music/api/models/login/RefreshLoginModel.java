package dev.undefinedteam.gensh1n.music.api.models.login;

import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.music.api.CryptoType;
import dev.undefinedteam.gensh1n.music.api.RequestBase;
import dev.undefinedteam.gensh1n.music.api.objs.model.StatusData;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;


@StringEncryption
@ControlFlowObfuscation
public class RefreshLoginModel extends RequestBase<StatusData> {
    @Override
    public StatusData request() {
        var tag = new JsonObject();
        return post(tag);  // Sending an empty JSON object as the body.
    }

    @Override
    public String getUri() {
        return "/api/login/token/refresh";
    }

    @Override
    public CryptoType crypto() {
        return CryptoType.PC_EAPI;
    }

    @Override
    public StatusData parse(String tag) {
        return api.GSON.fromJson(tag, StatusData.class);
    }
}
