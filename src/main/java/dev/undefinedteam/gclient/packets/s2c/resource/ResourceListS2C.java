package dev.undefinedteam.gclient.packets.s2c.resource;

import dev.undefinedteam.gclient.data.AssetsSet;
import dev.undefinedteam.gclient.packets.Packet;
import lombok.NoArgsConstructor;

import java.io.IOException;

@NoArgsConstructor
public class ResourceListS2C extends Packet {
    public AssetsSet assets;

    @Override
    public void read() throws IOException {
        this.assets = GSON.fromJson(buf.readString(), AssetsSet.class);
    }

    @Override
    public void write() throws IOException {
    }
}
