package dev.undefinedteam.gensh1n.system.modules.misc;

import dev.undefinedteam.gensh1n.events.game.ReceiveMessageEvent;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.utils.chat.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.Objects;
@StringEncryption
@ControlFlowObfuscation
public class AutoCalc extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Mode> mode = choice(sgGeneral, "mode", "Computing mode.", Mode.Pit);
    private final Setting<String> prefixContains = text(sgGeneral, "prefix-contains", "速算", () -> mode.get().equals(Mode.Pit));
    private final Setting<String> split = text(sgGeneral, "spilt", "Spilt char.", ":");

    private Stage currentStage = Stage.None;

    public AutoCalc() {
        super(Categories.Misc, "auto-calc", "Automatic calculation.");
    }

    @EventHandler
    private void onChat(ReceiveMessageEvent event) {
        try {
            String message = event.getMessage().getString();
            if (split.get().isEmpty()) return;
            if (!message.contains(split.get())) return;
            String[] texts = message.split(split.get());
            String precede = texts[0];
            if (mode.get().equals(Mode.Pit) && !precede.contains(prefixContains.get())) return;
            boolean hasEmptyChar = texts[1].startsWith(" ");
            String receive = hasEmptyChar ? message.replace(precede + split.get() + "  ", "") : message.replace(precede + split.get(), "");
            currentStage = updateStage(receive);
            if (currentStage.equals(Stage.None)) return;
            if (Objects.requireNonNull(mode.get()) == Mode.Pit) {
                double out = 0.0;
                switch (currentStage) {
                    case Subtract -> {
                        String m1 = receive;
                        if (receive.startsWith("-")) {
                            m1 = m1.substring(1);
                        }
                        String[] numbers = m1.split("-");
                        double n1 = Double.parseDouble(receive.startsWith("-") ? "-" + numbers[0] : numbers[0]);
                        double n2 = Double.parseDouble(numbers[1]);
                        out = n1 - n2;
                    }
                    case Add -> {
                        String[] numbers = receive.split("\\+");
                        double n1 = Double.parseDouble(numbers[0]);
                        double n2 = Double.parseDouble(numbers[1]);
                        out = n1 + n2;
                    }
                    case Divide -> {
                        String[] numbers = (receive.contains("/")) ? receive.split("/") : receive.split("÷");
                        double n1 = Double.parseDouble(numbers[0]);
                        double n2 = Double.parseDouble(numbers[1]);
                        out = n1 / n2;
                    }
                    case Multiply -> {
                        String[] numbers = (receive.contains("x")) ? receive.split("x") : receive.split("\\*");
                        double n1 = Double.parseDouble(numbers[0]);
                        double n2 = Double.parseDouble(numbers[1]);
                        out = n1 * n2;
                    }
                }
                String outString = String.valueOf(out);
                String send = outString.endsWith(".0") ? String.valueOf(((int) out)) : outString;
                ChatUtils.sendPlayerMsg(send);
            }
        } catch (Exception e) {
            error("calc error (std): " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Stage updateStage(String message) {
        Stage stage = Stage.None;
        if (message.contains("-")) {
            if (message.startsWith("-")) {
                String t = message.substring(1);
                if (t.contains("-")) {
                    stage = Stage.Subtract;
                }
            } else {
                stage = Stage.Subtract;
            }
        }
        if (message.contains("+")) {
            stage = Stage.Add;
        }
        if (message.contains("x") || message.contains("*")) {
            stage = Stage.Multiply;
        }
        if (message.contains("÷") || message.contains("/")) {
            stage = Stage.Divide;
        }
        return stage;
    }

    public enum Stage {
        Multiply,
        Divide,
        Add,
        Subtract,
        None
    }

    public enum Mode {
        Pit
    }
}
