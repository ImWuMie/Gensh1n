package dev.undefinedteam.gensh1n.utils.player;

import dev.undefinedteam.gclient.Timer;
import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.system.modules.misc.AutoPlay;
import dev.undefinedteam.gensh1n.system.modules.render.HitSound;
import dev.undefinedteam.gensh1n.utils.RandomUtils;
import dev.undefinedteam.gensh1n.utils.chat.ChatUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static dev.undefinedteam.gensh1n.Client.mc;

/**
 * @Author KuChaZi
 * @Date 2024/11/9 13:52
 * @ClassName: SoundManager
 */
@StringEncryption
@ControlFlowObfuscation
public class SoundManager {
    private static final File FOLDER = new File(Client.FOLDER, "sounds");
    public final Identifier KEYPRESS_SOUND = new Identifier("gensh1n:keypress");
    public SoundEvent KEYPRESS_SOUNDEVENT = SoundEvent.of(KEYPRESS_SOUND);
    public final Identifier IDK_SOUND = new Identifier("gensh1n:idk");
    public SoundEvent IDK_SOUNDEVENT = SoundEvent.of(IDK_SOUND);
    public final Identifier SEXY1_SOUND = new Identifier("gensh1n:sexy1");
    public SoundEvent SEXY1_SOUNDEVENT = SoundEvent.of(SEXY1_SOUND);
    public final Identifier SEXY2_SOUND = new Identifier("gensh1n:sexy2");
    public SoundEvent SEXY2_SOUNDEVENT = SoundEvent.of(SEXY2_SOUND);
    public final Identifier SEXY3_SOUND = new Identifier("gensh1n:sexy3");
    public SoundEvent SEXY3_SOUNDEVENT = SoundEvent.of(SEXY3_SOUND);
    public final Identifier SEXY4_SOUND = new Identifier("gensh1n:sexy4");
    public SoundEvent SEXY4_SOUNDEVENT = SoundEvent.of(SEXY4_SOUND);
    public final Identifier SKEET_SOUND = new Identifier("gensh1n:skeet");
    public SoundEvent SKEET_SOUNDEVENT = SoundEvent.of(SKEET_SOUND);

    public final Identifier GAMBARE_SOUND = new Identifier("gensh1n:gambare");
    public SoundEvent GAMBARE_SOUNDEVENT = SoundEvent.of(GAMBARE_SOUND);

    public final Identifier CHUNFENGJINLING_SOUND = new Identifier("gensh1n:chunfengjinling");
    public SoundEvent CHUNFENGJINLING_SOUNDEVENT = SoundEvent.of(CHUNFENGJINLING_SOUND);

    public final Identifier LOVE_SOUND = new Identifier("gensh1n:love");
    public SoundEvent LOVE_SOUNDEVENT = SoundEvent.of(LOVE_SOUND);

    public final Identifier JIABAILI_SOUND = new Identifier("gensh1n:jiabaili");
    public SoundEvent JIABAILI_SOUNDEVENT = SoundEvent.of(JIABAILI_SOUND);

    public final Identifier DUYE_SOUND = new Identifier("gensh1n:duye");
    public SoundEvent DUYE_SOUNDEVENT = SoundEvent.of(DUYE_SOUND);

    public final Identifier CHIJI_SOUND = new Identifier("gensh1n:chiji");
    public SoundEvent CHIJI_SOUNDEVENT = SoundEvent.of(CHIJI_SOUND);

    private final Timer scrollTimer = new Timer();

    private static final HitSound hitSound = new HitSound();
    private static final AutoPlay autoPlay = new AutoPlay();

    public SoundManager() {}

    public void CreateSoundsFolder() {
        if (!FOLDER.exists()) {
            if (FOLDER.mkdirs()) {
                System.out.print("Sounds folder created: " + FOLDER.getAbsolutePath());
            } else {
                System.out.print("Failed to create sounds folder!");
            }
        }
    }

