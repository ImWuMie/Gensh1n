package dev.undefinedteam.gclient;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.undefinedteam.gclient.assets.AssetsManager;
import dev.undefinedteam.gclient.codec.CompressionDecoder;
import dev.undefinedteam.gclient.data.AssetData;
import dev.undefinedteam.gclient.data.UserData;
import dev.undefinedteam.gclient.data.UserList;
import dev.undefinedteam.gclient.packets.Packet;
import dev.undefinedteam.gclient.packets.c2s.login.ReqChatC2S;
import dev.undefinedteam.gclient.packets.c2s.play.DisconnectC2S;
import dev.undefinedteam.gclient.packets.c2s.play.IGNChangeC2S;
import dev.undefinedteam.gclient.packets.c2s.play.PongC2S;
import dev.undefinedteam.gclient.packets.c2s.resource.ReqResourceC2S;
import dev.undefinedteam.gclient.packets.c2s.verify.HandshakeC2S;
import dev.undefinedteam.gclient.packets.c2s.verify.RegisterC2S;
import dev.undefinedteam.gclient.packets.c2s.verify.ReqClientC2S;
import dev.undefinedteam.gclient.packets.s2c.login.UserInfoS2C;
import dev.undefinedteam.gclient.packets.s2c.play.*;
import dev.undefinedteam.gclient.packets.s2c.resource.ResourceDataS2C;
import dev.undefinedteam.gclient.packets.s2c.resource.ResourceListS2C;
import dev.undefinedteam.gclient.packets.s2c.verify.ExpireDataS2C;
import dev.undefinedteam.gclient.packets.s2c.verify.RegisterStatusS2C;
import dev.undefinedteam.gclient.text.Style;
import dev.undefinedteam.gclient.text.Text;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.client.MinecraftClient;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.Logger;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import static dev.undefinedteam.gclient.GCClient.setupCompression;
import static dev.undefinedteam.gclient.GChat.LOG_PREFIX;

@StringEncryption
@NativeObfuscation
@ControlFlowObfuscation
public class GCUser extends SimpleChannelInboundHandler<Packet> {
    @NativeObfuscation.Inline
    public static final Logger LOG = GCClient.INSTANCE.LOGGER;
    public Channel channel;
    @NativeObfuscation.Inline
    public long ping;
    private UserData mUserInfo;

    private static int expireCode = 1;
    public long expireTime = System.currentTimeMillis();

    public UserList users = new UserList();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        channel = ctx.channel();

        channel.config().setAutoRead(true);

        GCClient.INSTANCE.session = this;
        setupCompression(channel, CompressionDecoder.MAXIMUM_COMPRESSED_LENGTH, true);

        send(new HandshakeC2S(GChat.VERSION_ID, 96, hwid()));
        String name = GChat.INSTANCE.username;
        String token = GChat.INSTANCE.passwd;
        if (name != null && token != null && !name.isEmpty() && !token.isEmpty()) {
            loginInChat(name, token);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        GCClient.INSTANCE.session = null;
    }

    public static boolean init;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
        var mc = MinecraftClient.getInstance();

        if (GCClient.get().session() == null) {
            GCClient.get().session = this;
        } else if (GCClient.get().session() != this) {
            this.channel.close().awaitUninterruptibly();
        }

