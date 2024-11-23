package dev.undefinedteam.gensh1n.music.api.objs.lyric;

import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;


@StringEncryption
@ControlFlowObfuscation
public class LyricChar {
    public long start;
    public long duration;
    public final String chars;

    public LyricChar(long start, long duration, String chars) {
        this.start = start;
        this.duration = duration;
        this.chars = chars;
    }
}
