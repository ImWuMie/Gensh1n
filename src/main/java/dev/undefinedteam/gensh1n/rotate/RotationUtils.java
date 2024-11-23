package dev.undefinedteam.gensh1n.rotate;

import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import static dev.undefinedteam.gensh1n.Client.mc;

@StringEncryption
@NativeObfuscation
@ControlFlowObfuscation
public class RotationUtils {
    @NativeObfuscation.Inline
    public static float[] getRotationBlock(BlockPos pos) {
        return getRotationsByVec(mc.player.getPos().add(0.0D, (double)mc.player.getEyeHeight(mc.player.getPose()), 0.0D), new Vec3d((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D));
    }
    @NativeObfuscation.Inline
    public static Vec3d flat(Vec3d a) {
        return new Vec3d(a.x, 0.0D, a.z);
    }
    @NativeObfuscation.Inline
    public static double lengthVector(Vec3d a)
    {
        return (double) MathHelper.sqrt((float) (a.x * a.x + a.y * a.y + a.z * a.z));
    }
    @NativeObfuscation.Inline
    public static float[] getRotationsByVec(Vec3d origin, Vec3d position) {
        Vec3d difference = position.subtract(origin);
        double distance = lengthVector(flat(difference));
        float yaw = (float)Math.toDegrees(Math.atan2(difference.z, difference.x)) - 90.0F;
        float pitch = (float)(-Math.toDegrees(Math.atan2(difference.y, distance)));
        return new float[]{yaw, pitch};
    }

    @NativeObfuscation.Inline
    public static Vec3d getVectorForRotation(final Rotation rotation) {
        float yawCos = MathHelper.cos(-rotation.yaw * 0.017453292F - (float) Math.PI);
        float yawSin = MathHelper.sin(-rotation.yaw * 0.017453292F - (float) Math.PI);
        float pitchCos = -MathHelper.cos(-rotation.pitch * 0.017453292F);
        float pitchSin = MathHelper.sin(-rotation.pitch * 0.017453292F);
        return new Vec3d(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
    }

}
