package dev.undefinedteam.gensh1n.music;

import com.google.gson.JsonParser;
import dev.undefinedteam.gensh1n.AsyncWorkerThread;
import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.gui.frags.MainGuiFragment;
import dev.undefinedteam.gensh1n.gui.frags.music.MusicFragment;
import dev.undefinedteam.gensh1n.gui.overlay.TNotifications;
import dev.undefinedteam.gensh1n.music.api.RequestApi;
import dev.undefinedteam.gensh1n.music.api.objs.Song;
import dev.undefinedteam.gensh1n.music.api.objs.UserProfile;
import dev.undefinedteam.gensh1n.music.api.objs.lyric.SongLyric;
import dev.undefinedteam.gensh1n.music.api.objs.model.*;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.settings.Settings;
import dev.undefinedteam.gensh1n.system.ChatAdapter;
import dev.undefinedteam.gensh1n.system.SettingAdapter;
import dev.undefinedteam.gensh1n.utils.RandomUtils;
import dev.undefinedteam.gensh1n.utils.network.Http;
import dev.undefinedteam.gensh1n.utils.render.ImageUtils;
import dev.undefinedteam.modernui.mc.MuiScreen;
import dev.undefinedteam.modernui.mc.MusicPlayer;
import icyllis.modernui.ModernUI;
import icyllis.modernui.audio.FFT;
import icyllis.modernui.core.Core;
import icyllis.modernui.graphics.BitmapFactory;
import icyllis.modernui.graphics.Image;
import icyllis.modernui.widget.Toast;
import lombok.extern.log4j.Log4j2;
import meteordevelopment.orbit.EventHandler;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import static dev.undefinedteam.gensh1n.Client.mc;
import static dev.undefinedteam.gensh1n.gui.frags.music.MusicFragment.formatTime;

@Log4j2
@NativeObfuscation
@StringEncryption
@ControlFlowObfuscation
public class GMusic extends ChatAdapter implements SettingAdapter {
    public static GMusic INSTANCE;
    public static final File FOLDER = new File(Client.FOLDER, "music");
    public final File CFG = new File(FOLDER, "config.json");
    public RequestApi api;
    public DetailData current;
    public SongUrlData.SongInfo currentUrl;
    public SongLyric currentLyric;
    public SongLyric currentTLyric;
    public Image currentMImage;
    public Image currentSImageRound;
    public UserProfile myProfile;
    public boolean shouldRefresh;
    public byte[] profileHeadBytes;
    public Image profileHead;
    public RecommendData recommendData;
    public TopListData topListData;
    public UserPlayListData userPlayListData;

    public final Settings settings = new Settings();
    public final SettingGroup sgDefault = settings.getDefaultGroup();
    public final Setting<LoginMode> loginMode = choice(sgDefault, "login-mode", LoginMode.QRCode);

    public final SettingGroup sgClient = settings.createGroup("client");
    public final Setting<Double> volume = doubleN(sgClient, "volume", 100, 0, 100);
    public final Setting<Integer> loopType = intN(sgClient, "loop-type", 0, 0, 2);
    private final Setting<String> playlist = text(sgClient, "play-list", """
        {"cur":0,"list":[]}""");
    public final Setting<Boolean> playlistExpand = bool(sgClient, "playlist-expand", true);

    public PlayList playList = new PlayList();

    private final AsyncWorkerThread worker = new AsyncWorkerThread();
    private final Executor executor = worker::submit;

    public Executor getExecutor() {
        return this.executor;
    }

    public boolean addList(PlayList.Data data_) {
        if (!this.playList.songs.contains(data_)) {
            this.playList.songs.add(data_);

            this.playlist.set(api.GSON.toJson(this.playList));
            return true;
        }
        return false;
    }

    public void removeList(PlayList.Data data) {
        if (this.playList.songs.contains(data)) {
            this.playList.songs.remove(data);
            this.playlist.set(api.GSON.toJson(this.playList));
        }
    }

    public void clear() {
        this.playList.songs.clear();
        this.playlist.set(api.GSON.toJson(this.playList));
    }

    public GMusic() {
        super("Music");
        INSTANCE = this;

        if (!FOLDER.exists()) {
            FOLDER.mkdirs();
        }

        worker.start();
    }

