package dev.undefinedteam.gclient.data;

import com.google.gson.annotations.SerializedName;
import icyllis.modernui.graphics.Image;

public class AssetData {
    @SerializedName("location")
    public String location;
    @SerializedName("md5")
    public String md5Hex;
    @GsonIgnore
    public byte[] data;
    @GsonIgnore
    public Image image;
}
