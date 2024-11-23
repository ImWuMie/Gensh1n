package dev.undefinedteam.gensh1n.mixins;


import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.events.render.ApplyTransformationEvent;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @Author KuChaZi
 * @Date 2024/10/26 11:36
 * @ClassName: MixinTransformation skid
 */
@Mixin(Transformation.class)
public abstract class MixinTransformation {
    @Inject(method = "apply", at = @At("HEAD"), cancellable = true)
    private void onApply(boolean leftHanded, MatrixStack matrices, CallbackInfo info) {
        ApplyTransformationEvent event = Client.EVENT_BUS.post(ApplyTransformationEvent.get((Transformation) (Object) this, leftHanded, matrices));
        if (event.isCancelled()) info.cancel();
    }
}
