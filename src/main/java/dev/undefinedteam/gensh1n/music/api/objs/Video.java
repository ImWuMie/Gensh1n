package dev.undefinedteam.gensh1n.music.api.objs;

import com.google.gson.annotations.SerializedName;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.List;

@StringEncryption
@ControlFlowObfuscation
public class Video {
    @SerializedName("coverUrl")
    public String coverUrl;

    @SerializedName("title")
    public String title;

    @SerializedName("durationms")
    public int durationMs;

    @SerializedName("playTime")
    public int playTime;

    @SerializedName("type")
    public int type;

    @SerializedName("creator")
    public List<Creator> creator;

    @SerializedName("vid")
    public String vid;

    @SerializedName("alg")
    public String alg;

    public static class Creator {
        @SerializedName("userId")
        public int userId;

        @SerializedName("userName")
        public String userName;
    }
}
