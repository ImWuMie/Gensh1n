//
// MessagePack for Java
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
package dev.undefinedteam.gensh1n.protocol.heypixel.msgpack.core.buffer;

import java.io.Closeable;
import java.io.IOException;

/**
 * Provides a sequence of MessageBuffer instances.
 *
 * A MessageBufferInput implementation has control of lifecycle of the memory so that it can reuse previously
 * allocated memory, use memory pools, or use memory-mapped files.
 */
public interface MessageBufferInput
        extends Closeable
{
    /**
     * Returns a next buffer to read.
     * <p>
     * This method should return a MessageBuffer instance that has data filled in. When this method is called twice,
     * the previously returned buffer is no longer used. Thus implementation of this method can safely discard it.
     * This is useful when it uses a memory pool.
     *
     * @return the next MessageBuffer, or return null if no more buffer is available.
     * @throws IOException when IO error occurred when reading the data
     */
    MessageBuffer next()
            throws IOException;

    /**
     * Closes the input.
     * <p>
     * When this method is called, the buffer previously returned from {@link #next()} method is no longer used.
     * Thus implementation of this method can safely discard it.
     * <p>
     * If the input is already closed then invoking this method has no effect.
     *
     * @throws IOException when IO error occurred when closing the data source
     */
    @Override
    void close()
            throws IOException;
}
