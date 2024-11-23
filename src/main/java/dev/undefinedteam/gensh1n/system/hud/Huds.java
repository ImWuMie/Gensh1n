package dev.undefinedteam.gensh1n.system.hud;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.Genshin;
import dev.undefinedteam.gensh1n.events.client.KeyEvent;
import dev.undefinedteam.gensh1n.events.client.MouseButtonEvent;
import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.events.render.Render2DBeforeChat;
import dev.undefinedteam.gensh1n.render.Renderer;
import dev.undefinedteam.gensh1n.system.System;
import dev.undefinedteam.gensh1n.system.Systems;
import dev.undefinedteam.gensh1n.system.hud.elements.*;
import dev.undefinedteam.gensh1n.system.hud.gui.HudEditorFragment;
import dev.undefinedteam.gensh1n.utils.RandomUtils;
import dev.undefinedteam.gensh1n.utils.Utils;
import dev.undefinedteam.gensh1n.utils.render.color.Color;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import static dev.undefinedteam.gensh1n.Client.mc;


@StringEncryption
@ControlFlowObfuscation
public class Huds extends System<Huds> implements Snapper.Container {
    private static final Color SPLIT_LINES_COLOR = new Color(255, 255, 255, 75);

    private static final Color INACTIVE_BG_COLOR = new Color(200, 25, 25, 50);
    private static final Color INACTIVE_OL_COLOR = new Color(200, 25, 25, 200);

    private static final Color HOVER_BG_COLOR = new Color(200, 200, 200, 50);
    private static final Color HOVER_OL_COLOR = new Color(200, 200, 200, 200);

    private static final Color SELECTION_BG_COLOR = new Color(225, 225, 225, 25);
    private static final Color SELECTION_OL_COLOR = new Color(225, 225, 225, 100);

    private final Map<Class<? extends HudElement>, ElementInfo> hudTypes = new Reference2ReferenceOpenHashMap<>();
    private final Map<ElementInfo, List<HudElement>> hudInstances = new Reference2ReferenceOpenHashMap<>();

    /**
     * In Editor
     */
    private final Snapper snapper;
    private Snapper.Element selectionSnapBox;
    private double lastMouseX, lastMouseY;
    private boolean pressed;
    private double clickX, clickY;
    private final List<HudElement> selection = new ArrayList<>();
    private boolean moved, dragging;
    private HudElement addedHoveredToSelectionWhenClickedElement;

    public Huds() {
        super("huds");

        this.snapper = new Snapper(this);
    }

    @Override
    public void init() {
        registerElement(ActiveMods.INFO);
        registerElement(Text.INFO);
        registerElement(MusicLyricHud.INFO);
        registerElement(TargetHud.INFO);
        registerElement(Watermark.INFO);
    }

    public boolean inEditMode, active = true, canDrag;

    @EventHandler
    private void onRender2D(Render2DBeforeChat e) {
        this.inEditMode = HudEditorFragment.isOpen() || mc.currentScreen instanceof ChatScreen;
        canDrag = HudEditorFragment.isOpen() || mc.currentScreen instanceof ChatScreen;
        render(e.drawContext, e.tickDelta);
    }

    @EventHandler
    private void onKey(KeyEvent e) {
        if (canDrag)
            keyInput(e.key);
    }

    public void render(DrawContext context, float delta) {
        renderElements(context, delta);

        if (!canDrag) return;

        var mouseX = (int) mc.mouse.getX();
        var mouseY = (int) mc.mouse.getY();

        if (lastMouseX != mouseX || lastMouseY != mouseY)
            mouseMoved(mouseX, mouseY);

        // Inactive
        for (HudElement element : getAll()) {
            if (!element.isActive()) renderElement(element, INACTIVE_BG_COLOR, INACTIVE_OL_COLOR);
        }

        // Selected
        if (pressed && !dragging) fillSelection(mouseX, mouseY);
        for (HudElement element : selection) renderElement(element, HOVER_BG_COLOR, HOVER_OL_COLOR);
        if (pressed && !dragging) selection.clear();

        // Selection
        if (pressed && !dragging) {
            double x1 = Math.min(clickX, mouseX);
            double x2 = Math.max(clickX, mouseX);

            double y1 = Math.min(clickY, mouseY);
            double y2 = Math.max(clickY, mouseY);

            renderQuad(x1, y1, x2 - x1, y2 - y1, SELECTION_BG_COLOR, SELECTION_OL_COLOR);
        }

        // Hovered
        if (!pressed) {
            HudElement hovered = getHovered(mouseX, mouseY);
            if (hovered != null) renderElement(hovered, HOVER_BG_COLOR, HOVER_OL_COLOR);
        }
    }

