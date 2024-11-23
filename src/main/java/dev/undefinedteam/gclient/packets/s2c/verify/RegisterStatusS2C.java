package dev.undefinedteam.gclient.packets.s2c.verify;

import dev.undefinedteam.gclient.packets.Packet;
import lombok.NoArgsConstructor;

import java.io.IOException;

@NoArgsConstructor
public class RegisterStatusS2C extends Packet {
    public int code;
    public String data;

    @Override
    public void read() throws IOException {
        this.code = buf.readVarInt();
        this.data = buf.readString();
    }

    @Override
    public void write() throws IOException {
    }
}
