package dev.undefinedteam.gensh1n.music.api.objs.model;

import com.google.gson.annotations.SerializedName;
import dev.undefinedteam.gensh1n.music.api.objs.Account;
import dev.undefinedteam.gensh1n.music.api.objs.Binding;
import dev.undefinedteam.gensh1n.music.api.objs.UserProfile;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.List;

@StringEncryption
@ControlFlowObfuscation
public class PhoneLoginData extends HttpData {
    @SerializedName("message")
    public String message;
    @SerializedName("loginType")
    public int loginType;
    @SerializedName("account")
    public Account account;
    @SerializedName("token")
    public String token;
    @SerializedName("profile")
    public UserProfile profile;
    @SerializedName("bindings")
    public List<Binding> bindings;
}
