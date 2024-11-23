package dev.undefinedteam.gensh1n.music.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.music.DeviceData;
import dev.undefinedteam.gensh1n.music.api.models.*;
import dev.undefinedteam.gensh1n.music.api.models.login.*;
import dev.undefinedteam.gensh1n.music.api.objs.model.*;
import dev.undefinedteam.gensh1n.music.api.types.SearchType;
import dev.undefinedteam.gensh1n.utils.RandomUtils;
import dev.undefinedteam.gensh1n.utils.StringUtils;
import dev.undefinedteam.gensh1n.utils.json.GsonUtils;
import dev.undefinedteam.gensh1n.utils.network.Http;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;

// Object api
@StringEncryption
@NativeObfuscation
@ControlFlowObfuscation
public class RequestApi {
    @NativeObfuscation.Inline
    public static final String ANDROID_APP_VER = "9.1.40";
    @NativeObfuscation.Inline
    public static final String PC_APP_VER = "3.0.16.203023";
    @NativeObfuscation.Inline
    public static final String ANDROID_OS_VER = "14";
    @NativeObfuscation.Inline
    public static final String ANDROID_BUILD_VER = "240820154029";
    @NativeObfuscation.Inline
    public static final String ANDROID_MOBILE_NAME = "194RCA8";
    @NativeObfuscation.Inline
    public static final String ANDROID_VERSION_CODE = "9001040";
    @NativeObfuscation.Inline
    public static final String PC_OS_VER = "Microsoft-Windows-10-Professional-build-22631-64bit";
    @NativeObfuscation.Inline
    public static final String API_DOMAIN = "https://interface.music.163.com";
    @NativeObfuscation.Inline
    public static final boolean ENCRYPT_RESPONSE = false;
    @NativeObfuscation.Inline
    public static final String PC_EAPI_DOMAIN = "https://interface.music.163.com";
    @NativeObfuscation.Inline
    public static final String ANDROID_EAPI_DOMAIN = "https://interface3.music.163.com";
    @NativeObfuscation.Inline
    public static final String DOMAIN = "https://music.163.com";

    public final Gson GSON = GsonUtils.newBuilderNoPretty().create();

    private final Executor executor = Executors.newCachedThreadPool();

    private final File FOLDER;
    private final File cookieFile;
    private final File deviceFile;
    private DeviceData deviceData;

    private String deviceId;
    private String macId;
    private Cookies cookies;
    public UATypes userAgent = UATypes.PC;

    public final RequestCrypto crypto;

    private final String anonymousToken;

    // API
    public PlayListDetailData playlistDetail(long id) {
        var model = newModel(new PlayListDetailModel(String.valueOf(id)));
        return model.request();
    }

    public PlayListDetailData playlistDetail(String id) {
        var model = newModel(new PlayListDetailModel(id));
        return model.request();
    }

    public UserPlayListData userPlaylist(long uid, int offset, int limit) {
        var model = newModel(new UserPlayListModel(uid, offset, limit));
        return model.request();
    }

    public RecommendData recommendSongs(int limit) {
        var model = newModel(new RecommendSongsModel(1));
        return model.request();
    }

    public TopListData topListDetail() {
        var model = newModel(new TopListDetailModel());
        return model.request();
    }

    public QrCreateData qrCreate() {
        var model = newModel(new QrCreateModel());
        return model.request();
    }

    public QrCheckData qrCheck(String key) {
        var model = newModel(new QrCheckModel(key));
        return model.request();
    }

