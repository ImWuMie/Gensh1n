package dev.undefinedteam.gensh1n.music.api.objs.model;

import com.google.gson.annotations.SerializedName;
import dev.undefinedteam.gensh1n.music.api.objs.*;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.List;

@StringEncryption
@ControlFlowObfuscation
public class SearchData extends HttpData {
    public static class SingleData extends SearchData {
        @SerializedName("result")
        public Result songs;

        public static class Result {
            @SerializedName("songs")
            public List<Song> songs;
        }
    }

    public static class AlbumData extends SearchData {
        @SerializedName("hlWords")
        public String[] hlWords;
        @SerializedName("result")
        public Result albums;
        @SerializedName("albumCount")
        public int albumCount;

        public static class Result {
            @SerializedName("hasMore")
            public boolean hasMore;
            @SerializedName("albums")
            public List<Album> songs;
        }
    }

    public static class ArtistsData extends SearchData {
        @SerializedName("result")
        public Result artists;

        public static class Result {
            @SerializedName("hasMore")
            public boolean hasMore;
            @SerializedName("artistCount")
            public int artistCount;
            @SerializedName("artists")
            public List<Artists> artists;
        }
    }

    public static class PlayListData extends SearchData {
        @SerializedName("result")
        public Result albums;

        public static class Result {
            @SerializedName("hasMore")
            public boolean hasMore;
            @SerializedName("playlists")
            public List<PlayList> artists;
            @SerializedName("playlistCount")
            public int playlistCount;
        }
    }

    public static class UserData extends SearchData {
        @SerializedName("result")
        public Result users;

        public static class Result {
            @SerializedName("hasMore")
            public boolean hasMore;
            @SerializedName("userprofiles")
            public List<UserProfile> profiles;
            @SerializedName("userprofileCount")
            public int userprofileCount;
        }
    }

    public static class MVData extends SearchData {
        @SerializedName("result")
        public Result users;

        public static class Result {
            @SerializedName("mvs")
            public List<MV> mvs;
            @SerializedName("mvCount")
            public int mvCount;
        }
    }

    public static class LyricsData extends SearchData {
        @SerializedName("result")
        public Result users;

        public static class Result {
            @SerializedName("songs")
            public List<Song> songs;
            @SerializedName("songCount")
            public int songCount;
        }
    }

    public static class FMData extends SearchData {
        @SerializedName("result")
        public Result users;

        public static class Result {
            @SerializedName("djRadios")
            public List<DjRadio> djRadios;
            @SerializedName("djRadiosCount")
            public int djRadiosCount;
        }
    }

    public static class VideoData extends SearchData {
        @SerializedName("result")
        public Result users;

        public static class Result {
            @SerializedName("hasMore")
            public boolean hasMore;
            @SerializedName("videos")
            public List<Video> videos;
            @SerializedName("videoCount")
            public int videoCount;
        }
    }
}
