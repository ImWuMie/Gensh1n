package dev.undefinedteam.gensh1n.plugins;

import dev.undefinedteam.modernui.mc.ModernUIMod;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import tech.skidonion.obfuscator.annotations.Renamer;

import java.util.List;
import java.util.Set;
@Renamer(obfuscated = false)
public class MixinPlugin implements IMixinConfigPlugin {
    private final boolean mDisableSmoothScrolling = Boolean.parseBoolean(
        ModernUIMod.getBootstrapProperty(ModernUIMod.BOOTSTRAP_DISABLE_SMOOTH_SCROLLING)
    );
    private final boolean mDisableEnhancedTextField = Boolean.parseBoolean(
        ModernUIMod.getBootstrapProperty(ModernUIMod.BOOTSTRAP_DISABLE_ENHANCED_TEXT_FIELD)
    );

    public MixinPlugin() {
    }

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mDisableSmoothScrolling) {
            return !mixinClassName.equals("icyllis.modernui.mc.mixin.MixinScrollPanel") &&
                !mixinClassName.equals("dev.undefinedteam.gensh1n.mixins.mixin.mc.MixinEntryListWidget");
        }
        if (mDisableEnhancedTextField) {
            return !mixinClassName.equals("dev.undefinedteam.gensh1n.mixins.mixin.mc.MixinTextFieldWidget") &&
                !mixinClassName.equals("dev.undefinedteam.gensh1n.mixins.mixin.mc.MixinTextHandler") &&
                !mixinClassName.equals("icyllis.modernui.mc.mixin.MixinTextFieldHelper");
        }

        return !mixinClassName.endsWith("DBG");
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
//        ClassSub.get().add(targetClass);
    }
}
