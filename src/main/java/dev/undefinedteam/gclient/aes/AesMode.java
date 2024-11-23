package dev.undefinedteam.gclient.aes;

public enum AesMode {
    ECB("ECB"),
    CBC("CBC");

    public final String name;

    AesMode(String name) {
        this.name = name;
    }
}
