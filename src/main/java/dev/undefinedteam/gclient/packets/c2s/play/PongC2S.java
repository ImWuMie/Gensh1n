package dev.undefinedteam.gclient.packets.c2s.play;

import dev.undefinedteam.gclient.packets.Packet;
import lombok.AllArgsConstructor;

import java.io.IOException;

@AllArgsConstructor
public class PongC2S extends Packet {
    public long ping;

    @Override
    public void read() throws IOException {
    }

    @Override
    public void write() throws IOException {
        buf.writeVarLong(ping);
    }
}
