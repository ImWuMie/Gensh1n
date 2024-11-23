package dev.undefinedteam.gensh1n.system.modules;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TModules {
    private Modules modules;

    public void init() {

    }

    public void add(Module module) {
        modules.add(module);
    }
}
