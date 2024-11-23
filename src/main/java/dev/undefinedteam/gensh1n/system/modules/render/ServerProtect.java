package dev.undefinedteam.gensh1n.system.modules.render;

import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

/**
 * @Author KuChaZi
 * @Date 2024/11/14 23:22
 * @ClassName: ServerProtect
 */
@StringEncryption
@ControlFlowObfuscation
public class ServerProtect extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    public final Setting<String> sn = text(sgGeneral, "Server Name", "花雨庭");

    public ServerProtect() {
        super(Categories.Render, "server-protect", "Change Server Name");
    }

}
