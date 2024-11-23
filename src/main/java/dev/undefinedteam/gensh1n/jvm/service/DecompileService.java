package dev.undefinedteam.gensh1n.jvm.service;

import dev.undefinedteam.gensh1n.jvm.service.decompiler.FernFlowerDecompiler;
import dev.undefinedteam.gensh1n.jvm.service.decompiler.IDecompiler;

public class DecompileService {
    public static IDecompiler getService() {
        return new FernFlowerDecompiler();
    }
}
