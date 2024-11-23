/*
 * Modern UI.
 * Copyright (C) 2019-2023 BloCamLimb. All rights reserved.
 *
 * Modern UI is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Modern UI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Modern UI. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.undefinedteam.modernui.mc.text;

import com.ibm.icu.text.*;
import icyllis.modernui.ModernUI;
import icyllis.modernui.graphics.text.*;
import icyllis.modernui.text.TextDirectionHeuristic;
import icyllis.modernui.text.TextDirectionHeuristics;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.*;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;

/**
 * This is where the text layout is actually performed.
 *
 * @author BloCamLimb
 */
public class TextLayoutProcessor {

    /**
     * Compile-time only.
     */
    public static final boolean DEBUG = false;


    //public static volatile boolean sAlignPixels = false;
    public static volatile int sLbStyle = LineBreakConfig.LINE_BREAK_STYLE_NONE;
    public static volatile int sLbWordStyle = LineBreakConfig.LINE_BREAK_WORD_STYLE_NONE;

    private final TextLayoutEngine mEngine;

    public final float mFontSize;

    /**
     * Char array builder. Formatting codes will be stripped from this array.
     */
    private final CharSequenceBuilder mBuilder = new CharSequenceBuilder();
    /**
     * Style appearance flags in logical order. Same indexing with {@link #mBuilder}.
     */
    private final IntArrayList mStyles = new IntArrayList();
    /**
     * Font names to use in logical order. Same indexing with {@link #mStyles}.
     */
    private final ArrayList<Identifier> mFontNames = new ArrayList<>();

    /*
     * Array of temporary style carriers.
     */
    //private final List<CharacterStyle> mDStyles = new ArrayList<>();

    /**
     * All glyph IDs for rendering. Can be 0 for fast chars.
     * Can be unicode code point for bitmap.
     * The order is visually left-to-right (i.e. in visual order).
     */
    private final IntArrayList mGlyphs = new IntArrayList();
    /**
     * The glyph's font of {@link #mGlyphs}, same order.
     */
    private final ByteArrayList mFontIndices = new ByteArrayList();
    private final ArrayList<Font> mFontVec = new ArrayList<>();
    private final HashMap<Font, Byte> mFontMap = new HashMap<>();
    private final Function<Font, Byte> mNextID = font -> {
        mFontVec.add(font);
        return (byte) mFontMap.size();
    };
    /**
     * Position x1 y1 x2 y2... relative to the same point, for rendering glyphs.
     * These values are not offset to glyph additional baseline but aligned.
     * Same indexing with {@link #mGlyphs}, align to left, in visual order.
     * <p>
     * Note the values are scaled to Minecraft GUI coordinates.
     */
    private final FloatArrayList mPositions = new FloatArrayList();
    /**
     * The length and order are relative to the raw string (with formatting codes).
     * Only grapheme cluster bounds have advances, others are zeros. For example:
     * [13.57, 0, 14.26, 0, 0]. {@link #mGlyphs}.length may less than grapheme cluster
     * count (invisible glyphs are removed). Logical order.
     * <p>
     * Note the values are scaled to Minecraft GUI coordinates.
     */
    private final FloatArrayList mAdvances = new FloatArrayList();
    /**
     * Glyph rendering flags. Same indexing with {@link #mGlyphs}, in visual order.
     */
    /*
     * lower 24 bits - 0xRRGGBB color
     * higher 8 bits
     * |--------|
     *         1  BOLD
     *        1   ITALIC
     *       1    UNDERLINE
     *      1     STRIKETHROUGH
     *     1      OBFUSCATED
     *    1       FAST_DIGIT_REPLACEMENT
     *   1        BITMAP_REPLACEMENT
     *  1         IMPLICIT_COLOR
     * |--------|
     */
    private final IntArrayList mGlyphFlags = new IntArrayList();
    /*
     * Glyphs to relative char indices of the strip string (without formatting codes).
     * For vanilla layout ({@link VanillaLayoutKey} and {@link TextLayoutEngine#lookupVanillaLayout(String)}),
     * these will be adjusted to string index (with formatting codes).
     * Same indexing with {@link #mGlyphs}, in visual order.
     */
    //private final IntArrayList mCharIndices = new IntArrayList();
    /**
     * Strip indices that are boundaries for Unicode line breaking, this list will be
     * sorted into logical order. 0 is not included.
     */
    private final IntArrayList mLineBoundaries = new IntArrayList();

