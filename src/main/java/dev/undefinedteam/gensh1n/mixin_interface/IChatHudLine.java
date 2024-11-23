package dev.undefinedteam.gensh1n.mixin_interface;

import com.mojang.authlib.GameProfile;

public interface IChatHudLine {
    String gensh1n$getText();

    int gensh1n$getId();

    void gensh1n$setId(int id);

    GameProfile gensh1n$getSender();

    void gensh1n$setSender(GameProfile profile);
}
