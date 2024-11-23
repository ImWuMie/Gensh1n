package dev.undefinedteam.gensh1n.events.network;

import dev.undefinedteam.gensh1n.events.Cancellable;
import net.minecraft.network.packet.Packet;

public class PacketEvent extends Cancellable {
    private static final PacketEvent INSTANCE = new PacketEvent();

    public TransferOrigin origin;
    public Packet<?> packet;

    public static PacketEvent get(TransferOrigin origin, Packet<?> packet) {
        INSTANCE.setCancelled(false);
        INSTANCE.origin = origin;
        INSTANCE.packet = packet;
        return INSTANCE;
    }

    public enum TransferOrigin {
        SEND, RECEIVE
    }
}
