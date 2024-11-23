package dev.undefinedteam.gensh1n.system.modules.render;

import dev.undefinedteam.gensh1n.events.render.Render3DEvent;
import dev.undefinedteam.gensh1n.render.Renderer;
import dev.undefinedteam.gensh1n.render._new.NText;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.settings.EntitySettings;
import icyllis.arc3d.core.Quaternion;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import static dev.undefinedteam.gensh1n.gui.frags.GMainGui.DEEP_BACKGROUND_COLOR;

public class NameTags extends Module {
    public NameTags() {
        super(Categories.Render, "name-tags", "Renders name tags on players.");
    }

    private final SettingGroup sgDefault = settings.getDefaultGroup();

    private final EntitySettings entities = entities(sgDefault);

    @EventHandler
    private void onRender3D(Render3DEvent e) {

    }
}
