package dev.undefinedteam.gensh1n.music.api;

import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

@StringEncryption
@ControlFlowObfuscation
public enum CryptoType {
    LinuxAPI,
    WEAPI,
    PC_EAPI,
    ANDROID_EAPI,
    API
}
