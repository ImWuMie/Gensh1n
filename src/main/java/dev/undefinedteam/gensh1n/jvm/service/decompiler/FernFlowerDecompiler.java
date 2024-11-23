package dev.undefinedteam.gensh1n.jvm.service.decompiler;

import dev.undefinedteam.gensh1n.Client;
import org.jetbrains.java.decompiler.main.Fernflower;
import org.jetbrains.java.decompiler.main.extern.IContextSource;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.jar.Manifest;

public class FernFlowerDecompiler implements IDecompiler {

    private byte[] bytes;
    private ResultSaver saver;
    private ContextSource context;

    @Override
    public String decompile(ClassNode classNode) {
        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        bytes = classWriter.toByteArray();

        saver = new ResultSaver();
        context = new ContextSource(classNode,bytes);

        Fernflower fernflower = new Fernflower(saver, new HashMap<>() {{
//            Config.get().fernFlower.group.settings.forEach(s -> {
//                Object v = s.get();
//                if (v.equals("true")) {
//                    v = "1";
//                } else if (v.equals("false")) {
//                    v = "0";
//                }
//                this.put(s.name, v);
//            });
        }}, new IFernflowerLogger() {
            @Override
            public void writeMessage(String message, Severity severity) {

            }

            @Override
            public void writeMessage(String message, Severity severity, Throwable t) {
                t.printStackTrace();
            }
        });

        fernflower.addSource(context);
        fernflower.decompileContext();
        return saver.source;
    }


    class ResultSaver implements IResultSaver {
        public String source;


        @Override
        public void saveFolder(String path) {

        }

        @Override
        public void copyFile(String source, String path, String entryName) {

        }

        @Override
        public void saveClassFile(String path, String qualifiedName, String entryName, String content, int[] mapping) {
            this.source = content;
        }

        @Override
        public void createArchive(String path, String archiveName, Manifest manifest) {

        }

        @Override
        public void saveDirEntry(String path, String archiveName, String entryName) {

        }

        @Override
        public void copyEntry(String source, String path, String archiveName, String entry) {

        }

        @Override
        public void saveClassEntry(String path, String archiveName, String qualifiedName, String entryName, String content) {

        }

        @Override
        public void closeArchive(String path, String archiveName) {

        }
    }

    class ContextSource implements IContextSource {
        public final ClassNode node;
        public final byte[] bytes;

        public ContextSource(ClassNode node,byte[] bytes) {
            this.node = node;
            this.bytes = bytes;
        }

        @Override
        public String getName() {
            return Client.ASSETS_LOCATION;
        }

        @Override
        public Entries getEntries() {
            return new Entries(List.of(new Entry(node.name,Entry.BASE_VERSION)), Collections.emptyList(),Collections.emptyList());
        }

        @Override
        public InputStream getInputStream(String resource) throws IOException {
            return new ByteArrayInputStream(bytes);
        }

        @Override
        public IOutputSink createOutputSink(IResultSaver saver) {
            return new IOutputSink() {
                @Override
                public void begin() {
                    // no-op
                }

                @Override
                public void acceptClass(String qualifiedName, String fileName, String content, int[] mapping) {
                    FernFlowerDecompiler.this.saver.source = content;
                }

                @Override
                public void acceptDirectory(String directory) {
                    // no-op
                }

                @Override
                public void acceptOther(String path) {
                    // no-op
                }

                @Override
                public void close() {
                    // no-op
                }
            };
        }
    }
}
