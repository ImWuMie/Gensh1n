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

import com.ibm.icu.text.BreakIterator;
import dev.undefinedteam.modernui.mc.EditBoxEditAction;
import dev.undefinedteam.modernui.mc.IModernTextFieldWidget;
import dev.undefinedteam.modernui.mc.MuiModApi;
import icyllis.modernui.core.UndoManager;
import icyllis.modernui.core.UndoOwner;

import icyllis.modernui.text.method.WordIterator;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.*;

/**
 * Changes:
 * <ul>
 * <li>Add Unicode cursor movement (by grapheme and by word).</li>
 * <li>Add undo/redo manager.</li>
 * <li>Reset cursor blink timer when cursor changed.</li>
 * <li>Adjust cursor blink cycle to from 0.6 seconds to 1 second.</li>
 * </ul>
 * <p>
 * This cannot be fully internationalized because of Minecraft bad implementation.
 */
@Mixin(TextFieldWidget.class)
public abstract class MixinTextFieldWidget implements IModernTextFieldWidget {

    @Shadow
    private int selectionStart;

    @Shadow
    private String text;

    @Shadow
    private long lastSwitchFocusTime;

    @Unique
    private WordIterator modernUI_MC$wordIterator;

    @Unique
    private long modernUI_MC$lastInsertTextNanos;

    @Unique
    private UndoManager modernUI_MC$undoManager = new UndoManager();

    @Inject(method = "<init>(Lnet/minecraft/client/font/TextRenderer;IIIILnet/minecraft/client/gui/widget/TextFieldWidget;Lnet/minecraft/text/Text;)V", at = @At("TAIL"))
    private void init(TextRenderer textRenderer, int x, int y, int width, int height, TextFieldWidget copyFrom, Text text, CallbackInfo ci) {
        modernUI_MC$undoManager = new UndoManager();
    }

    /**
     * Reset blink.
     */
    @Inject(method = "setSelectionStart", at = @At("RETURN"))
    public void onSetCursorPosition(int pos, CallbackInfo ci) {
        lastSwitchFocusTime = Util.getMeasuringTimeMs();
    }

    @Inject(method = "getCursorPosWithOffset", at = @At("HEAD"), cancellable = true)
    public void onGetCursorPosition(int dir,
                                    CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(MuiModApi.offsetByGrapheme(text, selectionStart, dir));
    }

    @Inject(method = "getWordSkipPosition(IIZ)I", at = @At("HEAD"), cancellable = true)
    public void onGetWordPosition(int dir, int cursor, boolean withEndSpace,
                                  CallbackInfoReturnable<Integer> cir) {
        // assume command starts with slash
        if ((dir == -1 || dir == 1) && !text.startsWith("/")) {
            WordIterator wordIterator = modernUI_MC$wordIterator;
            if (wordIterator == null) {
                modernUI_MC$wordIterator = wordIterator = new WordIterator();
            }
            wordIterator.setCharSequence(text, cursor, cursor);
            int offset;
            if (dir == -1) {
                offset = wordIterator.preceding(cursor);
            } else {
                offset = wordIterator.following(cursor);
            }
            if (offset != BreakIterator.DONE) {
                cir.setReturnValue(offset);
            } else {
                cir.setReturnValue(cursor);
            }
        }
    }

