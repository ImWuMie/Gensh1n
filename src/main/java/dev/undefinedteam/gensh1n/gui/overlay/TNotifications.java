package dev.undefinedteam.gensh1n.gui.overlay;

import dev.undefinedteam.gensh1n.render.Renderer;
import dev.undefinedteam.gensh1n.render._new.NText;
import dev.undefinedteam.gensh1n.render._new.NTextRenderer;
import dev.undefinedteam.gensh1n.utils.StringUtils;
import dev.undefinedteam.gensh1n.utils.Utils;
import dev.undefinedteam.gensh1n.utils.render.color.Color;
import net.minecraft.util.math.MathHelper;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;



@StringEncryption
public class TNotifications extends ROverlayGui {
    public static TNotifications INSTANCE;

    public static final int MAX_ELEMENTS = 3;
    public static final int ICON_MARGIN = 8;

    public static final int LONG = 5000;
    public static final int SHORT = 3000;

    public TNotifications() {
        super("top_notifications");
        INSTANCE = this;
    }

    private final List<Message> messages = new CopyOnWriteArrayList<>();

    private double maxY = 5;

    @Override
    public void render(Renderer renderer, float tickDelta) {
        var font = NText.regular;
        maxY = 5;
        for (Message message : messages) {
            message.render(renderer, font, tickDelta);
            message.maxY = maxY;
            message.targetY = maxY;
            maxY += message.height + 5;

            if (message.duration <= 100)
                message.targetY = -message.height - 5;
        }

        if (this.messages.size() > MAX_ELEMENTS) {
            this.messages.removeFirst();
        }

        messages.removeIf(m -> m.duration <= 0 && m.y <= 0);
    }

    public void info(String message, int duration,Object... args) {
        push(message, Type.INFO, duration,args);
    }

    public void warn(String message, int duration,Object... args) {
        push(message, Type.WARN, duration,args);
    }

    public void error(String message, int duration,Object... args) {
        push(message, Type.ERROR, duration,args);
    }

    public void idk(String message, int duration,Object... args) {
        push(message, Type.IDK, duration,args);
    }

    public void push(String message, Type type, int duration,Object... args) {
        var font = NText.regular;
        var msg = new Message(StringUtils.getReplaced(message,args), type, duration);
        msg.targetY = maxY;
        msg.maxY = maxY;

        var width = Utils.getWindowWidth();
        var mWidth = font.getWidth(msg.message) + ICON_MARGIN * 3 + 20;
        var mHeight = ICON_MARGIN * 2 + 20;
        msg.x = (double) width / 2 - mWidth / 2;
        msg.width = mWidth;
        msg.height = mHeight;
        this.messages.addLast(msg);
    }

    public static class Message {
        private final Color BG_COLOR = new Color(0, 0, 0, 100);

        public String message;
        public Type type;
        public int duration;

        public double x, y, width, height;
        private final Color TEXT_COLOR = new Color(255, 255, 255, 255);

        public double targetY, maxY;

        public double percent = 0.1;

        public Message(String message, Type type, int duration) {
            this.message = message;
            this.type = type;
            this.duration = duration;
            this.y = 0;
        }

        private long startMS = -1L, endMS;

        public void render(Renderer renderer, NTextRenderer font, float tickDelta) {
            this.percent = MathHelper.clamp((this.y / this.maxY), 0, 1);
            TEXT_COLOR.a = (int) (255 * this.percent);
            BG_COLOR.a = (int) (100 * this.percent);

            if (startMS == -1L) {
                startMS = System.currentTimeMillis();
                endMS = startMS + duration;
            }

            this.duration = (int) (endMS - System.currentTimeMillis());

            this.y = Utils.smooth_s(this.y, this.targetY, 0.05);
            renderer.drawRound((float) this.x, (float) this.y, (float) (this.width), (float) (this.height), 15, BG_COLOR);
            renderer.render();
            //renderer.drawRound((float) (this.x + ICON_MARGIN), (float) (this.y + ICON_MARGIN), 40, 40, 5, Color.WHITE);
            var text = this.type.symbol;
            font.draw(text, (float) (this.x + ICON_MARGIN) + (20 - font.getWidth(text)) / 2, (float) (this.y + ICON_MARGIN) + (20 - font.getHeight(text)) / 2, TEXT_COLOR.getPacked());
            font.draw(this.message, this.x + 20 + ICON_MARGIN * 2, this.y + (this.height - font.getHeight(this.message)) / 2, TEXT_COLOR.getPacked());
        }
    }

    public enum Type {
        INFO("\uD83D\uDE10"), WARN("\uD83D\uDE32"), ERROR("\uD83D\uDE31"), IDK("\uD83E\uDD14");

        public final String symbol;

        Type(String symbol) {
            this.symbol = symbol;
        }
    }
}
