package dev.undefinedteam.gensh1n.music.api.models;

import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.music.api.CryptoType;
import dev.undefinedteam.gensh1n.music.api.RequestBase;
import dev.undefinedteam.gensh1n.music.api.objs.model.SearchData;
import dev.undefinedteam.gensh1n.music.api.types.SearchType;
import lombok.AllArgsConstructor;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

@AllArgsConstructor
@StringEncryption
@ControlFlowObfuscation
public class SearchModel extends RequestBase<SearchData> {
    private final String keyword;
    private final int limit, offset;
    private final SearchType type;

    @Override
    public SearchData request() {
        var tag = new JsonObject();
        tag.addProperty("s", keyword);
        tag.addProperty("type", type.id);
        tag.addProperty("limit", limit);
        tag.addProperty("offset", offset);
        return post(tag);
    }

    @Override
    public String getUri() {
        return "/api/search/get";
    }

    @Override
    public CryptoType crypto() {
        return CryptoType.WEAPI;
    }

    @Override
    public SearchData parse(String tag) {
        return switch (type) {
            case SINGLE -> api.GSON.fromJson(tag, SearchData.SingleData.class);
            case ALBUM -> api.GSON.fromJson(tag, SearchData.AlbumData.class);
            case ARTISTS -> api.GSON.fromJson(tag, SearchData.ArtistsData.class);
            case PLAYLIST -> api.GSON.fromJson(tag, SearchData.PlayListData.class);
            case USER -> api.GSON.fromJson(tag, SearchData.UserData.class);
            case MV -> api.GSON.fromJson(tag, SearchData.MVData.class);
            case LYRICS -> api.GSON.fromJson(tag, SearchData.LyricsData.class);
            case FM -> api.GSON.fromJson(tag, SearchData.FMData.class);
            case VIDEO -> api.GSON.fromJson(tag, SearchData.VideoData.class);
        };
    }
}
