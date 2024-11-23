package dev.undefinedteam.gclient.packets.s2c.play;

import dev.undefinedteam.gclient.data.NameColor;
import dev.undefinedteam.gclient.data.UserData;
import dev.undefinedteam.gclient.packets.Packet;
import dev.undefinedteam.gclient.text.Text;
import lombok.NoArgsConstructor;

import java.io.IOException;

@NoArgsConstructor
public class ChatMessageS2C extends Packet {
    public Text message;
    public UserData sender;
    public boolean isCommand;

    @Override
    public void read() throws IOException {
        this.message = buf.readText();
        this.isCommand = buf.readBoolean();

        this.sender = new UserData();
        sender.mNickName = buf.readString();
        sender.mNameTag = buf.readString();
        sender.mHeadIcon = buf.readString();
        sender.name_color = NameColor.fromString(buf.readString());
        sender.group = buf.readString();

        if (sender.mNameTag.isEmpty()) sender.mNameTag = null;

        sender.mToken = "Hidden";
        sender.mHwid = "Hidden";
        sender.mLastLogin = 0L;
    }

    @Override
    public void write() throws IOException {
    }
}
