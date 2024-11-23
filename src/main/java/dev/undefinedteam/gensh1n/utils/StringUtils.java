package dev.undefinedteam.gensh1n.utils;

import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@StringEncryption
@ControlFlowObfuscation
public class StringUtils {
    @NativeObfuscation.Inline
    public static String getReplaced(String str,Object... args) {
        String s = str;
        for (Object a : args) {
            s = s.replaceFirst("\\{}",a == null ? "null" : a.toString());
        }
        return s;
    }


    public static Matcher matches(String str, String regex) {
        Pattern mPattern = Pattern.compile(regex);
        return mPattern.matcher(str);
    }
    public static String replaceAll(String str,String regex,String next) {
        String s = str;
        while (s.contains(regex)) {
            s = s.replaceFirst(regex,next);
        }
        return s;
    }
}
