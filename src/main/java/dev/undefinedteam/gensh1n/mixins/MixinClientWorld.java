package dev.undefinedteam.gensh1n.mixins;

import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.events.world.EntityJoinWorldEvent;
import dev.undefinedteam.gensh1n.protocol.heypixel.Heypixel;
import dev.undefinedteam.gensh1n.system.modules.Modules;
import dev.undefinedteam.gensh1n.system.modules.misc.Protocol;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class MixinClientWorld {
    @Inject(method = "addEntity", at = @At("RETURN"))
    private void onAddEntity(Entity entity, CallbackInfo info) {
        if (Modules.get().get(Protocol.class).isHeypixel()) {
            Heypixel.get().onEntityJoinWorld(EntityJoinWorldEvent.get(entity, (World) (Object) this));
        }
        Client.EVENT_BUS.post(EntityJoinWorldEvent.get(entity, (World) (Object) this));
    }
}
