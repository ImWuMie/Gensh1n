package dev.undefinedteam.gensh1n.system.hud.elements;

import dev.undefinedteam.gensh1n.music.GMusic;
import dev.undefinedteam.gensh1n.music.api.objs.lyric.LyricLine;
import dev.undefinedteam.gensh1n.render.GL;
import dev.undefinedteam.gensh1n.render.Renderer;
import dev.undefinedteam.gensh1n.render._new.NText;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.hud.ElementInfo;
import dev.undefinedteam.gensh1n.system.hud.HudElement;
import dev.undefinedteam.gensh1n.utils.render.color.Color;
import dev.undefinedteam.modernui.mc.MusicPlayer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

public class MusicLyricHud extends HudElement {
    public static final ElementInfo INFO = new ElementInfo("MusicLyric", "Show music lyric", MusicLyricHud.class);

    private final SettingGroup sgDefault = settings.getDefaultGroup();
    private final Setting<Boolean> shadow = bool(sgDefault, "shadow", true);
    private final Setting<Integer> maxLyricElement = intN(sgDefault, "max-lyric-element", 4, 1, 10);
    private final Setting<Double> smooth_delta = doubleN(sgDefault, "smooth-delta", 0.1, 0.001, 1);
    private final double spaceValue = 20;

    public MusicLyricHud() {
        super(INFO);
    }

    private double targetY;
    private double currentYPosition;

//    private final Image SHADOW = Image.create(Client.ASSETS_LOCATION, "shadow.png");

