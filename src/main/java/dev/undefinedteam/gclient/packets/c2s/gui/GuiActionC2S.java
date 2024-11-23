package dev.undefinedteam.gclient.packets.c2s.gui;

import dev.undefinedteam.gclient.packets.Packet;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.IOException;

@AllArgsConstructor
@NoArgsConstructor
public class GuiActionC2S extends Packet {
    public int guiId;

    @Override
    public void read() throws IOException {

    }

    @Override
    public void write() throws IOException {

    }
}
