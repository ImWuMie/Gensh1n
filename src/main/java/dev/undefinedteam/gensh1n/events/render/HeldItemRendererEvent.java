package dev.undefinedteam.gensh1n.events.render;

import dev.undefinedteam.gensh1n.events.Cancellable;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

/**
 * @Author KuChaZi
 * @Date 2024/10/27 12:39
 * @ClassName: HeldItemRendererEvent
 */
public class HeldItemRendererEvent extends Cancellable {
    private final Hand hand;
    private final ItemStack item;
    private float ep;
    private final MatrixStack stack;

    public HeldItemRendererEvent(Hand hand, ItemStack item, float equipProgress, MatrixStack stack) {
        this.hand = hand;
        this.item = item;
        this.ep = equipProgress;
        this.stack = stack;
    }

    public Hand getHand() {
        return hand;
    }

    public ItemStack getItem() {
        return item;
    }

    public float getEp() {
        return ep;
    }

    public MatrixStack getStack() {
        return stack;
    }
}
