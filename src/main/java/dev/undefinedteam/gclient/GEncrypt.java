package dev.undefinedteam.gclient;

import dev.undefinedteam.gclient.aes.Aes;
import dev.undefinedteam.gclient.aes.AesMode;
import dev.undefinedteam.gclient.aes.PaddingMode;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


@StringEncryption
@NativeObfuscation
@ControlFlowObfuscation
public class GEncrypt {
    private static final Aes aes = Aes.create();
    @NativeObfuscation.Inline
    public static final String CIPHER_KEY = "AASFasjgAKAIdmaL";
    @NativeObfuscation.Inline
    public static final byte[] CIPHER_IV = new byte[16];

    static {
        aes.mode = AesMode.CBC;
        aes.paddingMode = PaddingMode.PKCS5;

        CIPHER_IV[0] = (byte) 1;
        CIPHER_IV[1] = (byte) 1;
        CIPHER_IV[2] = (byte) 4;
        CIPHER_IV[3] = (byte) 5;
        CIPHER_IV[4] = (byte) 1;
        CIPHER_IV[5] = (byte) 4;
        CIPHER_IV[6] = (byte) 1;
        CIPHER_IV[7] = (byte) 9;
        CIPHER_IV[8] = (byte) 1;
        CIPHER_IV[9] = (byte) 9;
        CIPHER_IV[10] = (byte) 66;
        CIPHER_IV[11] = (byte) ('原' % 128);
        CIPHER_IV[12] = (byte) ('神' % 128);
        CIPHER_IV[13] = (byte) ('启' % 128);
        CIPHER_IV[14] = (byte) ('动' % 128);
        CIPHER_IV[15] = (byte) ('!' % 128);

        aes.Key = CIPHER_KEY.getBytes(StandardCharsets.US_ASCII);
        aes.IV = CIPHER_IV;
    }

    @NativeObfuscation.Inline
    public static ByteBuf encrypt(ByteBuf buf) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, IOException, InvalidKeyException {
        byte[] b = new byte[buf.readableBytes()];
        buf.readBytes(b);
        byte[] result = aes.encrypt(b);
        return Unpooled.wrappedBuffer(result);
    }

    @NativeObfuscation.Inline
    public static ByteBuf decrypt(ByteBuf buf) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, IOException, InvalidKeyException {
        byte[] b = new byte[buf.readableBytes()];
        buf.readBytes(b);
        byte[] result = aes.decrypt(b);
        return Unpooled.wrappedBuffer(result);
    }
}
