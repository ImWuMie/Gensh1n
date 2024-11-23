package dev.undefinedteam.gensh1n.system.modules.misc;

import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Category;
import dev.undefinedteam.gensh1n.system.modules.Module;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;


@StringEncryption
@ControlFlowObfuscation
public class AntiPacketKick extends Module {
    public AntiPacketKick() {
        super(Categories.Misc, "anti-packet-kick", "Attempts to prevent you from being disconnected by large packets.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Boolean> catchExceptions = bool(sgGeneral,"catch-exceptions","Drops corrupted packets.",true);
    public final Setting<Boolean> logExceptions = bool(sgGeneral,"log-exceptions","Logs caught exceptions.",false,catchExceptions::get);

    public boolean catchExceptions() {
        return isActive() && catchExceptions.get();
    }
}
