package dev.undefinedteam.gensh1n.music.api.types;

import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;


@StringEncryption
@ControlFlowObfuscation
public enum SearchType {
    SINGLE(1),
    ALBUM(10),
    ARTISTS(100),
    PLAYLIST(1000),
    USER(1002),
    MV(1004),
    LYRICS(1006),
    FM(1009),
    VIDEO(1014);

    public final int id;

    SearchType(int id) {
        this.id = id;
    }
}
