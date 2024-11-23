package dev.undefinedteam.gclient.packets.s2c.login;

import dev.undefinedteam.gclient.data.NameColor;
import dev.undefinedteam.gclient.data.UserData;
import dev.undefinedteam.gclient.packets.Packet;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.IOException;

@AllArgsConstructor
@NoArgsConstructor
public class UserInfoS2C extends Packet {
    public UserData data;

    @Override
    public void read() throws IOException {
        this.data = new UserData();
        data.mNickName = buf.readString();
        data.mNameTag = buf.readString();
        data.mHeadIcon = buf.readString();
        data.mLastLogin = buf.readVarLong();
        data.name_color = NameColor.valueOf(buf.readString());
        data.group = buf.readString();
        data.mToken = "Hidden";

        if (data.mNameTag.isEmpty()) data.mNameTag = null;
    }

    @Override
    public void write() throws IOException {
    }
}
