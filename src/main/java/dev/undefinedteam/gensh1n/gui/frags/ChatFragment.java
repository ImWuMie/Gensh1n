package dev.undefinedteam.gensh1n.gui.frags;

import dev.undefinedteam.gclient.GCClient;
import dev.undefinedteam.gclient.GCUser;
import dev.undefinedteam.gclient.GChat;
import dev.undefinedteam.gclient.packets.c2s.verify.CodeC2S;
import dev.undefinedteam.gclient.packets.s2c.play.MessageS2C;
import dev.undefinedteam.gclient.packets.s2c.verify.RegisterStatusS2C;
import dev.undefinedteam.gensh1n.gui.animators.ViewAnimators;
import dev.undefinedteam.gensh1n.gui.builders.LayoutBuilder;
import dev.undefinedteam.gensh1n.gui.builders.ViewBuilder;
import dev.undefinedteam.modernui.mc.MuiScreen;
import dev.undefinedteam.modernui.mc.ScreenCallback;
import icyllis.modernui.animation.Animator;
import icyllis.modernui.animation.AnimatorListener;
import icyllis.modernui.animation.ObjectAnimator;
import icyllis.modernui.animation.TimeInterpolator;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.graphics.drawable.ShapeDrawable;
import icyllis.modernui.graphics.drawable.StateListDrawable;
import icyllis.modernui.text.*;
import icyllis.modernui.text.style.ForegroundColorSpan;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.util.StateSet;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.LayoutInflater;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.*;
import icyllis.modernui.widget.Button;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.function.Supplier;

import static dev.undefinedteam.gensh1n.Client.mc;
import static dev.undefinedteam.gensh1n.gui.frags.GMainGui.*;

@StringEncryption
@ControlFlowObfuscation
@NativeObfuscation
public class ChatFragment extends Fragment implements ScreenCallback {
    public static final int BACKGROUND_COLOR = new Color(80, 80, 80, 20).getRGB();
    public static final int EDIT_COLOR = new Color(80, 80, 80, 70).getRGB();
    public static final int CHAT_NAME_COLOR = new Color(255, 255, 255, 200).getRGB();

    public static final int DISCONNECT_COLOR = Color.RED.getRGB();
    public static final int CONNECT_COLOR = GREEN;
    public static final int WARN_COLOR = Color.YELLOW.getRGB();

    public GCUser session = GCClient.INSTANCE.session();
    private boolean lastIsNull, lastIsLogged = false;

    private EditText user, tokenV;
    private LinearLayout baseLayout;
    private LayoutBuilder.LinearLayoutBuilder<LinearLayout> baseBuilder;

    private TextView mStatus, mInfo;
    private ObjectAnimator mInfoAnimatorIn, mInfoAnimatorOut;

    public final Supplier<Boolean> escClose;

    public ChatFragment() {
        this(null);
    }

