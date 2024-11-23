package dev.undefinedteam.gensh1n.music.api;

import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;


@StringEncryption
@ControlFlowObfuscation
public enum UATypes {
    PC("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36 Edg/127.0.0.0"),
    PC_DESKTOP("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Safari/537.36 Chrome/91.0.4472.164 NeteaseMusicDesktop/3.0.16.203023"),
    ANDROID_APP("NeteaseMusic/9.1.40.240820154029(9001040);Dalvik/2.1.0 (Linux; U; Android 14; {} Build/UP1A.231005.007)"),
    MOBILE("Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Mobile Safari/537.36 EdgA/127.0.0.0"),
    Linux("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36 Edg/127.0.2651.90");

    public final String ua;
    UATypes(String ua) {
        this.ua = ua;
    }
}
