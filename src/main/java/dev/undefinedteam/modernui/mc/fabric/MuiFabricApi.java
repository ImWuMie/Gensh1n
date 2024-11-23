/*
 * Modern UI.
 * Copyright (C) 2019-2023 BloCamLimb. All rights reserved.
 *
 * Modern UI is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Modern UI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Modern UI. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.undefinedteam.modernui.mc.fabric;

import dev.undefinedteam.modernui.mc.MuiModApi;
import dev.undefinedteam.modernui.mc.MuiScreen;
import icyllis.modernui.ModernUI;
import icyllis.modernui.fragment.Fragment;
import dev.undefinedteam.modernui.mc.ScreenCallback;
import dev.undefinedteam.modernui.mc.UIManager;
import net.fabricmc.fabric.impl.client.rendering.FabricShaderProgram;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

public final class MuiFabricApi extends MuiModApi {
    public static final MuiFabricApi INSTANCE = new MuiFabricApi();


    public MuiFabricApi() {
        ModernUI.LOGGER.info(ModernUI.MARKER, "Created MuiFabricApi");
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public <T extends Screen & MuiScreen> T createScreen(@Nonnull Fragment fragment,
                                                         @Nullable ScreenCallback callback,
                                                         @Nullable Screen previousScreen,
                                                         @Nullable CharSequence title) {
        return (T) new SimpleScreen(UIManager.getInstance(),
                fragment, callback, previousScreen, title);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public <T extends ScreenHandler, U extends Screen & ScreenHandlerProvider<T> & MuiScreen> U createMenuScreen(@NotNull Fragment fragment, @org.jetbrains.annotations.Nullable ScreenCallback callback, @NotNull T menu, @NotNull PlayerInventory inventory, @NotNull Text title) {
        return (U) new MenuScreen<>(UIManager.getInstance(),
            fragment, callback, menu, inventory, title);
    }

    @Override
    public boolean isGLVersionPromoted() {
        // we are unknown about this
        return false;
    }

    @Override
    public void loadEffect(GameRenderer gr, Identifier effect) {
        gr.loadPostProcessor(effect);
    }

    @Override
    public ShaderProgram makeShaderInstance(ResourceFactory resourceProvider, Identifier resourceLocation, VertexFormat vertexFormat) throws IOException {
        return new FabricShaderProgram(resourceProvider, resourceLocation, vertexFormat);
    }

    @Override
    public boolean isKeyBindingMatches(KeyBinding keyMapping, InputUtil.Key key) {
        return key.getCategory() == InputUtil.Type.KEYSYM
            ? keyMapping.matchesKey(key.getCode(), InputUtil.UNKNOWN_KEY.getCode())
            : keyMapping.matchesKey(InputUtil.UNKNOWN_KEY.getCode(), key.getCode());
    }

    @Override
    public Style applyRarityTo(Rarity rarity, Style baseStyle) {
        return baseStyle.withColor(rarity.formatting);
    }
}
