package com.yvl.app.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import androidx.fragment.app.Fragment;
import com.yvl.app.R;
import com.yvl.app.util.ThemeManager;

public class SettingsFragment extends Fragment {
    private ThemeManager tm;
    private View root;

    @Override public View onCreateView(LayoutInflater inf, ViewGroup parent, Bundle state) {
        root = inf.inflate(R.layout.fragment_settings, parent, false);
        tm = ThemeManager.getInstance(requireContext());
        applyTheme();

        setupRainbowToggle();
        setupColorSwatches();
        setupResetBtn();

        TextView title = root.findViewById(R.id.settings_title);
        if (title != null) { title.setAlpha(0f); title.animate().alpha(1f).setDuration(400).start(); }
        return root;
    }

    private void setupRainbowToggle() {
        View toggle = root.findViewById(R.id.rainbow_toggle);
        View swatchesSection = root.findViewById(R.id.swatches_section);
        if (toggle == null) return;
        toggle.setSelected(tm.isRainbow());
        updateToggleUI(toggle, tm.isRainbow());
        toggle.setOnClickListener(v -> {
            boolean now = !tm.isRainbow();
            tm.setRainbow(now);
            tm.clearCustomColor();
            updateToggleUI(toggle, now);
            if (swatchesSection != null) swatchesSection.setVisibility(now ? View.VISIBLE : View.GONE);
            applyTheme();
        });
        if (swatchesSection != null)
            swatchesSection.setVisibility(tm.isRainbow() ? View.VISIBLE : View.GONE);
    }

    private void updateToggleUI(View toggle, boolean on) {
        toggle.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
            on ? tm.getAccentColor() : tm.getBorderColor()));
        View thumb = root.findViewById(R.id.toggle_thumb);
        if (thumb != null) thumb.animate().translationX(on ? 44f : 0f).setDuration(300)
            .setInterpolator(new OvershootInterpolator(1.5f)).start();
    }

    private void setupColorSwatches() {
        LinearLayout swatchesRow = root.findViewById(R.id.swatches_row);
        if (swatchesRow == null) return;
        swatchesRow.removeAllViews();
        for (int i = 0; i < ThemeManager.RAINBOW_BG.length; i++) {
            final int idx = i;
            View swatch = new View(requireContext());
            int size = (int)(44 * requireContext().getResources().getDisplayMetrics().density);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
            lp.setMarginEnd((int)(8 * requireContext().getResources().getDisplayMetrics().density));
            swatch.setLayoutParams(lp);
            android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
            gd.setShape(android.graphics.drawable.GradientDrawable.OVAL);
            gd.setColor(ThemeManager.RAINBOW_BG[i]);
            gd.setStroke(4, i == tm.getColorIdx() ? tm.getAccentColor() : tm.getBorderColor());
            swatch.setBackground(gd);
            swatch.setOnClickListener(v -> {
                tm.setRainbow(true);
                tm.setColorIdx(idx);
                tm.clearCustomColor();
                setupColorSwatches();
                applyTheme();
            });
            if (i == tm.getColorIdx() && tm.getCustomColor() == -1) {
                swatch.setScaleX(1.25f); swatch.setScaleY(1.25f);
            }
            swatchesRow.addView(swatch);
        }
    }

    private void setupResetBtn() {
        View resetBtn = root.findViewById(R.id.reset_btn);
        if (resetBtn != null) resetBtn.setOnClickListener(v -> {
            tm.reset(); applyTheme(); setupColorSwatches();
            View swatchSec = root.findViewById(R.id.swatches_section);
            if (swatchSec != null) swatchSec.setVisibility(View.GONE);
        });
    }

    private void applyTheme() {
        if (root == null) return;
        root.setBackgroundColor(tm.getBgColor());
        TextView title = root.findViewById(R.id.settings_title);
        if (title != null) title.setTextColor(tm.getTextColor());
        View toggle = root.findViewById(R.id.rainbow_toggle);
        if (toggle != null) updateToggleUI(toggle, tm.isRainbow());
    }
}
