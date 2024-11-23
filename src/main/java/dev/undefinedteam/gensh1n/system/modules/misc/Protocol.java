package dev.undefinedteam.gensh1n.system.modules.misc;

import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.events.game.GameJoinedEvent;
import dev.undefinedteam.gensh1n.events.game.ReceiveMessageEvent;
import dev.undefinedteam.gensh1n.events.network.PacketEvent;
import dev.undefinedteam.gensh1n.events.world.EntityJoinWorldEvent;
import dev.undefinedteam.gensh1n.events.world.WorldChangeEvent;
import dev.undefinedteam.gensh1n.protocol.IProtocol;
import dev.undefinedteam.gensh1n.protocol.heypixel.Heypixel;
import dev.undefinedteam.gensh1n.protocol.nel.GameSessionProvider;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.system.modules.Modules;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

@StringEncryption
@ControlFlowObfuscation
public class Protocol extends Module {
    public Protocol() {
        super(Categories.Misc, "protocol", "netease protocol");

        mode.onChange((p) -> p.protocol.reload());
    }

    private final SettingGroup sgDefault = settings.getDefaultGroup();
    private final Setting<Protocols> mode = choice(sgDefault, "mode", Protocols.HeyPixel);
    private final Setting<NeteaseLauncher> nel = choice(sgDefault, "nel", NeteaseLauncher.Myth);

    private final Setting<Integer> mythPort = intN(sgDefault, "myth-port", 14250,0,65535,() -> nel.get().equals(NeteaseLauncher.Myth));

    public static NeteaseLauncher getNEL() {
        return Modules.get().get(Protocol.class).nel.get();
    }

    public static int getMythPort() {
        return Modules.get().get(Protocol.class).mythPort.get();
    }

    @EventHandler

    private void onTick(TickEvent.Pre e) {
        mode.get().protocol.tick();
    }

    @EventHandler

    private void onEntityJoin(EntityJoinWorldEvent e) {
        mode.get().protocol.onEntityJoinWorld(e);
    }

    @EventHandler

    private void onGameJoin(GameJoinedEvent e) {
        mode.get().protocol.onGameJoin(e);
    }

    @EventHandler

    private void onWorldChange(WorldChangeEvent e) {
        mode.get().protocol.onWorldChanged(e);
    }

    @EventHandler

    private void onPacket(PacketEvent e) {
        var cancel = mode.get().protocol.onPacket(e.packet);
        e.setCancelled(cancel);
    }

    @EventHandler
    private void onJoin(GameJoinedEvent e) {
        //mode.get().protocol.reload();
    }

    @EventHandler
    private void onMsg(ReceiveMessageEvent e) {
        mode.get().protocol.onMessage(e.getMessage());
    }

    @Override
    public void onActivate() {
        mode.get().protocol.reload();
    }

    public boolean isHeypixel() {
        return this.isActive() && mode.get().equals(Protocols.HeyPixel);
    }

    public enum NeteaseLauncher {
        Zone,
        Myth
    }

    public enum Protocols {
        HeyPixel(Heypixel.get()),
        NULL(new IProtocol() {

            @Override
            public void init() {

            }

            @Override
            public void reload() {

            }

            @Override
            public String name() {
                return "";
            }

            @Override
            public boolean onPacket(Packet packet) {
                return false;
            }

            @Override
            public void onMessage(Text text) {

            }

            @Override
            public void onEntityJoinWorld(EntityJoinWorldEvent e) {

            }

            @Override
            public void onWorldChanged(WorldChangeEvent e) {

            }

            @Override
            public void onGameJoin(GameJoinedEvent e) {

            }

            @Override
            public void tick() {

            }
        });

        public final IProtocol protocol;

        Protocols(IProtocol protocol) {
            this.protocol = protocol;
        }
    }
}
