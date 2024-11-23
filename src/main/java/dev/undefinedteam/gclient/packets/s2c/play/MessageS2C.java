package dev.undefinedteam.gclient.packets.s2c.play;

import dev.undefinedteam.gclient.packets.Packet;
import dev.undefinedteam.gclient.text.Text;
import lombok.NoArgsConstructor;

import java.io.IOException;

@NoArgsConstructor
public class MessageS2C extends Packet {
    public String title;
    public String message;

    @Override
    public void read() throws IOException {
        title = buf.readString();
        message = buf.readString();
    }

    @Override
    public void write() throws IOException {
    }
}
