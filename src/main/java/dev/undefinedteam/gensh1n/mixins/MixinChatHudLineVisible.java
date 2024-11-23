package dev.undefinedteam.gensh1n.mixins;

import com.mojang.authlib.GameProfile;
import dev.undefinedteam.gensh1n.mixin_interface.IChatHudLineVisible;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChatHudLine.Visible.class)
public class MixinChatHudLineVisible implements IChatHudLineVisible {
    @Shadow
    @Final
    private OrderedText content;
    @Unique
    private int id;
    @Unique
    private GameProfile sender;
    @Unique
    private boolean startOfEntry;

    @Override
    public String gensh1n$getText() {
        StringBuilder sb = new StringBuilder();

        content.accept((index, style, codePoint) -> {
            sb.appendCodePoint(codePoint);
            return true;
        });

        return sb.toString();
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

    @Override
    public boolean gensh1n$isStartOfEntry() {
        return startOfEntry;
    }

    @Override
    public void gensh1n$setStartOfEntry(boolean start) {
        startOfEntry = start;
    }
}
