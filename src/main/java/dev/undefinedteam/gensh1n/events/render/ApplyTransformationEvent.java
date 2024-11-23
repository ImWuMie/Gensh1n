package dev.undefinedteam.gensh1n.events.render;

import dev.undefinedteam.gensh1n.events.Cancellable;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.util.math.MatrixStack;

/**
 * @Author KuChaZi
 * @Date 2024/10/26 11:35
 * @ClassName: ApplyTransformationEvent
 */
public class ApplyTransformationEvent extends Cancellable {
    private static final ApplyTransformationEvent INSTANCE = new ApplyTransformationEvent();

    public Transformation transformation;
    public boolean leftHanded;
    public MatrixStack matrices;

    public static ApplyTransformationEvent get(Transformation transformation, boolean leftHanded, MatrixStack matrices) {
        INSTANCE.setCancelled(false);

        INSTANCE.transformation = transformation;
        INSTANCE.leftHanded = leftHanded;
        INSTANCE.matrices = matrices;

        return INSTANCE;
    }
}
