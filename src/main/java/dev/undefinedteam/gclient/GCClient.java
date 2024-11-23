package dev.undefinedteam.gclient;

import dev.undefinedteam.gclient.assets.AssetsManager;
import dev.undefinedteam.gclient.codec.*;
import dev.undefinedteam.gclient.packets.NetworkPacketsManager;
import dev.undefinedteam.gclient.packets.Packet;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.io.IOException;
import java.util.function.BiConsumer;




@StringEncryption
@ControlFlowObfuscation
public class GCClient {
    public final Logger LOGGER = LogManager.getLogger("GCClient");

    public Bootstrap bootstrap;

    private final NetworkPacketsManager packetsManager;

    public static GCClient INSTANCE;

    private final EventLoopGroup group;

    GCUser session;

    BiConsumer<GCUser, Packet> listener;

    public boolean connecting;
    public Channel connectChannel;

    public static GCClient get() {
        if (INSTANCE == null) {
            new GCClient();
        }

        return INSTANCE;
    }

    public void setListener(BiConsumer<GCUser, Packet> listener) {
        this.listener = listener;
    }

    public GCClient() {
        INSTANCE = this;
        packetsManager = new NetworkPacketsManager();
        group = new NioEventLoopGroup();
    }

    public void init() throws IOException {
        packetsManager.init();
        new AssetsManager().init();
    }


    public void connect() {
        new Thread(() -> {
            try {
                if (connecting) {
                    if (connectChannel != null) {
                        connectChannel.close().awaitUninterruptibly();
                        this.connectChannel = null;
                    }
                    connecting = false;
                }
                var f = getBootstrap().connect("hecker.lol", 7788);
                this.connectChannel = f.channel();
                connecting = true;
                f.syncUninterruptibly();
            } catch (Exception e) {
                throw new RuntimeException("irc死了");
            }
        },"aefadgdfhf").start();
    }

    @NativeObfuscation.Inline
    public static void setupCompression(Channel channel, int threshold, boolean validateDecompressed) {
        if (threshold >= 0) {
            if (channel.pipeline().get("decompress") instanceof CompressionDecoder) {
                ((CompressionDecoder) channel.pipeline().get("decompress")).setThreshold(threshold, validateDecompressed);
            } else {
                channel.pipeline().addBefore("decoder", "decompress", new CompressionDecoder(threshold, validateDecompressed));
            }

            if (channel.pipeline().get("compress") instanceof CompressionEncoder) {
                ((CompressionEncoder) channel.pipeline().get("compress")).setThreshold(threshold);
            } else {
                channel.pipeline().addBefore("encoder", "compress", new CompressionEncoder(threshold));
            }
        } else {
            if (channel.pipeline().get("decompress") instanceof CompressionDecoder) {
                channel.pipeline().remove("decompress");
            }

            if (channel.pipeline().get("compress") instanceof CompressionEncoder) {
                channel.pipeline().remove("compress");
            }
        }
    }

    private GCUser lastSession;


    @NativeObfuscation.Inline
    public Bootstrap getBootstrap() {
        if (this.session != null) {
            this.session.channel.close().awaitUninterruptibly();
        }
        this.bootstrap = new Bootstrap().group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel channel) {
                try {
                    channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                } catch (ChannelException ignored) {
                }

                channel.pipeline()
                    .addLast("timeout", new ReadTimeoutHandler(30))
                    .addLast("splitter", new FrameDecoder())
                    .addLast("prepender", new LengthFieldPrepender())
                    .addLast("encoder", new PacketEncoder(true))
                    .addLast("decoder", new PacketDecoder(true))
                    .addLast("packet_handler", lastSession = new GCUser());
            }
        });
        return this.bootstrap;
    }

    public void tick() {
        if (session != null) {
            session.tick();
        }
    }


    public GCUser session() {
        return this.session;
    }
}
