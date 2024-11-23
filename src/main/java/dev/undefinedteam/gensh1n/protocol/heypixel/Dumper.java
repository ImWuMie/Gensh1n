package dev.undefinedteam.gensh1n.protocol.heypixel;

import dev.undefinedteam.gclient.packets.PacketBuf;
import dev.undefinedteam.gensh1n.protocol.heypixel.msgpack.core.MessagePack;
import dev.undefinedteam.gensh1n.protocol.heypixel.msgpack.value.impl.ImmutableLongValueImpl;
import io.netty.buffer.Unpooled;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.IOException;

public class Dumper {
    public static void main(String[] args) throws DecoderException {
        var hex = "0664d92463313666356138322d363432662d346366642d393965352d643138303730616662333538d92436323635613639332d653234652d343263382d613035652d343131393065333231303632010000ce00011d58ce00011d58cf000001933463b52380";

        var bytes = new byte[] {
            -39, 36, 100, 102, 57, 101, 99, 55, 98, 57, 45, 102, 48, 99, 98, 45, 52, 52, 97, 54, 45, 98, 98, 54, 50, 45, 99, 99, 98, 54, 54, 101, 49, 50, 101, 57, 98, 49, -39, 36, 54, 52, 48, 55, 97, 50, 100, 56, 45, 52, 53, 99, 98, 45, 52, 102, 101, 50, 45, 97, 48, 48, 54, 45, 49, 101, 50, 99, 100, 49, 52, 53, 53, 97, 99, 102, 3, 4, 0, 118, 118, -49, 0, 0, 1, -109, 52, -88, 101, 80, -108, -66, 67, 58, 92, 87, 73, 78, 68, 79, 87, 83, 92, 115, 121, 115, 116, 101, 109, 51, 50, 92, 117, 114, 108, 109, 111, 110, 46, 100, 108, 108, -39, 32, 67, 58, 92, 87, 73, 78, 68, 79, 87, 83, 92, 83, 89, 83, 84, 69, 77, 51, 50, 92, 110, 101, 116, 117, 116, 105, 108, 115, 46, 100, 108, 108, -39, 32, 67, 58, 92, 87, 73, 78, 68, 79, 87, 83, 92, 83, 89, 83, 84, 69, 77, 51, 50, 92, 105, 101, 114, 116, 117, 116, 105, 108, 46, 100, 108, 108, -66, 67, 58, 92, 87, 73, 78, 68, 79, 87, 83, 92, 83, 89, 83, 84, 69, 77, 51, 50, 92, 115, 114, 118, 99, 108, 105, 46, 100, 108, 108
        };

        try(var unpacker = MessagePack.newDefaultUnpacker(bytes)) {

            // client id
            var clientId = unpacker.unpackString();
            // uuid
            var uuid = unpacker.unpackString();
            // --> main
            var id = unpacker.unpackValue().asIntegerValue().asLong();
            var size = unpacker.unpackValue().asIntegerValue().asLong();
            var size1 = unpacker.unpackValue().asIntegerValue().asLong();
            var size2 = unpacker.unpackValue().asIntegerValue().asLong();
            var size3 =unpacker.unpackValue().asIntegerValue().asLong();
            var time = unpacker.unpackValue().asIntegerValue().asLong();
            var list = unpacker.unpackValue().toJson();

            System.out.println("cid: " + clientId);
            System.out.println("uuid: " + uuid);
            System.out.println("id: " + id);
            System.out.println("size: " + size);
            System.out.println("size1: " + size1);
            System.out.println("size2: " + size2);
            System.out.println("size3: " + size3);
            System.out.println("time: " + time);
            System.out.println("list: " + list);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
