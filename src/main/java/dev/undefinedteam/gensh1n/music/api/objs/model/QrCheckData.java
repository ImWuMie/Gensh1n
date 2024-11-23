package dev.undefinedteam.gensh1n.music.api.objs.model;

import com.google.gson.annotations.SerializedName;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;


@StringEncryption
@ControlFlowObfuscation
public class QrCheckData extends HttpData {
    @SerializedName("message")
    public String message;

    @SerializedName("avatarUrl")
    public String avatarUrl;
    @SerializedName("nickname")
    public String nickname;

    public boolean completed() {
        return this.code == 803;
    }
}
