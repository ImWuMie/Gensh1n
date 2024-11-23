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

import static dev.undefinedteam.gensh1n.protocol.heypixel.msgpack.core.Preconditions.checkNotNull;

/**
 * MessageBufferInput adapter for byte arrays
 */
public class ArrayBufferInput
        implements MessageBufferInput
{
    private MessageBuffer buffer;
    private boolean isEmpty;

    public ArrayBufferInput(MessageBuffer buf)
    {
        this.buffer = buf;
        isEmpty = buf == null;
    }

    public ArrayBufferInput(byte[] arr)
    {
        this(arr, 0, arr.length);
    }

    public ArrayBufferInput(byte[] arr, int offset, int length)
    {
        this(MessageBuffer.wrap(checkNotNull(arr, "input array is null"), offset, length));
    }

    /**
     * Reset buffer. This method returns the old buffer.
     *
     * @param buf new buffer. This can be null to make this input empty.
     * @return the old buffer.
     */
    public MessageBuffer reset(MessageBuffer buf)
    {
        MessageBuffer old = this.buffer;
        this.buffer = buf;
        isEmpty = buf == null;
        return old;
    }

    public void reset(byte[] arr)
    {
        reset(MessageBuffer.wrap(checkNotNull(arr, "input array is null")));
    }

    public void reset(byte[] arr, int offset, int len)
    {
        reset(MessageBuffer.wrap(checkNotNull(arr, "input array is null"), offset, len));
    }

    @Override
    public MessageBuffer next()
    {
        if (isEmpty) {
            return null;
        }
        isEmpty = true;
        return buffer;
    }

    @Override
    public void close()
    {
        buffer = null;
        isEmpty = true;
    }
}