    /*
     * List of all processing glyphs
     */
    //private final List<BaseGlyphRender> mAllList = new ArrayList<>();

    /*
     * List of processing glyphs with same layout direction
     */
    //private final List<BaseGlyphRender> mBidiList = new ArrayList<>();

    //private final List<BaseGlyphRender> mTextList = new ArrayList<>();

    /*
     * All color states
     */
    //public final List<ColorStateInfo> colors = new ArrayList<>();

    /*
     * Indicates current style index in {@link #mDStyles} for layout processing
     */
    //private int mStyleIndex;

    /**
     * The total advance (horizontal width) of the processing text
     */
    private float mTotalAdvance;

    private final FontPaint mFontPaint = new FontPaint();

    /*
     * Needed in RTL layout
     */
    //private float mLayoutRight;

    /**
     * Mark whether this node should enable effect rendering
     */
    private boolean mHasEffect;
    //private boolean mHasFastDigit;
    private boolean mHasColorEmoji;

    private boolean mComputeAdvances = true;
    private boolean mComputeLineBoundaries = true;

    /**
     * Always LTR.
     */
    private final CharacterVisitor mSequenceBuilder = (index, style, codePoint) -> {
        int styleFlags = CharacterStyle.flatten(style);
        int charCount = mBuilder.addCodePoint(codePoint);
        while (charCount-- > 0) {
            mStyles.add(styleFlags);
            mFontNames.add(style.getFont());
        }
        return true;
    };

    /**
     * Transfer code points in logical order.
     */
    private final StringVisitable.StyledVisitor<Unit> mContentBuilder = (style, text) ->
        TextVisitFactory.visitFormatted(text, style, mSequenceBuilder)
            ? Optional.empty()
            : StringVisitable.TERMINATE_VISIT;

    public TextLayoutProcessor(@Nonnull TextLayoutEngine engine,float size) {
        mEngine = engine;
        mFontSize = size;
    }

    public int computeFontSize(int resLevel) {
        // Note max font size is 96
        return Math.min((int) mFontSize * resLevel, 96 * resLevel);
    }

    private void reset() {
        if (DEBUG) {
            if (mBuilder.length() != mStyles.size()) {
                throw new AssertionError();
            }
            if (mStyles.size() != mFontNames.size()) {
                throw new AssertionError();
            }
            /*if (mGlyphs.size() != mCharIndices.size()) {
                throw new AssertionError();
            }*/
            if (mGlyphs.size() * 2 != mPositions.size()) {
                throw new AssertionError();
            }
            if (mComputeAdvances &&
                mBuilder.length() != mAdvances.size()) {
                throw new AssertionError();
            }
            if (mGlyphs.size() != mGlyphFlags.size()) {
                throw new AssertionError();
            }
            if (mComputeLineBoundaries &&
                !mBuilder.isEmpty() &&
                mBuilder.length() != mLineBoundaries.getInt(mLineBoundaries.size() - 1)) {
                ModernUI.LOGGER.error("Last char cannot break line?");
            }
            if (mComputeAdvances &&
                Math.abs(mAdvances.doubleStream().sum() - mTotalAdvance) > 1) {
                ModernUI.LOGGER.error("Advance error is too large?");
            }
        }
        mBuilder.clear();
        mStyles.clear();
        mFontNames.clear();
        mGlyphs.clear();
        mFontIndices.clear();
        mFontVec.clear();
        mFontMap.clear();
        //mCharIndices.clear();
        mPositions.clear();
        mAdvances.clear();
        mGlyphFlags.clear();
        mLineBoundaries.clear();
        mTotalAdvance = 0;
        mHasEffect = false;
        //mHasFastDigit = false;
        mHasColorEmoji = false;
    }