    public void registerSounds() {
        Registry.register(Registries.SOUND_EVENT, KEYPRESS_SOUND, KEYPRESS_SOUNDEVENT);
        Registry.register(Registries.SOUND_EVENT, SEXY1_SOUND, SEXY1_SOUNDEVENT);
        Registry.register(Registries.SOUND_EVENT, SEXY2_SOUND, SEXY2_SOUNDEVENT);
        Registry.register(Registries.SOUND_EVENT, SEXY3_SOUND, SEXY3_SOUNDEVENT);
        Registry.register(Registries.SOUND_EVENT, SEXY4_SOUND, SEXY4_SOUNDEVENT);
        Registry.register(Registries.SOUND_EVENT, IDK_SOUND, IDK_SOUNDEVENT);
        Registry.register(Registries.SOUND_EVENT, SKEET_SOUND, SKEET_SOUNDEVENT);
        Registry.register(Registries.SOUND_EVENT, GAMBARE_SOUND, GAMBARE_SOUNDEVENT);

        Registry.register(Registries.SOUND_EVENT, CHUNFENGJINLING_SOUND, CHUNFENGJINLING_SOUNDEVENT);

        Registry.register(Registries.SOUND_EVENT, CHIJI_SOUND, CHIJI_SOUNDEVENT);
        Registry.register(Registries.SOUND_EVENT, JIABAILI_SOUND, JIABAILI_SOUNDEVENT);
        Registry.register(Registries.SOUND_EVENT, DUYE_SOUND, DUYE_SOUNDEVENT);
        Registry.register(Registries.SOUND_EVENT, LOVE_SOUND, LOVE_SOUNDEVENT);
    }

    public void playHitSound(HitSound.HitMode value) {
        switch (value) {
            case IDK -> playSound(IDK_SOUNDEVENT);
            case Skeet -> playSound(SKEET_SOUNDEVENT);
            case Keyboard -> playSound(KEYPRESS_SOUNDEVENT);
            case Sexy -> {
                SoundEvent sound = switch ((int) (RandomUtils.random(0, 3))) {
                    case 0 -> SEXY1_SOUNDEVENT;
                    case 1 -> SEXY2_SOUNDEVENT;
                    case 2 -> SEXY3_SOUNDEVENT;
                    default -> SEXY4_SOUNDEVENT;
                };
                playSound(sound);
            }
            case Custom -> playSound("Custom-Hit");
        }
    }

    public void playerSound(AutoPlay.PlayerMode value) {
        switch (value) {
            case Gambare -> playerSound(GAMBARE_SOUNDEVENT);
            case ChunFengJinLing -> playerSound(CHUNFENGJINLING_SOUNDEVENT);
            case DuYe -> playerSound(DUYE_SOUNDEVENT);
            case Love -> playerSound(LOVE_SOUNDEVENT);
            case JiaBaiLi -> playerSound(JIABAILI_SOUNDEVENT);
            case ChiJi -> playerSound(CHIJI_SOUNDEVENT);
        }
    }

    public void playerSound(SoundEvent sound) {
        if (mc.player != null && mc.world != null) {
            mc.world.playSound(mc.player, mc.player.getBlockPos(), sound, SoundCategory.BLOCKS, autoPlay.volume.get().floatValue() / 100f, 1f);
        }
    }


    public void playSound(SoundEvent sound) {
        if (mc.player != null && mc.world != null)
            mc.world.playSound(mc.player, mc.player.getBlockPos(), sound, SoundCategory.BLOCKS, hitSound.volume.get().floatValue() / 100f, 1f);
    }

    public void playSound(String name) {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File(FOLDER, name + ".wav").getAbsoluteFile()));
            FloatControl floatControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            floatControl.setValue((floatControl.getMaximum() - floatControl.getMinimum() * (hitSound.volume.get().floatValue() / 100f)) + floatControl.getMinimum());
            clip.start();
        } catch (Exception e) {
            ChatUtils.info("Error with playing sound! Check " + new File(FOLDER, name + ".wav").getAbsolutePath());
        }
    }

}
