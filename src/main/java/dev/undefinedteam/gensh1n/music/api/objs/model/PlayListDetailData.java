package dev.undefinedteam.gensh1n.music.api.objs.model;

import com.google.gson.annotations.SerializedName;
import dev.undefinedteam.gensh1n.music.api.RequestApi;
import dev.undefinedteam.gensh1n.music.api.objs.PlayList;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.List;

@StringEncryption
@ControlFlowObfuscation
public class PlayListDetailData extends HttpData {
    @SerializedName("playlist")
    public PlayList playList;

    public List<String> allIds() {
        return playList.tracks.stream().map(s -> String.valueOf(s.id)).toList();
    }

    public void loadUrls(RequestApi api) {
        var data = api.songUrl(allIds());
        for (var song : playList.tracks) {
            song.songUrl = data.get(song.id);
        }
    }

    public List<DetailData> songs() {
        return playList.tracks;
    }
}