    @Nonnull
    public TextLayout createVanillaLayout(@Nonnull String text, @Nonnull Style style,
                                          int resLevel, int computeFlags) {
        TextVisitFactory.visitFormatted(text, style, mSequenceBuilder);
        TextLayout layout = createNewLayout(resLevel, computeFlags);
        if (DEBUG) {
            ModernUI.LOGGER.info("Performed Vanilla Layout: {}, {}, {}",
                mBuilder.toString(), text, layout);
        }
        reset();
        return layout;
    }

    @Nonnull
    public TextLayout createTextLayout(@Nonnull StringVisitable text, @Nonnull Style style,
                                       int resLevel, int computeFlags) {
        text.visit(mContentBuilder, style);
        TextLayout layout = createNewLayout(resLevel, computeFlags);
        if (DEBUG) {
            ModernUI.LOGGER.info("Performed Text Layout: {}, {}, {}",
                mBuilder.toString(), text, layout);
        }
        reset();
        return layout;
    }

    @Nonnull
    public TextLayout createSequenceLayout(@Nonnull OrderedText sequence,
                                           int resLevel, int computeFlags) {
        sequence.accept(mSequenceBuilder);
        TextLayout layout = createNewLayout(resLevel, computeFlags);
        if (DEBUG) {
            ModernUI.LOGGER.info("Performed Sequence Layout: {}, {}, {}",
                mBuilder.toString(), sequence, layout);
        }
        reset();
        return layout;
    }

    /*
     * Formatting codes are not involved in rendering, so we should first extract formatting codes
     * from the raw string into a stripped text. The color codes must be removed for a font's
     * context-sensitive glyph substitution to work (like Arabic letter middle form) or Bidi analysis.
     * Results a new char array with all formatting codes removed from the given string.
     *
     * @param text      raw string with formatting codes to strip
     * @param baseStyle the base style if no formatting applied (initial or reset)
     * @see net.minecraft.util.StringDecomposer
     */
    /*private void buildVanilla(@Nonnull String text, @Nonnull final Style baseStyle) {
        int shift = 0;

        Style style = baseStyle;
        mDStyles.add(new CharacterStyle(0, 0, style, false));

        // also fix invalid surrogate pairs
        final int limit = text.length();
        for (int pos = 0; pos < limit; ++pos) {
            char c1 = text.charAt(pos);
            if (c1 == ChatFormatting.PREFIX_CODE) {
                if (pos + 1 >= limit) {
                    break;
                }

                ChatFormatting formatting = TextLayoutEngine.getFormattingByCode(text.charAt(pos + 1));
                if (formatting != null) {
                    *//* Classic formatting will set all FancyStyling (like BOLD, UNDERLINE) to false if it's a color
                    formatting *//*
                    style = formatting == ChatFormatting.RESET ? baseStyle : style.applyLegacyFormat(formatting);
                    mDStyles.add(new CharacterStyle(pos, pos - shift, style, true));
                }

                pos++;
                shift += 2;
            } else if (Character.isHighSurrogate(c1)) {
                if (pos + 1 >= limit) {
                    mBuilder.addChar(REPLACEMENT_CHAR);
                    break;
                }

                char c2 = text.charAt(pos + 1);
                if (Character.isLowSurrogate(c2)) {
                    mBuilder.addChar(c1);
                    mBuilder.addChar(c2);
                    ++pos;
                } else if (Character.isSurrogate(c1)) {
                    mBuilder.addChar(REPLACEMENT_CHAR);
                } else {
                    mBuilder.addChar(c1);
                }
            } else if (Character.isSurrogate(c1)) {
                mBuilder.addChar(REPLACEMENT_CHAR);
            } else {
                mBuilder.addChar(c1);
            }
        }

        *//*while ((next = string.indexOf('\u00a7', start)) != -1 && next + 1 < string.length()) {
            TextFormatting formatting = fromFormattingCode(string.charAt(next + 1));

            *//**//*
     * Remove the two char color code from text[] by shifting the remaining data in the array over on top of it.
     * The "start" and "next" variables all contain offsets into the original unmodified "str" string. The "shift"
     * variable keeps track of how many characters have been stripped so far, and it's used to compute offsets into
     * the text[] array based on the start/next offsets in the original string.
     *
     * If string only contains 1 formatting code (2 chars in total), this doesn't work
     *//**//*
            //System.arraycopy(text, next - shift + 2, text, next - shift, text.length - next - 2);

            if (formatting != null) {
                *//**//* forceFormatting will set all FancyStyling (like BOLD, UNDERLINE) to false if this is a color
                formatting *//**//*
                style = style.forceFormatting(formatting);


                data.codes.add(new FormattingStyle(next, next - shift, style));
            }

            start = next + 2;
            shift += 2;
        }*//*
    }*/

