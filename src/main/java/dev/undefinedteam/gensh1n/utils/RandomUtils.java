package dev.undefinedteam.gensh1n.utils;

import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.Random;

@StringEncryption
@ControlFlowObfuscation
public class RandomUtils {
    private static final Random RANDOM = new Random();

    public static long randomDelay(final int minDelay, final int maxDelay) {
        return RandomUtils.nextInt(minDelay, maxDelay);
    }

    public static int nextInt(int startInclusive, int endExclusive) {
        return (endExclusive - startInclusive <= 0) ? startInclusive : startInclusive + RANDOM.nextInt(endExclusive - startInclusive);
    }

    public static double nextDouble(double startInclusive,double endInclusive) {
        return (startInclusive == endInclusive || endInclusive - startInclusive <= 0.0) ? startInclusive : startInclusive + (endInclusive - startInclusive) * Math.random();
    }
    public static float nextFloat(double startInclusive,double endInclusive) {
        return (float) ((startInclusive == endInclusive || endInclusive - startInclusive <= 0.0f) ? startInclusive : (startInclusive + (endInclusive - startInclusive) * Math.random()));
    }

    public static boolean nextBoolean() {
        return RANDOM.nextBoolean();
    }

    public static String randomNumber(int length) {
        return random(length, "123456789");
    }

    public static String randomString(int length) {
        return random(length, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
    }

    public static String randomStringA(int length) {
        return random(length, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
    }

    public static String randomStringHex(int length) {
        return random(length, "1234567890ABCDEF");
    }

    public static String randomStringLower(int length) {
        return random(length, "0123456789abcdefghijklmnopqrstuvwxyz");
    }
    public static String randomStringHexLower(int length) {
        return random(length, "0123456789abcdef");
    }

    public static String random(int length, String chars) {
        return random(length, chars.toCharArray());
    }

    public static float random(float min, float max) {
        return (float) (Math.random() * (max - min) + min);
    }

    public static String random(int length, char[] chars) {
        StringBuilder stringBuilder = new  StringBuilder();
        for (int i = 0;i < length;i++){
            stringBuilder.append(chars[(RANDOM.nextInt(chars.length))]);
        }
        return stringBuilder.toString();
    }

    public static void nextBytes(byte[] array) {
        RANDOM.nextBytes(array);
    }
}
