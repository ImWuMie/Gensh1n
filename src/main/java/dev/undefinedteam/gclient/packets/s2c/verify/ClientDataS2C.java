package dev.undefinedteam.gclient.packets.s2c.verify;

import dev.undefinedteam.gclient.packets.Packet;
import lombok.NoArgsConstructor;

import java.io.IOException;

@NoArgsConstructor
public class ClientDataS2C extends Packet {
    public int mVersion;
    public byte[] client;

    @Override
    public void read() throws IOException {
        this.mVersion = buf.readVarInt();
        this.client = buf.readByteArray();
    }

    @Override
    public void write() throws IOException {
    }
}
