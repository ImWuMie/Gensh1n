package dev.undefinedteam.gensh1n.gui.frags;

import dev.undefinedteam.gclient.GChat;
import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.gui.builders.LayoutBuilder;
import dev.undefinedteam.gensh1n.gui.builders.ParamsBuilder;
import dev.undefinedteam.gensh1n.gui.builders.ViewBuilder;
import dev.undefinedteam.gensh1n.gui.frags.music.MusicFragment;
import dev.undefinedteam.gensh1n.gui.overlay.MusicSpectrum;
import dev.undefinedteam.gensh1n.gui.renders.ParticlesRender;
import dev.undefinedteam.gensh1n.gui.utils.BlockView;
import dev.undefinedteam.gensh1n.gui.utils.BlockViewGroup;
import dev.undefinedteam.gensh1n.system.hud.gui.HudEditorFragment;
import dev.undefinedteam.modernui.mc.MusicPlayer;
import icyllis.modernui.audio.FFT;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.fragment.FragmentContainerView;
import icyllis.modernui.fragment.FragmentTransaction;
import icyllis.modernui.graphics.*;
import icyllis.modernui.graphics.drawable.Drawable;
import icyllis.modernui.graphics.drawable.ShapeDrawable;
import dev.undefinedteam.modernui.mc.fabric.PreferencesFragment;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.view.*;
import icyllis.modernui.widget.*;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;


import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;

