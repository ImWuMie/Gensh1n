package dev.undefinedteam.gensh1n.gui.frags;

import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.gui.animators.ViewAnimators;
import dev.undefinedteam.gensh1n.gui.builders.LayoutBuilder;
import dev.undefinedteam.gensh1n.gui.builders.ViewBuilder;
import dev.undefinedteam.gensh1n.gui.weights.PageShower;
import dev.undefinedteam.gensh1n.render.Fonts;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.settings.Settings;
import dev.undefinedteam.gensh1n.system.ClientConfig;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Category;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.system.modules.Modules;
import icyllis.modernui.R;
import icyllis.modernui.animation.Animator;
import icyllis.modernui.animation.AnimatorListener;
import icyllis.modernui.animation.ObjectAnimator;
import icyllis.modernui.animation.TimeInterpolator;
import icyllis.modernui.annotation.NonNull;
import icyllis.modernui.core.Context;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.graphics.Image;
import icyllis.modernui.graphics.MathUtil;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.graphics.drawable.ShapeDrawable;
import icyllis.modernui.graphics.drawable.StateListDrawable;
import icyllis.modernui.text.InputFilter;
import icyllis.modernui.text.TextUtils;
import icyllis.modernui.util.ColorStateList;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.util.StateSet;
import icyllis.modernui.view.*;
import icyllis.modernui.widget.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.awt.Color;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static dev.undefinedteam.gensh1n.gui.frags.MainInfoSaver.NULL;
import static icyllis.modernui.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class GMainGui extends Fragment {
    public static final int FONT_COLOR = new Color(255, 255, 255).getRGB();
    public static final int HINT_FONT_COLOR = new Color(255, 255, 255, 120).getRGB();
    public static final int L_FONT_COLOR = new Color(165, 165, 165).getRGB();
    public static final int BACKGROUND_COLOR = new Color(100, 100, 100, 80).getRGB();
    public static final int DEEP_BACKGROUND_COLOR = new Color(30, 30, 30, 80).getRGB();
    public static final int EDGE_SIDES_COLOR = new Color(110, 110, 110, 100).getRGB();

    public static final int LINE_PAGE_COLOR = new Color(0, 0, 0, 80).getRGB();
    public static final int GREEN = new Color(51, 199, 90).getRGB();
    public static final int BUTTON_A = new Color(144, 144, 144).getRGB();

    public static final int TAB_MARGIN_PTS = 20;

    public static final int T_ICON_PTS = 52;
    public static final int T_ICON_MARGIN = 8;
    public static final int REACH_PTS = T_ICON_PTS + T_ICON_MARGIN; // 60pts

    public static final float ROUND_RADIUS = 7.0f;
    public static final float ROUND_RADIUS_HALF = ROUND_RADIUS / 2.0f;

    private static final int CATEGORY_ADDRESS = 0x888;

    public static final Image BOOKMARK_IMAGE = Image.create(Client.ASSETS_LOCATION, "icons/16px/bookmark.png");
    public static final Image ARROW_LEFT_IMAGE = Image.create(Client.ASSETS_LOCATION, "icons/16px/arrow_left.png");
    public static final Image ARROW_RIGHT_IMAGE = Image.create(Client.ASSETS_LOCATION, "icons/16px/arrow_right.png");
    public static final Image LOCATION_IMAGE = Image.create(Client.ASSETS_LOCATION, "icons/16px/location.png");
    public static final Image REFRESH_IMAGE = Image.create(Client.ASSETS_LOCATION, "icons/16px/refresh.png");
    public static final Image SYNC_IMAGE = Image.create(Client.ASSETS_LOCATION, "icons/16px/sync.png");
    public static final Image COPY_IMAGE = Image.create(Client.ASSETS_LOCATION, "icons/16px/copy.png");

    public static final Image EMPTY_IMAGE = Image.create(Client.ASSETS_LOCATION, "icons/64px/empty.png");

    public static final ColorStateList TAB_BUTTON_COLOR = new ColorStateList(
        new int[][]{
            new int[]{R.attr.state_checked},
            StateSet.get(StateSet.VIEW_STATE_HOVERED),
            StateSet.WILD_CARD},
        new int[]{
            0xFFFFFFFF, // selected
            0xFFE0E0E0, // hovered
            0xFFB4B4B4} // other
    );

    private static final Category CONFIG_CAT = new Category("m", FONT_COLOR);

    private final MainInfoSaver saver = MainGuiFragment.saver;

    private Pager mPager;
    private PageShower mShower;
    private TextView mTimeView;
    private TextView mTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, DataSet savedInstanceState) {
        var main = LayoutBuilder.newLinerBuilder(requireContext());
        main.vOrientation().hGravity(Gravity.CENTER_HORIZONTAL).vGravity(Gravity.CENTER_VERTICAL).gravity(Gravity.CENTER);
        main.params().h_match_parent().v_match_parent();

        var base = LayoutBuilder.newLinerBuilder(requireContext());
        base.hOrientation()
            .vGravity(Gravity.CENTER_VERTICAL)
            .params()
            .width(base.dp(700))
            .height(base.dp(500));

        mPager = new Pager(requireContext(), this, Categories.Combat);

        int tab_margin_dp2 = base.dp(TAB_MARGIN_PTS / 2.0f - 4);

        {
            var table = LayoutBuilder.newRadioGroupBuilder(requireContext());
            table.vOrientation()
                .hGravity(Gravity.CENTER_HORIZONTAL);

            {
                ShapeDrawable drawable = new ShapeDrawable();
                drawable.setShape(ShapeDrawable.RECTANGLE);
                drawable.setColor(DEEP_BACKGROUND_COLOR);
                drawable.setStroke(base.dp(1), EDGE_SIDES_COLOR);
                drawable.setCornerRadius(base.dp(T_ICON_PTS));
                table.bg(drawable);
            }

            final Int2ObjectMap<Category> catMap = new Int2ObjectArrayMap<>();

            int __address_cat = 0;
            for (Category category : Modules.loopCategories()) {
                var button = ViewBuilder.wrapLinear(new RadioButton(requireContext()));
                int vDp = base.dp(6);
                int hDp = base.dp(6);

                button.params().margin(hDp, vDp, hDp, vDp);

                {
                    StateListDrawable background = new StateListDrawable();
                    ShapeDrawable drawable = new ShapeDrawable();
                    drawable.setShape(ShapeDrawable.RECTANGLE);
                    drawable.setColor(BACKGROUND_COLOR);
                    drawable.setPadding(tab_margin_dp2, tab_margin_dp2, tab_margin_dp2, tab_margin_dp2);
                    drawable.setCornerRadius(base.dp(T_ICON_PTS));
                    background.addState(StateSet.get(StateSet.VIEW_STATE_HOVERED), drawable);
                    //background.addState(new int[]{R.attr.state_checked},drawable);
                    background.setEnterFadeDuration(250);
                    background.setExitFadeDuration(250);
                    button.bg(background);
                }

                button.view().setText(category.iconName);
                button.view().setTextStyle(Paint.BOLD);
                button.view().setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                button.view().setTextColor(TAB_BUTTON_COLOR);
                button.view().setTypeface(Fonts.ICON);
                button.id(CATEGORY_ADDRESS + __address_cat);
                catMap.put(button.view().getId(), category);

                if (button.view().getId() == saver.category_last_checked)
                    button.view().setChecked(true);

                table.add(button.build());
                __address_cat++;
            }
            table.params().gravity(Gravity.CENTER_VERTICAL);

            {
                var button = ViewBuilder.wrapLinear(new RadioButton(requireContext()));
                int vDp = base.dp(6);
                int hDp = base.dp(6);

                button.params().margin(hDp, vDp, hDp, vDp);

                {
                    StateListDrawable background = new StateListDrawable();
                    ShapeDrawable drawable = new ShapeDrawable();
                    drawable.setShape(ShapeDrawable.RECTANGLE);
                    drawable.setColor(BACKGROUND_COLOR);
                    drawable.setPadding(tab_margin_dp2, tab_margin_dp2, tab_margin_dp2, tab_margin_dp2);
                    drawable.setCornerRadius(base.dp(T_ICON_PTS));
                    background.addState(StateSet.get(StateSet.VIEW_STATE_HOVERED), drawable);
                    //background.addState(new int[]{R.attr.state_checked},drawable);
                    background.setEnterFadeDuration(250);
                    background.setExitFadeDuration(250);
                    button.bg(background);
                }

                button.view().setText(CONFIG_CAT.name);
                button.view().setTextStyle(Paint.BOLD);
                button.view().setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                button.view().setTextColor(TAB_BUTTON_COLOR);
                button.view().setTypeface(Fonts.ICON);
                button.id(CATEGORY_ADDRESS - 7);
                catMap.put(button.view().getId(), CONFIG_CAT);

                if (button.view().getId() == saver.category_last_checked)
                    button.view().setChecked(true);

                table.add(button.build());
            }

            table.onCheck(id -> {
                Category cat = catMap.getOrDefault((int) id, Categories.Combat);
                this.saver.category_last_checked = id;
                this.saver.last_select_category = cat;
                this.mPager.setCat(cat);
            });

            base.add(table.build(), 0);
        }

        {
            var center = LayoutBuilder.newRelativeBuilder(requireContext());
            center
                //.vOrientation()
                .hGravity(Gravity.CENTER_HORIZONTAL);

            {
                var top = LayoutBuilder.newLinerBuilder(requireContext());
                top
                    .hOrientation()
                    .vGravity(Gravity.CENTER_VERTICAL)
                    .hGravity(Gravity.CENTER_HORIZONTAL)
                    .gravity(Gravity.CENTER);
                {
                    ShapeDrawable drawable = new ShapeDrawable();
                    drawable.setShape(ShapeDrawable.RECTANGLE);
                    drawable.setColor(DEEP_BACKGROUND_COLOR);
                    drawable.setStroke(base.dp(1), EDGE_SIDES_COLOR);
                    drawable.setCornerRadius(base.dp(T_ICON_PTS));
                    top.bg(drawable);
                }

                {
                    var logo = ViewBuilder.wrapLinear(new TextView(requireContext()));
                    logo.params()
                        .margin(base.dp(5), 0, base.dp(30), 0)
                        .v_wrap_content().h_wrap_content();

                    logo.view().setTextColor(FONT_COLOR);
                    logo.view().setText(Client.FULL_SPECIAL_NAME);
                    logo.view().setTextIsSelectable(true);
                    logo.view().setPadding(0, -base.dp(2), 0, 0);
                    logo.view().setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    top.add(logo.build());
                }

                {
                    var page = LayoutBuilder.newLinerBuilder(requireContext());
                    page.hOrientation().vGravity(Gravity.CENTER_VERTICAL);

                    var page_left = ViewBuilder.wrapLinear(new ImageButton(requireContext()));
                    var shower = ViewBuilder.wrapLinear(new PageShower(requireContext(), base.dp(6)));
                    mShower = shower.view();
                    var page_right = ViewBuilder.wrapLinear(new ImageButton(requireContext()));
                    page_left.view().setImage(ARROW_LEFT_IMAGE);
                    page_right.view().setImage(ARROW_RIGHT_IMAGE);

                    shower.view().setPageCount(2);
                    shower.view().setCurrent(0);
                    mShower.setPageChangedListener((item) -> {
                        mPager.setCurrentItem(item, true);
                    });

                    shower.params()
                        .margin(base.dp(3), 0, base.dp(3), 0)
                        .height(base.dp(16))
                        .width(base.dp(4 + 2 * 6 + 4));


                    //page_left.view().setTextSize(20);
                    //page_right.view().setTextSize(20);

                    //page_left.view().setTextStyle(Paint.BOLD);
                    //page_right.view().setTextStyle(Paint.BOLD);

                    //page_left.view().setTextColor(FONT_COLOR);
                    //page_right.view().setTextColor(FONT_COLOR);

                    int dp4 = base.dp(4);
                    int dp6 = base.dp(6);
                    {
                        StateListDrawable background = new StateListDrawable();
                        ShapeDrawable drawable = new ShapeDrawable();
                        drawable.setShape(ShapeDrawable.RECTANGLE);
                        drawable.setColor(BACKGROUND_COLOR);
                        drawable.setPadding(dp6, dp6, dp6, dp6);
                        drawable.setCornerRadius(base.dp(T_ICON_PTS));
                        background.addState(StateSet.get(StateSet.VIEW_STATE_HOVERED), drawable);
                        //background.addState(new int[]{R.attr.state_checked},drawable);
                        background.setEnterFadeDuration(250);
                        background.setExitFadeDuration(250);
                        page_left.view().setBackground(background);
                        page_left.view().setOnClickListener((v) -> shower.view().setCurrent(MathUtil.clamp(shower.view().getCurrentPage() - 1, 0, shower.view().getPageCount() - 1)));
                    }
                    {
                        StateListDrawable background = new StateListDrawable();
                        ShapeDrawable drawable = new ShapeDrawable();
                        drawable.setShape(ShapeDrawable.RECTANGLE);
                        drawable.setColor(BACKGROUND_COLOR);
                        drawable.setPadding(dp6, dp6, dp6, dp6);
                        drawable.setCornerRadius(base.dp(T_ICON_PTS));
                        background.addState(StateSet.get(StateSet.VIEW_STATE_HOVERED), drawable);
                        //background.addState(new int[]{R.attr.state_checked},drawable);
                        background.setEnterFadeDuration(250);
                        background.setExitFadeDuration(250);
                        page_right.view().setBackground(background);
                        page_right.view().setOnClickListener((v) -> shower.view().setCurrent(MathUtil.clamp(shower.view().getCurrentPage() + 1, 0, shower.view().getPageCount() - 1)));
                    }

                    int hDp = base.dp(5);
                    int vDp = base.dp(5);
                    page_left.params().margin(hDp, vDp, hDp, vDp);
                    page_right.params().margin(hDp, vDp, 0, vDp);
                    page.add(page_left.build())
                        .add(shower.build())
                        .add(page_right.build());

                    top.add(page.build());
                }

                {
                    var search = LayoutBuilder.newLinerBuilder(requireContext());
                    search
                        .hOrientation()
                        .vGravity(Gravity.CENTER_VERTICAL);

                    var icon = ViewBuilder.wrapLinear(new ImageView(requireContext()));
                    icon.view().setImage(Image.create(Client.ASSETS_LOCATION, "icons/16px/search.png"));
                    icon.params()
                        .margin(base.dp(3), base.dp(3), base.dp(8), base.dp(3))
                        .h_wrap_content().v_wrap_content();
                    search.add(icon.build(), 0);

                    var box = ViewBuilder.wrapLinear(new EditText(requireContext()));
                    box.view().setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    box.view().setHint("Search...");
                    box.view().setHintTextColor(HINT_FONT_COLOR);
                    box.view().setSingleLine();
                    box.view().setMaxWidth(base.dp(250));
                    box.view().setTextSize(14);
                    box.view().setTextStyle(Paint.BOLD);
                    box.view().setTextColor(FONT_COLOR);
                    box.view().setFilters(new InputFilter.LengthFilter(32));
                    box.view().setOnKeyListener((view, b, c) -> {
                        this.mPager.setSearch(((EditText) view).getText().toString());
                        return false;
                    });

                    box.params().width(base.dp(250));
                    search.add(box.build(), 1);
                   /* {
                        int dp4 = base.dp(4);
                        ShapeDrawable drawable = new ShapeDrawable();
                        drawable.setShape(ShapeDrawable.RECTANGLE);
                        drawable.setPadding(dp4, dp4, dp4, dp4);
                        drawable.setColor(new Color(80, 80, 80, 20).getRGB());
                        drawable.setCornerRadius(base.dp(T_ICON_PTS));
                        search.bg(drawable);
                    }*/

                    search.params()
                        .gravity(Gravity.CENTER)
                        .margin(base.dp(40), 0, 0, 0);

                    top.add(search.build());
                }

                {
                    var time = ViewBuilder.wrapLinear(new TextView(requireContext()));
                    time.params()
                        .margin(base.dp(30), 0, base.dp(5), 0)
                        .v_wrap_content().h_wrap_content();

                    time.view().setTextColor(FONT_COLOR);
                    time.view().setText("22:30"); // Create time awa..
                    time.view().setTextStyle(Paint.BOLD);
                    time.view().setTextIsSelectable(true);
                    time.view().setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    mTimeView = time.view();

                    top.add(time.build());
                }

                int dp15 = base.dp(15);
                var params = top.relative_params()
                    .parent_mode()
                    .hCenter().top()
                    .margin(dp15, dp15, dp15, dp15)
                    .h_match_parent();

                center.add(top.build(params), 0);
            }

            {
                var view = LayoutBuilder.newLinerBuilder(requireContext());
                {
                    ShapeDrawable drawable = new ShapeDrawable();
                    drawable.setShape(ShapeDrawable.RECTANGLE);
                    drawable.setColor(DEEP_BACKGROUND_COLOR);
                    drawable.setStroke(base.dp(1), EDGE_SIDES_COLOR);
                    drawable.setCornerRadius(base.dp(TAB_MARGIN_PTS));
                    view.bg(drawable);
                }

                var pager = ViewBuilder.wrapLinear(this.mPager);
                pager.params().margin(base.dp(10), base.dp(10), base.dp(10), base.dp(10));
                pager.view().acceptSaver(saver);
                view.add(pager.build());

                var params = view
                    .relative_params()
                    .center()
                    .margin(base.dp(10), base.dp(10), base.dp(10), base.dp(10))
                    .height(base.dp(350)).h_match_parent();

                center.add(view.build(params), 1);
            }

            {
                var bottom = LayoutBuilder.newLinerBuilder(requireContext());

                var view = ViewBuilder.wrapLinear(new TextView(requireContext()));
                this.mTitle = view.view();

                view.view().setEllipsize(TextUtils.TruncateAt.START);
                view.view().setTextColor(FONT_COLOR);
                view.view().setTextSize(12);
                view.view().setTextStyle(Paint.BOLD);
                view.view().setText("Combat>");

                bottom.add(view.build(), 0);
                int dp15 = base.dp(15);
                var params = bottom.relative_params()
                    .parent_mode()
                    .hCenter().bottom()
                    .margin(dp15, 0, dp15, dp15)
                    .h_match_parent();

                center.add(bottom.build(params), 2);
            }

            center.params().h_match_parent().v_match_parent();

            base.add(center.build(), 1);
        }

        { // Actions
            this.mTimeView.post(this::updateTime);
        }

        main.add(base.build());
        return main.build();
    }

    private void updateTime() {
        LocalTime time = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        this.mTimeView.setText(time.format(formatter));
        this.mTimeView.postDelayed(this::updateTime, 1000);
    }

    @Override
    public void onDestroyView() {
        var adapter = ((Pager.Adapter) this.mPager.getAdapter());
        if (adapter.mScrollView != null) {
            saver.pager_scroll = NULL;
        }
        super.onDestroyView();
    }

    public static class Pager extends ViewPager {
        private final GMainGui parent;
        private Category cat;
        private String search = "", lastSearch = "last";
        private Module current;

        public Pager(Context context, GMainGui parent, Category cat) {
            super(context);
            this.cat = cat;
            this.parent = parent;

            setAdapter(this.new Adapter(cat));
            setFocusableInTouchMode(true);
            setKeyboardNavigationCluster(true);
            setEdgeEffectColor(EDGE_SIDES_COLOR);

            this.post(this::update);

            {
                var indicator = new LinearPagerIndicator(getContext());
                indicator.setPager(this);
                indicator.setLineWidth(dp(2));
                indicator.setLineColor(LINE_PAGE_COLOR);
                var lp = new ViewPager.LayoutParams();
                lp.height = dp(2);
                lp.isDecor = true;
                lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
                addView(indicator, lp);
            }

            var lp = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
            lp.gravity = Gravity.CENTER;
            setLayoutParams(lp);
        }

        public void setSearch(String s) {
            if (s == null) s = "";
            this.search = s;

            if (!lastSearch.equals(search)) {
                lastSearch = search;
                if (this.getAdapter() != null) {
                    Adapter adapter = (Adapter) this.getAdapter();
                    adapter.mods(getContext());
                    if (adapter.onSearchChanged != null) {
                        adapter.onSearchChanged.run();
                    }
                }
            }
        }

        public void setCat(Category c) {
            if (c.hashCode() != this.cat.hashCode()) {
                this.cat = c;
                if (getCurrentItem() == SETTINGS_PAGE)
                    this.setCurrentItem(MODULES_PAGE, true);
                this.setAdapter(new Adapter(this.cat));
            }
        }

        public void setModule(Module m) {
            Adapter adapter = (Adapter) this.getAdapter();
            current = m;
            adapter.sets(current == null ? null : current.settings, getContext());
            setCurrentItem(current == null ? MODULES_PAGE : SETTINGS_PAGE, true);
            parent.saver.last_select_module = m;
        }

        public void acceptSaver(MainInfoSaver saver) {
            setCat(saver.last_select_category);
            if (saver.last_select_module != null && saver.last_select_module.category.equals(saver.last_select_category))
                setModule(saver.last_select_module);
        }

        private static final int MODULES_PAGE = 0;
        private static final int SETTINGS_PAGE = 1;
        private static final int SETTINGS_VIEW_ID = Client.DEV.hashCode();

        private void update() {
            parent.mShower.setCurrent(getCurrentItem(), false);

            if (getCurrentItem() == 0) {
                parent.mTitle.setText(this.cat == CONFIG_CAT ? "Configs" : this.cat.title + ">");
            }
            if (getCurrentItem() == 1 && this.cat != CONFIG_CAT) {
                String txt = "no select";

                if (current != null) {
                    txt = current.title;
                }

                parent.mTitle.setText("...>" + txt);
            }
            this.postDelayed(this::update, 100);
        }

        public class Adapter extends PagerAdapter {
            private final Category cat;

            private LinearLayout mSettings;
            private LinearLayout mModules;

            public ScrollView mScrollView;

            public Runnable onSearchChanged;

            private final List<Module> MODULES = new ArrayList<>();

            public Adapter(Category cat) {
                this.cat = cat;
            }

            @Override
            public int getCount() {
                return 2;
            }

            private LinearLayout mods(Context context) {
                if (mModules == null) {
                    var builder = LayoutBuilder.newLinerBuilder(context);
                    builder.params().v_match_parent().h_match_parent();
                    mModules = builder.build();
                }

                mModules.removeAllViews();

                var base = LayoutBuilder.newLinerBuilder(context);
                base
                    .vOrientation()
                    .hGravity(Gravity.LEFT);

                MODULES.clear();
                MODULES.addAll(Modules.get().getModulesByCategory(this.cat));
                if (!search.isEmpty()) {
                    MODULES.clear();
                    MODULES.addAll(Modules.get().searchTitles(search));
                }

                for (Module module : MODULES) {
                    var src = LayoutBuilder.newLinerBuilder(context);
                    src
                        .vOrientation()
                        .vGravity(Gravity.CENTER_VERTICAL);

                    var title = ViewBuilder.wrapLinear(new TextView(context));
                    title.view().setText(module.title);
                    title.view().setTextColor(FONT_COLOR);
                    title.view().setTextSize(14);
                    title.params()
                        .margin(base.dp(2), base.dp(1), base.dp(2), base.dp(1))
                        .h_match_parent();

                    var desc = ViewBuilder.wrapLinear(new TextView(context));
                    desc.view().setText(module.description);
                    desc.view().setTextColor(L_FONT_COLOR);
                    desc.view().setTextSize(10);
                    desc.params()
                        .margin(base.dp(2), base.dp(1), base.dp(2), base.dp(1))
                        .h_match_parent();

                    src.add(title.build()).add(desc.build());

                    var actions = LayoutBuilder.newLinerBuilder(context);
                    actions
                        .hOrientation()
                        .vGravity(Gravity.CENTER_VERTICAL)
                        .hGravity(Gravity.CENTER_HORIZONTAL);

                    var bookmark = ViewBuilder.wrapLinear(new ImageButton(context));
                    bookmark.view().setImage(BOOKMARK_IMAGE);
                    bookmark.params().margin(0, 0, base.dp(10), 0);

                    var switch_btn = ViewBuilder.wrapLinear(new SwitchButton(context));
                    switch_btn.view().setChecked(module.isActive());
                    switch_btn.view().setOnCheckedChangeListener((__, checked) -> {
                        if (checked && !module.isActive()) {
                            module.toggle();
                        } else if (!checked && module.isActive()) {
                            module.toggle();
                        }
                    });
                    switch_btn.params().height(base.dp(16)).width(base.dp(32));

                    {
                        int dp2 = base.dp(2);
                        ShapeDrawable drawable = new ShapeDrawable();
                        drawable.setShape(ShapeDrawable.RECTANGLE);
                        drawable.setPadding(dp2, dp2, dp2, dp2);
                        drawable.setColor(BACKGROUND_COLOR);
                        drawable.setCornerRadius(base.dp(7));
                        actions.bg(drawable);
                    }

                    actions.add(bookmark.build(), 0).add(switch_btn.build(), 1);

                    actions.params()
                        .margin(base.dp(5), base.dp(3), base.dp(1), base.dp(2))
                        .v_wrap_content().h_wrap_content();

                    src.add(actions.build());
                    {
                        int dp2 = base.dp(3);
                        ShapeDrawable drawable = new ShapeDrawable();
                        drawable.setShape(ShapeDrawable.RECTANGLE);
                        drawable.setPadding(dp2, dp2, dp2, dp2);
                        drawable.setColor(new Color(0, 0, 0, 50).getRGB());
                        drawable.setCornerRadius(base.dp(7));
                        src.bg(drawable);
                    }

                    src.layout().setOnTouchListener((__, e) -> {
                        if (e.isCtrlPressed() && e.isButtonPressed(MotionEvent.BUTTON_PRIMARY)) {
                            module.toggle();
                            switch_btn.view().setChecked(module.isActive());
                            return true;
                        } else if (e.isButtonPressed(MotionEvent.BUTTON_SECONDARY)) {
                            current = module;
                            sets(current.settings, context);
                            setCurrentItem(SETTINGS_PAGE, true);
                            parent.saver.last_select_module = module;
                        }
                        return false;
                    });

                    src.params()
                        .margin(base.dp(5), base.dp(5), base.dp(5), base.dp(5))
                        .v_wrap_content().h_match_parent();
                    base.add(src.build());
                }

                base.params()
                    .margin(base.dp(2), 0, base.dp(2), 0)
                    .v_match_parent().h_match_parent();

                mModules.addView(base.build());
                return mModules;
            }

            private LinearLayout sets(Settings settings_, Context context) {
                if (mSettings == null) {
                    var builder = LayoutBuilder.newLinerBuilder(context);
                    builder.params().v_match_parent().h_match_parent();
                    mSettings = builder.build();
                }
                mSettings.removeAllViews();
                {
                    var base = LayoutBuilder.newLinerBuilder(context);
                    base.vOrientation().id(SETTINGS_VIEW_ID);

                    if (settings_ != null) {
                        if (settings_.sizeGroups() == 0) {
                            base.gravity(Gravity.CENTER);
                            base.params().v_match_parent().h_match_parent();

                            var empty = getEmpty(base, context);
                            empty.params().margin(0, base.dp(100), 0, 0).v_wrap_content().h_wrap_content();
                            base.add(empty.build());
                        } else {
                            base.hGravity(Gravity.CENTER_HORIZONTAL);

                            for (SettingGroup group : settings_.groups) {
                                var src = LayoutBuilder.newLinerBuilder(context);
                                src.vOrientation()
                                    .hGravity(Gravity.CENTER);

                                var settings = LayoutBuilder.newLinerBuilder(context);
                                settings.vOrientation().hGravity(Gravity.CENTER_HORIZONTAL);

                                {
                                    var relativeTop = LayoutBuilder.newRelativeBuilder(context);
                                    {
                                        var top = LayoutBuilder.newLinerBuilder(context);
                                        top.hOrientation()
                                            .vGravity(Gravity.CENTER_VERTICAL);

                                        var location = ViewBuilder.wrapLinear(new ImageView(context));
                                        var name = ViewBuilder.wrapLinear(new TextView(context));

                                        location.view().setImage(LOCATION_IMAGE);
                                        location.params().margin(0, base.dp(2), base.dp(7), base.dp(2));

                                        name.view().setTextColor(FONT_COLOR);
                                        name.view().setTextSize(10);
                                        name.view().setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                                        name.view().setText(group.name);

                                        top.add(location.build()).add(name.build());

                                        var params0 = top.relative_params()
                                            .parent_mode().vCenter().left()
                                            .margin(base.dp(7), base.dp(3), 0, base.dp(5))
                                            .h_wrap_content().height(base.dp(20));

                                        relativeTop.add(top.build(params0));
                                    }

                                    var expandAnimator = ObjectAnimator.ofFloat(settings.layout(),
                                        ViewAnimators.ALPHA_255, 0, 255);
                                    var expandAnimator1 = ObjectAnimator.ofFloat(settings.layout(),
                                        ViewAnimators.ALPHA_255, 255, 0);

                                    expandAnimator.setInterpolator(TimeInterpolator.ACCELERATE_DECELERATE);
                                    expandAnimator1.setInterpolator(TimeInterpolator.ACCELERATE_DECELERATE);

                                    {
                                        var expand = ViewBuilder.wrapLinear(new ImageView(context));
                                        expand.view().setImage(ARROW_RIGHT_IMAGE);
                                        expand.params().margin(0, base.dp(2), base.dp(5), base.dp(2));
                                        expand.view().setRotation(group.sectionExpanded ? 90 : 0);

                                        var arrow_o2f_Animator = ObjectAnimator.ofFloat(expand.view(),
                                            View.ROTATION, 90, 0);
                                        var arrow_f2o_Animator = ObjectAnimator.ofFloat(expand.view(),
                                            View.ROTATION, 0, 90);
                                        arrow_o2f_Animator.setInterpolator(TimeInterpolator.DECELERATE_QUINTIC);
                                        arrow_f2o_Animator.setInterpolator(TimeInterpolator.DECELERATE_QUINTIC);


                                        relativeTop.layout().setOnClickListener((v) -> {
                                            if (group.sectionExpanded) {
                                                expandAnimator1.addListener(new AnimatorListener() {
                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        src.layout().removeView(settings.layout());
                                                    }
                                                });

                                                expandAnimator1.start();
                                                arrow_o2f_Animator.start();
                                            }

                                            group.sectionExpanded = !group.sectionExpanded;

                                            if (group.sectionExpanded) {
                                                if (expandAnimator1.isRunning()) {
                                                    expandAnimator1.cancel();
                                                }
                                                if (arrow_o2f_Animator.isRunning()) {
                                                    arrow_o2f_Animator.cancel();
                                                }
                                                src.layout().addView(settings.layout());
                                                expandAnimator.start();
                                                arrow_f2o_Animator.start();
                                            }

                                            expand.view().setRotation(group.sectionExpanded ? 90 : 0);
                                        });

                                        var params = expand.relative_params()
                                            .parent_mode().vCenter().right()
                                            .h_wrap_content().height(20);
                                        relativeTop.add(expand.build(params));
                                    }

                                    relativeTop.params()
                                        .h_match_parent().v_wrap_content();
                                    src.add(relativeTop.build());
                                }

                                if (group.settings.isEmpty()) {
                                    var empty = getEmpty(src, context);
                                    empty.params().margin(base.dp(5), base.dp(5), base.dp(5), base.dp(5)).v_wrap_content().h_wrap_content();
                                    settings.add(empty.build());
                                } else {
                                    int id = 0;
                                    for (Setting<?> setting : group.settings) {
                                        View view = setting.createView(context, settings.layout());
                                        if (view != null) {
                                            view.setId(id + (114 * 514));
                                            settings.add(view);
                                            view.post(() -> setting.checkVisible0(context, view));
                                            id++;
                                        }
                                    }
                                }

                                settings.params()
                                    .margin(base.dp(5), base.dp(5), base.dp(3), base.dp(5))
                                    .h_match_parent().v_wrap_content();

                                if (group.sectionExpanded)
                                    src.add(settings.build());

                                src.params()
                                    .margin(base.dp(5), base.dp(5), base.dp(5), base.dp(5))
                                    .v_wrap_content().h_match_parent();

                                {
                                    int dp2 = base.dp(2);
                                    ShapeDrawable drawable = new ShapeDrawable();
                                    drawable.setShape(ShapeDrawable.RECTANGLE);
                                    drawable.setPadding(dp2, dp2, dp2, dp2);
                                    drawable.setColor(new Color(0, 0, 0, 50).getRGB());
                                    drawable.setCornerRadius(base.dp(7));
                                    src.bg(drawable);
                                }

                                base.add(src.build());
                            }

                            base.params()
                                .margin(base.dp(5), base.dp(3), base.dp(3), base.dp(5))
                                .h_match_parent();
                        }
                    } else {
                        base.gravity(Gravity.CENTER);
                        base.params().v_match_parent().h_match_parent();

                        var empty = getEmpty(base, context);
                        empty.params().margin(0, base.dp(100), 0, 0).v_wrap_content().h_wrap_content();
                        base.add(empty.build());
                    }

                    mSettings.addView(base.build());
                }
                return mSettings;
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

            private LayoutBuilder.LinearLayoutBuilder<LinearLayout> config_empty(LayoutBuilder.LinearLayoutBuilder<LinearLayout> base, Context context) {
                var empty = LayoutBuilder.newLinerBuilder(context);
                empty.vOrientation().hGravity(Gravity.CENTER).vGravity(Gravity.CENTER);

                var img = ViewBuilder.wrapLinear(new ImageView(context));
                var txt = ViewBuilder.wrapLinear(new TextView(context));

                img.view().setImage(EMPTY_IMAGE);
                img.params().margin(base.dp(5), base.dp(5), base.dp(5), base.dp(10));
                txt.view().setText("哎哟我去 还没做完 \uD83D\uDE05\uD83D\uDE05\uD83D\uDE05");
                txt.view().setTextColor(FONT_COLOR);

                empty.add(img.build(), 0)
                    .add(txt.build(), 1);
                return empty;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                var context = container.getContext();
                var sv = new ScrollView(context);
                this.mScrollView = sv;
                if (parent.saver.pager_scroll != NULL)
                    mScrollView.smoothScrollTo(parent.saver.pager_scroll);

                if (position == 1) {
                    if (this.cat != CONFIG_CAT) {
                        sv.addView(sets(current == null ? null : current.settings, context));
                    } else {
                        var base = LayoutBuilder.newLinerBuilder(context);
                        base.gravity(Gravity.CENTER);
                        base.params().v_match_parent().h_match_parent();

                        var empty = config_empty(base, context);
                        //empty.params().margin(0, base.dp(100), 0, 0).v_wrap_content().h_wrap_content();
                        base.add(empty.build());
                        sv.addView(base.build());
                    }
                } else {
                    sv.addView(this.cat == CONFIG_CAT ? sets(ClientConfig.get().settings, context) : mods(context));

                    var animator = ObjectAnimator.ofFloat(sv,
                        ViewAnimators.ALPHA_255, 0, 255);
                    animator.setInterpolator(TimeInterpolator.DECELERATE_QUINTIC);

                    onSearchChanged = animator::start;

                    sv.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
                                                   int oldTop, int oldRight, int oldBottom) {
                            if (onSearchChanged != null) {
                                onSearchChanged.run();
                            } else {
                                animator.start();
                            }
                            v.removeOnLayoutChangeListener(this);
                        }
                    });
                }
                sv.setEdgeEffectColor(EDGE_SIDES_COLOR);

                var params = new LinearLayout.LayoutParams(0, MATCH_PARENT, 1);
                var dp6 = sv.dp(6);
                params.setMargins(dp6, dp6, dp6, dp6);
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
