package dev.undefinedteam.gensh1n.rotate;

import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.system.modules.player.NoFall;
import dev.undefinedteam.gensh1n.system.modules.world.Scaffold;
import net.minecraft.entity.Entity;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import static dev.undefinedteam.gensh1n.Client.ROT;
import static dev.undefinedteam.gensh1n.Client.mc;

@StringEncryption
@NativeObfuscation
@ControlFlowObfuscation
public class URotation {
    public Rotation rot;

    public int priority, ticks;
    public boolean movefix, rotationPlace = false, silent, applyToPlayer;

    public boolean yawChanged, pitchChanged;
    public double reachDistance;

    public int passTicks;

    public URotation(Rotation rot, int priority, int ticks, boolean movefix, boolean rotationPlace, boolean silent, boolean applyToPlayer, boolean yawChanged, boolean pitchChanged, double reachDistance, int passTicks) {
        this.rot = rot;
        this.priority = priority;
        this.ticks = ticks;
        this.movefix = movefix;
        this.rotationPlace = rotationPlace;
        this.silent = silent;
        this.applyToPlayer = applyToPlayer;
        this.yawChanged = yawChanged;
        this.pitchChanged = pitchChanged;
        this.reachDistance = reachDistance;
        this.passTicks = passTicks;
    }

    public URotation(Module module) {
        this.rot = new Rotation(0, 0);
        priority(getPriority(module)).keep(1).reach(3);
    }

    public URotation yaw(float yaw) {
        if (this.rot.setYaw(yaw)) {
            this.yawChanged = true;
        }
        return this;
    }

    public URotation pitch(float pitch) {
        if (this.rot.setPitch(pitch)) {
            this.pitchChanged = true;
        }
        return this;
    }

    public URotation rotation(Rotation r) {
        if (Float.isNaN(r.yaw) || Float.isNaN(r.pitch)) return this;

        this.rot = r;
        yawChanged = true;
        pitchChanged = true;
        return this;
    }

    public URotation rotation(Entity e) {
        return this.rotation(ROT.getRotationsEntity(e));
    }

    public URotation rotation(float yaw, float pitch) {
        yaw(yaw).pitch(pitch);
        return this;
    }

    public URotation movefix() {
        this.movefix = true;
        return this;
    }

    public URotation rotationPlace() {
        this.rotationPlace = true;
        return this;
    }

    public URotation silent() {
        this.silent = true;
        return this;
    }

    public URotation player() {
        this.applyToPlayer = true;
        return this;
    }

    public URotation priority(int priority) {
        this.priority = priority;
        return this;
    }

    public URotation keep(int ticks) {
        this.ticks = ticks + 1;
        this.passTicks = ticks + 1;
        return this;
    }

    public URotation reach(double range) {
        this.reachDistance = range;
        return this;
    }

    public boolean set() {
        var result = ROT.apply(this);

        movefix = false;
        rotationPlace = false;
        silent = false;
        applyToPlayer = false;
        rotation(0, 0);
        yawChanged = pitchChanged = false;
        keep(1).reach(3);

        return result;
    }

    @NativeObfuscation.Inline
    public static int getPriority(Module module) {
        if (module instanceof NoFall) {
            return 2;
        } else if (module instanceof Scaffold) {
            return 1;
        }
        return 0;
    }

    public float yaw() {
        return yawChanged ? rot.yaw : mc.player.getYaw();
    }

    public float pitch() {
        return pitchChanged ? rot.pitch : mc.player.getPitch();
    }

    public URotation copy() {
        return new URotation(rot.copy(), priority, ticks, movefix, rotationPlace, silent, applyToPlayer, yawChanged, pitchChanged, reachDistance, passTicks);
    }
}
