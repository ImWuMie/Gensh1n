package dev.undefinedteam.gensh1n.music.api;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;


@StringEncryption
@ControlFlowObfuscation
@AllArgsConstructor
public class EncryptData {
    @SerializedName("params")
    public String params;
    @SerializedName("encSecKey")
    public String key;
}
