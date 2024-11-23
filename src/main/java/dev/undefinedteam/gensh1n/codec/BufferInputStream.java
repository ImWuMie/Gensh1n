package dev.undefinedteam.gensh1n.codec;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class BufferInputStream extends InputStream {

    private final ByteBuffer buffer;
    private int mark = -1;

    public BufferInputStream(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public int read() throws IOException {
        if (!buffer.hasRemaining()) {
            return -1;
        }
        return buffer.get() & 0xFF;
    }

    @Override
    public int read(byte[] bytes, int off, int len) throws IOException {
        if (!buffer.hasRemaining()) {
            return -1;
        }
        len = Math.min(len, buffer.remaining());
        buffer.get(bytes, off, len);
        return len;
    }

    @Override
    public synchronized void mark(int readLimit) {
        mark = buffer.position();
    }

    @Override
    public synchronized void reset() throws IOException {
        if (mark == -1) {
            throw new IOException("Mark not set");
        }
        buffer.position(mark);
    }

    @Override
    public boolean markSupported() {
        return true;
    }
}
