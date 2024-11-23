package dev.undefinedteam.gclient.aes;

public enum PaddingMode {
    None("NoPadding"),
    PKCS5("PKCS5Padding"),
    Zero("ZeroPadding");

    public final String name;

    PaddingMode(String name) {
        this.name = name;
    }
}
