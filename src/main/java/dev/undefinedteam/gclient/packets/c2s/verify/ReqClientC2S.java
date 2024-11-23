package dev.undefinedteam.gclient.packets.c2s.verify;

import dev.undefinedteam.gclient.packets.Packet;
import lombok.AllArgsConstructor;

import java.io.IOException;

@AllArgsConstructor
public class ReqClientC2S extends Packet {
    public String key;

    @Override
    public void read() throws IOException {
    }

    @Override
    public void write() throws IOException {
        buf.writeString(key);
    }
}
