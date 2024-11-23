package dev.undefinedteam.gensh1n.music.api;

import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.utils.network.Http;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;


@StringEncryption
@ControlFlowObfuscation
public abstract class RequestBase<T> {
    // 111

    protected final CryptoType DEFAULT_CRYPTO = CryptoType.PC_EAPI;

    protected Cookies cookies;
    protected RequestApi api;

    public abstract T request();

    public abstract String getUri();

    public abstract CryptoType crypto();

    public abstract T parse(String tag);

    protected <S> S post(JsonObject data) {
        return api.request(Http.Method.POST, this, data);
    }

    protected <S> S get(JsonObject data) {
        return api.request(Http.Method.GET, this, data);
    }
}
