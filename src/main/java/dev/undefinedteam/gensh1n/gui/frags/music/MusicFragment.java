package dev.undefinedteam.gensh1n.gui.frags.music;

import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.gui.animators.ViewAnimators;
import dev.undefinedteam.gensh1n.gui.builders.LayoutBuilder;
import dev.undefinedteam.gensh1n.gui.builders.ViewBuilder;
import dev.undefinedteam.gensh1n.gui.frags.GMainGui;
import dev.undefinedteam.gensh1n.gui.frags.MainGuiFragment;
import dev.undefinedteam.gensh1n.gui.overlay.DialogManager;
import dev.undefinedteam.gensh1n.gui.overlay.MusicSpectrum;
import dev.undefinedteam.gensh1n.gui.weights.PageShower;
import dev.undefinedteam.gensh1n.music.GMusic;
import dev.undefinedteam.gensh1n.music.PlayList;
import dev.undefinedteam.gensh1n.music.api.objs.Song;
import dev.undefinedteam.gensh1n.music.api.objs.model.DetailData;
import dev.undefinedteam.gensh1n.music.api.objs.model.SearchData;
import dev.undefinedteam.gensh1n.music.api.types.SearchType;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.utils.StringUtils;
import dev.undefinedteam.gensh1n.utils.network.Http;
import dev.undefinedteam.modernui.mc.MusicPlayer;
import icyllis.modernui.ModernUI;
import icyllis.modernui.R;
import icyllis.modernui.animation.ObjectAnimator;
import icyllis.modernui.animation.TimeInterpolator;
import icyllis.modernui.annotation.NonNull;
import icyllis.modernui.audio.FFT;
import icyllis.modernui.core.Context;
import icyllis.modernui.core.Core;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.graphics.*;
import icyllis.modernui.graphics.Image;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.graphics.drawable.*;
import icyllis.modernui.text.InputFilter;
import icyllis.modernui.text.TextUtils;
import icyllis.modernui.text.method.DigitsInputFilter;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.util.StateSet;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.LayoutInflater;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.*;
import icyllis.modernui.widget.Button;
import net.minecraft.util.math.MathHelper;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.awt.Color;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static dev.undefinedteam.gensh1n.gui.frags.GMainGui.EDGE_SIDES_COLOR;
import static dev.undefinedteam.gensh1n.gui.frags.GMainGui.T_ICON_PTS;
import static icyllis.modernui.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static icyllis.modernui.view.ViewGroup.LayoutParams.WRAP_CONTENT;

@StringEncryption
public class MusicFragment extends Fragment {
    public static final int DEFAULT_BG_COLOR = new Color(0, 0, 0, 20).getRGB();
    public static final int THEME_BG_COLOR = new Color(0, 0, 0, 40).getRGB();
    public static final int THEME_DARK_COLOR = new Color(255, 214, 255, 255).getRGB();
    public static final int FONT_COLOR = new Color(0, 0, 0, 255).getRGB();
    public static final int DARK_COLOR = new Color(0, 0, 0, 25).getRGB();

    private static final int PLAYLIST_OFFSET = 77; //草拟吗歪77了

    public static final int NFONT_COLOR = new Color(245, 245, 245, 180).getRGB();

    public static final int EDITTEXT_IN_COLOR = new Color(245, 245, 245, 255).getRGB();
    public static final int EDITTEXT_EDGE_COLOR = new Color(235, 235, 235, 255).getRGB();

    private static final Image ICON = Image.create(Client.ASSETS_LOCATION, "music/icon.png");
    public static final Image ARROW_LEFT_IMAGE = Image.create(Client.ASSETS_LOCATION, "icons/16px/arrow_left.png");
    public static final Image ARROW_RIGHT_IMAGE = Image.create(Client.ASSETS_LOCATION, "icons/16px/arrow_right.png");
    public static final Image SEARCH_IMAGE = Image.create(Client.ASSETS_LOCATION, "icons/16px/search.png");
    public static final Image SETTING_IMAGE = Image.create(Client.ASSETS_LOCATION, "icons/16px/setting.png");
    public static final Image CLOSE_BLACK_IMAGE = Image.create(Client.ASSETS_LOCATION, "icons/16px/close_black.png");
    public static final Image DELETE = Image.create(Client.ASSETS_LOCATION, "icons/16px/delete.png");
    public static final Image PAUSE = Image.create(Client.ASSETS_LOCATION, "icons/16px/pause.png");
    public static final Image RESUME = Image.create(Client.ASSETS_LOCATION, "icons/16px/resume.png");
    public static final Image PLAYLIST = Image.create(Client.ASSETS_LOCATION, "icons/16px/playlist.png");
    public static final Image VOLUME = Image.create(Client.ASSETS_LOCATION, "icons/16px/volume.png");
    public static final Image LOOP = Image.create(Client.ASSETS_LOCATION, "music/play/loop.png");
    public static final Image RANDOM = Image.create(Client.ASSETS_LOCATION, "music/play/random.png");
    public static final Image SINGLE = Image.create(Client.ASSETS_LOCATION, "music/play/single.png");
    public static final Image NEXT = Image.create(Client.ASSETS_LOCATION, "music/m_next.png");
    public static final Image PREVIOUS = Image.create(Client.ASSETS_LOCATION, "music/m_previous.png");

    public static final Image WATER_MARK = Image.create(Client.ASSETS_LOCATION, "music/watermark.png");
    public static final Image SYNC_IMAGE = Image.create(Client.ASSETS_LOCATION, "icons/16px/sync.png");
    public static final Image NONE_M = Image.create(Client.ASSETS_LOCATION, "music/none.png");
    public static final Image EMPTY_IMAGE = Image.create(Client.ASSETS_LOCATION, "icons/64px/empty.png");

    private LinearLayout mainLayout;
    private ImageView mLogo;
    private Button mNameButton;
    private ImageView mHeadIcon;

    private TextView qrStatus;
    private ImageView qrImage;
    private String currentQrKey;
    private final MusicSpectrum mSpectrumDrawable = MainGuiFragment.get().mSpectrumDrawable;

    private TextView songName;
    private TextView songAuthor;
    private ImageView songPic;

    private ImageView mPlayButton, mLoopType;

    private MusicPlayer mPlayer;
    private SeekLayout mSeekLayout;
    public static boolean isPlaying = false;

    private TextView infoTitle;
    private RadioGroup tabGroup;
    private ImageButton playlistExpand;
    private int prevSize = 0;

    private Pager mPager;
    private PageShower mShower;

    private final GMusic handler = GMusic.INSTANCE;

