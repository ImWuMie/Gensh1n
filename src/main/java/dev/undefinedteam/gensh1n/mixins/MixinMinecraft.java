package dev.undefinedteam.gensh1n.mixins;

import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.events.client.OpenScreenEvent;
import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.events.game.GameLeftEvent;
import dev.undefinedteam.gensh1n.events.player.DamageBlockEvent;
import dev.undefinedteam.gensh1n.events.player.InputTickEvent;
import dev.undefinedteam.gensh1n.events.player.ItemUseEvent;
import dev.undefinedteam.gensh1n.events.render.RenderTickEvent;
import dev.undefinedteam.gensh1n.events.world.WorldChangeEvent;
import dev.undefinedteam.gensh1n.mixin_interface.IMinecraft;
import dev.undefinedteam.modernui.mc.text.TextLayoutEngine;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraft implements IMinecraft {
    @Shadow
    public abstract Profiler getProfiler();

    @Shadow
    @Nullable
    public ClientWorld world;

    @Shadow
    @Final
    private ReloadableResourceManagerImpl resourceManager;

    @Shadow
    public int attackCooldown;

    @Shadow
    private int itemUseCooldown;

    @Shadow
    protected abstract void doItemUse();

    @Shadow
    protected abstract boolean doAttack();

    @Inject(method = "<init>",at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ReloadableResourceManagerImpl;reload(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;Ljava/util/List;)Lnet/minecraft/resource/ResourceReload;",shift = At.Shift.AFTER))
    private void onInit(CallbackInfo info) {
        TextLayoutEngine.getInstance().init(this.resourceManager);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;render(FJZ)V", shift = At.Shift.BEFORE))
    private void startRenderTick(boolean hasMemory, CallbackInfo ci) {
        getProfiler().push(Client.LC_NAME + "_pre_render_tick");
        Client.EVENT_BUS.post(RenderTickEvent.Start.get());
        getProfiler().pop();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;render(FJZ)V", shift = At.Shift.AFTER))
    private void endRenderTick(boolean hasMemory, CallbackInfo ci) {
        getProfiler().push(Client.LC_NAME + "_post_render_tick");
        Client.EVENT_BUS.post(RenderTickEvent.End.get());
        getProfiler().pop();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onPreTick(CallbackInfo info) {
        getProfiler().push(Client.LC_NAME + "_pre_tick");
        Client.EVENT_BUS.post(TickEvent.Pre.get());
        getProfiler().pop();
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;overlay:Lnet/minecraft/client/gui/screen/Overlay;",shift = At.Shift.BEFORE))
    private void onInput(CallbackInfo info) {
        getProfiler().swap(Client.LC_NAME + "_input_tick");
        Client.EVENT_BUS.post(InputTickEvent.INSTANCE);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onPostTick(CallbackInfo info) {
        getProfiler().push(Client.LC_NAME + "_post_tick");
        Client.EVENT_BUS.post(TickEvent.Post.get());
        getProfiler().pop();
    }

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void onSetScreen(Screen screen, CallbackInfo info) {
        if (Client.EVENT_BUS.post(new OpenScreenEvent(screen)).isCancelled()) info.cancel();
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
    private void onDisconnect(Screen disconnectionScreen, CallbackInfo ci) {
        if (world != null) {
            Client.EVENT_BUS.post(GameLeftEvent.INSTANCE);
        }
    }

    @Inject(method = "setWorld", at = @At("HEAD"))
    private void hookWorldChangeEvent(ClientWorld world, CallbackInfo ci) {
        Client.EVENT_BUS.post(WorldChangeEvent.get(world));
    }

    @Inject(method = "doItemUse", at = @At(value = "HEAD"))
    private void doItemUse(CallbackInfo ci) {
        Client.EVENT_BUS.post(new ItemUseEvent());
    }

    @Inject(method = "handleBlockBreaking", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;swingHand(Lnet/minecraft/util/Hand;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void handleBlockBreaking(boolean bl, CallbackInfo ci, BlockHitResult blockHitResult, BlockPos blockPos, Direction direction) {
        if (world == null) {
            return;
        }
        Client.EVENT_BUS.post(new DamageBlockEvent(blockHitResult, blockPos, direction));
    }

    @Override
    public void genshin$setItemUseCooldown(int i) {
        itemUseCooldown = i;
    }

    @Override
    public void genshin$setAttackCooldown(int i) {
        attackCooldown = i;
    }

    @Override
    public void genshin$rightClick() {
        doItemUse();
    }

    @Override
    public void genshin$leftClick() {
        doAttack();
    }
}
