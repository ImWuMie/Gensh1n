package dev.undefinedteam.gensh1n.gui.utils;

import dev.undefinedteam.gensh1n.render.Fonts;
import icyllis.modernui.core.Context;
import dev.undefinedteam.modernui.mc.ui.ThemeControl;
import icyllis.modernui.util.ColorStateList;
import icyllis.modernui.widget.LinearLayout;
import icyllis.modernui.widget.RadioButton;

import static icyllis.modernui.view.View.TEXT_ALIGNMENT_CENTER;

public class BlockView {
    public String id,tooltip;
    public int num_id;

    public String image;

    public BlockView(String id, String tooltip, int num_id, String image) {
        this.id = id;
        this.tooltip = tooltip;
        this.num_id = num_id;
        this.image = image;
    }

    public RadioButton getButton(Context context, ColorStateList color) {
        RadioButton button = new RadioButton(context);
        int dp50 = button.dp(50);
        int dp4 = button.dp(4);
        button.setId(num_id);
        if (image != null) {
            button.setTypeface(Fonts.ICON);
            button.setText(image);
            button.setTextSize(25);
        } else {
            button.setText(id);
            button.setTextSize(8);
        }
        button.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        button.setTextColor(color);
        ThemeControl.addBackground(button);
        if (tooltip != null) {
            button.setTooltipText(tooltip);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp50,dp50);
        params.setMargins(dp4,dp4,dp4,dp4);
        button.setLayoutParams(params);
        return button;
    }
}