    public static final int BUTTON_DARK = new Color(255, 255, 255, 40).getRGB();
    public static final int BUTTON_COLOR = new Color(255, 255, 255, 65).getRGB();
    public static final int BUTTON_BRIGHT = new Color(255, 255, 255, 80).getRGB();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup ____, DataSet savedInstanceState) {
        handler.refreshHead();

        mPlayer = MusicPlayer.getInstance();
        mPlayer.setOnTrackLoadCallback(track -> {
            if (track != null) {
                mPlayer.setAnalyzerCallback(
                    fft -> {
                        fft.setLogAverages(250, 14);
                        fft.setWindowFunc(FFT.NONE);
                    },
                    mSpectrumDrawable::updateAmplitudes
                );
                track.play();
                track.setGain(MathHelper.clamp((float) (handler.volume.get() / 100.0f), 0.0f, 1.0f));
                mPlayButton.setImage(PAUSE);
                isPlaying = true;
            } else {
                mPlayButton.setImage(RESUME);
                isPlaying = false;
                Toast.makeText(requireContext(), "Failed to open audio file", Toast.LENGTH_SHORT).show();
            }
        });

        if (mPlayer.isPlaying()) isPlaying = true;

        mPlayer.setAnalyzerCallback(null, mSpectrumDrawable::updateAmplitudes);

        var main = LayoutBuilder.newLinerBuilder(requireContext());
        mainLayout = main.layout();
        main.vOrientation()
            .gravity(Gravity.CENTER)
            .params()
            .v_match_parent()
            .h_match_parent();

        var base = LayoutBuilder.newLinerBuilder(requireContext());

        int dp700 = base.dp(700);
        int dp500 = base.dp(500);

        base.vOrientation()
            .params()
            .width(dp700)
            .height(dp500);

        {
            var drawable = new ShapeDrawable();
            drawable.setShape(ShapeDrawable.RECTANGLE);
            drawable.setCornerRadius(base.dp(10));
            drawable.setColor(DEFAULT_BG_COLOR);
            base.bg(drawable);
        }

        int dp32 = base.dp(32);
        int dp5 = base.dp(5);
        int dp10 = base.dp(10);
        int dp20 = base.dp(20);
        int dp2 = base.dp(2);
        int dp115 = base.dp(115);
        int dp150 = base.dp(150);
        int dp200 = base.dp(200);
        int dp43 = base.dp(43);

        {
            var titleBar = LayoutBuilder.newLinerBuilder(requireContext());
            titleBar.hOrientation().vGravity(Gravity.CENTER_VERTICAL)
                .params().width(dp700).height(dp43);

            {
                var drawable = new ShapeDrawable();
                drawable.setShape(ShapeDrawable.RECTANGLE);
                drawable.setColor(THEME_BG_COLOR);
                titleBar.bg(drawable);
            }

            var logoLayout = LayoutBuilder.newLinerBuilder(requireContext());
            logoLayout.hOrientation().vGravity(Gravity.CENTER_VERTICAL).hGravity(Gravity.CENTER_HORIZONTAL)
                .params().width(dp150).height(dp43);

            logoLayout.layout().setPadding(dp10, 0, 0, 0);

            {
                var logo = ViewBuilder.wrapLinear(new ImageView(requireContext()));
                this.mLogo = logo.view();
                logo.view().setImage(ICON);
                logo.view().setMaxHeight(32);
                logo.view().setMaxWidth(32);
                logo.params().margin(dp5, dp5, dp5, dp5).width(32).height(32);
                logoLayout.add(logo.build());
            }

            {
                var text = ViewBuilder.wrapLinear(new TextView(requireContext()));
                text.view().setText("网易云音乐");
                text.view().setTextStyle(Paint.BOLD);
                logoLayout.add(text.build());
            }

            titleBar.add(logoLayout.build());

            {
                var middle = LayoutBuilder.newLinerBuilder(requireContext());
                middle.hOrientation().vGravity(Gravity.CENTER_VERTICAL)
                    .params().margin(dp5, dp5, dp5, dp5);

                var page_left = ViewBuilder.wrapLinear(new ImageButton(requireContext()));
                var page_right = ViewBuilder.wrapLinear(new ImageButton(requireContext()));
                page_left.view().setImage(ARROW_LEFT_IMAGE);
                page_right.view().setImage(ARROW_RIGHT_IMAGE);
                var shower = ViewBuilder.wrapLinear(new PageShower(requireContext(), base.dp(6)));
                mShower = shower.view();

                shower.view().setPageCount(2);
                shower.view().setCurrent(0);
                mShower.setPageChangedListener((item) -> {
                    mPager.setCurrentItem(item, true);
                });

                shower.params()
                    .margin(base.dp(5), 0, base.dp(3), 0)
                    .height(base.dp(16))
                    .width(base.dp(4 + 2 * 6 + 4));

                int dp4 = base.dp(4);
                int dp6 = base.dp(6);
                {
                    StateListDrawable background = new StateListDrawable();
                    ShapeDrawable drawable = new ShapeDrawable();
                    drawable.setShape(ShapeDrawable.RECTANGLE);
                    drawable.setColor(BUTTON_COLOR);
                    drawable.setPadding(dp4, dp4, dp4, dp4);
                    drawable.setCornerRadius(base.dp(T_ICON_PTS));
                    background.addState(StateSet.get(StateSet.VIEW_STATE_HOVERED), drawable);
                    //background.addState(new int[]{R.attr.state_checked},drawable);
                    background.setEnterFadeDuration(250);
                    background.setExitFadeDuration(250);
                    page_left.bg(background);
                    page_left.view().setOnClickListener((v) -> shower.view().setCurrent(MathUtil.clamp(shower.view().getCurrentPage() - 1, 0, shower.view().getPageCount() - 1)));
                }
                {
                    StateListDrawable background = new StateListDrawable();
                    ShapeDrawable drawable = new ShapeDrawable();
                    drawable.setShape(ShapeDrawable.RECTANGLE);
                    drawable.setColor(BUTTON_COLOR);
                    drawable.setPadding(dp4, dp4, dp4, dp4);
                    drawable.setCornerRadius(base.dp(T_ICON_PTS));
                    background.addState(StateSet.get(StateSet.VIEW_STATE_HOVERED), drawable);
                    //background.addState(new int[]{R.attr.state_checked},drawable);
                    background.setEnterFadeDuration(250);
                    background.setExitFadeDuration(250);
                    page_right.bg(background);
                    page_right.view().setOnClickListener((v) -> shower.view().setCurrent(MathUtil.clamp(shower.view().getCurrentPage() + 1, 0, shower.view().getPageCount() - 1)));
                }

                int hDp = base.dp(3);
                int vDp = base.dp(5);
                page_left.params().margin(hDp, vDp, hDp, vDp);
                page_right.params().margin(hDp, vDp, 0, vDp);

                middle.add(page_left.build()).add(shower.build()).add(page_right.build());

                var search = LayoutBuilder.newLinerBuilder(requireContext());
                search.hOrientation().vGravity(Gravity.CENTER_VERTICAL);
                search.params().margin(dp20, dp2, dp5, dp2);

                {
                    ShapeDrawable drawable = new ShapeDrawable();
                    drawable.setShape(ShapeDrawable.RECTANGLE);
                    drawable.setColor(BUTTON_DARK);
                    drawable.setPadding(dp2, dp2, dp2, dp2);
                    drawable.setCornerRadius(dp32);
                    search.bg(drawable);
                }
                {
                    var search_icon = ViewBuilder.wrapLinear(new ImageButton(requireContext()));
                    search_icon.view().setImage(SEARCH_IMAGE);
                    {
                        StateListDrawable background = new StateListDrawable();
                        ShapeDrawable drawable = new ShapeDrawable();
                        drawable.setShape(ShapeDrawable.RECTANGLE);
                        drawable.setColor(BUTTON_COLOR);
                        drawable.setPadding(dp4, dp4, dp4, dp4);
                        drawable.setCornerRadius(base.dp(T_ICON_PTS));
                        background.addState(StateSet.get(StateSet.VIEW_STATE_HOVERED), drawable);
                        //background.addState(new int[]{R.attr.state_checked},drawable);
                        background.setEnterFadeDuration(250);
                        background.setExitFadeDuration(250);
                        search_icon.bg(background);
                    }

                    search_icon.params().margin(0, 0, dp5, 0);
                    search.add(search_icon.build());

                    var input_text = ViewBuilder.wrapLinear(new EditText(requireContext()));
                    input_text.view().setHint("原神");
                    input_text.view().setHintTextColor(BUTTON_COLOR);
                    input_text.view().setFilters(new InputFilter.LengthFilter(64));
                    input_text.view().setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                    input_text.view().setMinimumWidth(dp150);
                    input_text.view().setMaxWidth(dp150);
                    input_text.view().setTextSize(12);
                    input_text.view().setTextStyle(Paint.BOLD);
                    input_text.view().setSingleLine();
                    input_text.params().margin(0, dp2, dp2, dp2);
                    search.add(input_text.build());


                    search_icon.view().setOnClickListener(__ -> {
                        var text = input_text.view().getText().toString();
                        search(text);
                    });
                }

                middle.add(search.build());
                titleBar.add(middle.build());

                var music = handler;
                {
                    var right = LayoutBuilder.newLinerBuilder(requireContext());
                    right.hOrientation().vGravity(Gravity.CENTER_VERTICAL);

                    var settings = ViewBuilder.wrapLinear(new ImageButton(requireContext()));
                    var sync = ViewBuilder.wrapLinear(new ImageButton(requireContext()));
                    sync.view().setImage(SYNC_IMAGE);

                    var head = ViewBuilder.wrapLinear(new ImageView(requireContext()));
                    head.view().setMaxWidth(base.dp(25));
                    head.view().setMaxHeight(base.dp(25));
                    head.params().width(base.dp(25)).height(base.dp(25));
                    this.mHeadIcon = head.view();

                    if (music.logged()) {
                        head.view().setImage(music.profileHead);
                    }

                    var nameButton = ViewBuilder.wrapLinear(new Button(requireContext()));
                    nameButton.view().setEllipsize(TextUtils.TruncateAt.END);
                    nameButton.view().setSingleLine();
                    this.mNameButton = nameButton.view();

                    nameButton.view().setOnClickListener((__) -> {
                        if (!handler.logged()) {
                            var layout = switch (handler.loginMode.get()) {
                                case Phone -> createLoginDialog();
                                case QRCode -> {
                                    CompletableFuture.supplyAsync(() -> handler.api.qrCreate(), handler.getExecutor())
                                        .whenCompleteAsync((data, ex) -> {
                                            if (catchException(ex)) return;

                                            if (data == null) {
                                                Toast.makeText(requireContext(), "code:  (未响应) ", Toast.LENGTH_SHORT).show();
                                                this.qrStatus.setText("获取key失败 (未响应) ");
                                                return;
                                            }

                                            if (data.code == 200)
                                                currentQrKey = data.unikey;
                                            else nameButton.view().post(() -> {
                                                Toast.makeText(requireContext(), "code: " + data.code, Toast.LENGTH_SHORT).show();
                                                this.qrStatus.setText("获取key失败,code: " + data.code);
                                            });
                                        }, Core.getUiThreadExecutor());
                                    yield createQrDialog();
                                }
                            };
                            DialogManager.get().setLayout(layout).show();
                        } else DialogManager.get().setLayout(createLogout()).show();
                    });

                    settings.view().setOnClickListener(__ -> {
                        DialogManager.get().setLayout(createSettingDialog()).show();
                    });

                    nameButton.params().margin(dp5, dp5, dp5, dp5).width(base.dp(45)).v_wrap_content();
                    nameButton.view().setTextSize(12);
                    nameButton.view().setMaxWidth(base.dp(45));
                    nameButton.view().setText(music.logged() ? music.myProfile.nickname : "未登录");

                    settings.view().setImage(SETTING_IMAGE);

                    {
                        StateListDrawable background = new StateListDrawable();
                        ShapeDrawable drawable = new ShapeDrawable();
                        drawable.setShape(ShapeDrawable.RECTANGLE);
                        drawable.setColor(BUTTON_COLOR);
                        drawable.setPadding(dp4, dp4, dp4, dp4);
                        drawable.setCornerRadius(base.dp(T_ICON_PTS));
                        background.addState(StateSet.get(StateSet.VIEW_STATE_HOVERED), drawable);
                        //background.addState(new int[]{R.attr.state_checked},drawable);
                        background.setEnterFadeDuration(250);
                        background.setExitFadeDuration(250);
                        settings.bg(background);
                    }

                    {
                        StateListDrawable background = new StateListDrawable();
                        ShapeDrawable drawable = new ShapeDrawable();
                        drawable.setShape(ShapeDrawable.RECTANGLE);
                        drawable.setColor(BUTTON_COLOR);
                        drawable.setPadding(dp4, dp4, dp4, dp4);
                        drawable.setCornerRadius(base.dp(T_ICON_PTS));
                        background.addState(StateSet.get(StateSet.VIEW_STATE_HOVERED), drawable);
                        //background.addState(new int[]{R.attr.state_checked},drawable);
                        background.setEnterFadeDuration(250);
                        background.setExitFadeDuration(250);
                        sync.bg(background);
                    }

                    {
                        StateListDrawable background = new StateListDrawable();
                        ShapeDrawable drawable = new ShapeDrawable();
                        drawable.setShape(ShapeDrawable.RECTANGLE);
                        drawable.setColor(BUTTON_COLOR);
                        drawable.setPadding(dp4, dp4, dp4, dp4);
                        drawable.setCornerRadius(base.dp(T_ICON_PTS));
                        background.addState(StateSet.get(StateSet.VIEW_STATE_HOVERED), drawable);
                        //background.addState(new int[]{R.attr.state_checked},drawable);
                        background.setEnterFadeDuration(250);
                        background.setExitFadeDuration(250);
                        nameButton.bg(background);
                    }

                    sync.view().setOnClickListener(__ -> {
                        handler.refreshAll();
                    });

                    hDp = base.dp(3);
                    vDp = base.dp(5);
                    settings.params().margin(hDp, vDp, hDp, vDp);
                    sync.params().margin(hDp, vDp, hDp, vDp);
                    nameButton.params().margin(hDp, vDp, hDp, vDp);
                    head.params().margin(hDp, vDp, hDp, vDp);

                    right.params().margin(dp115, 0, 0, 0);

                    right.add(head.build());
                    right.add(nameButton.build());
                    right.add(sync.build());
                    right.add(settings.build());

                    titleBar.add(right.build());
                }
            }

            base.add(titleBar.build());

            {
                var middle = LayoutBuilder.newLinerBuilder(requireContext());
                middle.params().h_match_parent().height(base.dp(390));
                middle.hOrientation();

                {
                    var drawable = new ShapeDrawable();
                    drawable.setShape(ShapeDrawable.RECTANGLE);
                    drawable.setColor(DEFAULT_BG_COLOR);
                    middle.bg(drawable);
                }

                {
                    {
                        // H: 180 V:MATCH
                        var tabScroll = ViewBuilder.wrapLinear(new ScrollView(requireContext()));
                        tabScroll.params().v_match_parent().width(base.dp(135));
                        var tab = LayoutBuilder.newLinerBuilder(requireContext());
                        tab.vOrientation().hGravity(Gravity.CENTER_HORIZONTAL);
                        tab.params().v_match_parent().width(base.dp(135));
                        {
                            var drawable = new ShapeDrawable();
                            drawable.setShape(ShapeDrawable.RECTANGLE);
                            drawable.setColor(THEME_BG_COLOR);
                            tab.bg(drawable);
                        }
                        tabScroll.view().addView(tab.build());
                        var group = LayoutBuilder.newRadioGroupBuilder(requireContext());
                        tabGroup = group.layout();
                        {
                            group.params().v_wrap_content().h_match_parent();

                            var homepage = ViewBuilder.wrapLinear(new RadioButton(requireContext()));
                            var recommend = ViewBuilder.wrapLinear(new RadioButton(requireContext()));

                            homepage.view().setText("主页");
                            recommend.view().setText("推荐");

                            homepage.view().setId(5);
                            recommend.view().setId(6);

                            {
                                recommend.view().setTextSize(13);
                                recommend.view().setSingleLine();
                                recommend.view().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                                recommend.params().margin(0, dp5, 0, dp5).h_match_parent();
                                addTabBG(recommend.view());
                            }
                            {
                                homepage.view().setTextSize(13);
                                homepage.view().setSingleLine();
                                homepage.view().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                                homepage.params().margin(0, dp5, 0, dp5).h_match_parent();
                                addTabBG(homepage.view());
                            }

                            group.add(homepage.build()).add(recommend.build());
                            group.params().margin(dp20, 0, dp20, 0);
                        }

                        {
                            var title = ViewBuilder.wrapLinear(new TextView(requireContext()));
                            title.view().setText("我的");
                            title.view().setTextSize(10);
                            title.view().setTextColor(NFONT_COLOR);
                            title.view().setSingleLine();
                            title.view().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                            title.params().margin(0, dp10, 0, 0).h_match_parent();
                            infoTitle = title.view();
                            group.add(title.build());
                            loadUser();
                        }

                        group.onCheck(id -> {
                            switch (id) {
                                case 5 -> {
                                    this.mPager.setLeft(loadHomePage());
                                }
                                case 6 -> {
                                    this.mPager.setLeft(loadRecommend());
                                }
                                default -> {
                                    int playlistIndex = id - PLAYLIST_OFFSET;
                                    this.mPager.setLeft(loadPlaylistContainer(handler.userPlayListData.list.get(playlistIndex)));
                                }
                            }
                        });

                        tab.add(group.build());
                        var scroll = ViewBuilder.wrapLinear(new ScrollView(requireContext()));
                        scroll.params().v_match_parent().h_match_parent();
                        var container = LayoutBuilder.newLinerBuilder(requireContext());
                        container.params().h_match_parent().v_wrap_content();
                        container.vOrientation().hGravity(Gravity.CENTER_HORIZONTAL);

                        scroll.view().addView(container.build());
                        tab.add(scroll.build());
                        middle.add(tabScroll.build());
                    }

                    {
                        var data = LayoutBuilder.newLinerBuilder(requireContext());
                        this.mPager = new Pager(requireContext(), this);
                        var pager = ViewBuilder.wrapLinear(this.mPager);
                        pager.params().margin(dp2, dp2, dp2, dp2);
                        pager.params().h_match_parent().v_wrap_content();
                        data.add(pager.build());
                        middle.add(data.build());
                    }
                }

                base.add(middle.build());
            }

            {
                var bottom = LayoutBuilder.newLinerBuilder(requireContext());
                bottom.params().h_match_parent().height(base.dp(67));

                int margin = base.dp(8);
                int picRadius = base.dp(38);
                int picMargin = base.dp(6);

                var panel = LayoutBuilder.newRelativeBuilder(requireContext());
                panel.params().margin(margin, margin, margin, margin).v_match_parent().h_match_parent();

                {
                    ShapeDrawable drawable = new ShapeDrawable();
                    drawable.setColor(THEME_BG_COLOR);
                    drawable.setShape(ShapeDrawable.RECTANGLE);
                    drawable.setCornerRadius(picMargin);
                    panel.bg(drawable);
                }

                {
                    ShapeDrawable drawable = new ShapeDrawable();
                    drawable.setColor(THEME_BG_COLOR);
                    drawable.setShape(ShapeDrawable.RECTANGLE);
                    bottom.bg(drawable);
                }

                var left = LayoutBuilder.newLinerBuilder(requireContext());
                left.hOrientation().vGravity(Gravity.CENTER_VERTICAL);

                var pic = ViewBuilder.wrapLinear(new ImageButton(requireContext()));
                pic.view().setImage(NONE_M);
                pic.params().margin(picMargin, picMargin, picMargin, picMargin).width(picRadius).height(picRadius);

                songPic = pic.view();

                left.add(pic.build());
                {
                    var max = base.dp(120);
                    var info = LayoutBuilder.newLinerBuilder(requireContext());
                    info.params().v_wrap_content().width(max);
                    info.params().margin(base.dp(2), 0, 0, 0);
                    info.vOrientation();
                    var name = ViewBuilder.wrapLinear(new TextView(requireContext()));
                    name.view().setText("无");
                    name.view().setMaxWidth(max);
                    var author = ViewBuilder.wrapLinear(new TextView(requireContext()));
                    author.view().setText("无");
                    author.view().setMaxWidth(max);
                    songName = name.view();
                    songAuthor = author.view();
                    name.view().setTextSize(13);
                    author.view().setTextSize(11);
                    name.view().setEllipsize(TextUtils.TruncateAt.END);
                    author.view().setEllipsize(TextUtils.TruncateAt.END);
                    author.view().setSingleLine();
                    name.view().setSingleLine();
                    info.add(name.build()).add(author.build());
                    left.add(info.build());
                }

                {
                    var params = left.relative_params().left().vCenter().v_match_parent().h_wrap_content();
                    panel.add(left.build(params));
                }

                var control = LayoutBuilder.newLinerBuilder(requireContext());
                control.vOrientation().hGravity(Gravity.CENTER_HORIZONTAL).vGravity(Gravity.CENTER_VERTICAL);
                var c_top = LayoutBuilder.newLinerBuilder(requireContext());
                var c_bottom = LayoutBuilder.newLinerBuilder(requireContext());
                c_top.hOrientation().gravity(Gravity.CENTER);
                c_bottom.hOrientation().gravity(Gravity.CENTER);

                {
                    // TOp
                    var loopType = ViewBuilder.wrapLinear(new ImageButton(requireContext()));
                    this.mLoopType = loopType.view();
                    loopType.view().setImage(getLoopTypeImg(handler.loopType.get()));
                    loopType.view().setTooltipText(getLoopTooltip(handler.loopType.get()));
                    loopType.view().setOnClickListener(__ -> {
                        handler.nextLoopType();
                        loopType.view().post(() -> {
                            loopType.view().setImage(getLoopTypeImg(handler.loopType.get()));
                            loopType.view().setTooltipText(getLoopTooltip(handler.loopType.get()));
                        });

                    });
                    createDrawable(loopType);
                    int hDp = base.dp(10);

                    loopType.params().margin(hDp, 0, hDp, 0);
                    c_top.add(loopType.build());

                    var prev = ViewBuilder.wrapLinear(new ImageButton(requireContext()));
                    var next = ViewBuilder.wrapLinear(new ImageButton(requireContext()));

                    prev.view().setOnClickListener(__ -> {
                        handler.prev(this::catchException);
                        songName.setText("加载中。.");
                    });
                    next.view().setOnClickListener(__ -> {
                        handler.next(this::catchException);
                        songName.setText("加载中。.");
                    });

                    var play = ViewBuilder.wrapLinear(new ImageButton(requireContext()));
                    this.mPlayButton = play.view();
                    play.view().setImage(isPlaying ? PAUSE : RESUME);
                    prev.view().setImage(PREVIOUS);
                    next.view().setImage(NEXT);
                    createDrawable(prev);
                    createDrawable(next);
                    createDrawable(play);
                    prev.params().margin(hDp, 0, hDp, 0);
                    next.params().margin(hDp, 0, hDp, 0);
                    play.params().margin(hDp, 0, hDp, 0);
                    play.view().setOnClickListener(__ -> {
                        MusicPlayer player = MusicPlayer.getInstance();
                        if (player.hasTrack()) {
                            if (isPlaying) player.pause();
                            else player.play();

                            isPlaying = !isPlaying;
                        } else isPlaying = false;
                        play.view().post(() -> play.view().setImage(isPlaying ? PAUSE : RESUME));
                    });
                    c_top.add(prev.build()).add(play.build()).add(next.build());
                }

                {
                    var seek = ViewBuilder.wrapLinear(new SeekLayout(requireContext(), 10));
                    this.mSeekLayout = seek.view();
                    mSeekLayout.mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        boolean mPlaying;

                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            if (fromUser) {
                                float fraction = progress / 10000.0f;
                                float length = mPlayer.getTrackLength();
                                mSeekLayout.mMinText.setText(formatTime((int) (fraction * length)));
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                            mPlaying = mPlayer.isPlaying();
                            if (mPlaying) {
                                mPlayer.pause();
                            }
                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            mPlayer.seek(seekBar.getProgress() / 10000.0f);
                            if (mPlaying) {
                                mPlayer.play();
                            }
                        }
                    });
                    seek.params().width(base.dp(320)).v_wrap_content();
                    c_bottom.add(seek.build());
                }

                control.add(c_top.build());
                control.add(c_bottom.build());

                {
                    var params = control.relative_params().center().width(base.dp(300)).v_match_parent();
                    panel.add(control.build(params));
                }

                var right = LayoutBuilder.newLinerBuilder(requireContext());
                right.hOrientation().vGravity(Gravity.CENTER);
                var volume = LayoutBuilder.newLinerBuilder(requireContext());
                volume.hOrientation().vGravity(Gravity.CENTER);
                {
                    var seek = ViewBuilder.wrapLinear(new SeekBar(requireContext()));
                    seek.params().width(base.dp(100)).v_wrap_content();
                    seek.view().setMax(10000);
                    seek.view().setClickable(true);
                    seek.view().setProgress((int) Math.round(MathHelper.clamp((handler.volume.get() / 100.0f), 0.0f, 1.0f) * 10000));
                    seek.view().setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            mPlayer.setGain(progress / 10000.0f);
                            handler.volume.set(MathHelper.clamp(mPlayer.getGain() * 100.0, 0.0, 100.0));
                        }
                    });

                    var img = ViewBuilder.wrapLinear(new ImageButton(requireContext()));
                    img.view().setImage(VOLUME);
                    createDrawable(img);

                    img.view().setOnClickListener(__ -> {
                        if (seek.view().getProgress() != 0)
                            seek.view().setProgress(0);
                        else seek.view().setProgress(10000);
                    });

                    img.params().margin(0, 0, dp5, 0);
                    volume.add(img.build()).add(seek.build());
                }

                volume.params().margin(dp5, dp5, dp5, dp5);
                right.add(volume.build());

                var pl = ViewBuilder.wrapLinear(new ImageButton(requireContext()));
                pl.view().setImage(PLAYLIST);
                pl.params().margin(dp5, dp5, dp5, dp5);
                pl.view().setOnClickListener((__) -> playlist());
                createDrawable(pl);
                right.add(pl.build());

                {
                    var params = right.relative_params().vCenter().parent_mode().end().right().v_match_parent().h_wrap_content();
                    panel.add(right.build(params));
                }

                bottom.add(panel.build());
                base.add(bottom.build());
            }
        }

        main.add(base.build());
        main.layout().postDelayed(this::onUpdate, 50);
        return main.build();
    }

    private Image getLoopTypeImg(int type) {
        return switch (type) {
            case 0 -> LOOP;
            case 1 -> RANDOM;
            case 2 -> SINGLE;
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    private String getLoopTooltip(int type) {
        return switch (type) {
            case 0 -> "列表循环";
            case 1 -> "随机播放";
            case 2 -> "单曲循环";
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    private void createDrawable(ViewBuilder view) {
        StateListDrawable background = new StateListDrawable();
        ShapeDrawable drawable = new ShapeDrawable();
        drawable.setShape(ShapeDrawable.RECTANGLE);
        drawable.setColor(BUTTON_COLOR);

        int dp5 = view.view().dp(5);
        drawable.setPadding(dp5, dp5, dp5, dp5);
        drawable.setCornerRadius(view.view().dp(T_ICON_PTS));
        background.addState(StateSet.get(StateSet.VIEW_STATE_HOVERED), drawable);
        //background.addState(new int[]{R.attr.state_checked},drawable);
        background.setEnterFadeDuration(250);
        background.setExitFadeDuration(250);
        view.bg(background);
    }

    private void loadUser() {
        if (handler.logged()) {
            infoTitle.setText("我的");

            View view;
            if ((view = tabGroup.findViewById(7)) != null) {
                tabGroup.removeView(view);
            }

            if ((view = tabGroup.findViewById(8)) != null) {
                tabGroup.removeView(view);
            }

            //tabGroup.addView(like.build());

            var expandLayout = LayoutBuilder.newLinerBuilder(requireContext());
            expandLayout.hOrientation().vGravity(Gravity.CENTER_VERTICAL);
            expandLayout.params().h_match_parent();
            expandLayout.layout().setId(8);

            var expand = ViewBuilder.wrapLinear(new ImageButton(requireContext()));
            int dp5 = expand.dp(5);
            this.playlistExpand = expand.view();
            expand.view().setImage(ARROW_RIGHT_IMAGE);
            expand.params().margin(dp5, 0, 0, 0);
            expand.view().setRotation(handler.playlistExpand.get() ? 90 : 0);
            var arrow_o2f_Animator = ObjectAnimator.ofFloat(expand.view(),
                View.ROTATION, 90, 0);
            var arrow_f2o_Animator = ObjectAnimator.ofFloat(expand.view(),
                View.ROTATION, 0, 90);
            arrow_o2f_Animator.setInterpolator(TimeInterpolator.DECELERATE_QUINTIC);
            arrow_f2o_Animator.setInterpolator(TimeInterpolator.DECELERATE_QUINTIC);

            expand.view().setOnClickListener((__) -> {
                if (handler.playlistExpand.get()) {
                    arrow_o2f_Animator.start();
                    loadUserPlaylist(false);
                }

                handler.playlistExpand.set(!handler.playlistExpand.get());

                if (handler.playlistExpand.get()) {
                    if (arrow_o2f_Animator.isRunning()) {
                        arrow_o2f_Animator.cancel();
                    }
                    tabGroup.post(() ->
                        {
                            loadUserPlaylist(true);
                        }
                    );
                    arrow_f2o_Animator.start();
                }

                expand.view().setRotation(handler.playlistExpand.get() ? 90 : 0);
            });

            {
                StateListDrawable background = new StateListDrawable();
                ShapeDrawable drawable = new ShapeDrawable();
                drawable.setShape(ShapeDrawable.RECTANGLE);
                drawable.setColor(BUTTON_COLOR);
                int dp4 = tabGroup.dp(4);
                drawable.setPadding(dp4, dp4, dp4, dp4);
                drawable.setCornerRadius(tabGroup.dp(T_ICON_PTS));
                background.addState(StateSet.get(StateSet.VIEW_STATE_HOVERED), drawable);
                //background.addState(new int[]{R.attr.state_checked},drawable);
                background.setEnterFadeDuration(250);
                background.setExitFadeDuration(250);
                expand.bg(background);
            }

            var title = ViewBuilder.wrapLinear(new TextView(requireContext()));
            title.view().setText("收藏的歌单");
            title.view().setTextSize(10);
            title.view().setTextColor(NFONT_COLOR);
            title.view().setSingleLine();
            title.view().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            title.params().margin(0, 0, tabGroup.dp(10), 0);

            expandLayout.add(title.build()).add(expand.build());
            tabGroup.addView(expandLayout.build());

            if (handler.playlistExpand.get()) {
                loadUserPlaylist(true);
            }
        } else {
            infoTitle.setText("未登录");

            View view;
            if ((view = tabGroup.findViewById(7)) != null) {
                tabGroup.removeView(view);
            }

            if ((view = tabGroup.findViewById(8)) != null) {
                tabGroup.removeView(view);
            }

            loadUserPlaylist(false);
        }
    }

    private void loadUserPlaylist(boolean load) {
        if (load) {
            View view;
            if ((view = tabGroup.findViewById(9)) != null) {
                tabGroup.removeView(view);
            }

            if (handler.userPlayListData != null && prevSize != 0) {
                for (int i = 0; i < prevSize; i++) {
                    if ((view = tabGroup.findViewById(PLAYLIST_OFFSET + i)) != null) {
                        tabGroup.removeView(view);
                    }
                }
            }

            var myPlayList = LayoutBuilder.newLinerBuilder(requireContext());
            myPlayList.layout().setId(9);
            myPlayList.hOrientation().vGravity(Gravity.CENTER_VERTICAL);
            myPlayList.params().h_match_parent().v_wrap_content();

            if (handler.playlistExpand.get()) {
                if (handler.userPlayListData != null) {
                    int id = 0;
                    prevSize = handler.userPlayListData.list.size();
                    for (dev.undefinedteam.gensh1n.music.api.objs.PlayList playList : handler.userPlayListData.list) {
                        var button = ViewBuilder.wrapLinear(new RadioButton(requireContext()));
                        int dp5 = button.view().dp(5);
                        button.view().setText(playList.name);
                        button.view().setId(PLAYLIST_OFFSET + id);
                        {
                            button.view().setTextSize(10);
                            button.view().setEllipsize(TextUtils.TruncateAt.END);
                            button.view().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                            button.view().setMaxHeight(myPlayList.dp(40));
                            button.params().margin(0, dp5, 0, dp5).h_match_parent();
                            addTabBG(button.view());
                        }

                        tabGroup.addView(button.build());
                        id++;
                    }
                }
            }
        } else {
            View view;
            if ((view = tabGroup.findViewById(9)) != null) {
                tabGroup.removeView(view);
            }

            if (handler.userPlayListData != null && prevSize != 0) {
                for (int i = 0; i < prevSize; i++) {
                    if ((view = tabGroup.findViewById(PLAYLIST_OFFSET + i)) != null) {
                        tabGroup.removeView(view);
                    }
                }
            }
        }
    }

    private void onUpdate() {
        var music = handler;
        if (music.shouldRefresh) {
            this.mHeadIcon.setImage(music.profileHead);

            if (music.myProfile != null) this.mNameButton.setText(music.myProfile.nickname);
            else this.mNameButton.setText("未登录");

            loadUser();

            Toast.makeText(ModernUI.getInstance(), "已刷新Profile", Toast.LENGTH_SHORT).show();
            music.shouldRefresh = false;
        }

        if (mPlayer.hasTrack() && isPlaying) {
            this.mPlayButton.setImage(PAUSE);
        }

        if (music.current != null) {
            this.songName.setText(music.current.name);
            this.songAuthor.setText(music.current.author());
            if (music.currentSImageRound != null) {
                this.songPic.setImage(music.currentSImageRound);
            } else this.songPic.setImage(NONE_M);
        }

        if (mPager != null) {
            mPager.onUpdate();
        }

        if (mPlayer.isPlaying()) {
            float time = mPlayer.getTrackTime();
            float length = mPlayer.getTrackLength();

            var min = formatTime((int) time);
            var max = formatTime((int) length);
            mSeekLayout.mMinText.setText(min);
            mSeekLayout.mSeekBar.setProgress((int) (time / length * 10000));
            mSeekLayout.mMaxText.setText(max);
        }

        if (mPlayer.hasTrack() && isPlaying) {
            float time = mPlayer.getTrackTime();
            float length = mPlayer.getTrackLength();
            var min = formatTime((int) time);
            var max = formatTime((int) length);
            if (min.equals(max)) {
                isPlaying = false;
                this.mPlayButton.setImage(RESUME);
                mPlayer.clearTrack();
                handler.loop(this::catchException);
            }
        }

        mLogo.setRotation((mLogo.getRotation() + 5) % 360);
        mainLayout.postDelayed(this::onUpdate, 50);
    }

    public static String formatTime(int seconds) {
        int minutes = seconds / 60;
        seconds -= minutes * 60;
        int hours = minutes / 60;
        minutes -= hours * 60;
        return String.format("%d:%02d:%02d", hours, minutes, seconds);
    }

    private LinearLayout createSettingDialog() {
        var base = LayoutBuilder.newLinerBuilder(requireContext());
        base.vOrientation().params().h_match_parent().v_match_parent();
        base.hGravity(Gravity.CENTER_HORIZONTAL);

        var scroll = ViewBuilder.wrapLinear(new ScrollView(requireContext()));
        scroll.params().h_match_parent().height(base.dp(400));
        var settings = LayoutBuilder.newLinerBuilder(requireContext());
        settings.vOrientation().hGravity(Gravity.CENTER_HORIZONTAL);

        var group = handler.sgDefault;

        if (group.settings.isEmpty()) {
            var empty = getEmpty(base, requireContext());
            empty.params().margin(base.dp(5), base.dp(5), base.dp(5), base.dp(5)).v_wrap_content().h_wrap_content();
            settings.add(empty.build());
        } else {
            int id = 0;
            for (Setting<?> setting : group.settings) {
                setting.blackColor = true;
                View view = setting.createView(requireContext(), settings.layout());
                if (view != null) {
                    view.setId(id + (114 * 514));
                    settings.add(view);
                    view.post(() -> setting.checkVisible0(requireContext(), view));
                    id++;
                }
                setting.blackColor = false;
            }
        }

        settings.params()
            .margin(base.dp(5), base.dp(5), base.dp(3), base.dp(5))
            .h_match_parent().v_wrap_content();

        scroll.view().addView(settings.build());
        base.add(scroll.build());
        return createWindow("设置", 300, WRAP_CONTENT, base).build();
    }

    private LayoutBuilder.LinearLayoutBuilder<LinearLayout> getEmpty(LayoutBuilder.LinearLayoutBuilder<LinearLayout> base, Context context) {
        var empty = LayoutBuilder.newLinerBuilder(context);
        empty.vOrientation().hGravity(Gravity.CENTER).vGravity(Gravity.CENTER);

        var img = ViewBuilder.wrapLinear(new ImageView(context));
        var txt = ViewBuilder.wrapLinear(new TextView(context));

        img.view().setImage(EMPTY_IMAGE);
        img.params().margin(base.dp(5), base.dp(5), base.dp(5), base.dp(10));
        txt.view().setText("哎哟我去 啥都没有 \uD83D\uDE05\uD83D\uDE05\uD83D\uDE05");
        txt.view().setTextColor(FONT_COLOR);

        empty.add(img.build(), 0)
            .add(txt.build(), 1);
        return empty;
    }

    private LinearLayout createLogout() {
        var base = LayoutBuilder.newLinerBuilder(requireContext());
        base.vOrientation().params().h_match_parent().v_match_parent();
        base.hGravity(Gravity.CENTER_HORIZONTAL);

        var text = ViewBuilder.wrapLinear(new TextView(requireContext()));
        text.view().setTextSize(20);
        text.view().setText("登出 ?");
        text.view().setTextColor(FONT_COLOR);

        base.add(text.build());

        var button = ViewBuilder.wrapLinear(new Button(requireContext()));
        button.view().setText("确定");
        button.view().setTextColor(GMainGui.FONT_COLOR);
        button.params().width(base.dp(60)).height(base.dp(25));
        button.view().setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        button.view().setOnClickListener(__ -> {
            if (handler.logout()) {
                DialogManager.get().hidden();
            }
        });

        {
            ShapeDrawable drawable = new ShapeDrawable();
            drawable.setShape(ShapeDrawable.RECTANGLE);
            drawable.setCornerRadius(base.dp(7));
            drawable.setColor(new Color(240, 0, 0, 255).getRGB());
            button.bg(drawable);
        }

        base.add(button.build());

        return createWindow("退出登录", 300, 100, base).build();
    }

    private LinearLayout createQrDialog() {
        var base = LayoutBuilder.newLinerBuilder(requireContext());
        base.vOrientation().params().h_match_parent().v_match_parent();
        base.hGravity(Gravity.CENTER_HORIZONTAL);

        var qrcode = ViewBuilder.wrapLinear(new ImageView(requireContext()));
        var status = ViewBuilder.wrapLinear(new TextView(requireContext()));

        this.qrImage = qrcode.view();

        int dp5 = base.dp(5);
        qrcode.params().margin(dp5, dp5, dp5, dp5);
        status.params().margin(dp5, dp5, dp5, dp5);

        var api = handler.api;

        try {
            if (currentQrKey != null) {
                var qr = api.genQrCode(this.currentQrKey);
                var img = Image.createTextureFromBitmap(BitmapFactory.decodeByteArray(qr, 0, qr.length));
                qrcode.view().setImage(img);
            } else qrcode.view().postDelayed(this::loadQrCode, 50);

            base.add(qrcode.build());
        } catch (IOException e) {
            status.view().setText("你毁了，qrcode加载失败了");
        }

        status.view().setTextColor(FONT_COLOR);

        this.qrStatus = status.view();
        base.add(status.build());
        status.view().setText("请使用 '网易云音乐APP' 扫码登录");
        status.view().postDelayed(this::updateQr, 1000);
        return createWindow("登录", 300, 240, base).build();
    }

    private void loadQrCode() {
        boolean skip = false;
        if (this.currentQrKey == null) {
            qrStatus.setText("加载中...");
        } else {
            var api = handler.api;
            var qr = api.genQrCode(this.currentQrKey);
            try {
                var img = Image.createTextureFromBitmap(BitmapFactory.decodeByteArray(qr, 0, qr.length));
                qrImage.setImage(img);
                qrStatus.setText("请使用 '网易云音乐APP' 扫码登录");
                skip = true;
            } catch (IOException e) {
                qrStatus.setText("你毁了，qrcode加载失败了");
            }
        }

        if (!skip) {
            this.qrImage.postDelayed(this::loadQrCode, 50);
        }
    }

    private void updateQr() {
        AtomicBoolean hidden = new AtomicBoolean(false);
        if (this.currentQrKey != null) {
            var api = handler.api;

            CompletableFuture.supplyAsync(() -> api.qrCheck(this.currentQrKey), handler.getExecutor())
                .whenCompleteAsync((status, ex) -> {
                    if (!catchException(ex) && status != null) {
                        qrStatus.post(() -> {
                            this.qrStatus.setText(status.message);
                            if (status.completed()) {
                                currentQrKey = null;
                                handler.refreshAll();
                                DialogManager.get().hidden();
                                hidden.set(true);
                            }
                        });
                    }
                }, Core.getUiThreadExecutor());
        }

        if (!hidden.get()) {
            this.qrStatus.postDelayed(this::updateQr, 1000);
        }
    }

    private LinearLayout createLoginDialog() {
        var base = LayoutBuilder.newLinerBuilder(requireContext());
        base.vOrientation().params().h_match_parent().v_match_parent();
        base.hGravity(Gravity.CENTER);

        var watermark = ViewBuilder.wrapLinear(new ImageView(requireContext()));
        watermark.view().setImage(WATER_MARK);

        watermark.params().margin(0, 0, 0, base.dp(20));

        base.add(watermark.build());

        var phone = ViewBuilder.wrapLinear(new EditText(requireContext()));
        var captchaLayout = LayoutBuilder.newLinerBuilder(requireContext());

        var captcha = ViewBuilder.wrapLinear(new EditText(requireContext()));
        var login = ViewBuilder.wrapLinear(new Button(requireContext()));
        var c_send = ViewBuilder.wrapLinear(new Button(requireContext()));

        phone.params().width(base.dp(193)).height(base.dp(27));

        captchaLayout.params().width(base.dp(193)).height(base.dp(27));

        captcha.params().weight(1).width(base.dp(150)).height(base.dp(27));
        login.params().width(base.dp(193)).height(base.dp(27));

        int vMargin = base.dp(4);
        int hMargin = base.dp(5);

        captchaLayout.hOrientation().gravity(Gravity.START | Gravity.CENTER_VERTICAL);
        captchaLayout.add(captcha.build()).add(c_send.build());

        login.view().setText("登录");
        c_send.view().setText("发送");

        phone.params().margin(hMargin, vMargin, hMargin, vMargin);
        captchaLayout.params().margin(hMargin, vMargin, hMargin, vMargin);
        login.params().margin(hMargin, 4 * vMargin, hMargin, vMargin);
        c_send.params().margin(hMargin, vMargin, 0, 0);

        phone.view().setTextSize(12);
        captcha.view().setTextSize(12);
        login.view().setTextSize(12);
        c_send.view().setTextSize(12);

        phone.view().setTextStyle(Paint.BOLD);
        captcha.view().setTextStyle(Paint.BOLD);

        phone.view().setSingleLine();
        captcha.view().setSingleLine();

        login.view().setTextStyle(Paint.BOLD);
        c_send.view().setTextStyle(Paint.BOLD);

        phone.view().setTextColor(FONT_COLOR);
        captcha.view().setTextColor(FONT_COLOR);
        login.view().setTextColor(GMainGui.FONT_COLOR);
        c_send.view().setTextColor(FONT_COLOR);

        phone.view().setFilters(DigitsInputFilter.getInstance("1234567890"));
        captcha.view().setFilters(DigitsInputFilter.getInstance("1234567890"));

        phone.view().setOnKeyListener((__, __1, __2) -> {
            c_send.view().setEnabled(phone.view().getText().toString().length() == 11);
            c_send.view().setAlpha((c_send.view().isEnabled() ? 255 : 80) / 255.0f);
            return false;
        });

        c_send.view().setEnabled(phone.view().getText().toString().length() == 11);
        c_send.view().setAlpha((c_send.view().isEnabled() ? 255 : 80) / 255.0f);

        c_send.view().setOnClickListener(__ -> {
            var api = handler.api;
            var status = api.sendCaptcha(phone.view().getText().toString());

            if (status != null && status.code == 200) {
                Toast.makeText(requireContext(), StringUtils.getReplaced("code: {} , ({})", status.code, status.data), Toast.LENGTH_SHORT).show();
            }
        });

        login.view().setOnClickListener(__ -> {
            if (captcha.view().getText().toString().isEmpty()) {
                Toast.makeText(requireContext(), "请输入验证码", Toast.LENGTH_SHORT).show();
                return;
            }
            var api = handler.api;
            var data = api.phoneLogin(phone.view().getText().toString(), captcha.view().getText().toString());

            if (data.code == 200) {
                handler.refreshAll();
                DialogManager.get().hidden();
            } else if (data.message != null) {
                Toast.makeText(requireContext(), data.message, Toast.LENGTH_SHORT).show();
            }
        });

        {
            StateListDrawable background = new StateListDrawable();
            ShapeDrawable drawable = new ShapeDrawable();
            drawable.setShape(ShapeDrawable.RECTANGLE);
            drawable.setColor(DARK_COLOR);
            int dp4 = base.dp(4);
            drawable.setPadding(dp4, dp4, dp4, dp4);
            drawable.setCornerRadius(base.dp(T_ICON_PTS));
            background.addState(StateSet.get(StateSet.VIEW_STATE_HOVERED), drawable);
            //background.addState(new int[]{R.attr.state_checked},drawable);
            background.setEnterFadeDuration(250);
            background.setExitFadeDuration(250);
            c_send.bg(background);
        }

        {
            ShapeDrawable drawable = new ShapeDrawable();
            drawable.setShape(ShapeDrawable.RECTANGLE);
            drawable.setColor(EDITTEXT_IN_COLOR);
            drawable.setPadding(base.dp(7), base.dp(5), base.dp(5), base.dp(5));
            drawable.setStroke(base.dp(1), EDITTEXT_EDGE_COLOR);
            drawable.setCornerRadius(base.dp(T_ICON_PTS));
            phone.bg(drawable);
        }

        {
            ShapeDrawable drawable = new ShapeDrawable();
            drawable.setShape(ShapeDrawable.RECTANGLE);
            drawable.setColor(EDITTEXT_IN_COLOR);
            drawable.setPadding(base.dp(7), base.dp(5), base.dp(5), base.dp(5));
            drawable.setStroke(base.dp(1), EDITTEXT_EDGE_COLOR);
            drawable.setCornerRadius(base.dp(T_ICON_PTS));
            captcha.bg(drawable);
        }

        {
            StateListDrawable bg = new StateListDrawable();
            ShapeDrawable drawable = new ShapeDrawable();
            ShapeDrawable drawable1 = new ShapeDrawable();
            drawable.setShape(ShapeDrawable.RECTANGLE);
            drawable.setColor(new Color(255, 0, 0, 255).getRGB());
            drawable.setPadding(base.dp(7), base.dp(5), base.dp(5), base.dp(5));
            drawable.setStroke(base.dp(1), EDITTEXT_EDGE_COLOR);
            drawable.setCornerRadius(base.dp(T_ICON_PTS));

            drawable1.setColor(new Color(255, 0, 0, 200).getRGB());
            drawable1.setPadding(base.dp(7), base.dp(5), base.dp(5), base.dp(5));
            drawable1.setStroke(base.dp(1), EDITTEXT_EDGE_COLOR);
            drawable1.setCornerRadius(base.dp(T_ICON_PTS));

            bg.addState(StateSet.get(StateSet.VIEW_STATE_HOVERED), drawable1);
            bg.addState(StateSet.get(StateSet.VIEW_STATE_ENABLED), drawable);

            bg.setEnterFadeDuration(100);
            bg.setExitFadeDuration(100);
            login.bg(bg);
        }

        base.add(phone.build()).add(captchaLayout.build()).add(login.build());
        return createWindow("登录", 300, 240, base).build();
    }

    private void playlist() {
        var playlist = handler.playList;
        if (playlist.songs.isEmpty()) {
            this.mPager.setLeft(null);
            Toast.makeText(requireContext(), "列表没有歌曲", Toast.LENGTH_SHORT).show();
            return;
        }

        var base = LayoutBuilder.newLinerBuilder(requireContext());
        base.vOrientation().hGravity(Gravity.CENTER_HORIZONTAL);

        int dp10 = base.dp(10);

        base.params().margin(dp10, 0, dp10, 0).h_match_parent().v_wrap_content();

        {
            var title = LayoutBuilder.newLinerBuilder(requireContext());
            title.hOrientation().vGravity(Gravity.CENTER_VERTICAL);
            title.params().h_match_parent().v_wrap_content();

            var id = ViewBuilder.wrapLinear(new TextView(requireContext()));
            var author = ViewBuilder.wrapLinear(new TextView(requireContext()));
            var time = ViewBuilder.wrapLinear(new TextView(requireContext()));

            id.params().width(title.dp(250)).v_match_parent();
            author.params().width(title.dp(150)).v_match_parent();
            time.params().width(title.dp(75)).v_match_parent();

            id.view().setText("标题");
            id.view().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

            author.view().setText("作者");
            author.view().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

            time.view().setText("时长");
            time.view().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

            title.add(id.build()).add(author.build()).add(time.build());
            base.add(title.build());
        }

        {
            var songs = LayoutBuilder.newLinerBuilder(requireContext());
            songs.vOrientation().hGravity(Gravity.CENTER_HORIZONTAL);
            songs.params().h_match_parent().v_wrap_content();

            var status = ViewBuilder.wrapLinear(new TextView(requireContext()));
            status.view().setText("加载中");

            songs.add(status.build(), 0);

            var api = handler.api;
            songs.layout().removeViewAt(0);
            for (var song : playlist.songs) {
                var src = LayoutBuilder.newLinerBuilder(requireContext());
                src.hOrientation().vGravity(Gravity.CENTER_VERTICAL).params().h_match_parent().v_wrap_content();
                {
                    var id = ViewBuilder.wrapLinear(new TextView(requireContext()));
                    var author = ViewBuilder.wrapLinear(new TextView(requireContext()));
                    var time = ViewBuilder.wrapLinear(new TextView(requireContext()));
                    var del = ViewBuilder.wrapLinear(new ImageButton(requireContext()));

                    createDrawable(del);
                    del.view().setImage(DELETE);
                    del.view().setOnClickListener((__) -> {
                        handler.removeList(song);
                        base.layout().postDelayed(this::playlist, 100);
                    });

                    id.params().width(src.dp(250)).v_match_parent();
                    author.params().width(src.dp(150)).v_match_parent();
                    time.params().width(src.dp(75)).v_match_parent();

                    id.view().setText(song.name);
                    id.view().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

                    author.view().setText(song.author);
                    author.view().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

                    time.view().setText(formatTime((int) (song.duration / 1000)));

                    id.view().setEllipsize(TextUtils.TruncateAt.END);
                    author.view().setEllipsize(TextUtils.TruncateAt.END);
                    time.view().setEllipsize(TextUtils.TruncateAt.END);

                    id.view().setSingleLine();
                    author.view().setSingleLine();
                    time.view().setSingleLine();

                    src.add(id.build()).add(author.build()).add(time.build()).add(del.build());

                    id.view().setOnClickListener(__ -> {
                        songName.setText("加载中。。。");

                        CompletableFuture.supplyAsync(() -> handler.api.songUrl(song.id), handler.getExecutor())
                            .whenCompleteAsync((url, ex) -> {
                                if (!catchException(ex) && url != null && url.code == 200 && url.data != null && url.first() != null && url.first().url != null) {
                                    mPlayer.replaceTrack(song.name + " - " + song.author, url.first().url);
                                    GMusic.setCurrent(song.id);
                                    GMusic.setCurrentUrl(url.first());
                                }
                            }, Core.getUiThreadExecutor());
                    });
                }

                {
                    ShapeDrawable drawable = new ShapeDrawable();
                    drawable.setShape(ShapeDrawable.RECTANGLE);
                    drawable.setColor(THEME_BG_COLOR);
                    int dp4 = base.dp(8);
                    drawable.setPadding(dp4, dp4, dp4, dp4);
                    drawable.setCornerRadius(base.dp(15));
                    src.bg(drawable);
                }

                int dp5 = base.dp(5);
                src.params().margin(dp5, dp5, dp5, dp5);
                songs.add(src.build());
            }

            base.add(songs.build());
        }

        this.mPager.setLeft(base.build());
    }

    private void search(String keywords) {
        if (keywords.isEmpty()) return;

        var base = LayoutBuilder.newLinerBuilder(requireContext());
        base.vOrientation().hGravity(Gravity.CENTER_HORIZONTAL);

        int dp10 = base.dp(10);

        base.params().margin(dp10, 0, dp10, 0).h_match_parent().v_wrap_content();

        {
            var title = LayoutBuilder.newLinerBuilder(requireContext());
            title.hOrientation().vGravity(Gravity.CENTER_VERTICAL);
            title.params().h_match_parent().v_wrap_content();

            var id = ViewBuilder.wrapLinear(new TextView(requireContext()));
            var author = ViewBuilder.wrapLinear(new TextView(requireContext()));
            var time = ViewBuilder.wrapLinear(new TextView(requireContext()));

            id.params().width(title.dp(250)).v_match_parent();
            author.params().width(title.dp(150)).v_match_parent();
            time.params().width(title.dp(75)).v_match_parent();

            id.view().setText("标题");
            id.view().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

            author.view().setText("作者");
            author.view().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

            time.view().setText("时长");
            time.view().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

            title.add(id.build()).add(author.build()).add(time.build());
            base.add(title.build());
        }

        {
            var songs = LayoutBuilder.newLinerBuilder(requireContext());
            songs.vOrientation().hGravity(Gravity.CENTER_HORIZONTAL);
            songs.params().h_match_parent().v_wrap_content();

            var status = ViewBuilder.wrapLinear(new TextView(requireContext()));
            status.view().setText("加载中");

            songs.add(status.build(), 0);

            var api = handler.api;
            CompletableFuture.supplyAsync(() -> (SearchData.SingleData) api.search(keywords, 30, 0, SearchType.SINGLE), handler.getExecutor())
                .whenCompleteAsync((data, ex) -> {
                    if (!catchException(ex) && data != null && data.code == 200 && data.songs != null && data.songs.songs != null) {
                        this.mPager.post(() -> {
                            songs.layout().removeViewAt(0);
                            for (Song song : data.songs.songs) {
                                var src = LayoutBuilder.newLinerBuilder(requireContext());
                                src.hOrientation().vGravity(Gravity.CENTER_VERTICAL).params().h_match_parent().v_wrap_content();
                                {
                                    var id = ViewBuilder.wrapLinear(new TextView(requireContext()));
                                    var author = ViewBuilder.wrapLinear(new TextView(requireContext()));
                                    var time = ViewBuilder.wrapLinear(new TextView(requireContext()));
                                    var add = ViewBuilder.wrapLinear(new ImageButton(requireContext()));

                                    createDrawable(add);
                                    add.view().setImage(ARROW_RIGHT_IMAGE);
                                    add.view().setOnClickListener((__) -> {
                                        var d = new PlayList.Data(String.valueOf(song.id), song.name, song.author(), song.duration, null, null);
                                        handler.addList(d);
                                        Toast.makeText(requireContext(), song.name + " 已添加", Toast.LENGTH_SHORT).show();
                                    });

                                    id.params().width(src.dp(250)).v_match_parent();
                                    author.params().width(src.dp(150)).v_match_parent();
                                    time.params().width(src.dp(75)).v_match_parent();

                                    id.view().setText(song.name);
                                    id.view().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

                                    author.view().setText(song.author());
                                    author.view().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

                                    time.view().setText(formatTime((int) (song.duration / 1000)));

                                    id.view().setEllipsize(TextUtils.TruncateAt.END);
                                    author.view().setEllipsize(TextUtils.TruncateAt.END);
                                    time.view().setEllipsize(TextUtils.TruncateAt.END);

                                    id.view().setSingleLine();
                                    author.view().setSingleLine();
                                    time.view().setSingleLine();

                                    src.add(id.build()).add(author.build()).add(time.build()).add(add.build());

                                    id.view().setOnClickListener(__ -> {
                                        songName.setText("加载中。。。");

                                        CompletableFuture.runAsync(() -> {
                                            var target = new PlayList.Data(String.valueOf(song.id), song.name, song.author(), song.duration, null, null);
                                            var url = handler.api.songUrl(target.id);
                                            if (url != null && url.code == 200 && url.data != null && url.first() != null && url.first().url != null) {
                                                var t = target.data != null ? target.data : (target.data = ByteBuffer.wrap(Http.get(url.first().url).sendBytes(1048576)));
                                                mPlayer.replaceTrackMp3(song.name + " - " + song.author(), t);
                                                if (handler.addList(target)) {
                                                    handler.playList.cur = handler.playList.songs.size() - 1;
                                                }
                                                GMusic.setCurrent(song);
                                                GMusic.setCurrentUrl(url.first());
                                            }
                                        }, handler.getExecutor());
                                    });
                                }

                                {
                                    ShapeDrawable drawable = new ShapeDrawable();
                                    drawable.setShape(ShapeDrawable.RECTANGLE);
                                    drawable.setColor(THEME_BG_COLOR);
                                    int dp4 = base.dp(8);
                                    drawable.setPadding(dp4, dp4, dp4, dp4);
                                    drawable.setCornerRadius(base.dp(15));
                                    src.bg(drawable);
                                }

                                int dp5 = base.dp(5);
                                src.params().margin(dp5, dp5, dp5, dp5);

                                songs.add(src.build());
                            }
                        });
                    } else songs.layout().post(() -> {
                        status.view().setText("加载失败");
                        Toast.makeText(requireContext(), "code: " + (data == null ? "null data" : data.code), Toast.LENGTH_SHORT).show();
                    });
                }, handler.getExecutor());

            base.add(songs.build());
        }

        this.mPager.setLeft(base.build());
    }

    private boolean catchException(Throwable e) {
        if (e != null) {
            Toast.makeText(requireContext(), "ex: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return true;
        }
        return false;
    }

    private LayoutBuilder.LinearLayoutBuilder<LinearLayout> createWindow(String title, int width, int height, LayoutBuilder.LinearLayoutBuilder<LinearLayout> content) {
        var main = LayoutBuilder.newLinerBuilder(requireContext());
        main.params().width(main.dp(700)).height(main.dp(500));

        main.gravity(Gravity.CENTER);


        var base = LayoutBuilder.newLinerBuilder(requireContext());

        int hOff = base.dp(700) / 2 - base.dp(300) / 2 + 1;
        int vOff = base.dp(500) / 2 - base.dp(200) / 2 + 1;
        base.vOrientation().params().margin(hOff, vOff, hOff, vOff)
            .width(base.dp(width));

        if (height == WRAP_CONTENT) {
            base.params().v_wrap_content();
        } else base.params().height(base.dp(height));

        base.layout().setMinimumHeight(base.dp(50));

        {
            var drawable = new ShapeDrawable();
            drawable.setShape(ShapeDrawable.RECTANGLE);
            drawable.setColor(new Color(255, 255, 255, 255).getRGB());
            drawable.setCornerRadius(base.dp(4));
            base.bg(drawable);
        }

        var bar = LayoutBuilder.newLinerBuilder(requireContext());
        bar.hOrientation().gravity(Gravity.START).params().h_match_parent();

        int dp5 = base.dp(5);

        bar.params().margin(0, 0, 0, dp5);

        {
            var name = ViewBuilder.wrapLinear(new TextView(requireContext()));
            name.view().setText(title);
            name.view().setTextStyle(Paint.BOLD);
            name.view().setTextColor(FONT_COLOR);
            name.view().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            name.view().setTextSize(12);
            name.params()
                .weight(1)
                .gravity(Gravity.CENTER_VERTICAL)
                .margin(dp5, dp5, dp5, dp5)
                .h_wrap_content().v_match_parent();

            bar.add(name.build());

            var close = ViewBuilder.wrapLinear(new ImageButton(requireContext()));

            close.view().setImage(CLOSE_BLACK_IMAGE);
            close.view().setAlpha(0.8f);

            {
                StateListDrawable background = new StateListDrawable();
                ShapeDrawable drawable = new ShapeDrawable();
                drawable.setShape(ShapeDrawable.RECTANGLE);
                drawable.setColor(DARK_COLOR);
                int dp4 = base.dp(4);
                drawable.setPadding(dp4, dp4, dp4, dp4);
                drawable.setCornerRadius(base.dp(T_ICON_PTS));
                background.addState(StateSet.get(StateSet.VIEW_STATE_HOVERED), drawable);
                //background.addState(new int[]{R.attr.state_checked},drawable);
                background.setEnterFadeDuration(250);
                background.setExitFadeDuration(250);
                close.bg(background);
            }

            close.params().margin(dp5, dp5, dp5, dp5);

            close.view().setOnClickListener((__) -> {
                DialogManager.get().hidden();
                currentQrKey = null;
                if (this.qrImage != null)
                    qrImage.setImage(null);
            });

            bar.add(close.build());
        }

        base.add(bar.build());
        base.add(content.build());

        main.add(base.build());
        return main;
    }

    private LinearLayout loadRecommend() {
        var base = LayoutBuilder.newLinerBuilder(requireContext());
        return base.build();
    }

    private LinearLayout loadHomePage() {
        var base = LayoutBuilder.newLinerBuilder(requireContext());

        var data = handler.topListData;
        if (data != null) {

        }

        return base.build();
    }

    private LinearLayout loadPlaylistContainer(dev.undefinedteam.gensh1n.music.api.objs.PlayList list) {
        var base = LayoutBuilder.newLinerBuilder(requireContext());
        base.params().h_match_parent().v_wrap_content();
        base.vOrientation();

        var top = LayoutBuilder.newLinerBuilder(requireContext());
        top.hOrientation().vGravity(Gravity.CENTER_VERTICAL);
        int dp5 = base.dp(5);
        top.params().margin(dp5, dp5, dp5, dp5).h_match_parent().v_wrap_content();

        var image = ViewBuilder.wrapLinear(new ImageView(requireContext()));
        image.view().setImage(list.image != null ? list.image : null);
        image.params().width(base.dp(75)).height(base.dp(75));
        if (list.image == null) {
            CompletableFuture.supplyAsync(() -> Http.get(list.coverImgUrl).sendBytes(), handler.getExecutor())
                .whenCompleteAsync((bytes, ex) -> {
                    if (!catchException(ex) && bytes != null) {
                        try {
                            list.loadImage(bytes);
                            image.view().post(() -> image.view().setImage(list.image));
                        } catch (IOException e) {
                            catchException(e);
                        }
                    }
                }, Core.getUiThreadExecutor());
        }

        var infoLayout = LayoutBuilder.newLinerBuilder(requireContext());
        infoLayout.vOrientation();
        infoLayout.vGravity(Gravity.CENTER_VERTICAL);
        infoLayout.params().margin(base.dp(10), 0, 0, 0).h_match_parent().v_wrap_content();
        var name = ViewBuilder.wrapLinear(new TextView(requireContext()));
        name.view().setText(list.name);
        name.view().setSingleLine();
        name.view().setEllipsize(TextUtils.TruncateAt.END);
        name.view().setTextSize(15);
        name.view().setEllipsize(TextUtils.TruncateAt.END);
        name.view().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        var author = ViewBuilder.wrapLinear(new TextView(requireContext()));
        author.view().setText(list.creator.nickname);
        author.view().setSingleLine();
        author.view().setEllipsize(TextUtils.TruncateAt.END);
        author.view().setTextSize(12);
        author.view().setEllipsize(TextUtils.TruncateAt.END);
        author.view().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        var desc = ViewBuilder.wrapLinear(new TextView(requireContext()));
        desc.view().setText(list.description == null ? "" : list.description);
        desc.view().setSingleLine();
        desc.view().setEllipsize(TextUtils.TruncateAt.END);
        desc.view().setTextSize(12);
        desc.view().setEllipsize(TextUtils.TruncateAt.END);
        desc.view().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        var count = ViewBuilder.wrapLinear(new TextView(requireContext()));
        count.view().setText("歌曲数量: " + list.trackCount);
        count.view().setTextSize(12);
        count.view().setEllipsize(TextUtils.TruncateAt.END);
        count.view().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        infoLayout.add(name.build()).add(author.build()).add(desc.build()).add(count.build());
        top.add(image.build()).add(infoLayout.build());

        base.add(top.build());

        var p = LayoutBuilder.newLinerBuilder(requireContext());
        var playall = ViewBuilder.wrapLinear(new Button(requireContext()));
        playall.view().setText("播放全部");
        playall.view().setTextSize(12);
        {
            StateListDrawable background = new StateListDrawable();
            ShapeDrawable drawable = new ShapeDrawable();
            drawable.setShape(ShapeDrawable.RECTANGLE);
            drawable.setColor(BUTTON_COLOR);
            drawable.setPadding(dp5, dp5, dp5, dp5);
            drawable.setCornerRadius(playall.dp(10));
            background.addState(StateSet.get(StateSet.VIEW_STATE_HOVERED), drawable);
            //background.addState(new int[]{R.attr.state_checked},drawable);
            background.setEnterFadeDuration(250);
            background.setExitFadeDuration(250);
            playall.bg(background);
        }
        playall.view().setOnClickListener(__ -> {
            list.fillPlayList(handler.playList);
            GMusic.setCurrent(String.valueOf(list.detailData.songs().getFirst().id));
            GMusic.setCurrentUrl(list.detailData.songs().getFirst().songUrl);
        });

        playall.params().margin(dp5, dp5, dp5, dp5);
        p.hOrientation().vGravity(Gravity.CENTER_VERTICAL).params().h_match_parent();
        p.add(playall.build());
        base.add(p.build());
        var bar = LayoutBuilder.newLinerBuilder(requireContext());
        bar.hOrientation().vGravity(Gravity.CENTER_VERTICAL);
        bar.params().h_match_parent();

        {
            var index = ViewBuilder.wrapLinear(new TextView(requireContext()));
            index.view().setText("#");
            index.view().setTextSize(12);
            index.params().margin(dp5, dp5, dp5, dp5).width(base.dp(20)).v_match_parent();
            bar.add(index.build());
        }

        {
            var song = ViewBuilder.wrapLinear(new TextView(requireContext()));
            song.view().setText("歌曲");
            song.view().setTextSize(12);
            song.view().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            song.view().setSingleLine();
            song.view().setMaxWidth(base.dp(250));
            song.params().width(base.dp(250));
            bar.add(song.build());
            var album = ViewBuilder.wrapLinear(new TextView(requireContext()));
            album.view().setText("Author");
            album.view().setTextSize(12);
            album.view().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            album.view().setSingleLine();
            album.view().setMaxWidth(base.dp(150));
            album.params().width(base.dp(150));
            bar.add(album.build());
            var time = ViewBuilder.wrapLinear(new TextView(requireContext()));
            time.view().setText("时长");
            time.view().setTextSize(12);
            time.view().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            time.view().setSingleLine();
            time.view().setMaxWidth(base.dp(50));
            time.params().width(base.dp(50));
            bar.add(time.build());
        }
        base.add(bar.build());

        var box = LayoutBuilder.newLinerBuilder(requireContext());

        if (list.detailData == null) {
            CompletableFuture.supplyAsync(() -> {
                list.load(handler.api);
                return list.detailData;
            }, handler.getExecutor()).whenCompleteAsync((data, ex) -> {
                if (!catchException(ex) && data != null && data.code == 200) {
                    data.loadUrls(handler.api);
                }
            }, handler.getExecutor());
        }

        box.vOrientation().hGravity(Gravity.CENTER_HORIZONTAL);
        var status = ViewBuilder.wrapLinear(new TextView(requireContext()));
        status.view().setText("加载中....");
        box.add(status.build());
        int dp2 = base.dp(2);

        CompletableFuture.supplyAsync(() -> {
            if (list.detailData == null) {
                list.load(handler.api);
            }
            return list.detailData;
        }, handler.getExecutor()).whenCompleteAsync((data, ex) -> {
            if (!catchException(ex) && data != null && data.code == 200) {
                if (list.detailData == null) {
                    data.loadUrls(handler.api);
                }

                box.layout().post(() -> {
                    box.layout().removeView(status.view());
                    int id = 0;
                    for (DetailData track : list.detailData.songs()) {
                        var src = LayoutBuilder.newLinerBuilder(requireContext());
                        src.hOrientation().params().margin(0, dp2, 0, dp2).h_match_parent();
                        var index = ViewBuilder.wrapLinear(new TextView(requireContext()));
                        index.view().setText(id + "");
                        index.view().setTextSize(12);
                        index.view().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                        index.view().setSingleLine();
                        index.params().width(base.dp(20)).v_match_parent();
                        src.add(index.build());

                        id++;

                        var song = ViewBuilder.wrapLinear(new TextView(requireContext()));
                        song.view().setText(track.name);
                        song.view().setTextSize(12);
                        song.view().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                        song.view().setSingleLine();
                        song.view().setMaxWidth(base.dp(250));
                        song.params().margin(dp5, 0, dp5, 0).width(base.dp(250));
                        src.add(song.build());
                        var album = ViewBuilder.wrapLinear(new TextView(requireContext()));
                        album.view().setText(track.author());
                        album.view().setTextSize(12);
                        album.view().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                        album.view().setSingleLine();
                        album.view().setMaxWidth(base.dp(150));
                        album.params().margin(dp5, 0, dp5, 0).width(base.dp(150));
                        src.add(album.build());
                        var time = ViewBuilder.wrapLinear(new TextView(requireContext()));
                        time.view().setText(formatTime((int) (track.duration / 1000)));
                        time.view().setTextSize(12);
                        time.view().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                        time.view().setSingleLine();
                        time.view().setMaxWidth(base.dp(50));
                        time.params().margin(dp5, 0, dp5, 0).width(base.dp(50));
                        src.add(time.build());

                        {
                            ShapeDrawable drawable = new ShapeDrawable();
                            drawable.setShape(ShapeDrawable.RECTANGLE);
                            drawable.setColor(THEME_BG_COLOR);
                            int dp4 = base.dp(8);
                            drawable.setPadding(dp4, dp4, dp4, dp4);
                            drawable.setCornerRadius(base.dp(15));
                            src.bg(drawable);
                        }

                        box.add(src.build());
                    }
                });
            }
        }, handler.getExecutor());


        box.params().margin(0, dp2, dp2, dp2).h_match_parent().v_wrap_content();
        base.add(box.build());
        return base.build();
    }

    private void addTabBG(View view) {
        StateListDrawable background = new StateListDrawable();
        ShapeDrawable drawable = new ShapeDrawable();
        drawable.setShape(ShapeDrawable.RECTANGLE);
        drawable.setColor(BUTTON_COLOR);
        int dp5 = view.dp(5);
        drawable.setPadding(dp5, dp5, dp5, dp5);
        drawable.setCornerRadius(view.dp(10));

        ShapeDrawable drawable1 = new ShapeDrawable();
        drawable1.setShape(ShapeDrawable.RECTANGLE);
        drawable1.setColor(0x80a0a0a0);
        drawable1.setPadding(dp5, dp5, dp5, dp5);
        drawable1.setCornerRadius(view.dp(10));

        background.addState(StateSet.get(StateSet.VIEW_STATE_HOVERED), drawable);
        background.addState(new int[]{R.attr.state_checked}, drawable1);
        background.setEnterFadeDuration(250);
        background.setExitFadeDuration(250);
        view.setBackground(background);
    }

    public static class SeekLayout extends LinearLayout {

        public TextView mMinText;
        public SeekBar mSeekBar;
        public TextView mMaxText;

        public SeekLayout(Context context, float textSize) {
            super(context);
            setOrientation(HORIZONTAL);

            mMinText = new TextView(context);
            mMinText.setTextSize(textSize);
            mMinText.setMinWidth(dp(50));
            mMinText.setTextAlignment(TEXT_ALIGNMENT_VIEW_END);

            addView(mMinText);

            mSeekBar = new SeekBar(context);
            mSeekBar.setMax(10000);
            mSeekBar.setClickable(true);
            var lp = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            lp.weight = 1;
            mSeekBar.setLayoutParams(lp);

            addView(mSeekBar);

            mMaxText = new TextView(context);
            mMaxText.setTextSize(textSize);
            mMaxText.setMinWidth(dp(50));
            mMaxText.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);

            addView(mMaxText);

            setGravity(Gravity.CENTER);
        }
    }

    public static class Pager extends ViewPager {
        private final MusicFragment parent;

        private LinearLayout left, right;

        private boolean lockLeft, lockRight;

        public Pager(Context context, MusicFragment parent) {
            super(context);
            this.parent = parent;

            setAdapter(this.new Adapter(left, right));
            setFocusableInTouchMode(true);
            setKeyboardNavigationCluster(true);
            setEdgeEffectColor(EDGE_SIDES_COLOR);
        }

        public void setLeft(LinearLayout left) {
            if (!lockLeft) {
                this.left = left;
                setAdapter(this.new Adapter(left, right));
                this.setCurrentItem(0, true);
            }
        }

        public void setRight(LinearLayout right) {
            if (!lockRight) {
                this.right = right;
                setAdapter(this.new Adapter(left, right));
            }
        }

        public void lockLeft() {
            this.lockLeft = true;
        }

        public void unlockLeft() {
            this.lockLeft = false;
        }

        public void lockRight() {
            this.lockRight = true;
        }

        public void unlockRight() {
            this.lockRight = false;
        }

        public void onUpdate() {

        }

        public class Adapter extends PagerAdapter {
            public LinearLayout left;
            public LinearLayout right;

            public Adapter(LinearLayout left, LinearLayout right) {
                this.left = left;
                this.right = right;
            }

            @Override
            public int getCount() {
                return 2;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                var context = container.getContext();
                var sv = new ScrollView(context);
                this.left = this.left == null ? this.left = new LinearLayout(context) : this.left;
                this.right = this.right == null ? this.right = new LinearLayout(context) : this.right;

                if (position == 1) {
                    sv.addView(right);
                } else {
                    sv.addView(left);

                    var animator = ObjectAnimator.ofFloat(sv,
                        ViewAnimators.ALPHA_255, 0, 255);
                    animator.setInterpolator(TimeInterpolator.DECELERATE_QUINTIC);

                    sv.addOnLayoutChangeListener(new OnLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
                                                   int oldTop, int oldRight, int oldBottom) {
                            animator.start();
                            v.removeOnLayoutChangeListener(this);
                        }
                    });
                }

                var params = new LinearLayout.LayoutParams(0, MATCH_PARENT, 1);
                var dp2 = sv.dp(2);
                params.setMargins(dp2, dp2, dp2, dp2);
                container.addView(sv, params);

                return sv;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        }
    }
}
