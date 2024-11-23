package dev.undefinedteam.gensh1n.events.client;

import dev.undefinedteam.gensh1n.events.Cancellable;
import lombok.AllArgsConstructor;
import net.minecraft.client.gui.screen.Screen;

@AllArgsConstructor
public class OpenScreenEvent extends Cancellable {
    public Screen screen;
}
