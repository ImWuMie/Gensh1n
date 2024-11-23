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

package dev.undefinedteam.gensh1n.mixins.modernui.ui;

import dev.undefinedteam.modernui.mc.IModernTextFieldWidget;
import dev.undefinedteam.modernui.mc.MuiModApi;
import dev.undefinedteam.modernui.mc.FontResourceManager;
import dev.undefinedteam.modernui.mc.ModernUIClient;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Matcher;

/**
 * Transform emoji shortcodes.
 */
@Mixin(ChatScreen.class)
public class MixinChatScreen {

    @Shadow
    protected TextFieldWidget chatField;

    @Unique
    private boolean modernUI_MC$broadcasting;

    @Inject(method = "onChatFieldUpdate", at = @At("HEAD"))
    private void _onEdited(String s, CallbackInfo ci) {
        if (!modernUI_MC$broadcasting &&
                ModernUIClient.sEmojiShortcodes &&
                !chatField.getText().startsWith("/") &&
                (!(chatField instanceof IModernTextFieldWidget) ||
                        !((IModernTextFieldWidget) chatField).modernUI_MC$getUndoManager().isInUndo())) {
            final FontResourceManager manager = FontResourceManager.getInstance();
            CYCLE:
            for (;;) {
                final Matcher matcher = MuiModApi.EMOJI_SHORTCODE_PATTERN.matcher(chatField.getText());
                while (matcher.find()) {
                    int start = matcher.start();
                    int end = matcher.end();
                    if (end - start > 2) {
                        String replacement = manager.lookupEmojiShortcode(
                            chatField.getText().substring(start + 1, end - 1)
                        );
                        if (replacement != null) {
                            modernUI_MC$broadcasting = true;
                            chatField.setSelectionEnd(start);
                            chatField.setSelectionStart(end);
                            chatField.write(replacement);
                            modernUI_MC$broadcasting = false;
                            continue CYCLE;
                        }
                    }
                }
                break;
            }
        }
    }
}
