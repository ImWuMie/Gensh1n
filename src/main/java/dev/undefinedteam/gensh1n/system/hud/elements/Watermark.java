package dev.undefinedteam.gensh1n.system.hud.elements;

import dev.undefinedteam.gensh1n.render.Renderer;
import dev.undefinedteam.gensh1n.render._new.NText;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.hud.ElementInfo;
import dev.undefinedteam.gensh1n.system.hud.HudElement;
import dev.undefinedteam.gensh1n.utils.render.color.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Watermark extends HudElement {
    public static final ElementInfo INFO = new ElementInfo("Watermark", "Client", Watermark.class);

    public Watermark() {
        super(INFO);
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<String> clientName = text(sgGeneral, "client-name", "余胜军");
    private final Setting<ExtMode> extMode = choice(sgGeneral, "ext-mode", ExtMode.FPS);
    private final Setting<Boolean> background = bool(sgGeneral, "background", true);

    public enum ExtMode {
        FPS,
        Time
    }

    @Override
    public void render(DrawContext context, float delta) {
        var name = clientName.get();
        var ext = switch (extMode.get()) {
            case FPS -> mc.getCurrentFps() + " FPS";
            case Time -> {
                LocalTime time = LocalTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                yield time.format(formatter);
            }
        };

        var font = NText.regular20;

        var at0 = name.charAt(0);

        name = Formatting.BOLD.toString() + at0 + Formatting.WHITE + name.substring(1);

        var nameWidth = font.getWidth(name);
        var extWidth = font.getWidth(ext);

        var width = nameWidth + 10 + extWidth + 5;
        var height = Math.max(font.getHeight(name), font.getHeight(ext)) + 10;

        var x = getElementX();
        var y = getElementY();

        if (background.get()) {
            var renderer = Renderer.MAIN;
            var paint = renderer._paint();
            paint.setRGBA(0, 0, 0, 80);
            paint.setSmoothWidth(5);
            renderer._renderer().drawRoundRect((float) x, (float) y, (float) (x + width), (float) (y + height), 7, paint);
        }
        //renderer.render();

        font.draw(name, x + 5, y + 5, Color.WHITE.getPacked());
        font.draw(ext, x + nameWidth + 10, y + 5, Color.WHITE.getPacked());

        setElementSize(MathHelper.floor(width), MathHelper.floor(height));
    }

    @Override
    public void tick() {
        super.tick();
    }
}
