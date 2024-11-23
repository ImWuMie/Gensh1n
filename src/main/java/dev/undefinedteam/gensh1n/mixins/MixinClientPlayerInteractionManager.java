package dev.undefinedteam.gensh1n.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.events.player.AttackEvent;
import dev.undefinedteam.gensh1n.events.player.ClickSlotEvent;
import dev.undefinedteam.gensh1n.events.player.PlaceEvent;
import dev.undefinedteam.gensh1n.mixin_interface.IClientPlayerInteractionManager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager implements IClientPlayerInteractionManager {
    @Shadow
    protected abstract void syncSelectedSlot();

    @Override
    public void gensh1n$syncSelected() {
        syncSelectedSlot();
    }

    @Inject(method = "attackEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;syncSelectedSlot()V", shift = At.Shift.AFTER), cancellable = true)
    private void hookAttack(PlayerEntity player, Entity target, CallbackInfo callbackInfo) {
        if (Client.EVENT_BUS.post(new AttackEvent(target)).isCancelled()) {
            callbackInfo.cancel();
        }
    }
    @Inject(method = "interactBlock", at=@At(value = "INVOKE",target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;syncSelectedSlot()V",shift = At.Shift.AFTER),cancellable = true)
    public void hookPlace(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir){
        if(Client.EVENT_BUS.post(new PlaceEvent()).isCancelled()){
            cir.cancel();
            cir.setReturnValue(ActionResult.FAIL);
        }
    }

    @ModifyExpressionValue(method = "syncSelectedSlot", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerInventory;selectedSlot:I"))
    private int hookCustomSelectedSlot(int original) {
        return Client.HOTBAR.hasSelect() ? Client.HOTBAR.getSlot() : original;
    }


    @Inject(method = "clickSlot", at = @At("HEAD"), cancellable = true)
    public void clickSlotHook(int syncId, int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        ClickSlotEvent event = new ClickSlotEvent(actionType, slotId, button, syncId);
        Client.EVENT_BUS.post(event);
        if (event.isCancelled())
            ci.cancel();
    }
}
