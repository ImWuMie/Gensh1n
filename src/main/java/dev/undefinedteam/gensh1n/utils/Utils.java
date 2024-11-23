package dev.undefinedteam.gensh1n.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.joml.Matrix4f;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static dev.undefinedteam.gensh1n.Client.mc;
import static org.lwjgl.glfw.GLFW.*;

@StringEncryption
@ControlFlowObfuscation
public class Utils {
    public static VertexSorter vertexSorter;
    public static boolean rendering3D = true;

    public static void init() {
    }

    public static <T> int compareValueByCondition(T a, T b, Predicate<T> cond) {
        boolean condA = cond.test(a);
        boolean condB = cond.test(b);

        if (condA == condB) {
            return 0;
        } else if (condA) {
            return 1;
        } else {
            return -1;
        }
    }

    public static double getAttackDamage(ItemStack stack) {
        double entityBaseDamage = mc.player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        double baseDamage = stack.getDamage();
        if (baseDamage == 0) {
            return 0.0;
        }

        // Client-side damage calculation for enchantments does not exist anymore
        // see https://bugs.mojang.com/browse/MC-196250
        //
        // We now use the following formula to calculate the damage:
        // https://minecraft.wiki/w/Sharpness  -> 0.5 * level + 0.5.
        return entityBaseDamage + baseDamage + getSharpnessDamage(stack, EnchantmentHelper.getLevel(Enchantments.SHARPNESS, stack));
    }

    public static int getSharpnessLevel(ItemStack stack) {
        return EnchantmentHelper.getLevel(Enchantments.SHARPNESS, stack);
    }

    public static double getSharpnessDamage(ItemStack stack, int level) {
        if (level == 0) return 0.0;
        return 0.5 * level + 0.5;
    }

