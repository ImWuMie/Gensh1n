package dev.undefinedteam.gensh1n.music.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.utils.misc.ISerializable0;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.HashMap;
import java.util.Map;

@StringEncryption
@NativeObfuscation
@ControlFlowObfuscation
public class Cookies implements ISerializable0<Cookies> {
    public Map<String, String> cookies = new HashMap<>();

    public String find(String name) {
        return cookies.getOrDefault(name, null);
    }

    public boolean has(String name) {
        return find(name) != null && !find(name).isEmpty();
    }

    public String orElse(String name, String val) {
        return cookies.getOrDefault(name, val);
    }

    public void clear() {
        cookies.clear();
    }

    public void put(String name, String val) {
        if (this.cookies.containsKey(name)) {
            cookies.replace(name, val);
            return;
        }

        cookies.put(name, val);
    }

    @Override
    public JsonObject toTag() {
        var tag = new JsonObject();
        var array = new JsonArray();
        for (var cookie : this.cookies.entrySet()) {
            var obj = new JsonObject();
            obj.addProperty("name", cookie.getKey());
            obj.addProperty("value", cookie.getValue());
            array.add(obj);
        }
        tag.add("cookies", array);
        return tag;
    }

    @NativeObfuscation.Inline
    public static Cookies fromTag(JsonObject tag) {
        Cookies c = new Cookies();
        try {
            if (tag.has("cookies")) {
                var array = tag.get("cookies").getAsJsonArray();
                for (JsonElement element : array) {
                    var obj = element.getAsJsonObject();
                    var name = obj.get("name").getAsString();
                    var value = obj.get("value").getAsString();
                    c.put(name, value);
                }
            }
        } catch (Exception ignored) {}
        return c;
    }
}
