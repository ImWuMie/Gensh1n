package dev.undefinedteam.gensh1n.events.player;

import dev.undefinedteam.gensh1n.events.Cancellable;
import net.minecraft.entity.player.PlayerEntity;

/**
 * @Author KuChaZi
 * @Date 2024/11/9 14:05
 * @ClassName: DeathEvent
 */
public class DeathEvent extends Cancellable {
    private final PlayerEntity player;

    public DeathEvent(PlayerEntity player) {
        this.player = player;
    }

    public PlayerEntity getPlayer(){
        return player;
    }
}
