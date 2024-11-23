package dev.undefinedteam.gensh1n.music.api.objs.model;

import com.google.gson.annotations.SerializedName;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.List;

@StringEncryption
@ControlFlowObfuscation
public class TopListData extends HttpData {
    @SerializedName("msg")
    public String msg;

    @SerializedName("data")
    public List<TopInfo> data;

    public static class TopInfo {
        @SerializedName("name")
        public String name;
        @SerializedName("categoryCode")
        public String categoryCode;
        @SerializedName("list")
        public List<TopData> list;
    }

    public static class TopData {
        @SerializedName("name")
        public String name;
        @SerializedName("updateFrequency")
        public String updateFrequency;
        @SerializedName("coverUrl")
        public String coverUrl;
        @SerializedName("tracks")
        public List<TrackName> trackNames;
        @SerializedName("trackRankList")
        public List<TrackData> tracks;
        @SerializedName("category")
        public String category;
    }

    public static class TrackName {
        @SerializedName("first")
        public String name;
        @SerializedName("second")
        public String author;
    }

    public static class TrackData {
        @SerializedName("trackId")
        public long trackId;
        @SerializedName("songName")
        public String songName;
        @SerializedName("artistName")
        public String artistName;
        @SerializedName("coverImgUrl")
        public String coverImgUrl;
    }
}