    public GMusic init() {
        api = new RequestApi(FOLDER);
        api.load();
        if (CFG.exists()) {
            try {
                var parse = JsonParser.parseString(Files.readString(CFG.toPath()));
                this.settings.fromTag(parse.getAsJsonObject());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else shutdown();

        this.playList = api.GSON.fromJson(this.playlist.get(), PlayList.class);

        Client.EVENT_BUS.subscribe(this);

        refreshAll();
        return this;
    }

    @EventHandler
    private void onTick(TickEvent.Pre e) {
        if (mc.currentScreen instanceof MuiScreen m && m.getFragment() instanceof MainGuiFragment) {
            return;
        }

        var mPlayer = MusicPlayer.getInstance();

        if (mPlayer.hasTrack() && MusicFragment.isPlaying) {
            float time = mPlayer.getTrackTime();
            float length = mPlayer.getTrackLength();
            var min = formatTime((int) time);
            var max = formatTime((int) length);
            if (min.equals(max)) {
                MusicFragment.isPlaying = false;
                mPlayer.clearTrack();
                GMusic.INSTANCE.loop(ex -> {
                    ex.printStackTrace();
                    nWarn("Music: {}", NSHORT, ex.getMessage());
                });
                MusicPlayer.getInstance().setAnalyzerCallback(
                    fft -> {
                        fft.setLogAverages(250, 14);
                        fft.setWindowFunc(FFT.NONE);
                    },
                    MainGuiFragment.get().mSpectrumDrawable::updateAmplitudes
                );
            }
        }
    }

    public void shutdown() {
        api.save();

        try {
            var tag = settings.toTag();
            if (!CFG.exists()) CFG.createNewFile();
            Files.writeString(CFG.toPath(), Client.GSON.toJson(tag), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshHead() {
        if (this.profileHead == null) {
            if (this.myProfile != null && this.myProfile.avatarUrl != null) {
                CompletableFuture.runAsync(() -> {
                    try {
                        var bytes = ImageUtils.roundImage(this.profileHeadBytes != null ? this.profileHeadBytes : Http.get(myProfile.avatarUrl).sendBytes(), 33, 33, 33);
                        CompletableFuture.runAsync(() -> {
                            try {
                                this.profileHead = Image.createTextureFromBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                                shouldRefresh = true;
                            } catch (IOException e) {
                                shouldRefresh = false;
                            }
                        }, Core.getUiThreadExecutor());
                    } catch (IOException e) {
                        shouldRefresh = false;
                    }
                }, executor);
            }
        }
    }

    public void refreshHomepage(Consumer<Throwable> onError) {
        if (logged()) {
            var data = api.userPlaylist(myProfile.userId, 0, 100);
            if (data != null && data.code == 200) this.userPlayListData = data;
        }

        var recommendSongs = api.recommendSongs(100);
        if (recommendSongs != null && recommendSongs.code == 200) this.recommendData = recommendSongs;

        var topListDetail = api.topListDetail();
        if (topListDetail != null && topListDetail.code == 200) this.topListData = topListDetail;
    }

    public void refreshAll() {
        refreshProfile();
    }

    public void refreshProfile() {
        if (api.cookies().has("__csrf")) {
            this.profileHeadBytes = null;
            CompletableFuture.supplyAsync(() -> {
                var data = api.loginStatus();
                if (data.profile == null) {
                    return null;
                }

                this.myProfile = data.profile;
                if (this.myProfile.avatarUrl != null) {
                    this.profileHeadBytes = Http.get(myProfile.avatarUrl).sendBytes();
                }
                this.refreshHomepage(Throwable::printStackTrace);
                return this.profileHeadBytes;
            }, executor).whenCompleteAsync((data, ex) -> {
                try {
                    var bytes = ImageUtils.roundImage(data, 38, 38, 33);
                    this.profileHead = Image.createTextureFromBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    shouldRefresh = true;
                } catch (IOException e) {
                    shouldRefresh = false;
                }
            }, Core.getUiThreadExecutor());
        } else this.refreshHomepage(Throwable::printStackTrace);
    }

    public boolean logged() {
        return this.myProfile != null;
    }

    public boolean logout() {
        this.myProfile = null;
        this.profileHead = null;
        this.profileHeadBytes = null;
        this.api.logout();
        shouldRefresh = true;
        return true;
    }

    @NativeObfuscation.Inline
    public static void setCurrentUrl(SongUrlData.SongInfo obj) {
        if (INSTANCE != null) {
            if (obj == null) {
                INSTANCE.currentMImage = null;
                INSTANCE.current = null;
                INSTANCE.currentLyric = null;
                return;
            }

            INSTANCE.currentUrl = obj;
        }
    }

    @NativeObfuscation.Inline
    public static void setCurrent(String id) {
        if (INSTANCE != null) {
            if (id == null) {
                INSTANCE.currentMImage = null;
                INSTANCE.current = null;
                INSTANCE.currentLyric = null;
                return;
            }
            INSTANCE.current = INSTANCE.api.songDetail(id).first();
            var lyric = INSTANCE.api.lyricNew(id);
            INSTANCE.currentLyric = lyric == null ? null : lyric.lyric();
            INSTANCE.currentTLyric = lyric == null ? null : lyric.tLyric();
            if (INSTANCE.current != null && !INSTANCE.current.picUrl().isEmpty()) {
                byte[] stream = Http.get(INSTANCE.current.picUrl()).sendBytes();
                CompletableFuture.runAsync(() -> {
                    try {
                        INSTANCE.currentMImage = Image.createTextureFromBitmap(BitmapFactory.decodeByteArray(stream, 0, stream.length));

                        var rounded = ImageUtils.roundImage(stream, 45, 45, 8);
                        INSTANCE.currentSImageRound = Image.createTextureFromBitmap(BitmapFactory.decodeByteArray(rounded, 0, rounded.length));
                        var noti = TNotifications.INSTANCE;
                        if (noti != null) {
                            var name = INSTANCE.current.name + " - " + INSTANCE.current.author();
                            noti.info("Current playing: {}", TNotifications.SHORT, name);
                        }
                    } catch (IOException e) {
                        INSTANCE.currentMImage = null;
                        INSTANCE.currentSImageRound = null;
                    }
                }, Core.getUiThreadExecutor());
            }
        }
    }

    public static void setCurrent(Song obj) {
        setCurrent(obj == null ? null : String.valueOf(obj.id));
    }

    public void prev(Consumer<Throwable> ex) {
        var songs = this.playList.songs;

        if (songs.size() < 2) {
            var context = ModernUI.getInstance();
            CompletableFuture.runAsync(() -> Toast.makeText(context, "歌单歌曲少于2", Toast.LENGTH_SHORT).show(), Core.getUiThreadExecutor());
            return;
        }

        var player = MusicPlayer.getInstance();
        var limit = songs.size() - 1;
        if (playList.cur - 1 < 0) {
            playList.cur = limit;
        } else playList.cur--;

        CompletableFuture.runAsync(() -> {
            var target = this.playList.songs.get(playList.cur);
            try {
                SongUrlData url;
                if (target.url == null && ((url = GMusic.INSTANCE.api.songUrl(target.id)) != null && url.code == 200 && url.data != null && url.first() != null && url.first().url != null)) {
                    target.url = url.first();
                }
            } catch (Exception e) {
                ex.accept(new RuntimeException("song url is null: " + e.getMessage()));
                return;
            }

            try {
                var data = target.data != null ? target.data : (target.data = ByteBuffer.wrap(Http.get(target.url.url).sendBytes(1048576)));
                player.replaceTrackMp3(target.name, data);
                setCurrentUrl(target.url);
                setCurrent(String.valueOf(target.url.id));
            } catch (Exception e) {
                ex.accept(e);
            }
        }, executor);
    }

    @NativeObfuscation.Inline
    public void next(Consumer<Throwable> ex) {
        var songs = this.playList.songs;

        if (songs.size() < 2) {
            var context = ModernUI.getInstance();
            CompletableFuture.runAsync(() -> Toast.makeText(context, "歌单歌曲少于2", Toast.LENGTH_SHORT).show(), Core.getUiThreadExecutor());
            return;
        }

        var player = MusicPlayer.getInstance();
        var limit = songs.size() - 1;
        if (playList.cur + 1 > limit) {
            playList.cur = 0;
        } else playList.cur++;

        CompletableFuture.runAsync(() -> {
            var target = this.playList.songs.get(playList.cur);
            try {
                SongUrlData url;
                if (target.url == null && ((url = GMusic.INSTANCE.api.songUrl(target.id)) != null && url.code == 200 && url.data != null && url.first() != null && url.first().url != null)) {
                    target.url = url.first();
                }
            } catch (Exception e) {
                ex.accept(new RuntimeException("song url is null: " + e.getMessage()));
                return;
            }

            try {
                var data = target.data != null ? target.data : (target.data = ByteBuffer.wrap(Http.get(target.url.url).sendBytes(1048576)));
                player.replaceTrackMp3(target.name, data);
                setCurrentUrl(target.url);
                setCurrent(String.valueOf(target.url.id));
            } catch (Exception e) {
                ex.accept(e);
            }
        }, executor);
    }

    public void loop(Consumer<Throwable> ex) {
        var player = MusicPlayer.getInstance();
        var songs = this.playList.songs;
        CompletableFuture.runAsync(() -> {
            switch (loopType.get()) {
                case 0 -> {
                    var limit = songs.size() - 1;
                    if (playList.cur + 1 > limit) {
                        playList.cur = 0;
                    } else playList.cur++;

                    var target = this.playList.songs.get(playList.cur);
                    try {
                        SongUrlData url;
                        if (target.url == null && ((url = GMusic.INSTANCE.api.songUrl(target.id)) != null && url.code == 200 && url.data != null && url.first() != null && url.first().url != null)) {
                            target.url = url.first();
                        }
                    } catch (Exception e) {
                        ex.accept(new RuntimeException("song url is null: " + e.getMessage()));
                        return;
                    }

                    try {
                        var data = target.data != null ? target.data : (target.data = ByteBuffer.wrap(Http.get(target.url.url).sendBytes(1048576)));
                        player.replaceTrackMp3(target.name, data);
                        setCurrentUrl(target.url);
                        setCurrent(String.valueOf(target.url.id));
                    } catch (Exception e) {
                        ex.accept(e);
                    }
                }
                case 1 -> {
                    if (!songs.isEmpty()) {
                        playList.cur = RandomUtils.nextInt(0, songs.size() - 1);

                        var target = this.playList.songs.get(playList.cur);
                        try {
                            SongUrlData url;
                            if (target.url == null && ((url = GMusic.INSTANCE.api.songUrl(target.id)) != null && url.code == 200 && url.data != null && url.first() != null && url.first().url != null)) {
                                target.url = url.first();
                            }
                        } catch (Exception e) {
                            ex.accept(new RuntimeException("song url is null: " + e.getMessage()));
                            return;
                        }

                        try {
                            var data = target.data != null ? target.data : (target.data = ByteBuffer.wrap(Http.get(target.url.url).sendBytes(1048576)));
                            player.replaceTrackMp3(target.name, data);
                            setCurrentUrl(target.url);
                            setCurrent(String.valueOf(target.url.id));
                        } catch (Exception e) {
                            ex.accept(e);
                        }
                    }
                }
                case 2 -> {
                    var target = this.playList.songs.get(playList.cur);
                    try {
                        SongUrlData url;
                        if (target.url == null && ((url = GMusic.INSTANCE.api.songUrl(target.id)) != null && url.code == 200 && url.data != null && url.first() != null && url.first().url != null)) {
                            target.url = url.first();
                        }
                    } catch (Exception e) {
                        ex.accept(new RuntimeException("song url is null: " + e.getMessage()));
                        return;
                    }

                    try {
                        var data = target.data != null ? target.data : (target.data = ByteBuffer.wrap(Http.get(target.url.url).sendBytes(1048576)));
                        player.replaceTrackMp3(target.name, data);
                    } catch (Exception e) {
                        ex.accept(e);
                    }
                }
            }
            this.playlist.set(api.GSON.toJson(this.playList));
        }, executor);
    }

    public enum LoginMode {
        Phone, QRCode
    }

    public void nextLoopType() {
        int current = this.loopType.get();
        int next = current + 1;
        if (next > 2) next = 0;
        this.loopType.set(next);
    }
}