    /**
     * Perform text layout after building stripped characters (without formatting codes).
     *
     * @return the full layout result
     */
    @Nonnull
    private TextLayout createNewLayout(int resLevel, int computeFlags) {
        if (!mBuilder.isEmpty()) {
            // locale for GCB (grapheme cluster break)
            mFontPaint.setLocale(ModernUI.getSelectedLocale());

            mComputeAdvances = (computeFlags & TextLayoutEngine.COMPUTE_ADVANCES) != 0;
            mComputeLineBoundaries = (computeFlags & TextLayoutEngine.COMPUTE_LINE_BOUNDARIES) != 0;

            int fontSize = computeFontSize(resLevel);
            mFontPaint.setFontSize(fontSize);

            // pre allocate memory
            if (mComputeAdvances) {
                mAdvances.size(mBuilder.length());
            }
            // make a copied buffer
            final char[] textBuf = mBuilder.toCharArray();
            // steps 2-5
            analyzeBidi(textBuf);
            /*if (raw != null) {
                adjustForFastDigit(raw);
            }*/
            /*if (sAlignPixels) {
                float guiScale = mEngine.getGuiScale();
                mTotalAdvance = Math.round(mTotalAdvance * guiScale) / guiScale;
            }*/
            float[] positions = mPositions.toFloatArray();
            for (int i = 0; i < positions.length; i++) {
                positions[i] /= resLevel;
            }
            byte[] fontIndices;
            if (mFontVec.size() > 1) {
                fontIndices = mFontIndices.toByteArray();
            } else {
                fontIndices = null;
            }
            float[] advances;
            if (mComputeAdvances) {
                advances = mAdvances.toFloatArray();
                for (int i = 0; i < mBuilder.length(); i++) {
                    advances[i] /= resLevel;
                }
            } else {
                advances = null;
            }
            int[] lineBoundaries;
            if (mComputeLineBoundaries) {
                lineBoundaries = mLineBoundaries.toIntArray();
                // sort line boundaries to logical order, because runs are in visual order
                Arrays.sort(lineBoundaries);
            } else {
                lineBoundaries = null;
            }
            mTotalAdvance /= resLevel;
            return new TextLayout(textBuf, mGlyphs.toIntArray(),
                positions, fontIndices,
                mFontVec.toArray(new Font[0]),
                advances, mGlyphFlags.toIntArray(),
                lineBoundaries,
                mFontSize, mTotalAdvance,
                mHasEffect, mHasColorEmoji, resLevel, computeFlags);
        }
        return TextLayout.makeEmpty();
    }

