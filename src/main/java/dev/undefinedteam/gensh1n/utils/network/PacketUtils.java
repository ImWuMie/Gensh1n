package dev.undefinedteam.gensh1n.utils.network;

import net.minecraft.network.packet.Packet;

import static dev.undefinedteam.gensh1n.Client.mc;

public class PacketUtils {
    public static void sendNoEvent(Packet packet) {
        mc.player.networkHandler.getConnection().send(packet, null,true);
    }
}
