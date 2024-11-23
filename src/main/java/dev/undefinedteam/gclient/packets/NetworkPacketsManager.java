package dev.undefinedteam.gclient.packets;

import dev.undefinedteam.gclient.packets.c2s.gui.GuiActionC2S;
import dev.undefinedteam.gclient.packets.c2s.play.*;
import dev.undefinedteam.gclient.GCClient;
import dev.undefinedteam.gclient.packets.c2s.login.ReqChatC2S;
import dev.undefinedteam.gclient.packets.c2s.resource.ReqResourceC2S;
import dev.undefinedteam.gclient.packets.c2s.verify.HandshakeC2S;
import dev.undefinedteam.gclient.packets.c2s.verify.*;
import dev.undefinedteam.gclient.packets.s2c.play.*;
import dev.undefinedteam.gclient.packets.s2c.resource.ResourceDataS2C;
import dev.undefinedteam.gclient.packets.s2c.resource.ResourceListS2C;
import dev.undefinedteam.gclient.packets.s2c.gui.GuiDataS2C;
import dev.undefinedteam.gclient.packets.s2c.login.UserInfoS2C;
import dev.undefinedteam.gclient.packets.s2c.verify.*;
import org.apache.logging.log4j.Logger;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class NetworkPacketsManager {
    private final Logger LOG = GCClient.INSTANCE.LOGGER;

    private static final int OFFSET = 0x1145;
    public static NetworkPacketsManager INSTANCE;
    public final HashMap<Integer, Class<? extends Packet>> s2c = new HashMap<>();
    public final HashMap<Integer, Class<? extends Packet>> c2s = new HashMap<>();

    public NetworkPacketsManager() {
        INSTANCE = this;
    }

    public void init() {
        registerC2S(HandshakeC2S.class);
        registerC2S(ChatMessageC2S.class);
        registerC2S(PongC2S.class);
        registerC2S(DisconnectC2S.class);
        registerC2S(ReqChatC2S.class);
        registerC2S(ReqClientC2S.class);
        registerC2S(ReqResourceC2S.class);
        registerC2S(GuiActionC2S.class);
        registerC2S(RegisterC2S.class);
        registerC2S(CustomPayloadC2S.class);
        registerC2S(CodeC2S.class);
        registerC2S(IGNChangeC2S.class);

        registerS2C(ResourceDataS2C.class);
        registerS2C(ResourceListS2C.class);
        registerS2C(ClientDataS2C.class);
        registerS2C(ChatMessageS2C.class);
        registerS2C(MessageS2C.class);
        registerS2C(DisconnectS2C.class);
        registerS2C(PingS2C.class);
        registerS2C(UserInfoS2C.class);
        registerS2C(GuiDataS2C.class);
        registerS2C(RegisterStatusS2C.class);
        registerS2C(ExpireDataS2C.class);
        registerS2C(OnlineUsersS2C.class);
        registerS2C(CustomPayloadS2C.class);

        LOG.info("Registered {} s2c packets.", s2c.size());
        LOG.info("Registered {} c2s packets.", c2s.size());
    }


    public Class<? extends Packet> getS2C(int pid) {
        return s2c.getOrDefault(pid, null);
    }


    public Class<? extends Packet> getC2S(int pid) {
        return c2s.getOrDefault(pid, null);
    }

    private void registerS2C(Class<? extends Packet> packetClass) {
        s2c.put(s2c.size() + OFFSET, packetClass);
    }

    private void registerC2S(Class<? extends Packet> packetClass) {
        c2s.put(c2s.size() + OFFSET, packetClass);
    }


    public int getS2CPid(Class<? extends Packet> packetClass) {
        for (Map.Entry<Integer, Class<? extends Packet>> entry : s2c.entrySet()) {
            if (entry.getValue() == packetClass) {
                return entry.getKey();
            }
        }

        return -1;
    }

    public int getC2SPid(Class<? extends Packet> packetClass) {
        for (Map.Entry<Integer, Class<? extends Packet>> entry : c2s.entrySet()) {
            if (entry.getValue() == packetClass) {
                return entry.getKey();
            }
        }

        return -1;
    }
}
