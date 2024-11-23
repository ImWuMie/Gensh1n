package dev.undefinedteam.gensh1n.utils.heypixel;

import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.HashMap;
import java.util.Map;


@StringEncryption
@NativeObfuscation
@ControlFlowObfuscation
public class VIPList {
    public static final Map<String,String> vips = new HashMap<>();
    static {
        vips.put("\ue0f8", "[VIP1]");
        vips.put("\ue0f9", "[VIP2]");
        vips.put("\ue0fa", "[VIP3]");
        vips.put("\ue0fb", "[VIP4]");
        vips.put("\ue0fc", "[VIP5]");
        vips.put("\ue0fd", "[VIP6]");
        vips.put("\ue0fe", "[VIP7]");
        vips.put("\ue0ff", "[VIP8]");
    }
}
