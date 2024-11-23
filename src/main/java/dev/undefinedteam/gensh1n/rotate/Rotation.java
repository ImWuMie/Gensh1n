package dev.undefinedteam.gensh1n.rotate;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import static dev.undefinedteam.gensh1n.Client.ROT;
import static dev.undefinedteam.gensh1n.Client.mc;

@StringEncryption
@NativeObfuscation
@ControlFlowObfuscation
public class Rotation {

    public float yaw;
    public float pitch;

    public Rotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public boolean setYaw(float yaw) {
        if (this.yaw == yaw) {
            return false;
        }
        this.yaw = yaw;
        return true;
    }

    public boolean setPitch(float pitch) {
        if (this.pitch == pitch) {
            return false;
        }
        this.pitch = pitch;
        return true;
    }

    /**
     * Set rotations to [player]
     */
    public void toPlayer(AbstractClientPlayerEntity player) {
        if ((Float.isNaN(yaw) || Float.isNaN(pitch))) {
            return;
        }

        fixedSensitivity(mc.options.getMouseSensitivity().getValue().floatValue());

        player.setYaw(yaw);
        player.setPitch(pitch);
    }

    @NativeObfuscation.Inline
    public void fixedSensitivity(float sensitivity) {
        float f = sensitivity * 0.6F + 0.2F;
        float gcd = (float) (f * f * f * 8.0 * 0.15F);

        // get previous rotation
        Rotation rotation = ROT.serverRotation;

        // fix yaw
        float deltaYaw = yaw - rotation.yaw;
        deltaYaw -= deltaYaw % gcd;
        yaw = rotation.yaw + deltaYaw;

        // fix pitch
        float deltaPitch = pitch - rotation.pitch;
        deltaPitch -= deltaPitch % gcd;
        pitch = rotation.pitch + deltaPitch;
    }
    public Vec3d getRotationVec() {
        var yawCos = MathHelper.cos(-yaw * 0.017453292f);
        var yawSin = MathHelper.sin(-yaw * 0.017453292f);
        var pitchCos = MathHelper.cos(pitch * 0.017453292f);
        var pitchSin = MathHelper.sin(pitch * 0.017453292f);
        return new Vec3d((yawSin * pitchCos), (-pitchSin), (yawCos * pitchCos));
    }

    @Override
    public String toString() {
        return "Rotation(yaw=" + yaw + ", pitch=" + pitch + ")";
    }

    public Rotation copy() {
        return new Rotation(yaw, pitch);
    }
}
