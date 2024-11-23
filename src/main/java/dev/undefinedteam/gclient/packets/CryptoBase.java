package dev.undefinedteam.gclient.packets;

import dev.undefinedteam.gclient.aes.Aes;
import dev.undefinedteam.gclient.aes.AesMode;
import dev.undefinedteam.gclient.aes.PaddingMode;
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
@ControlFlowObfuscation
public class CryptoBase {
    public static Aes aes = Aes.create();

    static {
        aes.Key = "owo6pO7aD1a8aww0".getBytes(StandardCharsets.US_ASCII);

        aes.mode = AesMode.ECB;
        aes.paddingMode = PaddingMode.PKCS5;
    }

    @NativeObfuscation.Inline
    public static byte[] decrypt(byte[] b) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, IOException, InvalidKeyException {
        return aes.decrypt(b);
    }

    @NativeObfuscation.Inline
    public static byte[] encrypt(byte[] b) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, IOException, InvalidKeyException {
        return aes.encrypt(b);
    }
}
