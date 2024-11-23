package dev.undefinedteam.gensh1n.music.api.objs;

import com.google.gson.annotations.SerializedName;
import dev.undefinedteam.gensh1n.music.api.RequestApi;
import dev.undefinedteam.gensh1n.music.api.objs.model.DetailData;
import dev.undefinedteam.gensh1n.music.api.objs.model.PlayListDetailData;
import dev.undefinedteam.gensh1n.utils.json.GsonIgnore;
import icyllis.modernui.graphics.BitmapFactory;
import icyllis.modernui.graphics.Image;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.io.IOException;
import java.util.List;

@StringEncryption
@ControlFlowObfuscation
public class PlayList {
    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @SerializedName("description")
    public String description;
    @SerializedName("coverImgUrl")
    public String coverImgUrl;
    @SerializedName("creator")
    public Creator creator;
    @SerializedName("tracks")
    public List<DetailData> tracks;
    @SerializedName("subscribed")
    public boolean subscribed;
    @SerializedName("trackCount")
    public int trackCount;
    @SerializedName("playCount")
    public int playCount;

    public static class Creator {
        @SerializedName("userId")
        public String userId;
        @SerializedName("nickname")
        public String nickname;
        @SerializedName("avatarUrl")
        public String avatarUrl;
    }

    @GsonIgnore
    public PlayListDetailData detailData;
    @GsonIgnore
    public Image image;

    public void load(RequestApi api) {
        this.detailData = api.playlistDetail(this.id);
    }

    public void loadImage(byte[] bytes) throws IOException {
        this.image = Image.createTextureFromBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
    }

    public void fillPlayList(dev.undefinedteam.gensh1n.music.PlayList list) {
        list.cur = 0;
        list.songs.clear();
        if (this.detailData.songs() != null) {
            for (var data : this.detailData.songs()) {
                list.songs.add(new dev.undefinedteam.gensh1n.music.PlayList.Data(String.valueOf(data.id), data.name, data.author(), data.duration, null,null));
            }
        }
    }
}
