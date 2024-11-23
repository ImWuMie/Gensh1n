package dev.undefinedteam.gensh1n.music;

import com.google.gson.annotations.SerializedName;
import dev.undefinedteam.gensh1n.music.api.objs.model.SongUrlData;
import dev.undefinedteam.gensh1n.utils.json.GsonIgnore;
import lombok.AllArgsConstructor;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@StringEncryption
@ControlFlowObfuscation
public class PlayList {
    @SerializedName("curPos")
    public int cur = 0;
    @SerializedName("list")
    public List<Data> songs = new ArrayList<>();

    @AllArgsConstructor
    public static class Data {
        @SerializedName("id")
        public String id;
        @SerializedName("name")
        public String name;
        @SerializedName("author")
        public String author;
        @SerializedName("duration")
        public long duration;

        @GsonIgnore
        public ByteBuffer data;
        @GsonIgnore
        public SongUrlData.SongInfo url;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Data data = (Data) o;
            return Objects.equals(id, data.id);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }
    }
}
