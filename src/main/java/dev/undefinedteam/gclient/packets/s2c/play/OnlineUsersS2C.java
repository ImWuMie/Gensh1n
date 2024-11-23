package dev.undefinedteam.gclient.packets.s2c.play;

import dev.undefinedteam.gclient.data.UserList;
import dev.undefinedteam.gclient.packets.Packet;
import lombok.NoArgsConstructor;

import java.io.IOException;

@NoArgsConstructor
public class OnlineUsersS2C extends Packet {
    public UserList online;

    @Override
    public void read() throws IOException {
        online = GSON.fromJson(buf.readString(), UserList.class);
    }

    @Override
    public void write() throws IOException {
    }
}
