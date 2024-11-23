package dev.undefinedteam.gensh1n.events.client;

import dev.undefinedteam.gensh1n.events.Cancellable;
import dev.undefinedteam.gensh1n.utils.input.KeyAction;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class KeyEvent extends Cancellable {

    public int key, modifiers;
    public KeyAction action;
}
