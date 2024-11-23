package dev.undefinedteam.gensh1n.events.player;

import dev.undefinedteam.gensh1n.events.Cancellable;
import net.minecraft.util.math.Vec3d;

/**
 * @Author KuChaZi
 * @Date 2024/10/27 10:30
 * @ClassName: PlayerTravelEvent
 */
public class PlayerTravelEvent extends Cancellable {
    private Vec3d mVec;
    private boolean pre;

    public PlayerTravelEvent(Vec3d mVec,boolean pre) {
        this.mVec = mVec;
        this.pre = pre;
    }

    public Vec3d getmVec() {
        return mVec;
    }

    public boolean isPre() {
        return pre;
    }
}
