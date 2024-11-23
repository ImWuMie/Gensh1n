package dev.undefinedteam.gensh1n.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.events.render.Render3DEvent;
import dev.undefinedteam.gensh1n.render.GL;
import dev.undefinedteam.gensh1n.render.MSAA;
import dev.undefinedteam.gensh1n.render.world.NR3D;
import dev.undefinedteam.gensh1n.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
    @Shadow
    @Final
    MinecraftClient client;


    @Shadow
    @Final
    private Camera camera;

    @Shadow
    protected abstract void tiltViewWhenHurt(MatrixStack matrices, float tickDelta);

    @Shadow
    protected abstract void bobView(MatrixStack matrices, float tickDelta);

    @Unique
    private final MatrixStack matrices = new MatrixStack();

    /**
     * We change crossHairTarget according to server side rotations
     */
    @ModifyExpressionValue(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;raycast(DFZ)Lnet/minecraft/util/hit/HitResult;"))
    private HitResult hookRaycast(HitResult original) {
//        if (camera != client.player) {
//            return original;
//        }
        double d = this.client.interactionManager.getReachDistance();

        if (Client.ROT.shouldOverride()) {
            var rotation = Client.ROT.targetRotation;
            return Client.ROT.getRotationOver(rotation.rot, d);
        } else return original;
    }

//    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z", opcode = Opcodes.GETFIELD, ordinal = 0), method = "renderWorld")
//    void render3dHook(float tickDelta, long limitTime, @NotNull MatrixStack matrix, CallbackInfo ci) {
//        Particles particles = new Particles();
//        particles.onRender3D(matrix);
//    }

    @ModifyExpressionValue(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getRotationVec(F)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d hookRotationVector(Vec3d original) {
//        if (camera != client.player) {
//            return original;
//        }

        return Client.ROT.shouldOverride() ? Client.ROT.getRotationVec(Client.ROT.targetRotation) : original;
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = {"ldc=hand"}), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onRenderWorld(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci) {
        if (!Utils.canUpdate()) return;
//        RenderSystem.getModelViewStack().pushMatrix().mul(matrix4f2);
//        matrices.push();
//        tiltViewWhenHurt(matrices, camera.getLastTickDelta());
//        if (client.options.getBobView().getValue()) bobView(matrices, camera.getLastTickDelta());
//
//        RenderSystem.getModelViewStack().mul(matrices.peek().getPositionMatrix().invert());
//        matrices.pop();

//        RenderSystem.applyModelViewMatrix();

        client.getProfiler().swap(Client.ASSETS_LOCATION + "_r3d");
        GL.saveRootState();
//        RenderSystem.getModelViewStack().push();

        MSAA.use(() -> {
            NR3D.REGULAR.begin();
            Render3DEvent event = Render3DEvent.get(matrices, tickDelta);
            event.renderer = NR3D.REGULAR;
            Client.EVENT_BUS.post(event);
            NR3D.REGULAR.render(matrices, 1.0f);
            event.postTasks.forEach(Runnable::run);
        });

        GL.restoreRootState();
        // Update model view matrix
//        RenderSystem.getModelViewStack().pop();
    }
}
