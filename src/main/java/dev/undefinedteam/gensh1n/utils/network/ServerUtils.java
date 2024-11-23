package dev.undefinedteam.gensh1n.utils.network;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;

import java.util.Locale;

import static dev.undefinedteam.gensh1n.Client.mc;

/**
 * @Author KuChaZi
 * @Date 2024/11/5 21:19
 * @ClassName: ServerUtils
 */
public class ServerUtils {

//    public static boolean isHypixel() {
//        if (mc.world != null && mc.player != null) {
//            ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
//            if (networkHandler != null && networkHandler.getTabList() != null) {
//                Text headerText = networkHandler.getTabList().getHeader();
//                if (headerText != null) {
//                    return headerText.getString().toLowerCase(Locale.ROOT).contains("hypixel.net");
//                }
//            }
//        }
//        return false;
//    }

    public static boolean isHypixel() {
        if (mc.getNetworkHandler() != null) {
            String serverAddress = mc.getNetworkHandler().getConnection().getAddress().toString();
            return serverAddress.contains("hypixel.net");
        }
        return false;
    }
}
