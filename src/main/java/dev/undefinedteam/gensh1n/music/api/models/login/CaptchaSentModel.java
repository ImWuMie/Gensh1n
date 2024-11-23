package dev.undefinedteam.gensh1n.music.api.models.login;

import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.music.api.CryptoType;
import dev.undefinedteam.gensh1n.music.api.RequestBase;
import dev.undefinedteam.gensh1n.music.api.objs.model.StatusData;
import lombok.AllArgsConstructor;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

@AllArgsConstructor
@StringEncryption
@ControlFlowObfuscation
public class CaptchaSentModel extends RequestBase<StatusData> {
    private final String phone;

    @Override
    public StatusData request() {
        var tag = new JsonObject();
        tag.addProperty("cellphone", phone);
        tag.addProperty("ctcode", "86");
        return post(tag);
    }

    @Override
    public String getUri() {
        return "/api/sms/captcha/sent";
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
