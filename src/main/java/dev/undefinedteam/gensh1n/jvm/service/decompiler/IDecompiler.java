package dev.undefinedteam.gensh1n.jvm.service.decompiler;

import org.objectweb.asm.tree.ClassNode;

public interface IDecompiler {
    String decompile(ClassNode classNode);
}
