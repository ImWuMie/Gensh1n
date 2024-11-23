package dev.undefinedteam.gensh1n.fakeplayer;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

import static dev.undefinedteam.gensh1n.Client.mc;

public class FakePlayerEntity extends OtherClientPlayerEntity{
    public boolean doNotPush, hideWhenInsideCamera;

    public FakePlayerEntity(PlayerEntity player, String name, float health, boolean copyInv) {
        super(mc.world, new GameProfile(UUID.randomUUID(), name));

        copyPositionAndRotation(player);

        prevYaw = getYaw();
        prevPitch = getPitch();
        headYaw = player.headYaw;
        prevHeadYaw = headYaw;
        bodyYaw = player.bodyYaw;
        prevBodyYaw = bodyYaw;

        Byte playerModel = player.getDataTracker().get(PlayerEntity.PLAYER_MODEL_PARTS);
        dataTracker.set(PlayerEntity.PLAYER_MODEL_PARTS, playerModel);

        getAttributes().setFrom(player.getAttributes());
        setPose(player.getPose());

        capeX = getX();
        capeY = getY();
        capeZ = getZ();

        if (health <= 20) {
            setHealth(health);
        } else {
            setHealth(health);
            setAbsorptionAmount(health - 20);
        }

        if (copyInv) getInventory().clone(player.getInventory());
    }

    public void spawn() {
        unsetRemoved();
        mc.world.addEntity(this);
    }

    public void despawn() {
        mc.world.removeEntity(getId(), RemovalReason.DISCARDED);
        setRemoved(RemovalReason.DISCARDED);
    }

}
