package dev.undefinedteam.gensh1n.music.api.models;

import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.music.api.CryptoType;
import dev.undefinedteam.gensh1n.music.api.RequestBase;
import dev.undefinedteam.gensh1n.music.api.objs.model.SongUrlData;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.List;


@StringEncryption
@ControlFlowObfuscation
public class SongUrlModel extends RequestBase<SongUrlData> {
    public String[] ids;
    public int br;

    public SongUrlModel(int br, String... ids) {
        this.br = br;
        this.ids = ids;
    }

    public SongUrlModel(int br, List<String> ids) {
        this(br,ids.toArray(String[]::new));
    }

    @Override
    public SongUrlData request() {
        var tag = new JsonObject();
        tag.addProperty("ids", api.GSON.toJson(ids));
        tag.addProperty("br", br);
        return post(tag);
    }

    @Override
    public String getUri() {
        return "/api/song/enhance/player/url";
    }

    @Override
    public CryptoType crypto() {
        return CryptoType.PC_EAPI;
    }

    @Override
    public SongUrlData parse(String tag) {
        return api.GSON.fromJson(tag, SongUrlData.class);
    }
}
