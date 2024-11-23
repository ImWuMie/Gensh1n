package dev.undefinedteam.gensh1n.system.modules.misc;

import dev.undefinedteam.gensh1n.Genshin;
import dev.undefinedteam.gensh1n.events.network.PacketEvent;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.utils.chat.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.text.Text;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.Arrays;
import java.util.Random;

/**
 * @Author KuChaZi
 * @Date 2024/11/9 16:26
 * @ClassName: AutoPlay
 */
@StringEncryption
@ControlFlowObfuscation
public class AutoPlay extends Module {
    private final SettingGroup sgDefault = settings.getDefaultGroup();
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<ServerMode> server = choice(sgGeneral,"ServerMode", ServerMode.Heypixel);
    public final Setting<PlayerMode> soundmode = choice(sgGeneral,"SoundMode", PlayerMode.Null);
    public final Setting<Double> volume = doubleN(sgDefault,"Volume", 100, 0, 100);
    private final Setting<Boolean> randomPlay = bool(sgDefault, "RandomPlay", false);
    @NativeObfuscation.Inline
    public static final Random random = new Random();

    public enum ServerMode {
        Hypixel,
        Heypixel
    }

    public enum PlayerMode {
        Null,
        Gambare,
        ChunFengJinLing,
        Love,
        JiaBaiLi,
        DuYe,
        ChiJi
    }

    public AutoPlay() {
        super(Categories.Misc, "auto-play", "Auto Player (((");
    }


    @NativeObfuscation
    public static void playRandomSound() {
        PlayerMode[] modes = PlayerMode.values();
        PlayerMode[] vm = Arrays.stream(modes).filter(mode -> mode != PlayerMode.Null).toArray(PlayerMode[]::new);
        PlayerMode randomMode = vm[random.nextInt(vm.length)];
        Genshin.soundManager.playerSound(randomMode);
    }


    @EventHandler
    @SuppressWarnings("unused")
    public void onChatPacketReceive(PacketEvent event) {
        if (mc.world == null) return;
        if (event.origin == PacketEvent.TransferOrigin.RECEIVE && event.packet instanceof TitleS2CPacket packet) {
            Text text = packet.getTitle();
            if ((text.getString().contains("胜利")&& server.get() == ServerMode.Heypixel)//&& text.getString().contains("恭喜你赢下了比赛!")
                || (text.getString().contains("VICTORY!") && server.get() == ServerMode.Hypixel)) {
                ChatUtils.info("check 迪克 --> player sound");
                if (randomPlay.get()) {
                    playRandomSound();
                } else {
                    Genshin.soundManager.playerSound(soundmode.get());
                }
                nWarn("[AutoPlayer] 正在播放MVP音乐 😍😍😎", NLONG);
            }
        }
    }






//    @EventHandler
//    @SuppressWarnings("unused")
//    public void onChatPacket(PacketEvent event) {
//        if (mc.world == null) return;
//        if (event.origin == PacketEvent.TransferOrigin.RECEIVE && event.packet instanceof TitleS2CPacket packet) {
//            Text text = packet.getTitle();
//            mc.player.sendMessage(Text.literal(text.getString()), false);
//        }
//    }


}
