package dev.undefinedteam.gensh1n.system.modules.misc;

import dev.undefinedteam.gensh1n.events.network.PacketEvent;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.utils.chat.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.Arrays;
import java.util.UUID;

@StringEncryption
@ControlFlowObfuscation
public class AntiStaff extends Module {
    public static String[] staffs = {
        "绿豆乃SAMA",
        "nightbary",
        "体贴的炼金术雀",
        "StarNO1",
        "妖猫",
        "小妖猫",
        "妖猫的PC号",
        "小H修bug",
        "xiaotufei",
        "元宵",
        "CuteGirlQlQl",
        "彩笔",
        "布吉岛打工仔",
        "元宵的测试号",
        "抑郁的元宵",
        "元宵睡不醒",
        "抖音丶小匪",
        "练书法的苦力怕",
        "KiKiAman",
        "元宵睡不醒",
        "WS故",
        "彩笔qwq",
        "管理员-1",
        "管理员-2",
        "管理员-3",
        "管理员-4",
        "管理员-5",
        "管理员-6",
        "管理员-7",
        "管理员-8",
        "管理员-9",
        "管理员-10",
        "天使",
        "艾米丽",
        "可比不来嗯忑",
        "鸡你太美",
        "神伦子",
        "马哥乐"
    };
    private final SettingGroup sgDefault = settings.getDefaultGroup();
    private final Setting<AntiBot.Mode> mode = choice(sgDefault, "mode", AntiBot.Mode.Heypixel);
    private final Setting<Boolean> autohub = bool(sgDefault,"auto-hub",false);

    public AntiStaff() {
        super(Categories.Misc, "anti-staff", "Anti Staff");
    }

    @EventHandler
    public void onPacket(PacketEvent event) {
        if (event.packet instanceof PlayerListS2CPacket packet) {
            for (PlayerListS2CPacket.Entry entry : packet.getEntries()) {
                if (packet.getActions().contains(PlayerListS2CPacket.Action.ADD_PLAYER)) {
                    String name = entry.profile().getName();
                    if (Arrays.asList(staffs).contains(name)) {
                        if (entry.profile().getProperties().isEmpty() && packet.getEntries().size() == 1 &&
                            mc.getNetworkHandler() != null && mc.getNetworkHandler().getPlayerListEntry(entry.profile().getId()) != null) {
                            UUID playerId = entry.profile().getId();
                            var str = "Fake Staff Detected! " + entry.profile().getName();
                            warning(str);
                            nWarn(str, NSHORT);
                        } else {
                            var str = "Staff Detected! " + entry.profile().getName();
                            if(autohub.get()){
                                ChatUtils.sendPlayerMsg("/hub");
                            }
                            warning(str);
                            nWarn(str, NLONG);
                        }
                    }
                }
            }
        }
    }

    public enum Mode {
        Heypixel
    }
}
