package dev.undefinedteam.gensh1n.music.api.objs;

import com.google.gson.annotations.SerializedName;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;


@StringEncryption
@ControlFlowObfuscation
public class Artists {
    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @SerializedName("picUrl")
    public String picUrl;
    @SerializedName("albumSize")
    public int albumSize;
}
