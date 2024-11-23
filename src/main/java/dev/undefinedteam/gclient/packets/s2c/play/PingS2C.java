package dev.undefinedteam.gclient.packets.s2c.play;

import dev.undefinedteam.gclient.packets.Packet;
import lombok.NoArgsConstructor;

import java.io.IOException;

@NoArgsConstructor
public class PingS2C extends Packet {
    public long ping;

    @Override
    public void read() throws IOException {
        ping = buf.readVarLong();
    }

    @Override
    public void write() throws IOException {
    }
}
