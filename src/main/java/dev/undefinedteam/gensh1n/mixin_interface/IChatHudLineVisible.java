package dev.undefinedteam.gensh1n.mixin_interface;

public interface IChatHudLineVisible extends IChatHudLine {
    boolean gensh1n$isStartOfEntry();

    void gensh1n$setStartOfEntry(boolean start);
}
