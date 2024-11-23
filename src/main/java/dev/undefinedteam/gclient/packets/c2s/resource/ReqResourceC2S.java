package dev.undefinedteam.gclient.packets.c2s.resource;

import dev.undefinedteam.gclient.packets.Packet;

import java.io.IOException;

public class ReqResourceC2S extends Packet {
    public String[] resources;

    public ReqResourceC2S(String... resources) {
        this.resources = resources;
    }

    @Override
    public void read() throws IOException {
    }

    @Override
    public void write() throws IOException {
        buf.writeVarInt(resources.length);
        for (String resource : resources) {
            buf.writeString(resource);
        }
    }
}
