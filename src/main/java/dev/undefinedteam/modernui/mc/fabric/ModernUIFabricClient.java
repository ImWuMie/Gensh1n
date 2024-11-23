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
import dev.undefinedteam.gensh1n.events.render.RenderTickEvent;
import dev.undefinedteam.modernui.mc.*;
import icyllis.modernui.core.Core;
import icyllis.modernui.core.Handler;
import icyllis.modernui.graphics.ImageStore;

import dev.undefinedteam.modernui.mc.text.TextLayoutEngine;
import meteordevelopment.orbit.EventHandler;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.resource.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Locale;

@Environment(EnvType.CLIENT)
public class ModernUIFabricClient {

    private String mSelectedLanguageCode;
    private Locale mSelectedJavaLocale;

    private final Logger LOGGER;
    private final Marker MARKER;

    public ModernUIFabricClient(Logger LOG, Marker MARKER) {
        super();
        this.LOGGER = LOG;
        this.MARKER = MARKER;
    }

    public void onInitializeClient() {
        Client.EVENT_BUS.subscribe(this);

        KeyBindingHelper.registerKeyBinding(UIManagerFabric.OPEN_CENTER_KEY);

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return ModernUIMod.location("client");
            }

            @Override
            public void reload(ResourceManager manager) {
                ImageStore.getInstance().clear();
                Handler handler = Core.getUiHandlerAsync();
                // FML may throw ex, so it can be null
                if (handler != null) {
                    // Call in lambda, not in creating the lambda
                    handler.post(() -> UIManager.getInstance().updateLayoutDir(Config.CLIENT.mForceRtl.get()));
                }
            }
        });

        CoreShaderRegistrationCallback.EVENT.register(context -> {
            try {
                context.register(
                    ModernUIMod.location("rendertype_modern_tooltip"),
                    VertexFormats.POSITION,
                    TooltipRenderType::setShaderTooltip);
            } catch (IOException e) {
                LOGGER.error(MARKER, "Bad tooltip shader", e);
            }
        });

        ClientLifecycleEvents.CLIENT_STARTED.register((mc) -> {
            UIManagerFabric.initializeRenderer();
        });

        FontResourceManager.getInstance(); // Create INSTANCE

        ClientLifecycleEvents.CLIENT_STARTED.register((mc) -> {
            MuiModApi.addOnWindowResizeListener(TextLayoutEngine.getInstance());
        });

        MuiModApi.addOnDebugDumpListener(TextLayoutEngine.getInstance());

        ClientTickEvents.END_CLIENT_TICK.register((mc) -> TextLayoutEngine.getInstance().onEndClientTick());

        LOGGER.info(MARKER, "Initialized text engine");
        LOGGER.info(MARKER, "Initialized UI client");
    }

    @SuppressWarnings("ConstantValue")
    @Nonnull
    public Locale onGetSelectedLocale() {
        // Minecraft can be null if we're running DataGen
        // LanguageManager can be null if this method is being called too early
        MinecraftClient minecraft;
        LanguageManager languageManager;
        if ((minecraft = MinecraftClient.getInstance()) != null &&
            (languageManager = minecraft.getLanguageManager()) != null) {
            String languageCode = languageManager.getLanguage();
            if (!languageCode.equals(mSelectedLanguageCode)) {
                mSelectedLanguageCode = languageCode;
                String[] langSplit = languageCode.split("_", 2);
                mSelectedJavaLocale = langSplit.length == 1
                    ? new Locale(langSplit[0])
                    : new Locale(langSplit[0], langSplit[1]);
            }
            return mSelectedJavaLocale;
        }
        return Locale.getDefault();
    }

    @EventHandler
    private void onRenderTickStart(RenderTickEvent.Start e) {
        renderTick();
    }

    @EventHandler
    private void onRenderTickEnd(RenderTickEvent.End e) {
        renderTick();
    }

    private void renderTick() {
        Core.flushMainCalls();
        Core.flushRenderCalls();
        StillAlive.tick();
    }

}