    @Inject(
            method = "setText",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;text:Ljava/lang/String;",
                    opcode = Opcodes.PUTFIELD))
    public void onSetValue(String string, CallbackInfo ci) {
        if (modernUI_MC$undoManager.isInUndo()) {
            return;
        }
        if (text.isEmpty() && string.isEmpty()) {
            return;
        }
        // we see this operation as Replace
        EditBoxEditAction edit = new EditBoxEditAction(
                modernUI_MC$undoOwner(),
                selectionStart,
                /*oldText*/ text,
                0,
                /*newText*/ string
        );
        modernUI_MC$addEdit(edit, false);
    }

    @Inject(
            method = "write",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;text:Ljava/lang/String;",
                    opcode = Opcodes.PUTFIELD),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    public void onInsertText(String string, CallbackInfo ci,
                             int i, int j, int k, String string2, int l, String string3) {
        if (modernUI_MC$undoManager.isInUndo()) {
            return;
        }
        String oldText = text.substring(i, j);
        if (oldText.isEmpty() && string2.isEmpty()) {
            return;
        }
        EditBoxEditAction edit = new EditBoxEditAction(
                modernUI_MC$undoOwner(),
                selectionStart,
                oldText,
                i,
                /*newText*/ string2
        );
        final long nanos = Util.getMeasuringTimeNano();
        final boolean mergeInsert;
        // Minecraft split IME batch commit and even a single code point into code units,
        // if two charTyped() occur at the same time (<= 3ms), try to merge (concat) them.
        if (modernUI_MC$lastInsertTextNanos >= nanos - 3_000_000) {
            mergeInsert = true;
        } else {
            modernUI_MC$lastInsertTextNanos = nanos;
            mergeInsert = false;
        }
        modernUI_MC$addEdit(edit, mergeInsert);
    }

    @Inject(
            method = "eraseCharactersTo",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;text:Ljava/lang/String;",
                    opcode = Opcodes.PUTFIELD),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    public void onDeleteChars(int i, CallbackInfo ci,
                              int j, int k, String string) {
        if (modernUI_MC$undoManager.isInUndo()) {
            return;
        }
        String oldText = text.substring(j, k);
        if (oldText.isEmpty()) {
            return;
        }
        EditBoxEditAction edit = new EditBoxEditAction(
                modernUI_MC$undoOwner(),
                /*cursorPos*/ selectionStart,
                oldText,
                j,
                ""
        );
        modernUI_MC$addEdit(edit, false);
    }

    @Unique
    public void modernUI_MC$addEdit(EditBoxEditAction edit, boolean mergeInsert) {
        final UndoManager mgr = modernUI_MC$undoManager;
        mgr.beginUpdate("addEdit");
        EditBoxEditAction lastEdit = mgr.getLastOperation(
                EditBoxEditAction.class,
                edit.getOwner(),
                UndoManager.MERGE_MODE_UNIQUE
        );
        if (lastEdit == null) {
            mgr.addOperation(edit, UndoManager.MERGE_MODE_NONE);
        } else if (!mergeInsert || !lastEdit.mergeInsertWith(edit)) {
            mgr.commitState(edit.getOwner());
            mgr.addOperation(edit, UndoManager.MERGE_MODE_NONE);
        }
        mgr.endUpdate();
    }

    @Inject(method = "keyPressed", at = @At("TAIL"), cancellable = true)
    public void onKeyPressed(int i, int j, int k, CallbackInfoReturnable<Boolean> cir) {
        if (i == GLFW.GLFW_KEY_Z || i == GLFW.GLFW_KEY_Y) {
            if (Screen.hasControlDown() && !Screen.hasAltDown()) {
                if (!Screen.hasShiftDown()) {
                    UndoOwner[] owners = {modernUI_MC$undoOwner()};
                    if (i == GLFW.GLFW_KEY_Z) {
                        // CTRL+Z
                        if (modernUI_MC$undoManager.countUndos(owners) > 0) {
                            modernUI_MC$undoManager.undo(owners, 1);
                            cir.setReturnValue(true);
                        }
                    } else if (modernUI_MC$tryRedo(owners)) {
                        // CTRL+Y
                        cir.setReturnValue(true);
                    }
                } else if (i == GLFW.GLFW_KEY_Z) {
                    UndoOwner[] owners = {modernUI_MC$undoOwner()};
                    if (modernUI_MC$tryRedo(owners)) {
                        // CTRL+SHIFT+Z
                        cir.setReturnValue(true);
                    }
                }
            }
        }
    }

    @Unique
    private UndoOwner modernUI_MC$undoOwner() {
        return modernUI_MC$undoManager.getOwner("EditBox", this);
    }

    @Unique
    private boolean modernUI_MC$tryRedo(UndoOwner[] owners) {
        if (modernUI_MC$undoManager.countRedos(owners) > 0) {
            modernUI_MC$undoManager.redo(owners, 1);
            return true;
        }
        return false;
    }

    @Override
    public UndoManager modernUI_MC$getUndoManager() {
        return modernUI_MC$undoManager;
    }
}