    /**
     * Split the full text into contiguous LTR or RTL sections by applying the Unicode Bidirectional Algorithm. Calls
     * performBidiAnalysis() for each contiguous run to perform further analysis.
     *
     * @param text the full plain text (without formatting codes) to analyze in logical order
     * @see #handleBidiRun(char[], int, int, boolean)
     */
    private void analyzeBidi(@Nonnull char[] text) {
        TextDirectionHeuristic dir = mEngine.getTextDirectionHeuristic();
        /* Avoid performing full bidirectional analysis if text has no "strong" right-to-left characters */
        if ((dir == TextDirectionHeuristics.LTR
            || dir == TextDirectionHeuristics.FIRSTSTRONG_LTR
            || dir == TextDirectionHeuristics.ANYRTL_LTR)
            && !Bidi.requiresBidi(text, 0, text.length)) {
            /* If text is entirely left-to-right, then insert a node for the entire string */
            if (DEBUG) {
                ModernUI.LOGGER.info("All LTR");
            }
            handleBidiRun(text, 0, text.length, false);
        } else {
            final byte paraLevel;
            if (dir == TextDirectionHeuristics.LTR) {
                paraLevel = Bidi.LTR;
            } else if (dir == TextDirectionHeuristics.RTL) {
                paraLevel = Bidi.RTL;
            } else if (dir == TextDirectionHeuristics.FIRSTSTRONG_LTR) {
                paraLevel = Bidi.LEVEL_DEFAULT_LTR;
            } else if (dir == TextDirectionHeuristics.FIRSTSTRONG_RTL) {
                paraLevel = Bidi.LEVEL_DEFAULT_RTL;
            } else {
                final boolean isRtl = dir.isRtl(text, 0, text.length);
                paraLevel = isRtl ? Bidi.RTL : Bidi.LTR;
            }
            Bidi bidi = new Bidi(text.length, 0);
            bidi.setPara(text, paraLevel, null);

            /* If text is entirely right-to-left, then insert a node for the entire string */
            if (bidi.isRightToLeft()) {
                if (DEBUG) {
                    ModernUI.LOGGER.info("All RTL (analysis)");
                }
                handleBidiRun(text, 0, text.length, true);
            }
            /* If text is entirely left-to-right, then insert a node for the entire string */
            else if (bidi.isLeftToRight()) {
                if (DEBUG) {
                    ModernUI.LOGGER.info("All LTR (analysis)");
                }
                handleBidiRun(text, 0, text.length, false);
            }
            /* Otherwise text has a mixture of LTR and RLT, and it requires full bidirectional analysis */
            else {
                int runCount = bidi.getRunCount();
                //byte[] runs = new byte[runCount];

                /* Reorder contiguous runs of text into their display order from left to right */
                /*for (int i = 0; i < runCount; i++) {
                    runs[i] = (byte) bidi.getRunLevel(i);
                }
                int[] indexMap = Bidi.reorderVisual(runs);*/

                /*
                 * Every GlyphVector must be created on a contiguous run of left-to-right or right-to-left text. Keep
                 * track of the horizontal advance between each run of text, so that the glyphs in each run can be
                 * assigned a position relative to the start of the entire string and not just relative to that run.
                 */
                for (int visualIndex = 0; visualIndex < runCount; visualIndex++) {
                    /*int logicalIndex = indexMap[visualIndex];
                    performBidiRun(text, bidi.getRunStart(logicalIndex), bidi.getRunLimit(logicalIndex),
                            (bidi.getRunLevel(logicalIndex) & 1) != 0, fastDigit);*/

                    /* An odd numbered level indicates right-to-left ordering */
                    BidiRun run = bidi.getVisualRun(visualIndex);
                    if (DEBUG) {
                        ModernUI.LOGGER.info("VisualRun {}, {}", visualIndex, run);
                    }
                    handleBidiRun(text, run.getStart(), run.getLimit(), run.isOddRun());
                }
            }
        }
    }

