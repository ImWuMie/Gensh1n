package dev.undefinedteam.gensh1n.music.api.models.login;

import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.music.api.CryptoType;
import dev.undefinedteam.gensh1n.music.api.RequestBase;
import dev.undefinedteam.gensh1n.music.api.objs.model.HttpData;

public class LogoutModel extends RequestBase<HttpData> {
    @Override
    public HttpData request() {
        return post(new JsonObject());
    }

    @Override
    public String getUri() {
        return "/api/logout";
    }

    @Override
    public CryptoType crypto() {
        return CryptoType.WEAPI;
    }

    @Override
    public HttpData parse(String tag) {
        return api.GSON.fromJson(tag,HttpData.class);
    }
}
