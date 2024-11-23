package dev.undefinedteam.gensh1n.system.hud.elements;

import dev.undefinedteam.gensh1n.render.GL;
import dev.undefinedteam.gensh1n.render.Renderer;
import dev.undefinedteam.gensh1n.render._new.NText;
import dev.undefinedteam.gensh1n.render._new.NTextRenderer;
import dev.undefinedteam.gensh1n.settings.*;
import dev.undefinedteam.gensh1n.system.hud.Alignment;
import dev.undefinedteam.gensh1n.system.hud.ElementInfo;
import dev.undefinedteam.gensh1n.system.hud.HudElement;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.system.modules.Modules;
import dev.undefinedteam.gensh1n.utils.render.ColorUtils;
import dev.undefinedteam.gensh1n.utils.render.Palette;
import dev.undefinedteam.gensh1n.utils.render.color.Color;
import dev.undefinedteam.gensh1n.utils.render.color.SettingColor;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class ActiveMods extends HudElement {
    public static final ElementInfo INFO = new ElementInfo("ActiveMods", "Show active mods", ActiveMods.class);

    public ActiveMods() {
        super(INFO);
    }

    private static final Color WHITE = new Color();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Sort> sort = choice(sgGeneral, "sort", "How to sort active modules.", Sort.Biggest);

    private final Setting<RenderMode> renderMode = choice(sgGeneral, "render-mode", "How to render the active modules.", RenderMode.Rainbow);

    private final Setting<SettingColor> baseColor = color(sgGeneral, "base-color", new SettingColor(255, 255, 255, 255));
    private final Setting<Boolean> additionalInfo = bool(sgGeneral, "additional-info", "Shows additional info from the module next to the name in the active modules list.", false);
    private final Setting<Boolean> renderBackground = bool(sgGeneral, "render-background", true);
    private final Setting<Double> rainbowSpeed = doubleN(sgGeneral, "rainbow-speed", 6,1,20);
    private final Setting<SettingColor> additionalInfoColor = color(sgGeneral, "additional-info-color", new SettingColor(169, 169, 169, 255));

    private final List<Module> modules = new ArrayList<>();

    public enum RenderMode {
        Rainbow,
        Rainbow1,
        BlueIceSakura
    }

    public static float hue = 0.0F;

    @Override
    public void tick() {
        var font = NText.regular16;

        modules.clear();
        modules.addAll(Modules.get().getActive());

        if (modules.isEmpty()) {
            if (inEdit) {
                var str = "Active Modules";
                setElementSize((int) (font.getWidth(str, true)), (int) (font.getHeight(str, true)));
            }
            return;
        }

        modules.sort((e1, e2) -> switch (sort.get()) {
            case Alphabetical -> e1.title.compareTo(e2.title);
            case Biggest -> Double.compare(getModuleWidth(font, e2), getModuleWidth(font, e1));
            case Smallest -> Double.compare(getModuleWidth(font, e1), getModuleWidth(font, e2));
        });

        double width = 0;
        double height = 0;

        for (int i = 0; i < modules.size(); i++) {
            Module module = modules.get(i);

            width = Math.max(width, getModuleWidth(font, module));
            height += font.getHeight(module.title, true);
            if (i > 0) height += 2;
        }

        setElementSize((int) width, (int) height);
    }

    @Override
    public void render(DrawContext context, float delta) {
        double x = this.x;
        double y = this.y;

        var font = NText.regular16;
        var renderer = Renderer.MAIN;

        if (modules.isEmpty()) {
            if (inEdit) {
                font.draw("Active Modules", x, y, WHITE.getPacked(), true);
            }
            return;
        }

        java.awt.Color rainbowcolors = java.awt.Color.getHSBColor(hue / 255.0f, 0.4f, 0.8f);
        java.awt.Color rainbowcolors2 = java.awt.Color.getHSBColor(hue / 255.0f, 1f, 1f);
        java.awt.Color color2222 = java.awt.Color.getHSBColor(hue / 255.0F, 0.55F, 0.9F);

        hue += rainbowSpeed.get().floatValue() / 5.0F;
        if (hue > 255.0F) {
            hue = 0.0F;
        }
        float h = hue;

        java.awt.Color rainbowcolor = java.awt.Color.getHSBColor(h / 255.0f, 0.4f, 0.8f);
        java.awt.Color rainbowcolor2 = java.awt.Color.getHSBColor(h / 255.0f, 0.6f, 1f);

        for (int i = 0; i < modules.size(); i++) {
            var width = getModuleWidth(font, modules.get(i));
            double offset = alignX(width, Alignment.Auto);

            {
                var renderX = x + offset;
                Module module = modules.get(i);
                int colorXD = switch (renderMode.get()) {
                    case Rainbow -> ColorUtils.rainbow(i * -50, true).getRGB();
                    case Rainbow1 -> java.awt.Color.getHSBColor(h / 255.0f, 0.5f, 0.9f).getRGB();
                    case BlueIceSakura -> new Color(rainbowcolor2.getRed(), 190, 255).getPacked();
                };

                if (renderBackground.get()) {
                    var paint = renderer._paint();
                    paint.setRGBA(0, 0, 0, 70);
                    paint.setSmoothWidth(3.0f);

                    renderer._renderer().drawRoundRect((float) renderX - 2, (float) y - 2, (float) (renderX + width + 1.5), (float) (y + font.getHeight(module.title, true) + 1.5), 5.0f, paint);
                }

                font.draw(module.title, renderX, y, colorXD, true);

                double emptySpace = font.getWidth(" ");
                double textLength = font.getWidth(module.title, true);

                if (additionalInfo.get()) {
                    String info = module.getInfoString();
                    if (info != null) {
                        font.draw(info, renderX + emptySpace + textLength, y, additionalInfoColor.get().awt().getRGB(), true);
                    }
                }
            }

            y += 2 + font.getHeight(modules.get(i).title, true);
        }
    }

    private double getModuleWidth(NTextRenderer renderer, Module module) {
        double width = renderer.getWidth(module.title, true);

        if (additionalInfo.get()) {
            String info = module.getInfoString();
            if (info != null) width += renderer.getWidth(" ") + renderer.getWidth(info, true);
        }

        return width;
    }


    public enum Sort {
        Alphabetical,
        Biggest,
        Smallest
    }
}
