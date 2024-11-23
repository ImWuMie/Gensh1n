package dev.undefinedteam.gensh1n.music.api.objs.model;

import com.google.gson.annotations.SerializedName;
import dev.undefinedteam.gclient.data.GsonIgnore;
import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.music.api.objs.lyric.LyricChar;
import dev.undefinedteam.gensh1n.music.api.objs.lyric.LyricLine;
import dev.undefinedteam.gensh1n.music.api.objs.lyric.SongLyric;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@StringEncryption
@ControlFlowObfuscation
public class LyricData extends HttpData {
    @GsonIgnore
    public static final int NONE = -1;

    @SerializedName("lrc")
    public Part lrc;
    @SerializedName("tlyric")
    public Part tlyric;
    @SerializedName("yrc")
    public Part yrc;
    @SerializedName("ytlrc")
    public Part ytlrc;

    public static class Part {
        @SerializedName("version")
        public int version;
        @SerializedName("lyric")
        public String lyric;

        public SongLyric parseOld() {
            var lines = new ArrayList<>(List.of(lyric.split("\n")));
            lines.removeIf(String::isEmpty);

            var song_lyric = new SongLyric();
            var metadata = new ArrayList<Metadata>();
            Metadata prevMetadata = null;

            LyricLine prevLine = null;
            for (String line : lines) {
                if (line.startsWith("{")) {
                    var data = Client.GSON.fromJson(line, Metadata.class);
                    metadata.add(data);

                    if (prevMetadata != null) {
                        prevMetadata.nextStart = data.time;
                    }

                    prevMetadata = data;
                    continue;
                }

                var pattern = Pattern.compile("\\[(\\d{2}):(\\d{2})\\.(\\d{3})]\\s*(.*)");
                var matcher = pattern.matcher(line);

                if (!matcher.find()) {
                    pattern = Pattern.compile("\\[(\\d{2}):(\\d{2})\\.(\\d{2})]\\s*(.*)");
                } else {
                    pattern = Pattern.compile("\\[(\\d{2}):(\\d{2})\\.(\\d{3})]\\s*(.*)");
                }
                matcher = pattern.matcher(line);

                if (matcher.find()) {
                    int minutes = Integer.parseInt(matcher.group(1)); // 分钟
                    int seconds = Integer.parseInt(matcher.group(2)); // 秒
                    int ms = Integer.parseInt(matcher.group(3)); // 豪秒

                    int startTime = (minutes * 60 * 1000) + (seconds * 1000) + (ms);

                    if (prevMetadata != null) {
                        prevMetadata.nextStart = startTime;
                        prevMetadata = null;
                    }

                    String lyric = matcher.group(4);
                    List<LyricChar> chars = new ArrayList<>();

                    chars.add(new LyricChar(NONE, NONE, lyric));
                    var lyricLine = new LyricLine(startTime, NONE, chars);
                    song_lyric.lines.add(lyricLine);

                    if (prevLine != null) {
                        prevLine.duration = lyricLine.start - prevLine.start;
                        for (LyricChar lyricChar : prevLine.lyricChars) {
                            lyricChar.start = prevLine.start;
                            lyricChar.duration = prevLine.duration;
                        }
                    }

                    prevLine = lyricLine;
                }
            }

            if (prevLine != null) {
                for (LyricChar lyricChar : prevLine.lyricChars) {
                    lyricChar.start = prevLine.start;
                    lyricChar.duration = prevLine.duration;
                }
                prevLine = null;
            }

            if (!song_lyric.lines.isEmpty()) {
                var first = song_lyric.lines.getFirst();
                if (first != null) {
                    Collections.reverse(metadata);
                    for (Metadata d : metadata) {
                        song_lyric.lines.addFirst(d.lyricLine());
                    }
                }
            }
            return song_lyric;
        }

        public SongLyric parse() {
            var lines = new ArrayList<>(List.of(lyric.split("\n")));
            lines.removeIf(String::isEmpty);

            var song_lyric = new SongLyric();
            var metadata = new ArrayList<Metadata>();
            Metadata prevMetadata = null;
            for (String line : lines) {
                if (line.startsWith("{")) {
                    var data = Client.GSON.fromJson(line, Metadata.class);
                    metadata.add(data);

                    if (prevMetadata != null) {
                        prevMetadata.nextStart = data.time;
                    }

                    prevMetadata = data;
                    continue;
                }

                List<LyricChar> chars = new ArrayList<>();

                Pattern charPattern = Pattern.compile("\\((\\d+),(\\d+),\\d+\\)([^()]+)");
                Matcher charMatcher = charPattern.matcher(line);

                while (charMatcher.find()) {
                    int charStartTime = Integer.parseInt(charMatcher.group(1));
                    int charDuration = Integer.parseInt(charMatcher.group(2));
                    String character = charMatcher.group(3);
                    chars.add(new LyricChar(charStartTime, charDuration, character));
                }

                Pattern linePattern = Pattern.compile("\\[(\\d+),(\\d+)]");
                Matcher lineMatcher = linePattern.matcher(line);

                if (lineMatcher.find()) {
                    int lineStartTime = Integer.parseInt(lineMatcher.group(1));
                    int lineDuration = Integer.parseInt(lineMatcher.group(2));
                    song_lyric.lines.add(new LyricLine(lineStartTime, lineDuration, chars));

                    if (prevMetadata != null) {
                        prevMetadata.nextStart = lineStartTime;
                        prevMetadata = null;
                    }
                }
            }

            if (!song_lyric.lines.isEmpty()) {
                var first = song_lyric.lines.getFirst();
                if (first != null) {
                    Collections.reverse(metadata);
                    for (Metadata d : metadata) {
                        song_lyric.lines.addFirst(d.lyricLine());
                    }
                }
            }
            return song_lyric;
        }

        private static class Metadata {
            @SerializedName("t")
            public int time;
            @SerializedName("c")
            public List<Text> texts;

            @GsonIgnore
            private int nextStart;

            private static class Text {
                @SerializedName("tx")
                public String text;
            }

            public String text() {
                var builder = new StringBuilder();
                for (Text text : this.texts) {
                    builder.append(text.text);
                }
                return builder.toString();
            }

            public LyricLine lyricLine() {
                var start = this.time;
                var duration = this.nextStart - this.time;
                List<LyricChar> chars = new ArrayList<>();
                chars.add(new LyricChar(start, duration, text()));
                return new LyricLine(start, duration, chars);
            }
        }
    }

    public SongLyric parse_lrc() {
        return this.lrc == null ? null : this.lrc.parseOld();
    }

    public SongLyric parse_tlyric() {
        return this.tlyric == null ? null : this.tlyric.parseOld();
    }

    public SongLyric parse_yrc() {
        return this.yrc == null ? null : this.yrc.parse();
    }

    public SongLyric parse_ytlrc() {
        return this.ytlrc == null ? null : this.ytlrc.parseOld();
    }

    public boolean is_yrc() {
        return this.yrc != null && !this.yrc.lyric.isEmpty();
    }

    public SongLyric tLyric() {
        return is_yrc() ? parse_ytlrc() : parse_tlyric();
    }

    public SongLyric lyric() {
        return is_yrc() ? parse_yrc() : parse_lrc();
    }
}
