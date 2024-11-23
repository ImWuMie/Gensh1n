package dev.undefinedteam.gensh1n.system.hud.elements;

import dev.undefinedteam.gensh1n.render.Renderer;
import dev.undefinedteam.gensh1n.render._new.NText;
import dev.undefinedteam.gensh1n.system.hud.ElementInfo;
import dev.undefinedteam.gensh1n.system.hud.HudElement;
import dev.undefinedteam.gensh1n.system.modules.combat.KillAura;
import dev.undefinedteam.gensh1n.utils.entity.PlayerUtils;
import dev.undefinedteam.gensh1n.utils.render.color.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public class TargetHud extends HudElement {
    public static final ElementInfo INFO = new ElementInfo("TargetHud", "A target hud the fuck you thinkin bruv.", TargetHud.class);

    public TargetHud() {
        super(INFO);
    }

    public float lastHealthPercent, healthPercent;


    @Override
    public void render(DrawContext context, float delta) {
        var target = KillAura.getTarget();

        if (target == null && !inEdit) return;

        var name = (target != null ? target.getName() : mc.player.getName());

        if (name == null) return;

        if (target instanceof LivingEntity entity) {
            var health = entity.getHealth() + entity.getAbsorptionAmount();
            var maxHealth = entity.getMaxHealth() + entity.getMaxAbsorption();

            if (maxHealth > 0) {
                healthPercent = MathHelper.lerp(delta, lastHealthPercent, health / maxHealth);
            }
        }

        var font = NText.regular16;
        var renderer = Renderer.MAIN;

        var nameWidth = font.getWidth(name.getString(), true);
        var nameHeight = font.getHeight(name.getString(), true);

        var maxWidth = Math.max(nameWidth, 200);
        var maxHeight = 5 + nameHeight + 3 + 4;

        var paint = renderer._paint();
        paint.setRGBA(80, 80, 80, 80);
        paint.setSmoothWidth(5);

        var x = getElementX();
        var y = getElementY();
        renderer._renderer().drawRoundRect((float) x, (float) y, (float) (x + maxWidth), (float) (y + maxHeight), 5, paint);
        var healthY = y + 5 + nameHeight + 3;
        {
            var color = Color.WHITE;
            if (target instanceof PlayerEntity player) {
                color = PlayerUtils.getPlayerColor(player, color);
            }
            paint.setColor(color.getPacked());
            paint.setSmoothWidth(3);
            renderer._renderer().drawRoundRect((float) (x + 5), (float) healthY, (float) (x + maxWidth - 10), (float) (healthY + 4), 3, paint);
        }

        renderer.render();
        font.draw(name.asOrderedText(), x + 5, y + 5, Color.WHITE.getPacked(), true);

        setElementSize(MathHelper.floor(maxWidth), MathHelper.floor(maxHeight));
    }

    @Override
    public void tick() {
        lastHealthPercent = healthPercent;
    }
}
