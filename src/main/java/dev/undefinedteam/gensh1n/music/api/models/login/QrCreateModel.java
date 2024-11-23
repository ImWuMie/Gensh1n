package dev.undefinedteam.gensh1n.music.api.models.login;

import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.music.api.CryptoType;
import dev.undefinedteam.gensh1n.music.api.RequestBase;
import dev.undefinedteam.gensh1n.music.api.objs.model.QrCreateData;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;


@StringEncryption
@ControlFlowObfuscation
public class QrCreateModel extends RequestBase<QrCreateData> {
    @Override
    public QrCreateData request() {
        var tag = new JsonObject();
        tag.addProperty("type", 3);
        return post(tag);
    }

    @Override
    public String getUri() {
        return "/api/login/qrcode/unikey";
    }

    @Override
    public CryptoType crypto() {
        return CryptoType.PC_EAPI;
    }

    @Override
    public QrCreateData parse(String tag) {
        return api.GSON.fromJson(tag, QrCreateData.class);
    }
}
