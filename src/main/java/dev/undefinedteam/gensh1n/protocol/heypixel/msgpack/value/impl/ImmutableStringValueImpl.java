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
package dev.undefinedteam.gensh1n.protocol.heypixel.msgpack.value.impl;

import dev.undefinedteam.gensh1n.protocol.heypixel.msgpack.core.MessagePacker;
import dev.undefinedteam.gensh1n.protocol.heypixel.msgpack.value.ImmutableStringValue;
import dev.undefinedteam.gensh1n.protocol.heypixel.msgpack.value.Value;
import dev.undefinedteam.gensh1n.protocol.heypixel.msgpack.value.ValueType;

import java.io.IOException;
import java.util.Arrays;

/**
 * {@code ImmutableStringValueImpl} Implements {@code ImmutableStringValue} using a {@code byte[]} field.
 * This implementation caches result of {@code toString()} and {@code asString()} using a private {@code String} field.
 *
 * @see dev.undefinedteam.gensh1n.protocol.heypixel.msgpack.value.StringValue
 */
public class ImmutableStringValueImpl
        extends AbstractImmutableRawValue
        implements ImmutableStringValue
{
    public ImmutableStringValueImpl(byte[] data)
    {
        super(data);
    }

    public ImmutableStringValueImpl(String string)
    {
        super(string);
    }

    @Override
    public ValueType getValueType()
    {
        return ValueType.STRING;
    }

    @Override
    public ImmutableStringValue immutableValue()
    {
        return this;
    }

    @Override
    public ImmutableStringValue asStringValue()
    {
        return this;
    }

    @Override
    public void writeTo(MessagePacker pk)
            throws IOException
    {
        pk.packRawStringHeader(data.length);
        pk.writePayload(data);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Value v)) {
            return false;
        }
        if (!v.isStringValue()) {
            return false;
        }

        if (v instanceof ImmutableStringValueImpl bv) {
            return Arrays.equals(data, bv.data);
        }
        else {
            return Arrays.equals(data, v.asStringValue().asByteArray());
        }
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(data);
    }
}