    /**
     * Analyze the best matching font and paragraph context, according to layout direction and generate glyph vector.
     * In some languages, the original Unicode code is mapped to another Unicode code for visual rendering.
     * They will finally be converted into glyph codes according to different Font. This run is in visual order.
     *
     * @param text  the plain text (without formatting codes) in logical order
     * @param start start index (inclusive) of the text
     * @param limit end index (exclusive) of the text
     * @param isRtl layout direction
     * @see #handleStyleRun(char[], int, int, boolean, int, Identifier)
     */
    private void handleBidiRun(@Nonnull char[] text, int start, int limit, boolean isRtl) {
        final IntArrayList styles = mStyles;
        final List<Identifier> fonts = mFontNames;
        int lastPos, currPos;
        int lastStyle, currStyle;
        Identifier lastFont, currFont;
        // Style runs are in visual order
        if (isRtl) {
            lastPos = limit;
            currPos = limit - 1;
            lastStyle = styles.getInt(currPos);
            lastFont = fonts.get(currPos);
            currStyle = lastStyle;
            currFont = lastFont;
            while (currPos > start) {
                if ((currStyle = styles.getInt(currPos - 1)) != lastStyle ||
                    (currFont = fonts.get(currPos - 1)) != lastFont) {
                    handleStyleRun(text, currPos, lastPos, true,
                        lastStyle, lastFont);
                    lastPos = currPos;
                    lastStyle = currStyle;
                    lastFont = currFont;
                }
                currPos--;
            }
            assert currPos == start;
            handleStyleRun(text, currPos, lastPos, true,
                currStyle, currFont);
        } else {
            lastPos = start;
            currPos = start;
            lastStyle = styles.getInt(currPos);
            lastFont = fonts.get(currPos);
            currStyle = lastStyle;
            currFont = lastFont;
            while (currPos + 1 < limit) {
                currPos++;
                if ((currStyle = styles.getInt(currPos)) != lastStyle ||
                    (currFont = fonts.get(currPos)) != lastFont) {
                    handleStyleRun(text, lastPos, currPos, false,
                        lastStyle, lastFont);
                    lastPos = currPos;
                    lastStyle = currStyle;
                    lastFont = currFont;
                }
            }
            assert currPos + 1 == limit;
            handleStyleRun(text, lastPos, currPos + 1, false,
                currStyle, currFont);
        }
    }

