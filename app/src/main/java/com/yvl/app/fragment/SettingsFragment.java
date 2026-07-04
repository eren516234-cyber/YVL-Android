package com.yvl.app.fragment;

import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import androidx.fragment.app.Fragment;
import com.yvl.app.R;
import com.yvl.app.util.*;

public class SettingsFragment extends Fragment {
    private ThemeManager tm;
    private FontManager  fm;
    private View root;

    @Override public View onCreateView(LayoutInflater inf, ViewGroup parent, Bundle state) {
        root = inf.inflate(R.layout.fragment_settings, parent, false);
        tm = ThemeManager.getInstance(requireContext());
        fm = FontManager.getInstance(requireContext());
        applyTheme();
        setupThemeCards();
        setupFontSection();
        animateIn();
        return root;
    }

    private void setupThemeCards() {
        LinearLayout row = root.findViewById(R.id.theme_cards_row);
        if (row == null) return;
        row.removeAllViews();
        int dp = (int) requireContext().getResources().getDisplayMetrics().density;

        for (int i = 0; i < ThemeManager.THEME_NAMES.length; i++) {
            final int idx = i;
            // Outer container
            LinearLayout card = new LinearLayout(requireContext());
            card.setOrientation(LinearLayout.VERTICAL);
            card.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(84 * dp, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd(12 * dp);
            card.setLayoutParams(lp);

            // Gradient preview circle
            View circle = new View(requireContext());
            int sz = 60 * dp;
            LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(sz, sz);
            circle.setLayoutParams(clp);
            GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                    new int[]{ThemeManager.GRAD[i][0], ThemeManager.GRAD[i][1]});
            gd.setShape(GradientDrawable.OVAL);
            boolean selected = (tm.getThemeId() == i);
            gd.setStroke(selected ? 4 * dp : 2 * dp,
                         selected ? ThemeManager.GRAD[i][0] : 0x33FFFFFF);
            if (selected) circle.setScaleX(1.15f); circle.setScaleY(1.15f);
            circle.setBackground(gd);
            card.addView(circle);

            // Name label
            TextView label = new TextView(requireContext());
            label.setText(ThemeManager.THEME_NAMES[i]);
            label.setTextColor(selected ? 0xFFFFFFFF : 0xFF888888);
            label.setTextSize(11f);
            label.setTypeface(selected ? fm.getBoldTypeface() : fm.getTypeface());
            label.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tlp.topMargin = 8 * dp;
            label.setLayoutParams(tlp);
            card.addView(label);

            card.setOnClickListener(v -> {
                tm.setThemeId(idx);
                setupThemeCards();
                applyTheme();
                // Restart activity for full theme apply
                requireActivity().recreate();
            });
            row.addView(card);
        }
    }

    private void setupFontSection() {
        LinearLayout fontsRow = root.findViewById(R.id.fonts_row);
        if (fontsRow == null) return;
        fontsRow.removeAllViews();
        int dp = (int) requireContext().getResources().getDisplayMetrics().density;

        for (int i = 0; i < FontManager.FONT_NAMES.length; i++) {
            final int idx = i;
            boolean selected = (fm.getFontId() == i);

            FrameLayout card = new FrameLayout(requireContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 68 * dp);
            lp.bottomMargin = 10 * dp;
            card.setLayoutParams(lp);

            GradientDrawable bg = new GradientDrawable();
            bg.setShape(GradientDrawable.RECTANGLE);
            bg.setCornerRadius(20 * dp);
            bg.setColor(selected ? tm.getSurfaceColor() : tm.getBgColor());
            bg.setStroke(selected ? 2 * dp : 1 * dp,
                         selected ? tm.getAccentColor() : tm.getBorderColor());
            card.setBackground(bg);

            // Preview text
            TextView preview = new TextView(requireContext());
            preview.setText(FontManager.FONT_PREVIEWS[i]);
            preview.setTextSize(18f);
            preview.setTextColor(selected ? tm.getAccentColor() : tm.getTextColor());
            try { preview.setTypeface(Typeface.create(FontManager.FONT_FAMILIES[i], Typeface.NORMAL)); }
            catch (Exception ignored) {}
            preview.setGravity(Gravity.CENTER_VERTICAL);
            preview.setPadding(28 * dp, 0, 0, 0);
            preview.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT));
            card.addView(preview);

            // Checkmark if selected
            if (selected) {
                TextView check = new TextView(requireContext());
                check.setText("✓");
                check.setTextColor(tm.getAccentColor());
                check.setTextSize(18f);
                FrameLayout.LayoutParams clp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER_VERTICAL | Gravity.END);
                clp.rightMargin = 24 * dp;
                check.setLayoutParams(clp);
                card.addView(check);
            }

            card.setOnClickListener(v -> {
                fm.setFontId(idx);
                setupFontSection();
                applyTheme();
            });
            fontsRow.addView(card);
        }
    }

    private void applyTheme() {
        if (root == null) return;
        root.setBackgroundColor(tm.getBgColor());
        applyTextViews(root);
    }

    private void applyTextViews(View v) {
        if (v instanceof ViewGroup) {
            ViewGroup g = (ViewGroup) v;
            for (int i = 0; i < g.getChildCount(); i++) applyTextViews(g.getChildAt(i));
        } else if (v instanceof TextView) {
            TextView tv = (TextView) v;
            int id = tv.getId();
            if (id == R.id.settings_title) {
                tv.setTextColor(tm.getTextColor());
                tv.setTypeface(fm.getBoldTypeface());
            }
        }
    }

    private void animateIn() {
        TextView title = root.findViewById(R.id.settings_title);
        if (title != null) { title.setAlpha(0f); title.animate().alpha(1f).setDuration(400).start(); }
    }
}
