package dev.undefinedteam.gensh1n.render._new;

import icyllis.modernui.annotation.RenderThread;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.ArrayList;
import java.util.List;


@StringEncryption
@ControlFlowObfuscation
public class NText {
    public static NText INSTANCE;

    public NText() {
        INSTANCE = this;
    }

    public static final int TEXT_BUFFER_CAPACITY = 2097152;

    public static NTextRenderer regular;
    public static NTextRenderer regular13;
    public static NTextRenderer regular16;
    public static NTextRenderer regular20;
    public static NTextRenderer regular22;

    private static final List<NTextRenderer> FONT_MAP = new ArrayList<>();

    @RenderThread
    public void init() {
        regular = register(16);
        regular13 = register(13);
        regular16 = register(16);
        regular20 = register(20);
        regular22 = register(22);
    }

    private NTextRenderer register(float size) {
        var renderer = new NTextRenderer(TEXT_BUFFER_CAPACITY, size);
        FONT_MAP.add(renderer);
        return renderer;
    }

    public static void begin(DrawContext context) {
        FONT_MAP.forEach(f -> f.begin(context.getMatrices()));
    }

    public static void begin(MatrixStack matrices) {
        FONT_MAP.forEach(f -> f.begin(matrices));
    }

    public static void draw() {
        FONT_MAP.forEach(NTextRenderer::end);
    }
}
