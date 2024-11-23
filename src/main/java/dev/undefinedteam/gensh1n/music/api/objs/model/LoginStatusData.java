package dev.undefinedteam.gensh1n.music.api.objs.model;

import com.google.gson.annotations.SerializedName;
import dev.undefinedteam.gensh1n.music.api.objs.Account;
import dev.undefinedteam.gensh1n.music.api.objs.UserProfile;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;


@StringEncryption
@ControlFlowObfuscation
public class LoginStatusData extends HttpData {
    @SerializedName("account")
    public Account account;
    @SerializedName("profile")
    public UserProfile profile;
}
