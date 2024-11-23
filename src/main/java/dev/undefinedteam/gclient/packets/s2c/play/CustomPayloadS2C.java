package dev.undefinedteam.gclient.packets.s2c.play;

import dev.undefinedteam.gclient.packets.Packet;
import lombok.NoArgsConstructor;

import java.io.IOException;

@NoArgsConstructor
public class CustomPayloadS2C extends Packet {
    public String channel;
    public byte[] data;

    @Override
    public void read() throws IOException {
        this.channel = buf.readString();
        this.data = buf.readByteArray(114514);
    }

    @Override
    public void write() throws IOException {}
}
