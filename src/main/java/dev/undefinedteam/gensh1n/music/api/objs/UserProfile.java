package dev.undefinedteam.gensh1n.music.api.objs;

import com.google.gson.annotations.SerializedName;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.List;

@StringEncryption
@ControlFlowObfuscation
public class UserProfile {

    @SerializedName("userId")
    public long userId;

    @SerializedName("userType")
    public int userType;

    @SerializedName("nickname")
    public String nickname;

    @SerializedName("avatarImgId")
    public long avatarImgId;

    @SerializedName("avatarUrl")
    public String avatarUrl;

    @SerializedName("backgroundImgId")
    public long backgroundImgId;

    @SerializedName("backgroundUrl")
    public String backgroundUrl;

    @SerializedName("createTime")
    public long createTime;

    @SerializedName("userName")
    public String userName;

    @SerializedName("accountType")
    public int accountType;

    @SerializedName("shortUserName")
    public String shortUserName;

    @SerializedName("birthday")
    public long birthday;

    @SerializedName("accountStatus")
    public int accountStatus;

    @SerializedName("province")
    public int province;

    @SerializedName("city")
    public int city;

    @SerializedName("djStatus")
    public int djStatus;

    @SerializedName("locationStatus")
    public int locationStatus;

    @SerializedName("vipType")
    public int vipType;

    @SerializedName("followed")
    public boolean followed;

    @SerializedName("mutual")
    public boolean mutual;

}