        if (packet instanceof UserInfoS2C userInfoS2C) {
            this.mUserInfo = userInfoS2C.data;
            var missingAssets = AssetsManager.INSTANCE.missingData;
            String[] missings = new String[missingAssets.size()];
            for (int i = 0; i < missings.length; i++) {
                missings[i] = missingAssets.get(i).location;
            }

            send(new ReqResourceC2S(missings));
        } else if (packet instanceof OnlineUsersS2C onlineUsersS2C) {
            this.users.users.clear();
            this.users.users.addAll(onlineUsersS2C.online.users);
        } else if (packet instanceof ExpireDataS2C expireDataS2C) {
            onExpire(expireDataS2C);
        } else if (packet instanceof PingS2C s2c) {
            long time = System.currentTimeMillis();
            this.ping = time - s2c.ping;
            send(new PongC2S(time));
        } else if (packet instanceof ResourceListS2C res) {
            for (AssetData assetDatum : res.assets.assetData) {
                LOG.info("Receive Res: {}", assetDatum.location);
                if (AssetsManager.INSTANCE.find(assetDatum.location) == null) {
                    send(new ReqResourceC2S(assetDatum.location));
                }
            }
        } else if (packet instanceof ResourceDataS2C dataS2C) {
            LOG.info("Download Res: {} md5: {},{} bytes", dataS2C.location, dataS2C.md5, dataS2C.data.length);
            AssetsManager.INSTANCE.add(dataS2C.location, dataS2C.md5, dataS2C.data);
        } else if (packet instanceof MessageS2C p) {
            var text = LOG_PREFIX.copy();
            text.append(Text.of(" [").setStyle(Style.EMPTY.withFormatting(Formatting.GRAY)))
                .append(p.title)
                .append(Text.of("]: ").setStyle(Style.EMPTY.withFormatting(Formatting.GRAY)))
                .append(p.message);
            if (mc != null && mc.player != null && mc.world != null) {
                sendMessage(mc, text);
            } else LOG.info(text);
        } else if (packet instanceof DisconnectS2C p) {
            //netHandler.sendMessage("{}[{}Lemon{}Chat{}]{} Disconnect: {}", ChatFormatting.GRAY, ChatFormatting.YELLOW, ChatFormatting.GREEN, ChatFormatting.GRAY, ChatFormatting.RESET, p.reason);
            if (mc != null && mc.player != null && mc.world != null) {
                var text = LOG_PREFIX.copy();
                text.append(Text.of(" Disconnect: ").setStyle(Style.EMPTY.withFormatting(Formatting.GRAY)));
                text.append(p.reason).setStyle(Style.EMPTY.withFormatting(Formatting.GRAY));
                sendMessage(mc, text);
            } else LOG.info("Disconnect: " + p.reason);
        } else if (packet instanceof ChatMessageS2C p) {
            Text msg = LOG_PREFIX.copy().append(" ");
            var sender = p.sender;

            if (!p.isCommand) {
                var l = Text.of("[").setStyle(Style.EMPTY.withFormatting(Formatting.GRAY));
                var r = Text.of("]").setStyle(Style.EMPTY.withFormatting(Formatting.GRAY));
                var name = Text.of(sender.group).setStyle(Style.EMPTY.withFormatting(sender.name_color.formatting));

                msg.append(l).append(name).append(r);

                if (sender.mNameTag != null) {
                    var l1 = Text.of("[").setStyle(Style.EMPTY.withFormatting(Formatting.GRAY));
                    var r1 = Text.of("]").setStyle(Style.EMPTY.withFormatting(Formatting.GRAY));
                    var tag = Text.of(sender.mNameTag).setStyle(Style.EMPTY.withFormatting(Formatting.GRAY));
                    msg.append(l1).append(tag).append(r1);
                }

                var nick = Text.of(" " + sender.mNickName + ": ").setStyle(Style.EMPTY.withFormatting(Formatting.GRAY));
                msg.append(nick);
            }

            msg.append(p.message);

            if (mc != null && mc.player != null && mc.world != null) {
                sendMessage(mc, msg);
            } else LOG.info(msg);
        } else if (packet instanceof RegisterStatusS2C statusS2C) {
            if (statusS2C.code == 200) {
                String name = GChat.INSTANCE.username;
                String passwd = GChat.INSTANCE.passwd;
                if (name != null && passwd != null && !name.isEmpty() && !passwd.isEmpty()) {
                    loginInChat(name, passwd);
                }
            }
        } else if(packet instanceof CustomPayloadS2C payload) {
        }

