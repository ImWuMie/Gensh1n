package dev.undefinedteam.gensh1n.system.modules.misc;

import dev.undefinedteam.gensh1n.events.network.PacketEvent;
import dev.undefinedteam.gensh1n.events.world.WorldChangeEvent;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.system.modules.Modules;
import dev.undefinedteam.gensh1n.system.modules.world.Scaffold;
import dev.undefinedteam.gensh1n.utils.RandomUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.LinkedList;

@StringEncryption
@ControlFlowObfuscation
@NativeObfuscation
public class Disabler extends Module {
    private final SettingGroup sgDefault = settings.getDefaultGroup();
    private final Setting<Boolean> c0ffix = bool(sgDefault, "C0F-Fix", true);
    private final Setting<Boolean> duplicate = bool(sgDefault, "DuplicateRotPlace", true);
    private final Setting<Boolean> fabricated = bool(sgDefault, "FabricatedPlace", true);

//    private final Queue<ConfirmTransactionC2SPacket> c0fStorage = new LinkedList<>();
    private boolean lastTickSentC0F = false;
    private long lastLoadWorldTime = 0L;
    public Disabler() {
        super(Categories.Misc,"disabler","Disable AntiCheats");
    }

    @EventHandler
    public void onWorld(WorldChangeEvent event) {
        lastLoadWorldTime = System.currentTimeMillis();
    }

    @EventHandler
    public void onPacket(PacketEvent event){
        if (duplicate.get() && event.packet instanceof PlayerMoveC2SPacket pk) {
            if(Modules.get().get(Scaffold.class).isActive()){
                pk.yaw = getRandomYaw(pk.yaw);
            }
        }
    }

    @NativeObfuscation.Inline
    public static float getRandomYaw(float requestedYaw) {
        int rand = RandomUtils.nextInt(1, 200);
        return requestedYaw + (360 * rand);
    }

}
