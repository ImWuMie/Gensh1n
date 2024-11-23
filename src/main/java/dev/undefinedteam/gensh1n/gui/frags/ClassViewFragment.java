package dev.undefinedteam.gensh1n.gui.frags;

import com.codewaves.codehighlight.core.Highlighter;
import com.codewaves.codehighlight.core.StyleRenderer;
import dev.undefinedteam.gensh1n.gui.builders.LayoutBuilder;
import dev.undefinedteam.gensh1n.gui.builders.ViewBuilder;
import dev.undefinedteam.gensh1n.jvm.ClassSub;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.graphics.drawable.ShapeDrawable;
import icyllis.modernui.material.MaterialRadioButton;
import icyllis.modernui.text.*;
import icyllis.modernui.text.style.ForegroundColorSpan;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.LayoutInflater;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.HorizontalScrollView;
import icyllis.modernui.widget.ScrollView;
import icyllis.modernui.widget.TextView;
import org.objectweb.asm.tree.ClassNode;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static dev.undefinedteam.gensh1n.gui.frags.GMainGui.*;

public class ClassViewFragment extends Fragment {
    public static final int KEYWORD_COLOR = new Color(246,153,180).getRGB();
    public static final int STRING_COLOR = new Color(255, 159, 85).getRGB();
    public static final int NUM_COLOR = new Color(112, 218, 255).getRGB();
    public static final int TITLE_COLOR = new Color(255, 210, 112).getRGB();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, DataSet savedInstanceState) {
        var base = LayoutBuilder.newLinerBuilder(requireContext());
        base.hOrientation().hGravity(Gravity.CENTER_HORIZONTAL);
        base.params().v_wrap_content().h_match_parent();
        {
            ShapeDrawable drawable = new ShapeDrawable();
            drawable.setShape(ShapeDrawable.RECTANGLE);
            drawable.setColor(DEEP_BACKGROUND_COLOR);
            drawable.setStroke(base.dp(1), EDGE_SIDES_COLOR);
            drawable.setCornerRadius(base.dp(3));
            base.bg(drawable);
        }

        var scrollView = ViewBuilder.wrapLinear(new ScrollView(requireContext()));
        var classTree = LayoutBuilder.newRadioGroupBuilder(requireContext());
        int classId = 77;

        Map<Integer, ClassNode> classNodeMap = new HashMap<>();
        for (ClassNode aClass : ClassSub.get().classes) {
            var button = ViewBuilder.wrapLinear(new MaterialRadioButton(requireContext()));

            button.view().setEllipsize(TextUtils.TruncateAt.START);
            button.view().setMaxWidth(base.dp(200));
            button.view().setText(aClass.name);
            button.view().setId(classId);
            button.view().setSingleLine();
            classTree.add(button.build());

            classNodeMap.put(classId, aClass);
            classId++;
        }


        classTree.params().width(base.dp(200));
        scrollView.view().addView(classTree.build());
        scrollView.params().margin(0, 0, base.dp(5), 0).v_match_parent().h_wrap_content();

        var hScroll = ViewBuilder.wrapLinear(new HorizontalScrollView(requireContext()));
        var view = ViewBuilder.wrapLinear(new TextView(requireContext()));
        view.view().setTextIsSelectable(true);
        view.view().setHorizontalScrollBarEnabled(true);
        view.view().setVerticalScrollBarEnabled(true);
        //view.view().setEllipsize(TextUtils.TruncateAt.END);
        view.view().setTextStyle(Paint.BOLD);
        view.view().setTypeface(Typeface.MONOSPACED);
        view.view().setTextSize(13);
        view.view().setFallbackLineSpacing(false);
        {
            int dp2 = base.dp(2);
            ShapeDrawable drawable = new ShapeDrawable();
            drawable.setShape(ShapeDrawable.RECTANGLE);
            drawable.setPadding(dp2, dp2, dp2, dp2);
            drawable.setColor(new Color(0, 0, 0, 80).getRGB());
            drawable.setCornerRadius(base.dp(7));
            view.bg(drawable);
        }
        hScroll.params().h_match_parent().v_match_parent();
        hScroll.view().addView(view.build());

        classTree.onCheck(id -> {
            var klass = classNodeMap.get(id);
            String src = ClassSub.get().decompile(klass);
            view.view().setText(src);
            prepareCodeStyle(view.view(),src);
        });

        base.add(scrollView.build()).add(hScroll.build());
        return base.build();
    }


    private void prepareCodeStyle(TextView tv, String src) {
        Spannable spannable = new SpannableString(src);

        final Highlighter highlighter = new Highlighter(languageName -> new JHRender());
        final Highlighter.HighlightResult result = highlighter.highlight("java",
            src);
        final String styledCode = String.valueOf(result.getResult());

        String[] lines = styledCode.split("\n");
        Map<Integer[],String> res = new HashMap<>();
        for (String line : lines) {
            if (!line.isEmpty()) {
                var data = line.split("\\|");
                int start = Integer.parseInt(data[0]);
                int end = Integer.parseInt(data[1]);
                String style = data.length == 3 ? data[2] : "";
                res.put(new Integer[]{start, end}, style);
            }
        }

        res.forEach((range,style) -> {
            switch (style) {
                case "keyword" -> spannable.setSpan(new ForegroundColorSpan(KEYWORD_COLOR), range[0],range[1], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                case "string" -> spannable.setSpan(new ForegroundColorSpan(STRING_COLOR), range[0],range[1], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                case "number" -> spannable.setSpan(new ForegroundColorSpan(NUM_COLOR), range[0],range[1], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                case "title" -> spannable.setSpan(new ForegroundColorSpan(TITLE_COLOR), range[0],range[1], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        });


        var precomputed = PrecomputedText.create(spannable, tv.getTextMetricsParams());
        tv.post(() -> tv.setText(precomputed, TextView.BufferType.SPANNABLE));
    }

    private static class JHRender implements StyleRenderer {
        public Map<Integer[], String> mResult = new HashMap<>();

        int position;

        @Override
        public void onStart() {
            mResult.clear();
            position = 0;
        }

        @Override
        public void onFinish() {

        }

        private String mStyle = "";

        @Override
        public void onPushStyle(String style) {
            if (!mStyle.isEmpty()) {
                mStyle += "|" + style;
                return;
            }

            mStyle = style;
        }

        @Override
        public void onPopStyle() {
        }

        @Override
        public void onPushCodeBlock(CharSequence codeLexeme) {
            int before = position;
            position += codeLexeme.length();
            mResult.put(new Integer[]{before, position}, mStyle);
            mStyle = "";
        }

        @Override
        public void onPushSubLanguage(String name, CharSequence code) {

        }

        @Override
        public void onAbort(CharSequence code) {

        }

        @Override
        public CharSequence getResult() {
            StringBuilder result = new StringBuilder();
            mResult.forEach((range, type) -> {
                result.append(range[0]).append("|")
                    .append(range[1]).append("|")
                    .append(type).append("\n");
            });
            return result.toString();
        }
    }
}