        if (GCClient.INSTANCE.listener != null) {
            GCClient.INSTANCE.listener.accept(this, packet);
        }
    }

    public void sendMessage(MinecraftClient mc, Text text, Object... args) {
        mc.inGameHud.getChatHud().addMessage(text.vanilla());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
    }

    @NativeObfuscation(virtualize = NativeObfuscation.VirtualMachine.TIGER_BLACK)
    public void loginInChat(String name, String pwd) {
        send(new ReqChatC2S(name, pwd));
    }

    @NativeObfuscation(virtualize = NativeObfuscation.VirtualMachine.TIGER_BLACK)
    public void register(String name, String passwd) {
        send(new RegisterC2S(name, hwid(), passwd));
    }

    public void clientRequest() {
        send(new ReqClientC2S("yUAN神"));
    }

    public UserData user_info() {
        return this.mUserInfo;
    }

    @NativeObfuscation(virtualize = NativeObfuscation.VirtualMachine.TIGER_BLACK)
    public void onExpire(ExpireDataS2C expireDataS2C) {
        var code = expireDataS2C.code;
        var data = expireDataS2C.data;

        try {
            this.expireTime = Long.parseLong(data.substring(1));
            this.expireCode = code == 200 ? -88 : code;
        } catch (Exception e) {
        }
    }

    @NativeObfuscation(virtualize = NativeObfuscation.VirtualMachine.TIGER_BLACK)
    public boolean logged() {
        return user_info() != null;
    }

    @NativeObfuscation(virtualize = NativeObfuscation.VirtualMachine.TIGER_BLACK)
    public boolean verifyPass() {
        return (expireCode == 200 && expireTime >= System.currentTimeMillis()) || expireCode == -88;
    }

    public void onJoinGame(GameProfile profile) {
        this.send(new IGNChangeC2S(profile.getName(), profile.getId().toString()));
    }

    @NativeObfuscation(virtualize = NativeObfuscation.VirtualMachine.TIGER_BLACK)
    public static boolean globalVerifyPass() {
        return expireCode == -88;
    }

    public long getPing() {
        return this.ping;
    }

    public void tick() {
        if (this.channel != null) {
            this.channel.flush();
        }
    }

    public UserList.User getUserByName(String name) {
        for (UserList.User user : this.users.users) {
            if (user.gameName.equals(name)) {
                return user;
            }
        }

        return null;
    }

    public UserList.User getUserByUUID(String uuid) {
        for (UserList.User user : this.users.users) {
            if (user.gameUUid.equals(uuid)) {
                return user;
            }
        }

        return null;
    }

    public UserList.User getUserByProfile(GameProfile profile) {
        for (UserList.User user : this.users.users) {
            if (user.gameName.equals(profile.getName()) && user.gameUUid.equals(profile.getId().toString())) {
                return user;
            }
        }

        return null;
    }

    public void disconnect(String reason, Object... args) {
        reason = getReplaced(reason, args);
        if (this.channel.isOpen()) {
            this.send(new DisconnectC2S(reason));
            LOG.info("Disconnect: {}", reason);
            this.channel.close().awaitUninterruptibly();
        }
    }

    public void send(Packet packet) {
        if (packet != null) this.channel.writeAndFlush(packet);
    }

    public boolean isConnected() {
        return this.channel != null && this.channel.isOpen() && this.channel.isActive();
    }

    @NativeObfuscation(virtualize = NativeObfuscation.VirtualMachine.TIGER_BLACK)
    public static String hwid() {
        return DigestUtils.sha256Hex(
            System.getenv("os")
                + System.getProperty("os.name")
                + System.getProperty("os.arch")
                + System.getProperty("user.name")
                + System.getenv("PROCESSOR_LEVEL")
                + System.getenv("PROCESSOR_REVISION")
                + System.getenv("PROCESSOR_IDENTIFIER")
                + System.getenv("PROCESSOR_ARCHITEW6432") + "woxihuanni"
        );
//        return Wrapper.getVerifyToken();
    }

    @NativeObfuscation(virtualize = NativeObfuscation.VirtualMachine.TIGER_BLACK)
    public static String getReplaced(String str, Object... args) {
        String s = str;
        for (Object a : args) {
            s = s.replaceFirst("\\{}", a == null ? "null" : a.toString());
        }
        return s;
    }
}
