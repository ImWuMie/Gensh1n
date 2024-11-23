package dev.undefinedteam.gensh1n.music.api.objs;

import com.google.gson.annotations.SerializedName;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;


@StringEncryption
@ControlFlowObfuscation
public class DjRadio {
    @SerializedName("id")
    public int id;

    @SerializedName("dj")
    public UserProfile dj;

    @SerializedName("name")
    public String name;

    @SerializedName("picUrl")
    public String picUrl;

    @SerializedName("desc")
    public String desc;

    @SerializedName("subCount")
    public int subCount;

    @SerializedName("programCount")
    public int programCount;

    @SerializedName("createTime")
    public long createTime;

    @SerializedName("categoryId")
    public int categoryId;

    @SerializedName("category")
    public String category;

    @SerializedName("secondCategoryId")
    public int secondCategoryId;

    @SerializedName("secondCategory")
    public String secondCategory;

    @SerializedName("radioFeeType")
    public int radioFeeType;

    @SerializedName("feeScope")
    public int feeScope;

    @SerializedName("buyed")
    public boolean buyed;

    @SerializedName("finished")
    public boolean finished;

    @SerializedName("underShelf")
    public boolean underShelf;

    @SerializedName("purchaseCount")
    public int purchaseCount;

    @SerializedName("price")
    public int price;

    @SerializedName("originalPrice")
    public int originalPrice;

    @SerializedName("discountPrice")
    public Object discountPrice; // Assuming it could be null or another type, use Object for flexibility

    @SerializedName("lastProgramCreateTime")
    public long lastProgramCreateTime;

    @SerializedName("lastProgramName")
    public String lastProgramName;

    @SerializedName("lastProgramId")
    public long lastProgramId;

    @SerializedName("picId")
    public long picId;

    @SerializedName("hightQuality")
    public boolean hightQuality;

    @SerializedName("whiteList")
    public boolean whiteList;

    @SerializedName("playCount")
    public int playCount;

    @SerializedName("privacy")
    public boolean privacy;

    @SerializedName("intervenePicUrl")
    public String intervenePicUrl;

    @SerializedName("composeVideo")
    public boolean composeVideo;

    @SerializedName("shareCount")
    public int shareCount;

    @SerializedName("likedCount")
    public int likedCount;

    @SerializedName("alg")
    public String alg;

    @SerializedName("commentCount")
    public int commentCount;
}