    public void renderElements(DrawContext drawContext, float delta) {
        for (HudElement element : getAll()) {
            element.updatePos();
            element.transPos(delta);
            element.inEdit = this.inEditMode;

            if (active) {
                if (canDrag) {
                    element.render(drawContext, delta);
                } else if (element.isActive()) element.render(drawContext, delta);
            }
        }
    }


    @EventHandler
    private void onMouseInput(MouseButtonEvent e) {
        if (!canDrag) return;

        var mouseX = mc.mouse.getX();
        var mouseY = mc.mouse.getY();
        var keyAction = e.action;
        switch (keyAction) {
            case Press -> mouseClicked(mouseX, mouseY, e.button);
            case Release -> mouseReleased(mouseX, mouseY, e.button);
        }
    }

    @EventHandler
    private void onTick(TickEvent.Pre e) {
        for (List<HudElement> elements : hudInstances.values()) {
            for (HudElement element : elements) {
                if (element.active) element.tick();
            }
        }
    }

    public HudElement addElement(ElementInfo element) {
        try {
            var instance = (HudElement) element.target.getConstructor().newInstance();

            if (Client.isOnMinecraftEnv() && mc.getWindow() != null) {
                var windowWidth = mc.getWindow().getFramebufferWidth();
                var windowHeight = mc.getWindow().getFramebufferHeight();
                instance.setElementPos(windowWidth / 2, windowHeight / 2);
            }

            List<HudElement> elements = this.hudInstances.getOrDefault(element, null);
            if (elements == null) {
                elements = new ArrayList<>();
                elements.add(instance);
                this.hudInstances.put(element, elements);
            } else elements.add(instance);

            return instance;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            Genshin.LOG.info("Failed add hud type (constructor must be 'no args')", e);
        }
        return null;
    }

    public ElementInfo removeElement(HudElement element) {
        var elementInfo = element.mElementInfo;
        List<HudElement> elements = this.hudInstances.getOrDefault(elementInfo, null);
        if (elements != null) {
            elements.remove(element);
        }

        return elementInfo;
    }

    public void registerElement(ElementInfo info) {
        this.hudTypes.put(info.target, info);
    }

    public ElementInfo find(Class<? extends HudElement> klass) {
        return this.hudTypes.getOrDefault(klass, null);
    }

    public ElementInfo find(String name) {
        return this.hudTypes.values().stream().filter(i -> i.name.equals(name)).findFirst().orElse(null);
    }

    public static Huds get() {
        return Systems.get(Huds.class);
    }

    @Override
    public JsonObject toTag() {
        JsonObject tag = new JsonObject();
        JsonArray hudsTag = new JsonArray();
        for (List<HudElement> value : hudInstances.values()) {
            for (HudElement element : value) {
                JsonObject hudObject = element.toTag();
                hudsTag.add(hudObject);
            }
        }
        tag.addProperty("active", this.active);
        tag.add("elements", hudsTag);
        return tag;
    }

    @Override
    public Huds fromTag(JsonObject tag) {
        this.active = tag.get("active").getAsBoolean();

        JsonArray array = tag.getAsJsonArray("elements");
        for (JsonElement o : array) {
            var obj = o.getAsJsonObject();
            var name = obj.get("_element_name").getAsString();
            ElementInfo info;
            if ((info = find(name)) != null) {
                var element = addElement(info);
                if (element != null) element.fromTag(obj);
            }
        }
        return this;
    }


    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            pressed = true;
            selectionSnapBox = null;

            HudElement hovered = getHovered(mouseX, mouseY);
            dragging = hovered != null;
            if (dragging) {
                if (!selection.contains(hovered)) {
                    selection.clear();
                    selection.add(hovered);
                    addedHoveredToSelectionWhenClickedElement = hovered;
                }
            } else selection.clear();

