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

package dev.undefinedteam.modernui.mc;

import net.minecraft.util.Identifier;
import tech.skidonion.obfuscator.annotations.Renamer;

import javax.annotation.Nonnull;

/**
 * Mod class, common only.
 */
@Renamer(obfuscated = false)
public abstract class ModernUIMod extends ModernUIClient {

    public static final String BOOTSTRAP_DISABLE_SMOOTH_SCROLLING = "modernui_mc_disableSmoothScrolling";
    public static final String BOOTSTRAP_DISABLE_ENHANCED_TEXT_FIELD = "modernui_mc_disableEnhancedTextField";

    public static volatile boolean sDevelopment;
    public static volatile boolean sDeveloperMode;

    protected static boolean sOptiFineLoaded;
    protected static boolean sIrisApiLoaded;
    protected static volatile boolean sUntranslatedItemsLoaded;


    static {
        try {
            Class<?> clazz = Class.forName("optifine.Installer");
            sOptiFineLoaded = true;
            try {
                String version = (String) clazz.getMethod("getOptiFineVersion").invoke(null);
                LOGGER.info(MARKER, "OptiFine installed: {}", version);
            } catch (Exception e) {
                LOGGER.info(MARKER, "OptiFine installed...");
            }
        } catch (Exception ignored) {
        }
        try {
            Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            sIrisApiLoaded = true;
            LOGGER.info(MARKER, "Iris API installed...");
        } catch (Exception ignored) {
        }
    }

    @Nonnull
    public static Identifier location(String path) {
        return Identifier.of(ID, path);
    }

    public static boolean isOptiFineLoaded() {
        return sOptiFineLoaded;
    }

    public static boolean isIrisApiLoaded() {
        return sIrisApiLoaded;
    }

    public static boolean isUntranslatedItemsLoaded() {
        return sUntranslatedItemsLoaded;
    }

    public static boolean isDeveloperMode() {
        return sDeveloperMode || sDevelopment;
    }

    // check if it is on client side before calling
    public static String getBootstrapProperty(String key) {
        return ModernUIClient.getBootstrapProperty(key);
    }
}
