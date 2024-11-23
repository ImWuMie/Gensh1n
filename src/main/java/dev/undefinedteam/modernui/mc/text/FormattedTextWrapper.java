/*
 * Modern UI.
 * Copyright (C) 2019-2022 BloCamLimb. All rights reserved.
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

package dev.undefinedteam.modernui.mc.text;

import net.minecraft.text.*;

import javax.annotation.Nonnull;
import java.util.Optional;

public class FormattedTextWrapper implements OrderedText {

    @Nonnull
    public final StringVisitable mText;

    public FormattedTextWrapper(@Nonnull StringVisitable text) {
        mText = text;
    }

    /**
     * Needed only when compositing, do not use explicitly. This should be equivalent to
     *
     * @param sink code point consumer
     * @return true if all chars consumed, false otherwise
     */
    @Override
    public boolean accept(@Nonnull CharacterVisitor sink) {
        // do not reorder, transfer code points in logical order
        return mText.visit((style, text) ->
            TextVisitFactory.visitFormatted(text, style, sink) ? Optional.empty()
                        : StringVisitable.TERMINATE_VISIT, Style.EMPTY).isEmpty();
    }
}
