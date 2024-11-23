package dev.undefinedteam.gensh1n.music.api.objs;

import com.google.gson.annotations.SerializedName;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;


@StringEncryption
@ControlFlowObfuscation
public class Binding {
    @SerializedName("bindingTime")
    public long bindingTime;
    @SerializedName("refreshTime")
    public long refreshTime;
    @SerializedName("tokenJsonStr")
    public String tokenJsonStr;
    @SerializedName("expiresIn")
    public long expiresIn;
    @SerializedName("url")
    public String url;
    @SerializedName("expired")
    public boolean expired;
    @SerializedName("userId")
    public long userId;
    @SerializedName("id")
    public long id;
    @SerializedName("type")
    public int type;
}
