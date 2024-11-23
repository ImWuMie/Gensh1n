package dev.undefinedteam.gensh1n.music.api.models;

import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.music.api.CryptoType;
import dev.undefinedteam.gensh1n.music.api.RequestBase;
import dev.undefinedteam.gensh1n.music.api.objs.model.SongDetailData;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.ArrayList;
import java.util.List;


@StringEncryption
@ControlFlowObfuscation
public class SongDetailModel extends RequestBase<SongDetailData> {
    public List<String> ids;

    public SongDetailModel(long... ids) {
        var list = new ArrayList<String>();
        for (long id : ids) {
            list.add(String.valueOf(id));
        }
        this.ids = list;
    }

    public SongDetailModel(String... ids) {
        this(List.of(ids));
    }

    public SongDetailModel(List<String> ids) {
        this.ids = ids;
    }

    @Override
    public SongDetailData request() {
        var tag = new JsonObject();
        tag.addProperty("c", buildIds());
        return post(tag);
    }

    private String buildIds() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (String id : ids) {
            builder.append("{\"id\":\"").append(id).append("\"").append(",").append("\"v\":0").append("}");
            builder.append(",");
        }
        builder.setLength(builder.length() - 1);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public String getUri() {
        return "/api/v3/song/detail";
    }

    @Override
    public CryptoType crypto() {
        return CryptoType.PC_EAPI;
    }

    @Override
    public SongDetailData parse(String tag) {
        return api.GSON.fromJson(tag, SongDetailData.class);
    }
}