    public ChatFragment(Supplier<Boolean> escClose) {
        this.escClose = escClose;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, DataSet savedInstanceState) {
        GCClient.INSTANCE.setListener((session, packet) -> {
            if (mc == null || (mc.currentScreen instanceof MuiScreen s && s.getFragment() instanceof ChatFragment)) {
                mStatus.postDelayed(() -> {
                    if (packet instanceof MessageS2C p) {
                        info(WARN_COLOR, p.message);
                        mStatus.postDelayed(this::clearInfo, 3000);
                    } else if (packet instanceof RegisterStatusS2C statusS2C) {
                        info(WARN_COLOR, "code: " + statusS2C.code + " - " + statusS2C.data);
                        mStatus.postDelayed(this::clearInfo, 3000);
                    }
                }, 50);
            }
        });

        var main = LayoutBuilder.newLinerBuilder(requireContext());

        {
            main.vOrientation().gravity(Gravity.CENTER);
            {
                ShapeDrawable drawable = new ShapeDrawable();
                drawable.setShape(ShapeDrawable.RECTANGLE);
                drawable.setColor(BACKGROUND_COLOR);
                drawable.setStroke(main.dp(1), EDGE_SIDES_COLOR);
                drawable.setCornerRadius(main.dp(10));
                main.bg(drawable);
            }
        }

        {
            var base = LayoutBuilder.newLinerBuilder(requireContext());
            this.baseBuilder = base;
            this.baseLayout = base.layout();
            base.vOrientation().gravity(Gravity.CENTER);

            if (session == null) {
                base.add(noConnect());
                lastIsNull = true;
            } else {
                if (session.logged()) {
                    overlay(base);
                    lastIsLogged = true;
                } else connect(base);
                lastIsNull = false;
            }
            base.params().v_wrap_content().h_match_parent();
            main.add(base.build());
        }

        {
            this.mStatus = new TextView(requireContext());
            var builder = ViewBuilder.wrapLinear(mStatus);
            mStatus.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            mStatus.setTextColor(FONT_COLOR);
            mStatus.setTextSize(10);
            updateColor(session == null ? DISCONNECT_COLOR : CONNECT_COLOR, session == null ? "Disconnect." : "Connect.");

            int dp2 = main.dp(2);
            builder.params().margin(dp2, dp2, dp2, dp2);
            builder.params().v_wrap_content().v_wrap_content();

            this.mInfo = new TextView(requireContext());
            mInfo.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            mInfo.setTextColor(FONT_COLOR);
            mInfo.setTextSize(14);

            {
                mInfoAnimatorIn = ObjectAnimator.ofFloat(mInfo, ViewAnimators.ALPHA_255, 0, 255);
                mInfoAnimatorOut = ObjectAnimator.ofFloat(mInfo, ViewAnimators.ALPHA_255, 255, 0);

                mInfoAnimatorIn.setInterpolator(TimeInterpolator.DECELERATE);
                mInfoAnimatorOut.setInterpolator(TimeInterpolator.DECELERATE);

                mInfoAnimatorOut.addListener(new AnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mInfo.setText("");
                    }
                });
            }

            var infoBuilder = ViewBuilder.wrapLinear(mInfo);
            infoBuilder.params().margin(dp2, dp2, dp2, dp2);
            infoBuilder.params().v_wrap_content().v_wrap_content();

