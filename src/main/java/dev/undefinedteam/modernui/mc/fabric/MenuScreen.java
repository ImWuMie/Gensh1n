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

import dev.undefinedteam.modernui.mc.MuiScreen;
import icyllis.modernui.fragment.Fragment;
import dev.undefinedteam.modernui.mc.ScreenCallback;
import dev.undefinedteam.modernui.mc.UIManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * ContainerScreen holds a container menu for item stack interaction and
 * network communication. As a feature of Minecraft, GUI initiated by the
 * server will always be this class. It behaves like JEI checking if
 * instanceof {@link ScreenHandler}. Therefore, this class serves
 * as a marker, the complexity of business logic is not reflected in this
 * class, we don't need anything in the super class.
 *
 * @param <T> the type of container menu
 * @see SimpleScreen
 */
final class MenuScreen<T extends ScreenHandler>
        extends HandledScreen<T>
        implements MuiScreen {

    private final UIManager mHost;
    private final Fragment mFragment;
    @Nullable
    private final ScreenCallback mCallback;

    MenuScreen(UIManager host, Fragment fragment, @Nullable ScreenCallback callback,
               T menu, PlayerInventory inventory, Text title) {
        super(menu, inventory, title);
        mHost = host;
        mFragment = Objects.requireNonNull(fragment);
        mCallback = callback != null ? callback :
                fragment instanceof ScreenCallback cbk ? cbk : null;
    }

    /*@Override
    public void init(@Nonnull Minecraft minecraft, int width, int height) {
        this.minecraft = minecraft;
        this.width = width;
        this.height = height;
        init();
        MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.InitGuiEvent.Post(this, buttons, this::widget, this::widget));
    }*/

    @Override
    protected void init() {
        super.init();
        mHost.initScreen(this);
    }

    @Override
    public void resize(@Nonnull MinecraftClient minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        //MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.InitGuiEvent.Post(this, buttons, this::widget,
        // this::widget));

        /*ModernUI.LOGGER.debug("Scaled: {}x{} Framebuffer: {}x{} Window: {}x{}", width, height, minecraft
        .getMainWindow().getFramebufferWidth(),
                minecraft.getMainWindow().getFramebufferHeight(), minecraft.getMainWindow().getWidth(), minecraft
                .getMainWindow().getHeight());*/
    }

    @Override
    public void render(@Nonnull DrawContext gr, int mouseX, int mouseY, float deltaTick) {
        ScreenCallback callback = getCallback();
        if (callback == null || callback.hasDefaultBackground()) {
            renderBackground(gr, mouseX, mouseY, deltaTick);
        }
        mHost.render();
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
    }

    @Override
    public void removed() {
        super.removed();
        mHost.removed();
    }

    @Nonnull
    @Override
    public Screen self() {
        return this;
    }

    @Nonnull
    @Override
    public Fragment getFragment() {
        return mFragment;
    }

    @Nullable
    @Override
    public ScreenCallback getCallback() {
        return mCallback;
    }

    @Nullable
    @Override
    public Screen getPreviousScreen() {
        return null;
    }

    @Override
    public boolean isMenuScreen() {
        return true;
    }

    @Override
    public void onBackPressed() {
        mHost.getOnBackPressedDispatcher().onBackPressed();
    }

    // IMPL - GuiEventListener

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        mHost.onHoverMove(true);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY) {
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        mHost.onScroll(deltaX, deltaY);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        mHost.onKeyPress(keyCode, scanCode, modifiers);
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        mHost.onKeyRelease(keyCode, scanCode, modifiers);
        return false;
    }

    @Override
    public boolean charTyped(char ch, int modifiers) {
        return mHost.onCharTyped(ch);
    }
}
