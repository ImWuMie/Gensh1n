/*
 * Copyright 2022 Enaium
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.undefinedteam.gensh1n.jvm.service.decompiler;

import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.settings.Settings;
import dev.undefinedteam.gensh1n.system.SettingAdapter;

@SuppressWarnings("unused")
public class FernFlowerConfig implements SettingAdapter {
    public SettingGroup group;

    public Setting<Boolean> rbr;
    public Setting<Boolean> rsy;
    public Setting<Boolean> din;
    public Setting<Boolean> dc4;
    public Setting<Boolean> das;
    public Setting<Boolean> hes;
    public Setting<Boolean> hdc;
    public Setting<Boolean> dgs;
    public Setting<Boolean> ner;
    public Setting<Boolean> esm;
    public Setting<Boolean> den;
    public Setting<Boolean> rgn;
    public Setting<Boolean> lit;
    public Setting<Boolean> bto;
    public Setting<Boolean> asc;
    public Setting<Boolean> nns;
    public Setting<Boolean> uto;
    public Setting<Boolean> udv;
    public Setting<Boolean> ump;
    public Setting<Boolean> rer;
    public Setting<Boolean> fdi;
    public Setting<Boolean> inn;
    public Setting<Boolean> lac;
    public Setting<Boolean> bsm;
    public Setting<Boolean> dcl;
    public Setting<Boolean> iib;
    public Setting<Boolean> vac;
    public Setting<Boolean> tcs;
    public Setting<Boolean> pam;
    public Setting<Boolean> tlf;
    public Setting<Boolean> tco;
    public Setting<Boolean> swe;
    public Setting<Boolean> shs;
    public Setting<Boolean> ovr;
    public Setting<Boolean> ssp;
    public Setting<Boolean> iec;
    public Setting<Boolean> jrt;
    public Setting<Boolean> ega;
    public Setting<Boolean> isl;
    public Setting<Boolean> mpm;
    public Setting<Boolean> ren;
    public Setting<String> urc;
    public Setting<Boolean> nls;
    public Setting<String> ind;
    public Setting<Integer> pll;
    public Setting<String> erm;
    public Setting<Integer> thr;
    public Setting<Boolean> jvn;
    public Setting<Boolean> dbe;
    public Setting<Boolean> dee;
    public Setting<Boolean> dec;

    public FernFlowerConfig(Settings settings) {
        this.group = settings.createGroup("FernFlower");

        rbr = bool(group, "Remove Bridge Methods", true, "Removes any methods that are marked as bridge from the decompiled output.");
        rsy = bool(group, "Remove Synthetic Methods And Fields", true, "Removes any methods and fields that are marked as synthetic from the decompiled output.");
        din = bool(group, "Decompile Inner Classes", true, "Process inner classes and add them to the decompiled output.");
        dc4 = bool(group, "Decompile Java 4 class references", true, "Java 1 to Java 4 had a different class reference format. This resugars them properly.");
        das = bool(group, "Decompile Assertions", true, "Decompile assert statements.");
        hes = bool(group, "Hide Empty super()", true, "Hide super() calls with no parameters.");
        hdc = bool(group, "Hide Default Constructor", true, "Hide constructors with no parameters and no code.");
        dgs = bool(group, "Decompile Generics", true, "Decompile generics in variables, fields, and statements.");
        ner = bool(group, "No Exceptions In Return", true, "Integrate returns better in try-catch blocks.");
        esm = bool(group, "Ensure synchronized ranges are complete", true, "If a synchronized block has a monitorenter without any corresponding monitorexit, try to deduce where one should be to ensure the synchronized is proper.");
        den = bool(group, "Decompile Enums", true, "Decompile enums.");
        rgn = bool(group, "Remove reference getClass()", true, "obj.new Inner() or calling invoking a method on a method reference will create a synthetic getClass() call. This removes it.");
        lit = bool(group, "Keep Literals As Is", false, "Keep NaN, infinties, and pi values as is without resugaring them.");
        bto = bool(group, "Represent boolean as 0/1", true, "The JVM represents booleans as integers 0 and 1. This decodes 0 and 1 as boolean when it makes sense.");
        asc = bool(group, "ASCII String Characters", false, "Encode non-ASCII characters in string and character literals as Unicode escapes.");
        nns = bool(group, "Synthetic Not Set", false, "Treat some known structures as synthetic even when not explicitly set.");
        uto = bool(group, "Treat Undefined Param Type As Object", true, "Treat nameless types as java.lang.Object.");
        udv = bool(group, "Use LVT Names", true, "Use LVT names for local variables and parameters instead of var<index>_<version>.");
        ump = bool(group, "Use Method Parameters", true, "Use method parameter names, as given in the MethodParameters attribute.");
        rer = bool(group, "Remove Empty try-catch blocks", true, "Remove try-catch blocks with no code.");
        fdi = bool(group, "Decompile Finally", true, "Decompile finally blocks.");
        inn = bool(group, "Resugar Intellij IDEA @NotNull", true, "Resugar Intellij IDEA's code generated by @NotNull annotations.");
        lac = bool(group, "Decompile Lambdas as Anonymous Classes", false, "Decompile lambda expressions as anonymous classes.");
        bsm = bool(group, "Bytecode to Source Mapping", false, "Map Bytecode to source lines.");
        dcl = bool(group, "Dump Code Lines", false, "Dump line mappings to output archive zip entry extra data");
        iib = bool(group, "Ignore Invalid Bytecode", false, "Ignore bytecode that is malformed.");
        vac = bool(group, "Verify Anonymous Classes", false, "Verify that anonymous classes are local.");
        tcs = bool(group, "Ternary Constant Simplification", false, "Fold branches of ternary expressions that have boolean true and false constants.");
        pam = bool(group, "Pattern Matching", true, "Decompile with if and switch pattern matching enabled.");
        tlf = bool(group, "[Experimental] Try-Loop fix", false, "Code with a while loop inside of a try-catch block sometimes is malformed. This attempts to fix it, but may cause other issues.");
        tco = bool(group, "[Experimental] Ternary In If Conditions", false, "Tries to collapse if statements that have a ternary in their condition.");
        swe = bool(group, "Decompile Switch Expressions", true, "Decompile switch expressions in modern Java class files.");
        shs = bool(group, "[Debug] Show hidden statements", false, "Display code blocks hidden, for debugging purposes");
        ovr = bool(group, "Override Annotation", true, "Display override annotations for methods known to the decompiler.");
        ssp = bool(group, "Second-Pass Stack Simplficiation", true, "Simplify variables across stack bounds to resugar complex statements.");
        iec = bool(group, "Include Entire Classpath", false, "Give the decompiler information about every jar on the classpath.");
        jrt = bool(group, "Include Java Runtime", false, "Give the decompiler information about the Java runtime.");
        ega = bool(group, "Explicit Generic Arguments", false, "Put explicit diamond generic arguments on method calls.");
        isl = bool(group, "Inline Simple Lambdas", true, "Remove braces on simple, one line, lambda expressions.");
        mpm = bool(group, "[DEPRECATED] Max time to process method", false, "Maximum time in seconds to process a method. This is deprecated, do not use.");
        ren = bool(group, "Rename Members", false, "Rename classes, fields, and methods with a number suffix to help in deobfuscation.");
        urc = text(group, "User Renamer Class", "Path to a class that implements IIdentifierRenamer.", "");
        nls = bool(group, "New Line Seperator", false, "Character that seperates lines in the decompiled output.");
        ind = text(group, "Indent String", "A string of spaces or tabs that is placed for each indent level.", "   ");
        pll = intN(group, "Preferred line length", "Max line length before formatting is applied.", 160, 1, 500);
        //ban = stringSetting(group,"User Renamer Class", "Path to a class that implements IIdentifierRenamer.","");
        erm = text(group, "Error Message", "Message to display when an error occurs in the decompiler.", "Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)");
        thr = intN(group, "Thread Count", "How many threads to use to decompile.", 16, 1, 32);
        jvn = bool(group, "JAD-Style Variable Naming", false, "Use JAD-style variable naming for local variables, instead of var<index>_<version>A.");
        dbe = bool(group, "Dump Bytecode On Error", true, "Put the bytecode in the method body when an error occurs.");
        dee = bool(group, "Dump Exceptions On Error", true, "Put the exception message in the method body when an error occurs.");
        dec = bool(group, "Decompiler Comments", true, "Sometimes, odd behavior of the bytecode or unfixable problems occur. This enables or disables the adding of those to the decompiled output.");
    }
}
