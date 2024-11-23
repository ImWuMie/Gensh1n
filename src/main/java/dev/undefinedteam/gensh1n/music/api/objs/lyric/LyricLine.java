package dev.undefinedteam.gensh1n.music.api.objs.lyric;

import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@StringEncryption
@ControlFlowObfuscation
public class LyricLine {
    public final long start;
    public long duration;

    public float tempWidth = 200;
    public float tempHeight = 20;
    public float tempLyricHeight = 20;
    public float tempLyricWidth = 20;
    public float temptLyricWidth = 20;

    public final List<LyricChar> lyricChars;

    private final List<LyricChar> reversed = new ArrayList<>();

    public LyricLine(long start, long duration) {
        this.start = start;
        this.duration = duration;
        this.lyricChars = new ArrayList<>();
        reversed.addAll(lyricChars);
        Collections.reverse(reversed);
    }

    public LyricLine(long start, long duration, List<LyricChar> chars) {
        this.start = start;
        this.duration = duration;
        this.lyricChars = chars;
        reversed.addAll(lyricChars);
        Collections.reverse(reversed);
    }

    private String text = "";

    public String text() {
        if (this.text.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (LyricChar c : this.lyricChars) {
                builder.append(c.chars);
            }
            this.text = builder.toString();
        }
        return this.text;
    }

    public LyricChar getChar(int ms) {
        for (var c : reversed) {
            if (c.start <= ms)
                return c;
        }
        return null;
    }

    public double calc(int ms) {
        if (lyricChars.isEmpty() || lyricChars.size() == 1) return 1.0;

        long elapsedDuration = ms - start; // 当前时间相对于歌词行开始时间的偏移量
        if (elapsedDuration <= 0) return 0.0; // 若时间在歌词行开始前，进度为0
        if (elapsedDuration >= duration) return 1.0; // 若时间超过了歌词行的结束时间，进度为1

        double progress = 0.0;
        long cumulativeDuration = 0;

        for (LyricChar c : lyricChars) {
            cumulativeDuration += c.duration;

            if (elapsedDuration < cumulativeDuration) {
                // 当前时间在该字符的持续时间内，计算字符的部分进度
                long timeIntoChar = cumulativeDuration - elapsedDuration;
                progress += (1.0 - timeIntoChar / (double) c.duration) / lyricChars.size();
                break;
            }

            // 累加已完全显示字符的进度
            progress += 1.0 / lyricChars.size();
        }

        return progress;
    }
}