    public static void unscaledProjection() {
        vertexSorter = RenderSystem.getVertexSorting();
        RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0, mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight(), 0, 1000, 21000), VertexSorter.BY_Z);
        rendering3D = false;
    }

    public static void scaledProjection() {
        RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0, (float) (mc.getWindow().getFramebufferWidth() / mc.getWindow().getScaleFactor()), (float) (mc.getWindow().getFramebufferHeight() / mc.getWindow().getScaleFactor()), 0, 1000, 21000), vertexSorter);
        rendering3D = true;
    }

    public static boolean canUpdate() {
        return mc != null && mc.player != null && mc.world != null;
    }

    public static double squaredDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dX = x2 - x1;
        double dY = y2 - y1;
        double dZ = z2 - z1;
        return dX * dX + dY * dY + dZ * dZ;
    }

    public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dX = x2 - x1;
        double dY = y2 - y1;
        double dZ = z2 - z1;
        return Math.sqrt(dX * dX + dY * dY + dZ * dZ);
    }


    public static double distance(Vec3i a, Vec3i b) {
        return distance(a.getX(), a.getY(), a.getZ(), b.getX(), b.getY(), b.getZ());
    }

    public static double distance(Vec3d a, Vec3d b) {
        return distance(a.getX(), a.getY(), a.getZ(), b.getX(), b.getY(), b.getZ());
    }

    public static double distance(double p1, double p2) {
        return Math.abs(p1 - p2);
    }

    public static String nameToTitle(String name) {
        return Arrays.stream(name.split("-")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
    }

    public static String titleToName(String title) {
        return title.replace(" ", "-").toLowerCase(Locale.ROOT);
    }

    public static float getTickDelta() {
        return mc.getTickDelta();
    }

    public static byte[] readBytes(InputStream in) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            byte[] buffer = new byte[256];
            int read;
            while ((read = in.read(buffer)) > 0) out.write(buffer, 0, read);

            in.close();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    public static float lerp(float tickDelta, float old, float current) {
        return old + (current - old) * tickDelta;
    }

    public static double lerp(float tickDelta, double old, double current) {
        return old + (current - old) * tickDelta;
    }

    public static double smoothTrans(float tickDelta, double current, double old) {
        return current * tickDelta + (old * (1 - tickDelta));
    }

    public static Object2IntMap<StatusEffect> createStatusEffectMap() {
        Object2IntMap<StatusEffect> map = new Object2IntArrayMap<>(Registries.STATUS_EFFECT.getIds().size());

        Registries.STATUS_EFFECT.forEach(potion -> map.put(potion, 0));

        return map;
    }

    public static String getKeyName(int key) {
        return switch (key) {
            case GLFW_KEY_UNKNOWN -> "Unknown";
            case GLFW_KEY_ESCAPE -> "Esc";
            case GLFW_KEY_GRAVE_ACCENT -> "Grave Accent";
            case GLFW_KEY_WORLD_1 -> "World 1";
            case GLFW_KEY_WORLD_2 -> "World 2";
            case GLFW_KEY_PRINT_SCREEN -> "Print Screen";
            case GLFW_KEY_PAUSE -> "Pause";
            case GLFW_KEY_INSERT -> "Insert";
            case GLFW_KEY_DELETE -> "Delete";
            case GLFW_KEY_HOME -> "Home";
            case GLFW_KEY_PAGE_UP -> "Page Up";
            case GLFW_KEY_PAGE_DOWN -> "Page Down";
            case GLFW_KEY_END -> "End";
            case GLFW_KEY_TAB -> "Tab";
            case GLFW_KEY_LEFT_CONTROL -> "Left Control";
            case GLFW_KEY_RIGHT_CONTROL -> "Right Control";
            case GLFW_KEY_LEFT_ALT -> "Left Alt";
            case GLFW_KEY_RIGHT_ALT -> "Right Alt";
            case GLFW_KEY_LEFT_SHIFT -> "Left Shift";
            case GLFW_KEY_RIGHT_SHIFT -> "Right Shift";
            case GLFW_KEY_UP -> "Arrow Up";
            case GLFW_KEY_DOWN -> "Arrow Down";
            case GLFW_KEY_LEFT -> "Arrow Left";
            case GLFW_KEY_RIGHT -> "Arrow Right";
            case GLFW_KEY_APOSTROPHE -> "Apostrophe";
            case GLFW_KEY_BACKSPACE -> "Backspace";
            case GLFW_KEY_CAPS_LOCK -> "Caps Lock";
            case GLFW_KEY_MENU -> "Menu";
            case GLFW_KEY_LEFT_SUPER -> "Left Super";
            case GLFW_KEY_RIGHT_SUPER -> "Right Super";
            case GLFW_KEY_ENTER -> "Enter";
            case GLFW_KEY_KP_ENTER -> "Numpad Enter";
            case GLFW_KEY_NUM_LOCK -> "Num Lock";
            case GLFW_KEY_SPACE -> "Space";
            case GLFW_KEY_F1 -> "F1";
            case GLFW_KEY_F2 -> "F2";
            case GLFW_KEY_F3 -> "F3";
            case GLFW_KEY_F4 -> "F4";
            case GLFW_KEY_F5 -> "F5";
            case GLFW_KEY_F6 -> "F6";
            case GLFW_KEY_F7 -> "F7";
            case GLFW_KEY_F8 -> "F8";
            case GLFW_KEY_F9 -> "F9";
            case GLFW_KEY_F10 -> "F10";
            case GLFW_KEY_F11 -> "F11";
            case GLFW_KEY_F12 -> "F12";
            case GLFW_KEY_F13 -> "F13";
            case GLFW_KEY_F14 -> "F14";
            case GLFW_KEY_F15 -> "F15";
            case GLFW_KEY_F16 -> "F16";
            case GLFW_KEY_F17 -> "F17";
            case GLFW_KEY_F18 -> "F18";
            case GLFW_KEY_F19 -> "F19";
            case GLFW_KEY_F20 -> "F20";
            case GLFW_KEY_F21 -> "F21";
            case GLFW_KEY_F22 -> "F22";
            case GLFW_KEY_F23 -> "F23";
            case GLFW_KEY_F24 -> "F24";
            case GLFW_KEY_F25 -> "F25";
            default -> {
                String keyName = glfwGetKeyName(key, 0);
                yield keyName == null ? "Unknown" : StringUtils.capitalize(keyName);
            }
        };
    }

    public static String getButtonName(int button) {
        return switch (button) {
            case -1 -> "Unknown";
            case 0 -> "Mouse Left";
            case 1 -> "Mouse Right";
            case 2 -> "Mouse Middle";
            default -> "Mouse " + button;
        };
    }

    public static boolean searchTextDefault(String text, String filter, boolean caseSensitive) {
        return searchInWords(text, filter) > 0 || searchLevenshteinDefault(text, filter, caseSensitive) < text.length() / 2;
    }

    public static int searchLevenshteinDefault(String text, String filter, boolean caseSensitive) {
        return levenshteinDistance(caseSensitive ? filter : filter.toLowerCase(Locale.ROOT), caseSensitive ? text : text.toLowerCase(Locale.ROOT), 1, 8, 8);
    }

    public static int searchInWords(String text, String filter) {
        if (filter.isEmpty()) return 1;

        int wordsFound = 0;
        text = text.toLowerCase(Locale.ROOT);
        String[] words = filter.toLowerCase(Locale.ROOT).split(" ");

        for (String word : words) {
            if (!text.contains(word)) return 0;
            wordsFound += StringUtils.countMatches(text, word);
        }

        return wordsFound;
    }

    public static int levenshteinDistance(String from, String to, int insCost, int subCost, int delCost) {
        int textLength = from.length();
        int filterLength = to.length();

        if (textLength == 0) return filterLength * insCost;
        if (filterLength == 0) return textLength * delCost;

        // Populate matrix
        int[][] d = new int[textLength + 1][filterLength + 1];

        for (int i = 0; i <= textLength; i++) {
            d[i][0] = i * delCost;
        }

        for (int j = 0; j <= filterLength; j++) {
            d[0][j] = j * insCost;
        }

        // Find best route
        for (int i = 1; i <= textLength; i++) {
            for (int j = 1; j <= filterLength; j++) {
                int sCost = d[i - 1][j - 1] + (from.charAt(i - 1) == to.charAt(j - 1) ? 0 : subCost);
                int dCost = d[i - 1][j] + delCost;
                int iCost = d[i][j - 1] + insCost;
                d[i][j] = Math.min(Math.min(dCost, iCost), sCost);
            }
        }

        return d[textLength][filterLength];
    }

    /**
     * 判断是否hover
     *
     * @param x      x pos
     * @param y      y pos
     * @param width  width
     * @param height height
     * @param mouseX mouse x
     * @param mouseY mouse y
     * @return hover
     */
    public static boolean isHoveringRect(double x, double y, double width, double height, double mouseX, double mouseY) {
        return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height;
    }

    public static boolean isHoveringRect(float x, float y, float width, float height, float mouseX, float mouseY) {
        return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height;
    }

    /**
     * 基于计算的渐变平滑
     *
     * @param start 初始值
     * @param end   结束值
     * @return 平滑值
     */
    public static double smooth_s(double start, double end) {
        return smooth_s(start, end, 0.1);
    }

    /**
     * 基于计算的渐变平滑
     *
     * @param start 初始值
     * @param end   结束值
     * @param speed 速度
     * @return 平滑值
     */
    public static double smooth_s(double start, double end, double speed) {
        double percent = (end - start) * speed;

        if (percent > 0) {
            percent = Math.max(speed, percent);
            percent = Math.min(end - start, percent);
        } else if (percent < 0) {
            percent = Math.min(-speed, percent);
            percent = Math.max(end - start, percent);
        }
        return start + percent;
    }

    /**
     * 基于tick的平滑
     *
     * @param current 初始
     * @param last    上一个
     * @return 平滑
     */
    public static double smoothTrans(double current, double last) {
        float tickDelta = Utils.getTickDelta();
        return current * tickDelta + (last * (1 - tickDelta));
    }

    public static int getWindowWidth() {
        return mc.getWindow().getFramebufferWidth();
    }

    public static int getWindowHeight() {
        return mc.getWindow().getFramebufferHeight();
    }
}
