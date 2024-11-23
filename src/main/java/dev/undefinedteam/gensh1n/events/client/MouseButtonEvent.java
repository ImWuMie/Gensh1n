package dev.undefinedteam.gensh1n.events.client;

import dev.undefinedteam.gensh1n.events.Cancellable;
import dev.undefinedteam.gensh1n.utils.input.KeyAction;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MouseButtonEvent extends Cancellable {

    public int button,mods;
    public KeyAction action;
}
