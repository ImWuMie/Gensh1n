package dev.undefinedteam.gensh1n.music.api.objs.lyric;

import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@StringEncryption
@ControlFlowObfuscation
public class SongLyric {
    public final List<LyricLine> lines = new ArrayList<>();

    private final List<LyricLine> reversed = new ArrayList<>();

    public int index(LyricLine cur) {
        return lines.indexOf(cur);
    }

    public int index(int ms) {
        return lines.indexOf(line(ms));
    }

    public LyricLine prev(LyricLine cur) {
        int index = lines.indexOf(cur) - 1;
        if (index < 0) return null;
        return lines.get(index);
    }

    public LyricLine next(LyricLine cur) {
        int index = lines.indexOf(cur) + 1;
        if (index >= lines.size()) return null;
        return lines.get(index);
    }

    public LyricLine line(int ms) {
        for (LyricLine line : reversed()) {
            if (line.start <= ms)
                return line;
        }
        return null;
    }

    public String text(int ms) {
        return line(ms).text();
    }

    public double progress(int ms) {
        var line = line(ms);
        return line == null ? 1 : line.calc(ms);
    }

    public List<LyricLine> reversed() {
        if (reversed.isEmpty() && !lines.isEmpty()) {
            reversed.addAll(this.lines);
            Collections.reverse(reversed);
        }
        return reversed;
    }
}
