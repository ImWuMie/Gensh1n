package dev.undefinedteam.gensh1n.music.api.objs;

import com.google.gson.annotations.SerializedName;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;


@StringEncryption
@ControlFlowObfuscation
public class Account {
    @SerializedName("id")
    public long id;
    @SerializedName("username")
    public String username;
    @SerializedName("type")
    public int type;
    @SerializedName("status")
    public int status;
    @SerializedName("whitelistAuthority")
    public int whitelistAuthority;
    @SerializedName("createTime")
    public long createTime;
    @SerializedName("salt")
    public String salt;
    @SerializedName("tokenVersion")
    public int tokenVersion;
    @SerializedName("ban")
    public int ban;
    @SerializedName("baoyueVersion")
    public int baoyueVersion;
    @SerializedName("donateVersion")
    public int donateVersion;
    @SerializedName("vipType")
    public int vipType;
    @SerializedName("viptypeVersion")
    public long viptypeVersion;
    @SerializedName("anonimousUser")
    public boolean anonimousUser;
}
