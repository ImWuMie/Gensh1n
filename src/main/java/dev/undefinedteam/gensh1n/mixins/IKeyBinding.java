package dev.undefinedteam.gensh1n.mixins;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(KeyBinding.class)
public interface IKeyBinding {
    @Accessor("CATEGORY_ORDER_MAP")
    static Map<String, Integer> getCategoryOrderMap() {
        return null;
    }

    @Accessor("boundKey")
    InputUtil.Key getKey();
}
