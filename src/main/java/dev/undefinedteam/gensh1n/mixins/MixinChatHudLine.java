package dev.undefinedteam.gensh1n.mixins;

import com.mojang.authlib.GameProfile;
import dev.undefinedteam.gensh1n.mixin_interface.IChatHudLine;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = ChatHudLine.class)
public class MixinChatHudLine implements IChatHudLine {
    @Shadow
    @Final
    private Text content;
    @Unique
    private int id;
    @Unique
    private GameProfile sender;

    @Override
    public String gensh1n$getText() {
        return content.getString();
    }

    @Override
    public int gensh1n$getId() {
        return id;
    }

    @Override
    public void gensh1n$setId(int id) {
        this.id = id;
    }

    @Override
    public GameProfile gensh1n$getSender() {
        return sender;
    }

    @Override
    public void gensh1n$setSender(GameProfile profile) {
        sender = profile;
    }
}