    public byte[] genQrCode(String key) {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8);
        hints.put(EncodeHintType.MARGIN, 1);
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode("https://music.163.com/login?codekey=" + key, BarcodeFormat.QR_CODE, 200, 200, hints);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            var stream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", stream);
            return stream.toByteArray();
        } catch (WriterException | IOException e) {
            throw new RuntimeException("你毁了，qrcode创建失败");
        }
    }

    public SongDetailData songDetail(long... id) {
        var model = newModel(new SongDetailModel(id));
        return model.request();
    }

    public SongDetailData songDetail(String... id) {
        var model = newModel(new SongDetailModel(id));
        return model.request();
    }

    public SongDetailData songDetail(List<String> id) {
        var model = newModel(new SongDetailModel(id));
        return model.request();
    }

    public LyricData lyricNew(long id) {
        var model = newModel(new LyricNewModel(String.valueOf(id)));
        return model.request();
    }

    public LyricData lyricNew(String id) {
        var model = newModel(new LyricNewModel(id));
        return model.request();
    }

    public LyricData lyric(String id) {
        var model = newModel(new LyricModel(id));
        return model.request();
    }

    public LyricData lyric(long id) {
        var model = newModel(new LyricModel(String.valueOf(id)));
        return model.request();
    }

    public StatusData refreshLogin() {
        var model = newModel(new RefreshLoginModel());
        return model.request();
    }

    public HttpData logout() {
        var data = newModel(new LogoutModel()).request();

        cookies.put("MUSIC_A_T", "");
        cookies.put("MUSIC_R_T", "");

        return data;
    }

    public LoginStatusData loginStatus() {
        var model = newModel(new LoginStatusModel());
        return model.request();
    }

    public PhoneLoginData phoneLogin(String phone, String captcha) {
        var model = newModel(new PhoneLoginModel(phone, captcha));
        return model.request();
    }

    public StatusData sendCaptcha(String phone) {
        var model = newModel(new CaptchaSentModel(phone));
        return model.request();
    }

    public <T extends SearchData> T search(String keywords, int limit, int offset, SearchType type) {
        var model = newModel(new SearchModel(keywords, limit, offset, type));
        return (T) model.request();
    }

    public SongUrlData songUrl(int br, String... ids) {
        var model = newModel(new SongUrlModel(br, ids));
        return model.request();
    }

    public SongUrlData songUrl(int br, List<String> ids) {
        var model = newModel(new SongUrlModel(br, ids));
        return model.request();
    }

    public SongUrlData songUrl(String... ids) {
        var model = newModel(new SongUrlModel(320000, ids));
        return model.request();
    }

    public SongUrlData songUrl(List<String> ids) {
        var model = newModel(new SongUrlModel(320000, ids));
        return model.request();
    }

    public <T extends RequestBase<?>> T newModel(T base) {
        base.cookies = this.cookies;
        base.api = this;
        return base;
    }

    public RequestApi(File FOLDER) {
        String anonymousTokenFinal;
        this.FOLDER = FOLDER;

        this.crypto = new RequestCrypto(this);

        if (!FOLDER.exists() && !FOLDER.mkdirs()) {
            throw new RuntimeException("Failed to create folder");
        }

        this.cookieFile = new File(FOLDER, "cookies.json");
        this.deviceFile = new File(FOLDER, "device.json");

        try {
            var anonymousTokenFile = File.createTempFile("anonymousToken", ".gensh1n");
            if (!anonymousTokenFile.exists() && anonymousTokenFile.createNewFile()) {
                anonymousTokenFinal = RandomUtils.randomString(16);
                Files.writeString(anonymousTokenFile.toPath(), anonymousTokenFinal);
            } else if (anonymousTokenFile.exists()) {
                anonymousTokenFinal = Files.readString(anonymousTokenFile.toPath());
            } else {
                anonymousTokenFinal = RandomUtils.randomString(16);
            }
        } catch (IOException e) {
            anonymousTokenFinal = RandomUtils.randomString(16);
        }
        anonymousToken = anonymousTokenFinal;
    }

    public void load() {
        boolean changed = false;
        try {
            var hex = "0123456789abcdef";
            var example = "12345678901234567890123456789012345678901234567890";
            if (deviceFile.exists()) {
                try {
                    var content = Files.readString(this.deviceFile.toPath());
                    this.deviceData = GSON.fromJson(content, DeviceData.class);
                } catch (IOException e) {
                    this.deviceData = new DeviceData();
                }
            } else {
                this.deviceData = new DeviceData();
            }

            this.macId = deviceData.macId;
            this.deviceId = deviceData.deviceId;

            var scrwT  = "1111111111111";
            var scrwT1 = "11111111111111111111111111111111111111111111111111111111111111";

            if (deviceData.scrw.isEmpty() || deviceData.scrw.length() != scrwT.length()) {
                deviceData.scrw = "00" + RandomUtils.random(scrwT.length(), hex).toUpperCase();
                changed = true;
            }

            if (deviceData.scrw1.isEmpty() || deviceData.scrw1.length() != scrwT1.length()) {
                deviceData.scrw1 = "00" + RandomUtils.random(scrwT.length(), hex).toUpperCase();
                changed = true;
            }

            if (deviceData.deviceId.isEmpty()) {
                // Random deviceId
                deviceId = RandomUtils.random(example.length(), hex).toUpperCase();
                changed = true;
            } else if (deviceId.length() != example.length() + 2) {
                deviceId = "00" + RandomUtils.random(example.length(), hex).toUpperCase();
                changed = true;
            }

            deviceData.deviceId = deviceId;

            example = "11:22:33:44:55:66";

            if (deviceData.macId.isEmpty() || macId.length() != example.length()) {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < 6; i++) {
                    builder.append(RandomUtils.random(2, hex)).append(":");
                }
                builder.setLength(builder.length() - 1);
                this.macId = builder.toString().toUpperCase();
                changed = true;
            }

            deviceData.macId = macId;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cookieFile.exists()) {
            try {
                var content = Files.readString(this.cookieFile.toPath());
                var obj = JsonParser.parseString(content).getAsJsonObject();
                this.cookies = Cookies.fromTag(obj);
            } catch (IOException e) {
                this.cookies = new Cookies();
            }
        } else {
            this.cookies = new Cookies();
            changed = true;
        }

        if  (changed) {
            save();
        }
    }

    public void save() {
        try {
            Files.writeString(this.deviceFile.toPath(), Client.GSON.toJson(this.deviceData));
            Files.writeString(this.cookieFile.toPath(), Client.GSON.toJson(this.cookies.toTag()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("all")
    public <T> T request(Http.Method method, RequestBase base, JsonObject data) {
        var url = switch (base.crypto()) {
            case LinuxAPI -> "https://music.163.com/api/linux/forward";
            case WEAPI ->
                DOMAIN + "/weapi/" + base.getUri().substring("/api/".length()) + (cookies.has("__csrf") ? "?csrf_token=" + cookies.find("__csrf") : "");
            case API -> API_DOMAIN + base.getUri();
            // PC & MOBILE
            case PC_EAPI -> PC_EAPI_DOMAIN + "/eapi/" + base.getUri().substring("/api/".length());
            case ANDROID_EAPI -> ANDROID_EAPI_DOMAIN + "/eapi/" + base.getUri().substring("/api/".length());
        };


        this.userAgent = switch (base.crypto()) {
            case LinuxAPI -> UATypes.Linux;
            case API, WEAPI -> UATypes.PC;
            case PC_EAPI -> UATypes.PC_DESKTOP;
            case ANDROID_EAPI -> UATypes.ANDROID_APP;
        };

        var http = Http.to(method, url).cookieSpec(CookieSpecs.STANDARD);
        http.userAgent(StringUtils.getReplaced(this.userAgent.ua, ANDROID_MOBILE_NAME));
        var os = switch (base.crypto()) {
            case LinuxAPI -> "linux";
            case API, WEAPI -> cookies.orElse("os", "android");
            case PC_EAPI -> {
                cookies.put("os", "pc");
                yield "pc";
            }
            case ANDROID_EAPI -> {
                cookies.put("os", "android");
                yield "pc";
            }
        };

        var isMobile = "android".equals(os);

        var cookie = new HashMap<String, Object>();

        cookies.put("_ntes_nuid", RandomUtils.randomString(16));

        if (!base.getUri().contains("login")) {
            var str = RandomUtils.randomString(16);
            cookies.put("NMTID", str);
        }

        if (cookies.has("__csrf")) cookie.put("__csrf", this.cookies.find("__csrf"));
        if (cookies.has("MUSIC_U")) cookie.put("MUSIC_U", this.cookies.find("MUSIC_U"));

        if (base.crypto().equals(CryptoType.WEAPI)) {
            cookies.put("__remember_me", "true");

            if (!cookies.has("MUSIC_U")) {
                if (!cookies.has("MUSIC_A")) {
                    this.cookies.put("MUSIC_A", anonymousToken);
                }
            }

            this.cookies.cookies.forEach((key, val) -> {
                if (this.cookies.has(key)) {
                    if (cookie.containsKey(key)) {
                        cookie.replace(key, val);
                    } else cookie.put(key, val);
                }
            });
        }

        StringEntity encryptData = null;

        var header = new HashMap<String, Object>();
        if (base.crypto().equals(CryptoType.API) || base.crypto().equals(CryptoType.PC_EAPI) || base.crypto().equals(CryptoType.ANDROID_EAPI)) {
            header.put("os", os);
            header.put("appver", isMobile ? ANDROID_APP_VER : PC_APP_VER);
            header.put("osver", isMobile ? ANDROID_OS_VER : PC_OS_VER);

            if (isMobile) {
                header.put("buildver", ANDROID_BUILD_VER);
                header.put("versioncode", ANDROID_VERSION_CODE);
                header.put("mobilename", ANDROID_MOBILE_NAME);
                header.put("resolution", "2316x1080");
            }

            header.put("ntes_kaola_ad", "1");
            header.put("channel", "netease");
            header.put(isMobile ? "EVNSM" : "WEVNSM", "1.0.0");
            cookie.putAll(header);
        }

        switch (base.crypto()) {
            case API -> encryptData = new StringEntity(GSON.toJson(data), StandardCharsets.UTF_8);
            case PC_EAPI -> {
                // Encrypt Response
                data.addProperty("e_r", ENCRYPT_RESPONSE);

                var tag = new JsonObject();
                tag.addProperty("clientSign", macId + "@@@SCRW" + deviceData.scrw + "@@@@@@7" + deviceData.scrw1);
                tag.addProperty("os", "pc");
                tag.addProperty("appver", PC_APP_VER);
                tag.addProperty("deviceId", deviceId);
                tag.addProperty("requestId", 0);
                tag.addProperty("osver", PC_OS_VER);

                data.add("header", tag);
                var result = crypto.eapi(base.getUri(), data);
                List<NameValuePair> pairs = new ArrayList<>();
                pairs.add(new BasicNameValuePair("params", result.params));
                encryptData = new UrlEncodedFormEntity(pairs, StandardCharsets.UTF_8);
            }
            case ANDROID_EAPI -> {
                data.add("header", new JsonObject());
                data.addProperty("e_r", ENCRYPT_RESPONSE);

                var result = crypto.eapi(base.getUri(), data);
                List<NameValuePair> pairs = new ArrayList<>();
                pairs.add(new BasicNameValuePair("params", result.params));
                encryptData = new UrlEncodedFormEntity(pairs, StandardCharsets.UTF_8);
            }
            case WEAPI -> {
                http.header("Referer", "https://music.163.com");
                http.userAgent(UATypes.PC.ua);
                var content = GSON.toJson(data);

                var result = crypto.weapiEncrypt(content);
                List<NameValuePair> pairs = new ArrayList<>();
                pairs.add(new BasicNameValuePair("params", result.params));
                pairs.add(new BasicNameValuePair("encSecKey", result.key));
                encryptData = new UrlEncodedFormEntity(pairs, StandardCharsets.UTF_8);
            }
            case LinuxAPI -> {
                http.userAgent(UATypes.Linux.ua);
                var result = crypto.linuxapi(data);
                List<NameValuePair> pairs = new ArrayList<>();
                pairs.add(new BasicNameValuePair("eparams", result.params));
                encryptData = new UrlEncodedFormEntity(pairs, StandardCharsets.UTF_8);
            }
        }

        http.cookies(cookieObjToString(cookie));
        if (method.equals(Http.Method.POST))
            http.bodyForm(encryptData);

        Supplier<T> supplier = () -> {
            return (T) http.sendResponse(response -> {
                if (response.getStatusLine().getStatusCode() == 502) {
                    throw new RuntimeException("你毁了，网易云音乐死了");
                }

                byte[] bytes;
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[8192];
                    int read;
                    while ((read = response.getEntity().getContent().read(buffer)) > 0) out.write(buffer, 0, read);
                    bytes = out.toByteArray();
                } catch (IOException e) {
                    bytes = null;
                }
                if (bytes == null) return null;

                if (response.containsHeader("Set-Cookie")) {
                    var cookieHeaders = response.getHeaders("Set-Cookie");
                    for (Header cHeader : cookieHeaders) {
                        for (HeaderElement element : cHeader.getElements()) {
                            cookies.put(element.getName(), element.getValue());
                            break;
                        }
                    }
                    save();
                }

                var res = new String(bytes, StandardCharsets.UTF_8);
                return base.parse(res);
            });
        };

        try {
            return supplier.get();
        } catch (Exception e) {
            return supplier.get();
        }
    }

    @NativeObfuscation.Inline
    public String cookieObjToString(Map<String, Object> cookieMap) {
        return cookieMap.entrySet().stream()
            .map(entry -> entry.getKey() + "=" + entry.getValue().toString())
            .collect(Collectors.joining("; "));
    }

    public Cookies cookies() {
        return cookies;
    }
}
