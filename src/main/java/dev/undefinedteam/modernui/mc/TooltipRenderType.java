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

import com.google.common.collect.ImmutableList;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import org.jetbrains.annotations.ApiStatus;

/**
 * Modern tooltip.
 */
@ApiStatus.Internal
public class TooltipRenderType extends RenderLayer {

    private static net.minecraft.client.gl.ShaderProgram sShaderTooltip;

    static final RenderPhase.ShaderProgram RENDERTYPE_MODERN_TOOLTIP = new RenderPhase.ShaderProgram(TooltipRenderType::getShaderTooltip);

    private static final ImmutableList<RenderPhase> STATES = ImmutableList.of(
        RENDERTYPE_MODERN_TOOLTIP,
        NO_TEXTURE,
        TRANSLUCENT_TRANSPARENCY,
        LEQUAL_DEPTH_TEST,
        ENABLE_CULLING,
        ENABLE_LIGHTMAP,
        DISABLE_OVERLAY_COLOR,
        NO_LAYERING,
        MAIN_TARGET,
        DEFAULT_TEXTURING,
        ALL_MASK,
        FULL_LINE_WIDTH
    );

    static final RenderLayer
            TOOLTIP = new TooltipRenderType("modern_tooltip", 1536,
            () -> STATES.forEach(RenderPhase::startDrawing),
            () -> STATES.forEach(RenderPhase::endDrawing));

    private TooltipRenderType(String name, int bufferSize, Runnable setupState, Runnable clearState) {
        super(name, VertexFormats.POSITION, VertexFormat.DrawMode.QUADS,
                bufferSize, false, false, setupState, clearState);
    }

    public static RenderLayer tooltip() {
        return TOOLTIP;
    }

    public static net.minecraft.client.gl.ShaderProgram getShaderTooltip() {
        return sShaderTooltip;
    }

    public static void setShaderTooltip(net.minecraft.client.gl.ShaderProgram shaderTooltip) {
        sShaderTooltip = shaderTooltip;
    }
}