    /**
     * Analyze the best matching font and paragraph context, according to layout direction and generate glyph vector.
     * In some languages, the original Unicode code is mapped to another Unicode code for visual rendering.
     * They will finally be converted into glyph codes according to different Font. This run is in visual order.
     *
     * @param text       the plain text (without formatting codes) to analyze in logical order
     * @param start      start index (inclusive) of the text
     * @param limit      end index (exclusive) of the text
     * @param isRtl      layout direction
     * @param styleFlags the style to lay out the text
     * @param fontName   the font name to lay out the text
     * @see FontCollection#itemize(char[], int, int)
     */
    private void handleStyleRun(@Nonnull char[] text, int start, int limit, boolean isRtl,
                                int styleFlags, Identifier fontName) {
        /*if (fastDigit) {
         *//*
         * Convert all digits in the string to a '0' before layout to ensure that any glyphs replaced on the fly
         * will all have the same positions. Under Windows, Java's "SansSerif" logical font uses the "Arial" font
         * for digits, in which the "1" digit is slightly narrower than all other digits. Digits are not on SMP.
         *//*
            for (int i = start; i < limit; i++) {
                if (text[i] <= '9' && text[i] >= '0' &&
                        // also check COMBINING ENCLOSING KEYCAP, don't break GCB
                        (i + 1 >= limit || text[i + 1] != Emoji.VARIATION_SELECTOR_16)) {
                    text[i] = '0';
                }
            }
        }*/

        int fontStyle = FontPaint.NORMAL;
        if ((styleFlags & CharacterStyle.BOLD_MASK) != 0) {
            fontStyle |= FontPaint.BOLD;
        }
        if ((styleFlags & CharacterStyle.ITALIC_MASK) != 0) {
            fontStyle |= FontPaint.ITALIC;
        }

        mFontPaint.setFont(mEngine.getFontCollection(fontName,this.mFontSize));
        mFontPaint.setFontStyle(fontStyle);

        if ((styleFlags & CharacterStyle.OBFUSCATED_MASK) == 0) {
            int glyphStart = mGlyphs.size();

            float advance = ShapedText.doLayoutRun(
                text, start, limit, start, limit,
                isRtl, mFontPaint, 0, // <- text array starts at 0
                mComputeAdvances ? mAdvances.elements() : null,
                mTotalAdvance, mGlyphs, mPositions,
                mFontIndices, f -> mFontMap.computeIfAbsent(f, mNextID),
                null, null
            );

            for (int glyphIndex = glyphStart,
                 glyphEnd = mGlyphs.size();
                 glyphIndex < glyphEnd;
                 glyphIndex++) {
                mHasEffect |= (styleFlags & CharacterStyle.EFFECT_MASK) != 0;
                int glyphFlags = styleFlags;
                var font = mFontVec.get(mFontIndices.getByte(glyphIndex));
                if (font instanceof BitmapFont) {
                    glyphFlags |= CharacterStyle.BITMAP_REPLACEMENT;
                } else if (font instanceof EmojiFont) {
                    glyphFlags |= CharacterStyle.COLOR_EMOJI_REPLACEMENT | 0xFFFFFF;
                    glyphFlags &= ~CharacterStyle.IMPLICIT_COLOR_MASK;
                    mHasColorEmoji = true;
                }
                mGlyphFlags.add(glyphFlags);
            }

            mTotalAdvance += advance;
        } else {
            final var items = mFontPaint.getFont()
                .itemize(text, start, limit);
            // Font runs are in visual order
            for (int runIndex = isRtl ? items.size() - 1 : 0;
                 isRtl ? runIndex >= 0 : runIndex < items.size();
            ) {
                var run = items.get(runIndex);

                var font = run.getBestFont(text, fontStyle);
                int runStart = run.start();
                int runLimit = run.limit();

                float adv = font.doSimpleLayout(new char[]{'0'},
                    0, 1, mFontPaint, null, null, 0, 0);
                if (adv > 0) {
                    float offset = mTotalAdvance;
                    byte fontIdx = mFontMap.computeIfAbsent(font, mNextID);

                    // Process code point in visual order
                    for (int i = runStart; i < runLimit; i++) {
                        if (mComputeAdvances) {
                            mAdvances.set(i, adv);
                        }

                        float pos = offset;

                        mGlyphs.add(0);
                        mPositions.add(pos);
                        mPositions.add(0);
                        mFontIndices.add(fontIdx);
                        mGlyphFlags.add(styleFlags);
                        mHasEffect |= (styleFlags & CharacterStyle.EFFECT_MASK) != 0;

                        offset += adv;

                        char c1 = text[i];
                        if (i + 1 < limit && Character.isHighSurrogate(c1)) {
                            char c2 = text[i + 1];
                            if (Character.isLowSurrogate(c2)) {
                                ++i;
                            }
                        }
                    }
                    mTotalAdvance = offset;
                }

                if (isRtl) {
                    runIndex--;
                } else {
                    runIndex++;
                }
            }
        }

        if (mComputeLineBoundaries) {
            // Compute line break boundaries, will be sorted into logical order.
            BreakIterator breaker = BreakIterator.getLineInstance(
                LineBreaker.getLocaleWithLineBreakOption(mFontPaint.getLocale(), sLbStyle, sLbWordStyle)
            );
            final CharArrayIterator charIterator = new CharArrayIterator(text, start, limit);
            breaker.setText(charIterator);
            int prevPos = start, currPos;
            while ((currPos = breaker.following(prevPos)) != BreakIterator.DONE) {
                mLineBoundaries.add(currPos);
                prevPos = currPos;
            }
        }
    }
}
