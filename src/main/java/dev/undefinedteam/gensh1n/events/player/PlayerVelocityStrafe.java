package dev.undefinedteam.gensh1n.events.player;

import net.minecraft.util.math.Vec3d;

public class PlayerVelocityStrafe {
    public Vec3d movementInput;
    public float speed,yaw;
    public Vec3d velocity;

    public PlayerVelocityStrafe(Vec3d movementInput, float speed, float yaw, Vec3d velocity) {
        this.movementInput = movementInput;
        this.speed = speed;
        this.yaw = yaw;
        this.velocity = velocity;
    }
}
