package dev.undefinedteam.gclient.packets;

import com.google.gson.Gson;
import dev.undefinedteam.gclient.GChat;

import java.io.IOException;

public abstract class Packet {
    public PacketBuf buf;
    public boolean breakpoint = false;

    protected final Gson GSON = GChat.GSON;

    public abstract void read() throws IOException;

    public abstract void write() throws IOException;
}
