package dev.undefinedteam.gclient.packets.c2s.play;

import dev.undefinedteam.gclient.packets.Packet;
import lombok.AllArgsConstructor;

import java.io.IOException;

@AllArgsConstructor
public class ChatMessageC2S extends Packet {
    public String message;

    @Override
    public void read() throws IOException {
    }

    @Override
    public void write() throws IOException {
        buf.writeString(this.message,256);
    }
}
