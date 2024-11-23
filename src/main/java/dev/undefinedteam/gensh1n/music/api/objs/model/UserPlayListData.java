package dev.undefinedteam.gensh1n.music.api.objs.model;

import com.google.gson.annotations.SerializedName;
import dev.undefinedteam.gensh1n.music.api.objs.PlayList;
import lombok.ToString;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.List;


@ToString
@StringEncryption
@ControlFlowObfuscation
public class UserPlayListData extends HttpData {
    @SerializedName("version")
    public String version;
    @SerializedName("more")
    public boolean hasMore;

    @SerializedName("playlist")
    public List<PlayList> list;
}
