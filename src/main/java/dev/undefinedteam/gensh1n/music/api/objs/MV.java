package dev.undefinedteam.gensh1n.music.api.objs;

import com.google.gson.annotations.SerializedName;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;


@StringEncryption
@ControlFlowObfuscation
public class MV {
    @SerializedName("id")
    public long id;

    @SerializedName("cover")
    public String cover;

    @SerializedName("name")
    public String name;

    @SerializedName("playCount")
    public int playCount;

    @SerializedName("artistName")
    public String artistName;

    @SerializedName("artistId")
    public long artistId;

    @SerializedName("duration")
    public long duration;

    @SerializedName("artists")
    public Artists artists;
}
