package dev.undefinedteam.gensh1n.music.api.models.login;

import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.music.api.CryptoType;
import dev.undefinedteam.gensh1n.music.api.RequestBase;
import dev.undefinedteam.gensh1n.music.api.objs.model.QrCheckData;
import lombok.AllArgsConstructor;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

@AllArgsConstructor
@StringEncryption
@ControlFlowObfuscation
public class QrCheckModel extends RequestBase<QrCheckData> {
    private final String key;

    @Override
    public QrCheckData request() {
        var tag = new JsonObject();
        tag.addProperty("key", key);
        tag.addProperty("type", 3);
        return post(tag);
    }

    @Override
    public String getUri() {
        return "/api/login/qrcode/client/login";
    }

    @Override
    public CryptoType crypto() {
        return CryptoType.PC_EAPI;
    }

    @Override
    public QrCheckData parse(String tag) {
        return api.GSON.fromJson(tag, QrCheckData.class);
    }
}
