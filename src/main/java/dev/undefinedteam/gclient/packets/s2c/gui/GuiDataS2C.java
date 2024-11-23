package dev.undefinedteam.gclient.packets.s2c.gui;

import dev.undefinedteam.gclient.packets.Packet;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.IOException;

@AllArgsConstructor
@NoArgsConstructor
public class GuiDataS2C extends Packet {
    public int guiId;

    @Override
    public void read() throws IOException {

    }

    @Override
    public void write() throws IOException {

    }
}
