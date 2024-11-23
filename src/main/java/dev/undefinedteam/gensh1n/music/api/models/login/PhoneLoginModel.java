package dev.undefinedteam.gensh1n.music.api.models.login;

import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.music.api.CryptoType;
import dev.undefinedteam.gensh1n.music.api.RequestBase;
import dev.undefinedteam.gensh1n.music.api.objs.model.PhoneLoginData;
import lombok.AllArgsConstructor;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

@AllArgsConstructor
@StringEncryption
@ControlFlowObfuscation
public class PhoneLoginModel extends RequestBase<PhoneLoginData> {
    private String phone;
    private String captcha;

    @Override
    public PhoneLoginData request() {
        var tag = new JsonObject();
        tag.addProperty("type","1");
        tag.addProperty("phone", phone);
        tag.addProperty("captcha", captcha);
        tag.addProperty("remember", "true");
        tag.addProperty("https", "true");
        tag.addProperty("countrycode", "86");
        return post(tag);
    }

    @Override
    public String getUri() {
        return "/api/w/login/cellphone";
    }

    @Override
    public CryptoType crypto() {
        return CryptoType.PC_EAPI;
    }

    @Override
    public PhoneLoginData parse(String tag) {
        return api.GSON.fromJson(tag, PhoneLoginData.class);
    }
}