    @Override
    public void render(DrawContext context, float delta) {
        float maxWidth = 50;
        var handler = GMusic.INSTANCE;
        var renderer = Renderer.MAIN;
        var player = MusicPlayer.getInstance();
        var lyrics = handler.currentLyric;
        var tLyrics = handler.currentTLyric;

        double maxHeight = 50;

        if (inEdit && lyrics == null) {
            if (shadow.get()) {
                var paint = renderer._paint();
                paint.setAntiAlias(true);
                paint.setRGBA(0, 0, 0, (int) (90 * 1.0f));
                paint.setSmoothWidth(30.0f);

                float left = (float) getElementX() - 4;
                float top = (float) getElementY() - 4;
                float right = (float) (getElementX() + maxWidth)+ 4;
                float bottom = (float) (getElementY() + maxHeight) + 4;

                renderer._renderer().drawRoundRect(left,top,right,bottom, 15.0f,paint);

                //renderer._renderer().drawImage(SHADOW, 0,0,SHADOW.asTextureView().getWidth(),SHADOW.asTextureView().getHeight(),left,top,right,bottom, paint);
                renderer.render();
            }

            var centerX = getElementX() + (getElementWidth() - NText.regular22.getWidth("Lyric")) / 2;
            var centerY = getElementY() + (getElementHeight() - NText.regular22.getHeight("Lyric")) / 2;

            NText.regular22.draw("Lyric", centerX, centerY, new Color(231, 231, 231, 231).getPacked());
        }

        renderer.render();
        NText.regular22._render();

        if (lyrics != null) {
            double height = 0;
            for (LyricLine line : lyrics.lines) {
                if (line == null) continue;

                var lineWidth = NText.regular22.getWidth(line.text());
                line.tempLyricWidth = lineWidth;
                line.tempWidth = lineWidth + 8;
                if (lineWidth > maxWidth) maxWidth = line.tempWidth;

                var lineHeight = NText.regular22.getHeight(line.text());
                line.tempLyricHeight = lineHeight + 2;
                if (tLyrics != null) {
                    var tLine = tLyrics.line((int) line.start);
                    if (tLine != null) {
                        lineHeight += 1.0f + NText.regular16.getHeight(tLine.text());
                        line.temptLyricWidth = NText.regular16.getWidth(tLine.text());
                    }
                }
                line.tempHeight = lineHeight + 4;
            }

            var current = lyrics.line(player.getTrackMS());
            if (current != null) {
                var cur = current;
                var nCur = current;
                for (int i = 0; i < MathHelper.floor((float) maxLyricElement.get() / 2); i++) {
                    cur = lyrics.prev(cur);
                    if (cur != null) {
                        height += cur.tempHeight + 4;
                    }
                    nCur = lyrics.prev(nCur);
                    if (nCur != null) {
                        height += nCur.tempHeight + 4;
                    }
                }
            }

            maxHeight = Math.max(50, height);
        }

        setElementSize(MathHelper.floor(maxWidth), MathHelper.floor(maxHeight));

        if (lyrics != null) {
            // 更新 currentYPosition 的位置，添加动画效果
            currentYPosition = MathHelper.lerp(smooth_delta.get(), currentYPosition, targetY);

            var currentLine = lyrics.line(player.getTrackMS());
            if (currentLine == null) return;
            double centerY = getElementY() + getElementHeight() / 2 - currentLine.tempHeight / 2;
            double currentY = centerY - currentYPosition;
            GL.scissorStart(getElementX(), getElementY(), getElementWidth(), getElementHeight());

            for (LyricLine line : lyrics.lines) {
                if (line == null) continue;

                GL.scissor(getElementX(), getElementY(), getElementWidth(), getElementHeight());
                var distance = Math.abs(currentY - centerY);
                double alpha = distance < (getElementHeight() / 2) ? 1 - MathHelper.clamp(distance / ((getElementHeight() + 20) / 2), 0.2, 1) : 0;
                boolean playing = line.equals(lyrics.line(player.getTrackMS()));

                var renderX = getElementX() + (getElementWidth() - line.tempLyricWidth) / 2;
                boolean hovering = isHovering(renderX, currentY);
                if (!hovering) alpha = 0;

                var finalAlpha = MathHelper.clamp(alpha, 0, 1);
                var intAlpha = MathHelper.clamp((int) (255 * finalAlpha), 0, 255);

                if (shadow.get()) {
                    var paint = renderer._paint();
                    paint.setAntiAlias(true);
                    paint.setRGBA(0, 0, 0, (int) (90 * alpha));
                    paint.setSmoothWidth(30.0f);

                    float left = (float) renderX - 4;
                    float top = (float) currentY - 4;
                    float right = (float) (renderX + line.tempLyricWidth)+ 4;
                    float bottom = (float) (currentY + line.tempLyricHeight) + 4;

                    renderer._renderer().drawRoundRect(left,top,right,bottom, 15.0f,paint);

                    //renderer._renderer().drawImage(SHADOW, 0,0,SHADOW.asTextureView().getWidth(),SHADOW.asTextureView().getHeight(),left,top,right,bottom, paint);
                    renderer.render();
                }

                NText.regular22.draw(line.text(), renderX, currentY, new Color(231, 231, 231, intAlpha).getPacked());
                NText.regular22._render();

                if (playing) {
                    var progress = lyrics.progress(player.getTrackMS());
                    GL.scissor(renderX - 3, currentY - 3, line.tempLyricWidth * progress, line.tempLyricHeight);
                    NText.regular22.draw(line.text(), renderX, currentY, new Color(255, 255, 255, intAlpha).getPacked());
                    NText.regular22._render();
                    GL.scissor(getElementX(), getElementY(), getElementWidth(), getElementHeight());
                }

                if (tLyrics != null) {
                    var tLine = tLyrics.line((int) line.start);
                    if (tLine != null) {
                        NText.regular16.draw(tLine.text(), getElementX() + (getElementWidth() - line.temptLyricWidth) / 2, currentY + line.tempLyricHeight, new Color(231, 231, 231, intAlpha).getPacked());
                        NText.regular16._render();
                    }
                }

                currentY += line.tempHeight + spaceValue;
            }

            GL.scissorEnd();
        }
    }

    @Override
    public void tick() {
        var handler = GMusic.INSTANCE;
        var lyrics = handler.currentLyric;
        var player = MusicPlayer.getInstance();

        if (lyrics != null) {
            double currentOffsetY = 0;

            int indexToCur = 0;
            for (LyricLine line : lyrics.lines) {
                if (indexToCur == lyrics.index(player.getTrackMS())) {
                    targetY = currentOffsetY;
                    break;
                }

                currentOffsetY += line.tempHeight + spaceValue;
                indexToCur++;
            }
        }
    }
}
