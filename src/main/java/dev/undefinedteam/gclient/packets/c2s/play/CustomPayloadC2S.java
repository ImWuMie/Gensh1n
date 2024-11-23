package dev.undefinedteam.gclient.packets.c2s.play;

import dev.undefinedteam.gclient.packets.Packet;
import lombok.AllArgsConstructor;

import java.io.IOException;

@AllArgsConstructor
public class CustomPayloadC2S extends Packet {
    public String channel;
    public byte[] data;

    @Override
    public void read() throws IOException {}

    @Override
    public void write() throws IOException {
        buf.writeString(channel);
        buf.writeByteArray(this.data);
    }
}
