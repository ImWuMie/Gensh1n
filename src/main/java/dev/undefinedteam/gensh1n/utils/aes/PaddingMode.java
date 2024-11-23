package dev.undefinedteam.gensh1n.utils.aes;

public enum PaddingMode {
    None("NoPadding"),
    PKCS5("PKCS5Padding"),
    Zero("ZeroPadding");

    public final String name;

    PaddingMode(String name) {
        this.name = name;
    }
}
