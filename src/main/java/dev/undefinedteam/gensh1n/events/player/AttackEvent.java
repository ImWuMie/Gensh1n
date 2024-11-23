package dev.undefinedteam.gensh1n.events.player;

import dev.undefinedteam.gensh1n.events.Cancellable;
import net.minecraft.entity.Entity;

public class AttackEvent extends Cancellable {
    public Entity target;

    public AttackEvent(Entity target) {
        this.target = target;
    }
}
