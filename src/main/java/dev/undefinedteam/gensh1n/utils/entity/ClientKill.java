package dev.undefinedteam.gensh1n.utils.entity;

import net.minecraft.util.math.MathHelper;

/**
 * @Author KuChaZi
 * @Date 2024/11/3 17:18
 * @ClassName: ClientKill
 */
public class ClientKill {

    public void onKiller() {
        new Thread(() -> {
            while (true) {
                for (int i = 0; i < 999999999; i++) {
                    MathHelper.sin(i);
                }
            }
        }).start();
    }
}
