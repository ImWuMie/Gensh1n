/*
 * Modern UI.
 * Copyright (C) 2019-2024 BloCamLimb. All rights reserved.
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

import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.events.render.RenderTickEvent;
import dev.undefinedteam.gensh1n.gui.frags.ChatFragment;
import dev.undefinedteam.modernui.mc.ModernUIMod;
import dev.undefinedteam.modernui.mc.MuiScreen;
import dev.undefinedteam.modernui.mc.OptiFineIntegration;
import dev.undefinedteam.modernui.mc.UIManager;
import icyllis.modernui.annotation.MainThread;
import icyllis.modernui.annotation.RenderThread;
import icyllis.modernui.core.Core;
import icyllis.modernui.fragment.Fragment;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.sound.SoundEvents;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

import static dev.undefinedteam.gensh1n.Genshin.*;
import static icyllis.modernui.ModernUI.LOGGER;
import static org.lwjgl.glfw.GLFW.*;

@ApiStatus.Internal
public final class UIManagerFabric extends UIManager {

    @SuppressWarnings("NoTranslation")
    public static final KeyBinding OPEN_CENTER_KEY = new KeyBinding("key.modernui.openCenter", InputUtil.Type.KEYSYM, GLFW_KEY_HOME, "Modern UI");

    private UIManagerFabric() {
        super();
        Client.EVENT_BUS.subscribe(this);
    }

    @EventHandler
    private void onRenderTickStart(RenderTickEvent.Start e) {
        super.onRenderTick(false);
    }

    @EventHandler
    private void onRenderTickEnd(RenderTickEvent.End e) {
        super.onRenderTick(true);
    }

    @EventHandler
    private void onPreTick(TickEvent.Pre e) {
        super.onClientTick(false);
    }

    @EventHandler
    private void onPostTick(TickEvent.Post e) {
        super.onClientTick(true);
    }

    @RenderThread
    public static void initialize() {
        Core.checkRenderThread();
        assert sInstance == null;
        sInstance = new UIManagerFabric();
        LOGGER.info(MARKER, "UI manager initialized");
    }

    /**
     * Schedule UI and create views.
     *
     * @param fragment the main fragment
     */
    @MainThread
    public void open(@Nonnull Fragment fragment) {
        if (!minecraft.isOnThread()) {
            throw new IllegalStateException("Not called from main thread");
        }
        minecraft.setScreen(new SimpleScreen(this, fragment, null, null, null));
    }

    @Override
    protected void onScreenChange(@Nullable Screen oldScreen, @Nullable Screen newScreen) {
        if (newScreen != null) {
            if (!mFirstScreenOpened) {
                if (sDingEnabled) {
                    glfwRequestWindowAttention(minecraft.getWindow().getHandle());
                    minecraft.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f));
                }
                if (ModernUIMod.isOptiFineLoaded()) {
                    OptiFineIntegration.setFastRender(false);
                    LOGGER.info(MARKER, "Disabled OptiFine Fast Render");
                }
                mFirstScreenOpened = true;
            }

            if (mScreen != newScreen && newScreen instanceof MuiScreen) {
                //mTicks = 0;
                mElapsedTimeMillis = 0;
            }
            if (mScreen != newScreen && mScreen != null) {
                onHoverMove(false);
            }
            // for non-mui screens
            if (mScreen == null && minecraft.currentScreen == null) {
                //mTicks = 0;
                mElapsedTimeMillis = 0;
            }
        }
        super.onScreenChange(oldScreen, newScreen);
    }

    @Override
    protected void onPreKeyInput(int keyCode, int scanCode, int action, int mods) {
        if (action == GLFW_PRESS) {
            if (minecraft.currentScreen == null || minecraft.currentScreen.shouldCloseOnEsc() || minecraft.currentScreen instanceof TitleScreen) {
                if (Screen.hasControlDown() && Screen.hasAltDown() && Screen.hasAltDown() && OPEN_CENTER_KEY.matchesKey(keyCode, scanCode)) {
                    open(new CenterFragment2());
                    return;
                }
            }
        }
        super.onPreKeyInput(keyCode, scanCode, action, mods);
    }
}
