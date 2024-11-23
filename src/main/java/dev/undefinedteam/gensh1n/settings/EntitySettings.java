package dev.undefinedteam.gensh1n.settings;

import dev.undefinedteam.gensh1n.system.SettingAdapter;
import dev.undefinedteam.gensh1n.system.friend.Friends;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;

import java.util.function.Predicate;

import static dev.undefinedteam.gensh1n.Client.mc;

public class EntitySettings implements SettingAdapter {
    public final Setting<Boolean> players;
    public final Setting<Boolean> friends;
    public final Setting<Boolean> mobs;
    public final Setting<Boolean> animals;
    public final Setting<Boolean> villagers;
    public final Setting<Boolean> endermans;
    public final Setting<Boolean> angry_endermans;
    public final Setting<Boolean> endermites;
    public final Setting<Boolean> zombified_piglin;
    public final Setting<Boolean> armor_stand;
    public final Setting<Boolean> babies;
    public final Setting<Boolean> nametagged;
    public final Setting<Boolean> boats;


    public EntitySettings(SettingGroup group) {
        this.players = bool(group, "players", true);
        this.friends = bool(group, "friends", false);
        this.mobs = bool(group, "mobs", true);
        this.animals = bool(group, "animals", false);
        this.villagers = bool(group, "villagers", false);
        this.endermans = bool(group, "endermans", false);
        this.angry_endermans = bool(group, "angry-endermans", true);
        this.endermites = bool(group, "endermites", false);
        this.zombified_piglin = bool(group, "zombified-piglin", false);
        this.armor_stand = bool(group, "armor-stand", false);
        this.babies = bool(group, "babies", true);
        this.nametagged = bool(group, "nametagged", true);
        this.boats = bool(group, "boat", false);
    }

    public boolean checkLiving(Entity entity, Predicate<Entity> predicate) {
        return check(entity, predicate) && (entity instanceof LivingEntity || (boats.get() && entity instanceof BoatEntity));
    }

    public boolean check(Entity entity, Predicate<Entity> predicate) {
        boolean check = predicate == null || predicate.test(entity);

        if (entity.equals(mc.player) || entity.equals(mc.cameraEntity)) check = false;
        if ((entity instanceof LivingEntity && ((LivingEntity) entity).isDead()) || !entity.isAlive()) check = false;

        if (entity instanceof PlayerEntity player) {
            if (!players.get()) check = false;
            if (player.isCreative()) check = false;
            if (!friends.get() && !Friends.get().shouldAttack(player)) check = false;
        }

        if (entity instanceof Tameable tameable
            && tameable.getOwnerUuid() != null
            && tameable.getOwnerUuid().equals(mc.player.getUuid())) check = false;

        if (entity instanceof MobEntity e && !(entity instanceof AnimalEntity)) {
            if (!mobs.get()) check = false;
            if (!villagers.get() && e instanceof VillagerEntity) check = false;
            if (!endermites.get() && e instanceof EndermiteEntity) check = false;
            if (!zombified_piglin.get() && e instanceof ZombifiedPiglinEntity) check = false;
            if (e instanceof EndermanEntity enderman) {
                if (!angry_endermans.get() && enderman.isAngry()) check = false;
                if (!endermans.get()) check = false;
            }
        }

        if (entity instanceof AnimalEntity animal) {
            if (!animals.get()) check = false;
            if (animals.get() && babies.get() && !animal.isBaby()) check = false;
        }

        if (!nametagged.get() && entity.hasCustomName()) check = false;
        if (!armor_stand.get() && entity instanceof ArmorStandEntity) check = false;
        if (!boats.get() && entity instanceof BoatEntity) check = false;

        return check;
    }
}