import static dev.undefinedteam.gensh1n.gui.frags.GMainGui.TAB_BUTTON_COLOR;
import static dev.undefinedteam.gensh1n.gui.frags.MainInfoSaver.NULL;
import static icyllis.modernui.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static icyllis.modernui.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MainGuiFragment extends Fragment {
    private static MainGuiFragment INSTANCE;

    public static MainGuiFragment get() {
        if (INSTANCE == null) {
            INSTANCE = new MainGuiFragment();
        }
        return INSTANCE;
    }

    public static final int id_left_container = 0xfff1;
    public static final int id_view = 0xfff2;

    private static final ParticlesRender _particles = new ParticlesRender();
    private final BlockViewGroup group = new BlockViewGroup(id_left_container);

    private float mouseX, mouseY;

    @NotNull
    public MusicSpectrum mSpectrumDrawable;
    public static MainInfoSaver saver = new MainInfoSaver();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, DataSet savedInstanceState) {
        var base = LayoutBuilder.newLinerBuilder(requireContext());
        mSpectrumDrawable = new MusicSpectrum(
            base.dp(15),
            0,
            base.dp(640),
            new Color(156, 197, 255, 135)
        );

        MusicPlayer.getInstance().setAnalyzerCallback(
            fft -> {
                fft.setLogAverages(250, 14);
                fft.setWindowFunc(FFT.NONE);
            },
            mSpectrumDrawable::updateAmplitudes
        );


        /*base.layout().setOnGenericMotionListener((__, event) -> {
            mouseX = event.getX();
            mouseY = event.getY();
            if (event.isButtonPressed(MotionEvent.BUTTON_PRIMARY)) {
                _particles.mouseClicked(mouseX, mouseY, MotionEvent.BUTTON_PRIMARY);
            }
            return false;
        });
        base.fg(_particles.getDrawable(() -> this.mouseX, () -> this.mouseY));*/

        if (!Client.isOnMinecraftEnv()) {
            Image bg_image;
            try {
                InputStream icon = MainGuiFragment.class.getResourceAsStream("/download.png");
                bg_image = Image.createTextureFromBitmap(BitmapFactory.decodeStream(icon));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            base.bg(new Drawable() {
                @Override
                public void draw(Canvas canvas) {
                    Paint paint = Paint.obtain();
                    paint.setRGB(255, 255, 255);
                    canvas.drawImage(bg_image, getBounds(), getBounds(), paint);
                    paint.recycle();
                }
            });
        }

        {
            this.group.clear();
            this.group.add(new BlockView("Modules", "All modules.", 0, "m"));
            this.group.add(new BlockView("Music", "Netease music.", 1, "y"));
//            this.group.add(new BlockView("C", "class viewer.", 2, null));
            this.group.add(new BlockView(Client.SINGLE_SPECIAL_NAME, "chat' genshin.", 3, null));

            this.group.add(new BlockView("GUI", "gui settings.", 4, null));
            this.group.add(new BlockView("HUD", "HUD", 5, null));
        }

        var left_bar = LayoutBuilder.newLinerBuilder(requireContext());
        left_bar
            .vOrientation()
            .vGravity(Gravity.CENTER_VERTICAL);
        {

            var radioGroup = LayoutBuilder.wrapRadioGroup(this.group.getLayout(requireContext(), TAB_BUTTON_COLOR, saver));
            radioGroup.onCheck(id -> {
                var fm = getChildFragmentManager();
                saver.tab_last_checked = id;

                var ft = switch (id) {
                    case 0 -> fm.beginTransaction()
                        .replace(id_view, GMainGui.class, null, "modules");
                    case 1 -> fm.beginTransaction()
                        .replace(id_view, MusicFragment.class, null, "music");
//                    case 2 -> fm.beginTransaction()
//                        .replace(id_view, ClassViewFragment.class, null, "class_view");
                    case 3 -> fm.beginTransaction()
                        .replace(id_view, ChatFragment.class, null, "chat_genshin");
                    case 4 -> fm.beginTransaction()
                        .replace(id_view, PreferencesFragment.class, null, "gui_setting");
                    case 5 -> fm.beginTransaction()
                        .replace(id_view, HudEditorFragment.class, null, "hud");
                    default -> fm.beginTransaction();
                };
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .setReorderingAllowed(true)
                    .commit();
            });
            int dp2 = base.dp(2);
            radioGroup
                .padding(dp2, dp2, dp2, dp2)
                .vGravity(Gravity.CENTER_VERTICAL)
                .params()
                .gravity(Gravity.CENTER_VERTICAL)
                .h_wrap_content().v_wrap_content();
            left_bar.add(radioGroup.build(), 0);
            {
                ShapeDrawable drawable = new ShapeDrawable();
                drawable.setColor(new Color(80, 80, 80, 120).getRGB());
                drawable.setShape(ShapeDrawable.RECTANGLE);
                drawable.setCornerRadius(base.dp(2));
                left_bar.bg(drawable);
            }
            left_bar.params().width(base.dp(60)).v_match_parent();
            left_bar.id(id_left_container);
            base.add(left_bar.build(), 0);
        }

        {
            var center = LayoutBuilder.newLinerBuilder(requireContext());
            center
                .vOrientation()
                .gravity(Gravity.CENTER)
                .hGravity(Gravity.CENTER_HORIZONTAL)
                .vGravity(Gravity.CENTER_VERTICAL)
                .bg(mSpectrumDrawable);

            var scrollView = new LinearLayout(requireContext());
            scrollView.setGravity(Gravity.CENTER);
            scrollView.setId(id_view - 10);
            {
                var params = ParamsBuilder.newLinerBuilder();
                params
                    .h_match_parent().v_match_parent()
                ;
                //params.rule(RelativeLayout.CENTER_IN_PARENT);
                scrollView.setLayoutParams(params.build());
            }

            var containerView = ViewBuilder.wrapLinear(new FragmentContainerView(requireContext()));

            containerView.id(id_view);
            containerView.params()
                .h_match_parent().v_match_parent()
            ;
            scrollView.addView(containerView.build(), 0);

            {
                if (saver.tab_last_checked != NULL) {
                    var fm = getChildFragmentManager();
                    var ft = switch (saver.tab_last_checked) {
                        case 0 -> fm.beginTransaction()
                            .replace(id_view, GMainGui.class, null, "modules");
                        case 1 -> fm.beginTransaction()
                            .replace(id_view, MusicFragment.class, null, "music");
                        case 2 -> fm.beginTransaction()
                            .replace(id_view, ClassViewFragment.class, null, "class_view");
                        case 3 -> fm.beginTransaction()
                            .replace(id_view, ChatFragment.class, null, "chat_genshin");
                        case 4 -> fm.beginTransaction()
                            .replace(id_view, PreferencesFragment.class, null, "gui_setting");
                        default -> fm.beginTransaction();
                    };
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .setReorderingAllowed(true)
                        .commit();
                }
            }

            //base.setRotation(30);
            center.add(scrollView, 0);
            center.params()
                .h_match_parent()
                .v_match_parent();

            base.add(center.build(), 1);
        }

        base.params().v_match_parent().h_match_parent();
        return base.build();
    }
}
