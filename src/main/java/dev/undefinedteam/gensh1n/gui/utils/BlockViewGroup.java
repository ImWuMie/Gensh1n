package dev.undefinedteam.gensh1n.gui.utils;

import dev.undefinedteam.gensh1n.gui.frags.MainInfoSaver;
import icyllis.modernui.core.Context;
import icyllis.modernui.util.ColorStateList;
import icyllis.modernui.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BlockViewGroup {
    public int id;

    public BlockViewGroup(int id) {
        this.id = id;
    }

    private final List<BlockView> views = new ArrayList<>();
    private Consumer<BlockView> changedAction;

    public BlockViewGroup onChanged(Consumer<BlockView> func) {
        this.changedAction = func;
        return this;
    }

    public BlockViewGroup clear() {
        this.views.clear();
        return this;
    }

    public BlockViewGroup add(BlockView view) {
        this.views.add(view);
        return this;
    }

    public RadioGroup getLayout(Context context, ColorStateList colors, MainInfoSaver saver) {
        RadioGroup group = new RadioGroup(context);
        group.setId(this.id);
        group.setVerticalScrollBarEnabled(true);
        for (int i = 0; i < views.size(); i++) {
            var button = views.get(i).getButton(context, colors);
            if (saver.tab_last_checked == button.getId()) {
                button.setChecked(true);
            }

            group.addView(button, i);
        }
        return group;
    }
}
