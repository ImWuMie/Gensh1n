package dev.undefinedteam.gclient.aes;

import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;



@StringEncryption
@NativeObfuscation
@ControlFlowObfuscation
public class Aes {
    public PaddingMode paddingMode;
    public AesMode mode;
    @NativeObfuscation.Inline
    public final String algorithm = "AES";

    private Cipher cipher;
    public byte[] Key;
    public byte[] IV;

    private Aes() {
        this.paddingMode = PaddingMode.None;
        this.mode = AesMode.ECB;
    }

    public static Aes create() {
        return new Aes();
    }

    public byte[] encrypt(byte[] bytes) throws InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        return this.encrypt(bytes, 0, bytes.length);
    }

    public byte[] encrypt(byte[] bytes, int offset) throws InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        return this.encrypt(bytes, offset, bytes.length);
    }

    public byte[] encrypt(byte[] bytes, int offset, int len) throws InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        initCipher(Cipher.ENCRYPT_MODE);
        byte[] result;
        {
            ByteArrayOutputStream pad = new ByteArrayOutputStream();
            pad.write(bytes,offset,len);
            if (this.paddingMode.equals(PaddingMode.Zero)) {
                try {
                    int zeroSize = 16 - (pad.size() % 16);
                    if (zeroSize != 16) {
                        pad.write(new byte[zeroSize]);
                    }
                } catch (IOException e) {
                    throw new BadPaddingException("Zero padding error" + e);
                }
            }
            result = this.cipher.doFinal(pad.toByteArray());
        }
        return result;
    }

    public byte[] decrypt(byte[] bytes) throws InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        return this.decrypt(bytes, 0, bytes.length);
    }

    public byte[] decrypt(byte[] bytes, int offset) throws InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        return this.decrypt(bytes, offset, bytes.length);
    }

    public byte[] decrypt(byte[] bytes, int offset, int len) throws InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        initCipher(Cipher.DECRYPT_MODE);
        byte[] result;
        {
            ByteArrayOutputStream pad = new ByteArrayOutputStream();
            pad.write(bytes);
            if (this.paddingMode.equals(PaddingMode.Zero)) {
                try {
                    int zeroSize = 16 - (pad.size() % 16);
                    if (zeroSize != 16) {
                        pad.write(new byte[zeroSize]);
                    }
                } catch (IOException e) {
                    throw new BadPaddingException("Zero padding error" + e);
                }
            }
            result = this.cipher.doFinal(pad.toByteArray(), offset, len);
        }
        return result;
    }

    private void initCipher(int mode) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        this.cipher = Cipher.getInstance(this.algorithm + "/" + this.mode.name + "/" + (this.paddingMode.equals(PaddingMode.Zero) ? "NoPadding" : this.paddingMode.name));
        if (this.Key != null && this.IV != null) {
            this.cipher.init(mode, new SecretKeySpec(this.Key, this.algorithm), new IvParameterSpec(this.IV));
        } else if (this.Key != null) {
            this.cipher.init(mode, new SecretKeySpec(this.Key, this.algorithm));
        } else {
            throw new NoSuchAlgorithmException("Key can't be null");
        }
    }
}
