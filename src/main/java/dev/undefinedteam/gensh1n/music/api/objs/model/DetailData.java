package dev.undefinedteam.gensh1n.music.api.objs.model;

import com.google.gson.annotations.SerializedName;
import dev.undefinedteam.gensh1n.music.api.objs.Artists;
import dev.undefinedteam.gensh1n.music.api.objs.Quality;
import dev.undefinedteam.gensh1n.utils.json.GsonIgnore;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.List;

@StringEncryption
@ControlFlowObfuscation
public class DetailData {
    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @SerializedName("ar")
    public List<Artists> artists;
    @SerializedName("al")
    public al al;

    @SerializedName("dt")
    public long duration;

    @SerializedName("h")
    private Quality h;
    @SerializedName("m")
    private Quality m;
    @SerializedName("l")
    private Quality l;
    @SerializedName("sq")
    private Quality sq;

    @GsonIgnore
    public SongUrlData.SongInfo songUrl;

    public String author() {
        StringBuilder a = new StringBuilder();
        for (var temp : artists) {
            a.append(temp.name).append("/");
        }
        return a.substring(0, a.length() - 1);
    }

    public String picUrl() {
        return al.picUrl;
    }

    public static class al {
        @SerializedName("id")
        private long id;
        @SerializedName("name")
        private String name;
        @SerializedName("picUrl")
        private String picUrl;
    }
}
