package dev.undefinedteam.gensh1n.music.api.objs;

import com.google.gson.annotations.SerializedName;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.List;

@StringEncryption
@ControlFlowObfuscation
public class Song {
    @SerializedName("id")
    public long id;
    @SerializedName("name")
    public String name;
    @SerializedName("artists")
    public List<Artists> artists;
    @SerializedName("album")
    public Album album;
    @SerializedName("duration")
    public long duration;
    @SerializedName("copyrightId")
    public int copyrightId;
    @SerializedName("status")
    public int status;
    @SerializedName("mark")
    public long mark;

    public String author() {
        StringBuilder a = new StringBuilder();
        for (var temp : artists) {
            a.append(temp.name).append("/");
        }
        return a.substring(0, a.length() - 1);
    }
}
