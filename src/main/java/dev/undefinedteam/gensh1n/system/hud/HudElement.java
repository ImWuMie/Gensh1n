package dev.undefinedteam.gensh1n.system.hud;

import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.settings.Settings;
import dev.undefinedteam.gensh1n.system.SettingAdapter;
import dev.undefinedteam.gensh1n.utils.Utils;
import dev.undefinedteam.gensh1n.utils.misc.ISerializable;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class HudElement implements ISerializable<HudElement>, Snapper.Element, SettingAdapter {
    protected final MinecraftClient mc = Client.mc;
    private final ElementBox box;

    public final Settings settings = new Settings();

    public final ElementInfo mElementInfo;

    protected boolean inEdit;
    @Getter
    public boolean active = true;

    public XAnchor xAnchor = XAnchor.Left;
    public YAnchor yAnchor = YAnchor.Top;
    public boolean autoAnchors = true;
    private double targetX, targetY;

    public double x, y;

    public HudElement(ElementInfo info) {
        this.mElementInfo = info;
        this.box = new ElementBox(this);
    }

    public void render(DrawContext context, float delta) {
    }

    public void tick() {
    }

    public void toggle() {
        active = !active;
    }

    @Override
    public double getElementX() {
        return this.x;
    }

    @Override
    public double getElementY() {
        return this.y;
    }

    @Override
    public double getElementWidth() {
        return this.box.width;
    }

    @Override
    public double getElementHeight() {
        return this.box.height;
    }

    public void setElementSize(int width, int height) {
        this.box.setSize(width, height);
    }

    @Override
    public void setElementPos(double x, double y) {
        if (autoAnchors) {
            box.setPos(x, y);
            box.xAnchor = XAnchor.Left;
            box.yAnchor = YAnchor.Top;
            box.updateAnchors();
        } else {
            box.setPos(box.x + (x - this.targetX), box.y + (y - this.targetY));
        }

        updatePos();
    }

    @Override
    public void move(double deltaX, double deltaY) {
        box.move(deltaX, deltaY);
        updatePos();
    }

    public void updatePos() {
        targetX = box.getRenderX();
        targetY = box.getRenderY();
    }

    public void transPos(float delta) {
        this.x = Utils.smooth_s(this.x, targetX, 0.5);
        this.y = Utils.smooth_s(this.y, targetY, 0.5);
    }

    protected double alignX(double width, Alignment alignment) {
        return box.alignX(getElementWidth(), width, alignment);
    }

    public void setElementX(int x) {
        this.box.x = x;
        updatePos();
    }

    public void setElementY(int y) {
        this.box.y = y;
        updatePos();
    }

    public String getName() {
        return mElementInfo.name;
    }

    public String getDescription() {
        return mElementInfo.description;
    }

    public String getTitle() {
        return mElementInfo.title;
    }

    public boolean isHovering(double mouseX, double mouseY) {
        return Utils.isHoveringRect(this.getElementX(), this.getElementY(), this.getElementWidth(), this.getElementHeight(), mouseX, mouseY);
    }

    @Override
    public JsonObject toTag() {
        JsonObject tag = new JsonObject();
        tag.addProperty("_element_name", this.getName());
        tag.addProperty("_element_x", this.getElementX());
        tag.addProperty("_element_y", this.getElementY());
        tag.addProperty("_element_width", this.getElementWidth());
        tag.addProperty("_element_height", this.getElementHeight());
        tag.addProperty("_element_active", this.isActive());
        tag.addProperty("_element_x_anchor", this.xAnchor.name());
        tag.addProperty("_element_y_anchor", this.yAnchor.name());
        tag.addProperty("_element_auto_anchors", this.autoAnchors);

        tag.add("settings", settings.toTag());
        return tag;
    }

    @Override
    public HudElement fromTag(JsonObject tag) {
        int x = tag.get("_element_x").getAsInt();
        int y = tag.get("_element_y").getAsInt();
        int width = tag.get("_element_width").getAsInt();
        int height = tag.get("_element_height").getAsInt();
        this.active = tag.get("_element_active").getAsBoolean();
        this.xAnchor = XAnchor.valueOf(tag.get("_element_x_anchor").getAsString());
        this.yAnchor = YAnchor.valueOf(tag.get("_element_y_anchor").getAsString());
        this.autoAnchors = tag.get("_element_auto_anchors").getAsBoolean();

        this.box.setPos(x, y);
        this.box.setSize(width, height);
        this.settings.fromTag(tag.getAsJsonObject("settings"));
        updatePos();

        return this;
    }

}
