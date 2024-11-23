package dev.undefinedteam.gensh1n.utils.network;

import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.utils.Utils;
import meteordevelopment.orbit.EventHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.impl.networking.payload.PayloadHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static dev.undefinedteam.gensh1n.Client.mc;

@StringEncryption
@NativeObfuscation
@ControlFlowObfuscation
public class NetPayload {
    @NativeObfuscation.Inline
    public static final Map<Identifier, Consumer<PacketByteBuf>> REGISTERED_CHANNELS = new HashMap<>();

    @NativeObfuscation.Inline
    public static void register(Identifier channel, Consumer<PacketByteBuf> buf) {
        if (REGISTERED_CHANNELS.containsKey(channel)) return;

        REGISTERED_CHANNELS.put(channel, buf);
    }

    @NativeObfuscation.Inline
    public static final List<Runnable> preTasks = new ArrayList<>();

    public static void pre(Runnable task) {
        preTasks.add(task);
    }

    @NativeObfuscation.Inline
    public static void send(Identifier channel, PacketByteBuf buf) {
        if (mc.getNetworkHandler() != null) {
            mc.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new PayloadPacket(channel, buf)));
        }
    }

    @EventHandler(priority = 11451)
    private static void onTick(TickEvent.Pre event) {
        if (Utils.canUpdate() && mc.getNetworkHandler() != null) {
            for (Runnable task : preTasks) {
                task.run();
            }
            preTasks.clear();
        }
    }

    public static class PayloadPacket implements CustomPayload {
        public Identifier channel;
        public PacketByteBuf buf;

        public PayloadPacket(Identifier channel, PacketByteBuf buf) {
            this.channel = channel;
            this.buf = buf;
        }

        @Override
        public String toString() {
            return channel.toString() + " " + buf.toString();
        }

        @Override
        public void write(PacketByteBuf buf) {
            PayloadHelper.write(buf, this.buf);
        }

        @Override
        public Identifier id() {
            return channel;
        }
    }
}
