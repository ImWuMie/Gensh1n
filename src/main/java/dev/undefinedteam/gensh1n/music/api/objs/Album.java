package dev.undefinedteam.gensh1n.music.api.objs;

import com.google.gson.annotations.SerializedName;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

@StringEncryption
@ControlFlowObfuscation
public class Album {
    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @SerializedName("artist")
    public Artists artist;
    @SerializedName("publishTime")
    public long publishTime;
    @SerializedName("size")
    public int size;
    @SerializedName("copyrightId")
    public int copyrightId;
    @SerializedName("status")
    public int status;
    @SerializedName("picId")
    public long picId;
    @SerializedName("mark")
    public long mark;
    @SerializedName("picUrl")
    private String picUrl;
}
