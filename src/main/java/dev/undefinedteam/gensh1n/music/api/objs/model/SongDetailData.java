package dev.undefinedteam.gensh1n.music.api.objs.model;

import com.google.gson.annotations.SerializedName;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.List;

@StringEncryption
@ControlFlowObfuscation
public class SongDetailData extends HttpData {
    @SerializedName("songs")
    public List<DetailData> songs;

    public DetailData first() {
        return songs == null ? null : songs.getFirst();
    }

    public DetailData get(String id) {
        return songs.stream().filter(s -> s.id.equals(id)).findFirst().orElse(null);
    }

    public DetailData get(long id) {
        return get(String.valueOf(id));
    }
}
