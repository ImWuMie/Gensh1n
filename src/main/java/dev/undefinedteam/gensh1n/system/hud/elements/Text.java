package dev.undefinedteam.gensh1n.system.hud.elements;

import dev.undefinedteam.gensh1n.render._new.NText;
import dev.undefinedteam.gensh1n.render._new.NTextRenderer;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.hud.ElementInfo;
import dev.undefinedteam.gensh1n.system.hud.HudElement;
import dev.undefinedteam.gensh1n.utils.render.color.SettingColor;
import net.minecraft.client.gui.DrawContext;

public class Text extends HudElement {
    public static final ElementInfo INFO = new ElementInfo("Text", "Custom text", Text.class);

    private final SettingGroup sg = settings.getDefaultGroup();

    private final Setting<String> text = text(sg, "text", "input", "Example");
    private final Setting<Integer> size = intN(sg, "font-size", 18, 1, 96);
    private final Setting<Boolean> shadow = bool(sg, "shadow", false);
    private final Setting<SettingColor> color = color(sg, "color", new SettingColor(255, 255, 255, 255));

    public Text() {
        super(INFO);
        size.onChange(s -> renderer = new NTextRenderer(NText.TEXT_BUFFER_CAPACITY, s));
    }

    private NTextRenderer renderer;

    @Override
    public void render(DrawContext context, float delta) {
        if (renderer == null) {
            renderer = new NTextRenderer(NText.TEXT_BUFFER_CAPACITY, size.get());
        }
        var font = renderer;
        var text = this.text.get();
        if (text.isEmpty() && inEdit) {
            text = "\"Empty\"";
        }

        font.begin(context.getMatrices());
        font.draw(text, this.x, this.y, this.color.get().getPacked(), this.shadow.get());
        font.end();

        setElementSize((int) font.getWidth(text, this.shadow.get()), (int) font.getHeight(text, this.shadow.get()));
    }
}
