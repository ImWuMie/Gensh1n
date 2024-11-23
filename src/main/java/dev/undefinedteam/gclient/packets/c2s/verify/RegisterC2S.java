package dev.undefinedteam.gclient.packets.c2s.verify;

import dev.undefinedteam.gclient.packets.Packet;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.IOException;

@AllArgsConstructor
public class RegisterC2S extends Packet {
    public String name;
    public String hwid;
    public String passwd;

    @Override
    public void read() throws IOException {

    }

    @Override
    public void write() throws IOException {
        buf.writeString(this.name);
        buf.writeString(this.hwid);
        buf.writeString(this.passwd);
    }
}
