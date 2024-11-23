package dev.undefinedteam.gensh1n.jvm;

import dev.undefinedteam.gensh1n.jvm.service.DecompileService;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassSub {
    private static ClassSub sInstance;

    public final List<ClassNode> classes = new ArrayList<>();

    private final Map<ClassNode, String> sources = new HashMap<>();

    public static ClassSub get() {
        if (sInstance == null) sInstance = new ClassSub();

        return sInstance;
    }

    public ClassSub() {


    }

    public void add(ClassNode classNode) {
        this.classes.add(classNode);
    }

    public void loadJar(JarFile jar) throws IOException {
        for (Iterator<JarEntry> it = jar.entries().asIterator(); it.hasNext(); ) {
            var entry = it.next();
            if (entry.getRealName().endsWith(".class")) {
                ClassReader reader = new ClassReader(jar.getInputStream(entry));
                ClassNode node = new ClassNode();
                reader.accept(node, ClassReader.SKIP_DEBUG);
                add(node);
            }
        }
    }

    public String decompile(ClassNode node) {
        String src = sources.getOrDefault(node, DecompileService.getService().decompile(node));
        if (!sources.containsKey(node)) {
            sources.put(node, src);
        }
        return src;
    }
}
