package dev.undefinedteam.gensh1n.music.api.models.login;

import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.music.api.CryptoType;
import dev.undefinedteam.gensh1n.music.api.RequestBase;
import dev.undefinedteam.gensh1n.music.api.objs.model.LoginStatusData;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;


@StringEncryption
@ControlFlowObfuscation
public class LoginStatusModel extends RequestBase<LoginStatusData> {
    @Override
    public LoginStatusData request() {
        return post(new JsonObject());
    }

    @Override
    public String getUri() {
        return "/api/w/nuser/account/get";
    }

    @Override
    public CryptoType crypto() {
        return CryptoType.WEAPI;
    }

    @Override
    public LoginStatusData parse(String tag) {
        return api.GSON.fromJson(tag, LoginStatusData.class);
    }
}