            clickX = mouseX;
            clickY = mouseY;
        }
    }

    public void mouseMoved(double mouseX, double mouseY) {
        if (dragging && !selection.isEmpty()) {
            if (selectionSnapBox == null) selectionSnapBox = new SelectionBox();
            snapper.move(selectionSnapBox, (int) (mouseX - lastMouseX), (int) (mouseY - lastMouseY));
        }

        if (pressed) moved = true;

        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) pressed = false;

        if (addedHoveredToSelectionWhenClickedElement != null) {
            selection.remove(addedHoveredToSelectionWhenClickedElement);
            addedHoveredToSelectionWhenClickedElement = null;
        }

        if (moved) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && !dragging) fillSelection(mouseX, mouseY);
        } else {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                HudElement hovered = getHovered(mouseX, mouseY);
                if (hovered != null) hovered.toggle();
            } else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                HudElement hovered = getHovered(mouseX, mouseY);

                if (hovered != null && HudEditorFragment.get().current == null)
                    HudEditorFragment.get().setCurrentElement(hovered);
            }
        }

        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            snapper.unsnap();
            moved = dragging = false;
        }
    }

    public void keyInput(int keyCode) {
        if (!pressed) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                HudElement hovered = getHovered(lastMouseX, lastMouseY);
                if (hovered != null) hovered.toggle();
            } else if (keyCode == GLFW.GLFW_KEY_DELETE) {
                HudElement hovered = getHovered(lastMouseX, lastMouseY);

                if (hovered != null) removeElement(hovered);
                else {
                    for (HudElement element : selection) removeElement(element);
                    selection.clear();
                }
            }
        }
    }

    private HudElement getHovered(double mouseX, double mouseY) {
        for (List<HudElement> value : this.hudInstances.values()) {
            for (HudElement element : value) {
                if (element.isHovering(mouseX, mouseY)) {
                    return element;
                }
            }
        }
        return null;
    }

    private void renderQuad(double x, double y, double w, double h, Color bgColor, Color olColor) {
        var renderer = Renderer.MAIN;

        renderer.drawRect(x + 1, y + 1, w - 2, h - 2, bgColor);

        renderer.drawRect(x, y, w, 1, olColor);
        renderer.drawRect(x, y + h - 1, w, 1, olColor);
        renderer.drawRect(x, y + 1, 1, h - 2, olColor);
        renderer.drawRect(x + w - 1, y + 1, 1, h - 2, olColor);
    }

    private void renderElement(HudElement element, Color bgColor, Color olColor) {
        renderQuad(element.getElementX(), element.getElementY(), element.getElementWidth(), element.getElementHeight(), bgColor, olColor);
    }

    private void fillSelection(double mouseX, double mouseY) {
        double x1 = Math.min(clickX, mouseX);
        double x2 = Math.max(clickX, mouseX);

        double y1 = Math.min(clickY, mouseY);
        double y2 = Math.max(clickY, mouseY);

        for (HudElement e : getAll()) {
            if ((e.getElementX() <= x2 && e.getElementX2() >= x1) && (e.getElementY() <= y2 && e.getElementY2() >= y1))
                selection.add(e);
        }
    }

    public List<HudElement> getAll() {
        return hudInstances.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public Iterable<Snapper.Element> getElements() {
        return () -> new Iterator<>() {
            private final Iterator<HudElement> it = getAll().iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Snapper.Element next() {
                return it.next();
            }
        };
    }

    @Override
    public boolean shouldNotSnapTo(Snapper.Element element) {
        return selection.contains((HudElement) element);
    }

    @Override
    public int getSnappingRange() {
        return 0;
    }

    public Collection<ElementInfo> types() {
        return this.hudTypes.values();
    }


    private class SelectionBox implements Snapper.Element {
        private double x, y;
        private final double width, height;

        public SelectionBox() {
            double x1 = Integer.MAX_VALUE;
            double y1 = Integer.MAX_VALUE;

            double x2 = 0;
            double y2 = 0;

            for (HudElement element : selection) {
                if (element.getElementX() < x1) x1 = element.getElementX();
                else if (element.getElementX() > x2) x2 = element.getElementX();

                if (element.getElementX2() < x1) x1 = element.getElementX2();
                else if (element.getElementX2() > x2) x2 = element.getElementX2();

                if (element.getElementY() < y1) y1 = element.getElementY();
                else if (element.getElementY() > y2) y2 = element.getElementY();

                if (element.getElementY2() < y1) y1 = element.getElementY2();
                else if (element.getElementY2() > y2) y2 = element.getElementY2();
            }

            this.x = x1;
            this.y = y1;
            this.width = x2 - x1;
            this.height = y2 - y1;
        }

        @Override
        public double getElementX() {
            return x;
        }

        @Override
        public double getElementY() {
            return y;
        }

        @Override
        public double getElementWidth() {
            return width;
        }

        @Override
        public double getElementHeight() {
            return height;
        }

        @Override
        public void setElementPos(double x, double y) {
            for (HudElement element : selection)
                element.setElementPos(x + (element.getElementX() - this.x), y + (element.getElementY() - this.y));

            this.x = x;
            this.y = y;
        }

        @Override
        public void move(double deltaX, double deltaY) {
            double prevX = x;
            double prevY = y;

            int border = 1;
            x = MathHelper.clamp(x + deltaX, border, Utils.getWindowWidth() - width - border);
            y = MathHelper.clamp(y + deltaY, border, Utils.getWindowHeight() - height - border);

            for (HudElement element : selection) element.move(x - prevX, y - prevY);
        }
    }
}
