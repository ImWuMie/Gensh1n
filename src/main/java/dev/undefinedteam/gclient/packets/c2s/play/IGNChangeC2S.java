package dev.undefinedteam.gclient.packets.c2s.play;

import dev.undefinedteam.gclient.packets.Packet;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.IOException;

@AllArgsConstructor
public class IGNChangeC2S extends Packet {
    public String name;
    public String uuid;

    @Override
    public void read() throws IOException {
    }

    @Override
    public void write() throws IOException {
        buf.writeString(this.name);
        buf.writeString(this.uuid);
    }
}
