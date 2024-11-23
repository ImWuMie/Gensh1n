package dev.undefinedteam.gclient.packets.s2c.resource;

import dev.undefinedteam.gclient.packets.Packet;
import lombok.NoArgsConstructor;

import java.io.IOException;

@NoArgsConstructor
public class ResourceDataS2C extends Packet {
    public String location;
    public String md5;
    public byte[] data;

    @Override
    public void read() throws IOException {
        location = buf.readString();
        md5 = buf.readString();
        data = buf.readByteArray();
    }

    @Override
    public void write() throws IOException {
    }
}
