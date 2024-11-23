package dev.undefinedteam.gensh1n.system.modules.misc;

import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.system.modules.Modules;
import dev.undefinedteam.gensh1n.utils.chat.TextUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.ArrayList;
import java.util.Objects;

@StringEncryption
@ControlFlowObfuscation
public class Teams extends Module {
    public Teams() {
        super(Categories.Misc, "teams", "Prevents KillAura from attacking team mates.");
    }

    private final SettingGroup sgDefault = settings.getDefaultGroup();
    private final Setting<Boolean> scoreboard = bool(sgDefault, "scoreboard", true);
    private final Setting<Boolean> nameColor = bool(sgDefault, "name-color", true);
    private final Setting<Boolean> prefix = bool(sgDefault, "prefix", true);

    private final Setting<Boolean> armorColor = bool(sgDefault, "armor-color", true);

    private final Setting<Boolean> helmet = bool(sgDefault, "helmet", true, armorColor::get);
    private final Setting<Boolean> chestPlate = bool(sgDefault, "chest-plate", false, armorColor::get);
    private final Setting<Boolean> pants = bool(sgDefault, "pants", false, armorColor::get);
    private final Setting<Boolean> boots = bool(sgDefault, "bots", false, armorColor::get);

    public static boolean isInTeam(LivingEntity entity) {
        var teams = Modules.get().get(Teams.class);
        return teams.isActive() && teams.isTeammate(entity);
    }

    public boolean isTeammate(LivingEntity entity) {
        if (scoreboard.get() && mc.player.isTeammate(entity)) {
            return true;
        }

        var clientDisplayName = mc.player.getDisplayName();
        var targetDisplayName = entity.getDisplayName();

        if (clientDisplayName == null || targetDisplayName == null) {
            return false;
        }

        return checkName(clientDisplayName, targetDisplayName) ||
            checkPrefix(targetDisplayName, clientDisplayName) ||
            checkArmor(entity);
    }

    public boolean checkName(Text clientDisplayName, Text targetDisplayName) {
        if (!nameColor.get()) {
            return false;
        }

        var targetColor = clientDisplayName.getStyle().getColor();
        var clientColor = targetDisplayName.getStyle().getColor();

        return targetColor != null && clientColor != null && targetColor == clientColor;
    }

    public boolean checkPrefix(Text targetDisplayName, Text clientDisplayName) {
        if (!prefix.get()) {
            return false;
        }

        var targetName = TextUtils.stripMinecraftColorCodes(targetDisplayName.getString());
        var clientName = TextUtils.stripMinecraftColorCodes(clientDisplayName.getString());
        var targetSplit = targetName.split(" ");
        var clientSplit = clientName.split(" ");

        // Check if both names have a prefix
        return targetSplit.length > 1 && clientSplit.length > 1 && Objects.equals(targetSplit[0], clientSplit[0]);
    }

    public boolean checkArmor(LivingEntity entity) {
        if (!(entity instanceof PlayerEntity player)) {
            return false;
        }

        var list = new ArrayList<Integer>();
        if (helmet.get()) list.add(3);
        if (chestPlate.get()) list.add(2);
        if (pants.get()) list.add(1);
        if (boots.get()) list.add(0);

        var hasMatchingArmorColor = true;

        for (int slot : list) {
            if (!matchesArmorColor(player, slot)) {
                hasMatchingArmorColor = false;
                break;
            }
        }

        return hasMatchingArmorColor;
    }

    private boolean matchesArmorColor(PlayerEntity player, int armorSlot) {
        var ownStack = mc.player.getInventory().getArmorStack(armorSlot);
        var otherStack = player.getInventory().getArmorStack(armorSlot);
        // returns false if the armor is not dyeable (e.g., iron armor)
        // to avoid a false positive from `null == null`
        var ownColor = getArmorColor(ownStack);
        var otherColor = getArmorColor(otherStack);

        if (ownColor == -88888 || otherColor == -88888) {
            return false;
        }

        return ownColor == otherColor;
    }

    public TextColor getTeamColor(Entity entity) {
        return entity.getDisplayName().getStyle().getColor();
    }

    public int getArmorColor(ItemStack stack) {
        var item = stack.getItem();
        if (item instanceof ArmorItem armorItem && armorItem instanceof DyeableArmorItem dyeableArmorItem) {
            return dyeableArmorItem.getColor(stack);
        }

        return -88888;
    }
}
