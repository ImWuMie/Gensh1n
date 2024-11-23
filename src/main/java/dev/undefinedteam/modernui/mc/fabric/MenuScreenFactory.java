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

import dev.undefinedteam.modernui.mc.ContainerMenuView;
import dev.undefinedteam.modernui.mc.ScreenCallback;
import dev.undefinedteam.modernui.mc.UIManager;
import icyllis.modernui.fragment.Fragment;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * The factory interface is used to create menu screens with a main {@link Fragment}.
 * The factory is invoked when the server requires the client to open an application
 * screen to interact with a container menu. The menu instance is created on the
 * client with {@link ExtendedScreenHandlerType.ExtendedFactory#create(int, PlayerInventory, net.minecraft.network.PacketByteBuf)},
 * which contains custom network data from server. For example:
 * <pre>{@code
 * @Override
 * public void onInitializeClient() {
 *     MenuScreens.register(MyRegistry.MY_MENU, MenuScreenFactory.create(menu -> new MyFragment()));
 * }
 * }</pre>
 *
 * @see ExtendedScreenHandlerFactory
 */
@FunctionalInterface
public interface MenuScreenFactory<T extends ScreenHandler> extends
    HandledScreens.Provider<T, HandledScreen<T>> {

    /**
     * Helper method that down-casts the screen factory.
     *
     * @param factory the factory
     * @param <T>     the menu type
     * @return the factory
     */
    static <T extends ScreenHandler> MenuScreenFactory<T> create(MenuScreenFactory<T> factory) {
        return factory;
    }

    @Nonnull
    @Override
    default HandledScreen<T> create(@Nonnull T menu,
                                              @Nonnull PlayerInventory inventory,
                                              @Nonnull Text title) {
        return new MenuScreen<>(UIManager.getInstance(),
                Objects.requireNonNullElseGet(createFragment(menu), Fragment::new),
                createCallback(menu),
                menu,
                inventory,
                title);
    }

    /**
     * Creates a new {@link Fragment} for the given menu. This method is called on the main thread.
     * <p>
     * Specially, the main {@link Fragment} subclass can implement {@link ScreenCallback}
     * or return it by {@link #createCallback(ScreenHandler)} to describe the screen
     * properties.
     * <p>
     * Note: You should not interact player inventory or block container via the Fragment.
     * Instead, use {@link T ContainerMenu} and {@link ContainerMenuView}.
     *
     * @param menu the container menu
     * @return the main fragment
     */
    @Nonnull
    Fragment createFragment(T menu);

    @Nullable
    default ScreenCallback createCallback(T menu) {
        return null;
    }
}
