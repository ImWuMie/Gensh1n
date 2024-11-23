package dev.undefinedteam.gensh1n.music.api.objs;

import com.google.gson.annotations.SerializedName;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

@StringEncryption
@ControlFlowObfuscation
public class Quality {
    @SerializedName("br")
    private int bitrate;
    @SerializedName("size")
    private int size;
}
