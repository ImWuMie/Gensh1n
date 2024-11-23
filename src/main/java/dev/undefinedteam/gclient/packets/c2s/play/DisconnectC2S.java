package dev.undefinedteam.gclient.packets.c2s.play;

import dev.undefinedteam.gclient.packets.Packet;
import lombok.AllArgsConstructor;

import java.io.IOException;

@AllArgsConstructor
public class DisconnectC2S extends Packet {
    public String reason;

    @Override
    public void read() throws IOException {
    }

    @Override
    public void write() throws IOException {
        buf.writeString(reason);
    }
}