            main.add(builder.build()).add(infoBuilder.build());
        }

        main.params().v_match_parent().h_match_parent();

        baseLayout.post(this::updateStatus);
        return main.build();
    }

    private void updateStatus() {
        this.session = GCClient.INSTANCE.session();

        if (session != null) {
            if (session.logged() && !lastIsLogged) {
                baseLayout.removeAllViews();
                overlay(baseBuilder);
                lastIsLogged = true;
            } else if (!session.logged() && lastIsLogged) {
                baseLayout.removeAllViews();
                connect(baseBuilder);
                lastIsLogged = false;
            }
        }

        if (session == null && !lastIsNull) {
            baseLayout.removeAllViews();

            baseBuilder.vGravity(Gravity.CENTER_VERTICAL);
            baseBuilder.gravity(Gravity.CENTER);
            baseBuilder.add(noConnect());
            updateColor(session == null ? DISCONNECT_COLOR : CONNECT_COLOR, session == null ? "Disconnect." : "Connect.");
            lastIsNull = true;
        } else if (session != null && lastIsNull) {
            baseLayout.removeAllViews();
            if (session.logged()) {
                overlay(baseBuilder);
                lastIsLogged = true;
            } else connect(baseBuilder);
            updateColor(session == null ? DISCONNECT_COLOR : CONNECT_COLOR, session == null ? "Disconnect." : "Connect.");
            lastIsNull = false;
        }

        if (session != null && session.logged()) {
            updateColor(CONNECT_COLOR, "Ping: " + session.getPing());
        }

        baseLayout.postDelayed(this::updateStatus, 200);
    }

    private void overlay(LayoutBuilder.LinearLayoutBuilder<LinearLayout> main) {
        var base = LayoutBuilder.newLinerBuilder(requireContext());
        base.vOrientation()
            .gravity(Gravity.CENTER).vGravity(Gravity.CENTER_VERTICAL).hGravity(Gravity.CENTER_HORIZONTAL);

        {
            var text = ViewBuilder.wrapLinear(new TextView(requireContext()));
            text.view().setTextSize(20);
            text.view().setTextStyle(Paint.BOLD);
            text.view().setText(GChat.SPECIAL_NAME + " - 已登陆");
            base.add(text.build());
        }

        var cardText = ViewBuilder.wrapLinear(new TextView(requireContext()));
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = format.format(session.expireTime);
            cardText.view().setTextSize(20);
            cardText.view().setTextStyle(Paint.BOLD);
            cardText.view().setText(session.verifyPass() ? "到期时间: " + date : "六六六你到期了 ↓赶快的。");
            base.add(cardText.build());
        }

        var cardBox = ViewBuilder.wrapLinear(new EditText(requireContext()));
        int dp5 = base.dp(5);
        int dp3 = base.dp(5);

        {
            cardBox.view().setHint("输入你的卡密。");
            cardBox.view().setTextColor(FONT_COLOR);
            cardBox.view().setSingleLine();
            cardBox.view().setTextSize(12);

            {
                ShapeDrawable drawable = new ShapeDrawable();
                drawable.setShape(ShapeDrawable.RECTANGLE);
                drawable.setColor(EDIT_COLOR);
                drawable.setStroke(base.dp(1), EDGE_SIDES_COLOR);
                drawable.setPadding(dp5, dp5, dp5, dp5);
                drawable.setCornerRadius(base.dp(5));
                cardBox.bg(drawable);

                cardBox.params().margin(dp3, dp3, dp3, dp3)
                    .height(base.dp(30)).width(base.dp(250));
            }

            base.add(cardBox.build());
        }

        {
            var button = ViewBuilder.wrapLinear(new Button(requireContext()));
            button.view().setText("充值");
            button.view().setTextStyle(Paint.BOLD);
            button.params().margin(dp5, dp5, dp5, dp5);
            button.view().setOnClickListener((e) -> {
                var code = cardBox.view().getText().toString();
                session.send(new CodeC2S(code));
                button.view().postDelayed(() -> {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String date = format.format(session.expireTime);
                    cardText.view().setText(session.verifyPass() ? "到期时间: " + date : "六六六你到期了 ↓赶快的。");
                    cardBox.view().setText("");
                }, 1250);
            });

            {
                int tab_margin_dp2 = base.dp(TAB_MARGIN_PTS / 2.0f - 4);
                StateListDrawable background = new StateListDrawable();
                ShapeDrawable drawable = new ShapeDrawable();
                drawable.setShape(ShapeDrawable.RECTANGLE);
                drawable.setColor(EDIT_COLOR);
                drawable.setPadding(tab_margin_dp2, tab_margin_dp2, tab_margin_dp2, tab_margin_dp2);
                drawable.setCornerRadius(base.dp(5));
                background.addState(StateSet.get(StateSet.VIEW_STATE_HOVERED), drawable);
                //background.addState(new int[]{R.attr.state_checked},drawable);
                background.setEnterFadeDuration(250);
                background.setExitFadeDuration(250);
                button.bg(background);
            }

            base.add(button.build());
        }

        main.add(base.build());
    }

    private void connect(LayoutBuilder.LinearLayoutBuilder<LinearLayout> base) {
        var layout = LayoutBuilder.newLinerBuilder(requireContext());
        layout.vOrientation()
            .gravity(Gravity.CENTER).vGravity(Gravity.CENTER_VERTICAL).hGravity(Gravity.CENTER_HORIZONTAL);

        var text = ViewBuilder.wrapLinear(new TextView(requireContext()));
        text.view().setTextSize(20);
        text.view().setTextStyle(Paint.BOLD);
        text.view().setText(GChat.SPECIAL_NAME + " - Login");
        text.view().setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        int dp5 = base.dp(5);
        int dp3 = base.dp(5);
        text.params().margin(dp5, dp5, dp5, dp5);

        layout.add(text.build());

        {
            var noti = ViewBuilder.wrapLinear(new TextView(requireContext()));
            noti.view().setTextSize(14);
            noti.view().setText("注册只有一次机会，填用户名和密码注册，登录直接填后登录");
            noti.view().setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            noti.params().margin(dp5, dp5, dp5, dp5);

            layout.add(noti.build());
        }

        {
            var username = ViewBuilder.wrapLinear(new EditText(requireContext()));
            var passwd = ViewBuilder.wrapLinear(new EditText(requireContext()));

            String u_name = GChat.INSTANCE.username;
            String u_token = GChat.INSTANCE.passwd;
            if (u_name != null) username.view().setText(u_name);
            if (u_token != null) passwd.view().setText(u_token);

            this.user = username.view();
            this.tokenV = passwd.view();

            username.view().setHint("Name");
            passwd.view().setHint("Passwd");
//            passwd.view().setTypeface(Fonts.ICON);

            username.view().setTextColor(FONT_COLOR);
            passwd.view().setTextColor(FONT_COLOR);
            username.view().setHintTextColor(HINT_FONT_COLOR);
            passwd.view().setHintTextColor(HINT_FONT_COLOR);

            username.view().setSingleLine();
            passwd.view().setSingleLine();

            username.view().setTextSize(12);
            passwd.view().setTextSize(12);

            {
                ShapeDrawable drawable = new ShapeDrawable();
                drawable.setShape(ShapeDrawable.RECTANGLE);
                drawable.setColor(EDIT_COLOR);
                drawable.setStroke(base.dp(1), EDGE_SIDES_COLOR);
                drawable.setPadding(dp5, dp5, dp5, dp5);
                drawable.setCornerRadius(base.dp(5));
                username.bg(drawable);

                username.params().margin(dp3, dp3, dp3, dp3)
                    .height(base.dp(30)).width(base.dp(250));
            }
            {
                ShapeDrawable drawable = new ShapeDrawable();
                drawable.setShape(ShapeDrawable.RECTANGLE);
                drawable.setColor(EDIT_COLOR);
                drawable.setStroke(base.dp(1), EDGE_SIDES_COLOR);
                drawable.setPadding(dp5, dp5, dp5, dp5);
                drawable.setCornerRadius(base.dp(5));
                passwd.bg(drawable);

                passwd.params().margin(dp3, dp3, dp3, dp3)
                    .height(base.dp(30)).width(base.dp(250));
            }

            layout.add(username.build());
            layout.add(passwd.build());

            {
                var src = LayoutBuilder.newLinerBuilder(requireContext());
                src.hOrientation()
                    .gravity(Gravity.CENTER).vGravity(Gravity.CENTER_VERTICAL).hGravity(Gravity.CENTER_HORIZONTAL);

                {
                    var button = ViewBuilder.wrapLinear(new Button(requireContext()));
                    button.view().setText("Login");
                    button.view().setTextStyle(Paint.BOLD);
                    button.params().margin(dp5, dp5, dp5, dp5);
                    button.view().setOnClickListener((e) -> {
                        GChat.INSTANCE.username = String.valueOf(username.view().getText());
                        GChat.INSTANCE.passwd = String.valueOf(passwd.view().getText());
                        try {
                            GChat.INSTANCE.saveUser();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        session.loginInChat(GChat.INSTANCE.username, GChat.INSTANCE.passwd);
                    });

                    {
                        int tab_margin_dp2 = base.dp(TAB_MARGIN_PTS / 2.0f - 4);
                        StateListDrawable background = new StateListDrawable();
                        ShapeDrawable drawable = new ShapeDrawable();
                        drawable.setShape(ShapeDrawable.RECTANGLE);
                        drawable.setColor(EDIT_COLOR);
                        drawable.setPadding(tab_margin_dp2, tab_margin_dp2, tab_margin_dp2, tab_margin_dp2);
                        drawable.setCornerRadius(base.dp(5));
                        background.addState(StateSet.get(StateSet.VIEW_STATE_HOVERED), drawable);
                        //background.addState(new int[]{R.attr.state_checked},drawable);
                        background.setEnterFadeDuration(250);
                        background.setExitFadeDuration(250);
                        button.bg(background);
                    }

                    src.add(button.build());
                }

                {
                    {
                        var button = ViewBuilder.wrapLinear(new Button(requireContext()));
                        button.view().setText("Register");
                        button.view().setTextStyle(Paint.BOLD);
                        button.params().margin(dp5, dp5, dp5, dp5);
                        button.view().setOnClickListener((e) -> {
                            var name = String.valueOf(username.view().getText());
                            if (name.isBlank() || name.isEmpty()) {
                                info(WARN_COLOR, "名字不合法");
                                mStatus.postDelayed(this::clearInfo, 3000);
                                return;
                            }
                            var pwd = String.valueOf(passwd.view().getText());

                            GChat.INSTANCE.username = String.valueOf(username.view().getText());
                            GChat.INSTANCE.passwd = String.valueOf(passwd.view().getText());
                            try {
                                GChat.INSTANCE.saveUser();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }

                            session.register(name, pwd);
                        });

                        {
                            int tab_margin_dp2 = base.dp(TAB_MARGIN_PTS / 2.0f - 4);
                            StateListDrawable background = new StateListDrawable();
                            ShapeDrawable drawable = new ShapeDrawable();
                            drawable.setShape(ShapeDrawable.RECTANGLE);
                            drawable.setColor(EDIT_COLOR);
                            drawable.setPadding(tab_margin_dp2, tab_margin_dp2, tab_margin_dp2, tab_margin_dp2);
                            drawable.setCornerRadius(base.dp(5));
                            background.addState(StateSet.get(StateSet.VIEW_STATE_HOVERED), drawable);
                            //background.addState(new int[]{R.attr.state_checked},drawable);
                            background.setEnterFadeDuration(250);
                            background.setExitFadeDuration(250);
                            button.bg(background);
                        }

                        src.add(button.build());
                    }
                }

                layout.add(src.build());
            }
        }

        base.vGravity(Gravity.CENTER_VERTICAL);
        base.gravity(Gravity.CENTER);
        base.add(layout.build());
    }

    private LinearLayout noConnect() {
        var base = LayoutBuilder.newLinerBuilder(requireContext());
        base.vOrientation()
            .gravity(Gravity.CENTER).vGravity(Gravity.CENTER_VERTICAL).hGravity(Gravity.CENTER_HORIZONTAL);

        var text = ViewBuilder.wrapLinear(new TextView(requireContext()));
        text.view().setTextSize(20);
        text.view().setTextStyle(Paint.BOLD);
        text.view().setText(GChat.SPECIAL_NAME + " - 无法连接到服务器");

        var button = ViewBuilder.wrapLinear(new Button(requireContext()));
        button.view().setText("Reconnect");
        button.view().setTextStyle(Paint.BOLD);
        int dp5 = base.dp(5);
        button.params().margin(dp5, dp5, dp5, dp5);
        button.view().setOnClickListener((e) -> {
            GCClient.INSTANCE.connect();

            button.view().setEnabled(false);
            button.view().setAlpha(80 / 255.0f);

            button.view().postDelayed(() -> {
                button.view().setEnabled(true);
                button.view().setAlpha(1.0f);
            }, 5000);
        });

        {
            int tab_margin_dp2 = base.dp(TAB_MARGIN_PTS / 2.0f - 4);
            StateListDrawable background = new StateListDrawable();
            ShapeDrawable drawable = new ShapeDrawable();
            drawable.setShape(ShapeDrawable.RECTANGLE);
            drawable.setColor(EDIT_COLOR);
            drawable.setPadding(tab_margin_dp2, tab_margin_dp2, tab_margin_dp2, tab_margin_dp2);
            drawable.setCornerRadius(base.dp(5));
            background.addState(StateSet.get(StateSet.VIEW_STATE_HOVERED), drawable);
            //background.addState(new int[]{R.attr.state_checked},drawable);
            background.setEnterFadeDuration(250);
            background.setExitFadeDuration(250);
            button.bg(background);
        }

        base.add(text.build());
        base.add(button.build());
        return base.build();
    }

    private void updateColor(int color, String src) {
        Spannable spannable = new SpannableString("● " + src);

        spannable.setSpan(new ForegroundColorSpan(color), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        var precomputed = PrecomputedText.create(spannable, mStatus.getTextMetricsParams());
        mStatus.post(() -> mStatus.setText(precomputed, TextView.BufferType.SPANNABLE));
    }

    private void info(int color, String src) {
        Spannable spannable = new SpannableString("● " + src);

        spannable.setSpan(new ForegroundColorSpan(color), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        var precomputed = PrecomputedText.create(spannable, mInfo.getTextMetricsParams());
        mInfo.post(() -> {
            mInfo.setText(precomputed, TextView.BufferType.SPANNABLE);
            if (mInfoAnimatorOut.isRunning()) mInfoAnimatorOut.cancel();

            mInfoAnimatorIn.start();
        });
    }

    private void clearInfo() {
        if (mInfoAnimatorIn.isRunning()) mInfoAnimatorIn.cancel();

        mInfoAnimatorOut.start();
    }

    @Override
    public boolean shouldClose() {
        return escClose.get();
    }
}
