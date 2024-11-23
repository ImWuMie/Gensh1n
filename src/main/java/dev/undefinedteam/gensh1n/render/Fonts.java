package dev.undefinedteam.gensh1n.render;

import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.Genshin;
import dev.undefinedteam.gensh1n.utils.ResLocation;
import icyllis.modernui.ModernUI;
import icyllis.modernui.graphics.text.FontFamily;
import icyllis.modernui.text.Typeface;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static dev.undefinedteam.gensh1n.Client.mc;

public class Fonts {
    public static final File FOLDER = new File(Client.FOLDER, "fonts");

    private static final boolean BUILTIN = false;
    private static final String[] BUILTIN_FONTS = new String[]{
        "regular.ttf", "icon.ttf"
    };

    public static Typeface REGULAR,ICON;

    // After typeface loaded
    public static void init() {
        if (!FOLDER.exists()) {
            FOLDER.mkdirs();
        }

        try {
            unpack();
            REGULAR = _create(BUILTIN_FONTS[0]);
            //REGULAR = ModernUI.getSelectedTypeface();
            ICON = _create(BUILTIN_FONTS[1]);
        } catch (IOException e) {
            Genshin.LOG.info("Failed to unpack fonts.", e);
        } catch (FontFormatException e) {
            Genshin.LOG.info("Failed to load font.", e);
        }
    }

    public static Typeface _create(String name) throws IOException, FontFormatException {
        InputStream stream;
        if (BUILTIN) {
            Identifier location = ResLocation.of("fonts/" + name);
            stream = Fonts.class.getResourceAsStream("/assets/gensh1n/fonts/"+name);
        } else {
            File dest = new File(FOLDER, name);
            stream = Files.newInputStream(dest.toPath());
        }
        return Typeface.createTypeface(FontFamily.createFamily(stream, false));
    }

    private static void unpack() throws IOException {
        if (mc == null) return;

        for (String builtinFont : BUILTIN_FONTS) {
            Identifier location = ResLocation.of("fonts/" + builtinFont);
            InputStream stream = Fonts.class.getResourceAsStream("/assets/gensh1n/fonts/"+builtinFont);
            File dest = new File(FOLDER, builtinFont);
            if (!dest.exists()) {
                Files.copy(stream, dest.toPath());
            }
        }
    }
}
