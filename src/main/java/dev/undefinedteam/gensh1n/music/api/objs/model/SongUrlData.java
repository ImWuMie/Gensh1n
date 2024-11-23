package dev.undefinedteam.gensh1n.music.api.objs.model;

import com.google.gson.annotations.SerializedName;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.List;

@StringEncryption
@ControlFlowObfuscation
public class SongUrlData extends HttpData {
    @SerializedName("data")
    public List<SongInfo> data;

    public SongInfo first() {
        return data == null ? null : data.getFirst();
    }

    public SongInfo get(long id) {
        return data.stream().filter(s -> s.id == id).findFirst().orElse(null);
    }

    public SongInfo get(String id) {
        return data.stream().filter(s -> String.valueOf(s.id).equals(id)).findFirst().orElse(null);
    }

    public static class SongInfo {
        @SerializedName("id")
        public long id;
        @SerializedName("url")
        public String url;
        @SerializedName("br")
        public int br;
        @SerializedName("size")
        public int size;
        @SerializedName("md5")
        public String md5;
        @SerializedName("encodeType")
        public String encodeType;
    }
}
