package dev.undefinedteam.gensh1n.events.client;


import dev.undefinedteam.gensh1n.system.modules.Module;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ModuleBindChangedEvent {
    public Module module;
}
