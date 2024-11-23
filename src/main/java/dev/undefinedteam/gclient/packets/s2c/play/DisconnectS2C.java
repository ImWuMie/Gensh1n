package dev.undefinedteam.gclient.packets.s2c.play;

import dev.undefinedteam.gclient.packets.Packet;
import dev.undefinedteam.gclient.text.Text;
import lombok.NoArgsConstructor;

import java.io.IOException;

@NoArgsConstructor
public class DisconnectS2C extends Packet {
    public Text reason;

    @Override
    public void read() throws IOException {
        this.reason = buf.readText();
    }

    @Override
    public void write() throws IOException {
    }
}
