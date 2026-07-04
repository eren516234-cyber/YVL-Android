package com.yvl.app.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.yvl.app.PlayerActivity;
import com.yvl.app.R;
import com.yvl.app.adapter.SongAdapter;
import com.yvl.app.util.*;
import com.yvl.app.view.VinylLogoView;

public class HomeFragment extends Fragment {
    @Override public View onCreateView(LayoutInflater inf, ViewGroup parent, Bundle state) {
        View root = inf.inflate(R.layout.fragment_home, parent, false);
        ThemeManager tm = ThemeManager.getInstance(requireContext());
        applyTheme(root, tm);
        setupMoods(root, tm);
        setupSongs(root, tm);
        animateIn(root);

        VinylLogoView miniVinyl = root.findViewById(R.id.mini_vinyl);
        if (miniVinyl != null) miniVinyl.startSpin();

        root.findViewById(R.id.account_btn).setOnClickListener(v ->
            ((com.yvl.app.MainActivity)requireActivity()).getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_up_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_down_out)
                .replace(com.yvl.app.R.id.fragment_container, new AccountFragment())
                .addToBackStack(null)
                .commit()
        );
        return root;
    }

    private void applyTheme(View root, ThemeManager tm) {
        root.setBackgroundColor(tm.getBgColor());
        TextView yvlTitle = root.findViewById(R.id.yvl_title);
        if (yvlTitle != null) yvlTitle.setTextColor(tm.getTextColor());
    }

    private void setupMoods(View root, ThemeManager tm) {
        String[] moods = {"For you \u00b7219", "Rock \u00b7240", "Hip-hop \u00b7589", "K-Pop \u00b7719"};
        LinearLayout chipsRow = root.findViewById(R.id.chips_row);
        if (chipsRow == null) return;
        int selected = 0;
        for (int i = 0; i < moods.length; i++) {
            final int idx = i;
            TextView chip = new TextView(requireContext());
            String[] parts = moods[i].split("\u00b7");
            chip.setText(parts[0].trim());
            chip.setPadding(36, 18, 36, 18);
            chip.setTextSize(15f);
            chip.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
            chip.setTextColor(idx == 0 ? tm.getBgColor() : tm.getMutedColor());
            chip.setBackground(createPillBg(idx == 0 ? tm.getTextColor() : Color.TRANSPARENT,
                tm.getBorderColor()));
            chip.setOnClickListener(v -> selectChip(chipsRow, chip, tm, idx));
            chipsRow.addView(chip);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)chip.getLayoutParams();
            lp.setMarginEnd(8);
            chip.setLayoutParams(lp);
        }
    }

    private void selectChip(LinearLayout row, TextView sel, ThemeManager tm, int idx) {
        for (int i = 0; i < row.getChildCount(); i++) {
            TextView c = (TextView) row.getChildAt(i);
            boolean active = c == sel;
            c.setTextColor(active ? tm.getBgColor() : tm.getMutedColor());
            c.setBackground(createPillBg(active ? tm.getTextColor() : Color.TRANSPARENT, tm.getBorderColor()));
            c.animate().scaleX(active?1.05f:1f).scaleY(active?1.05f:1f).setDuration(200).start();
        }
    }

    private android.graphics.drawable.Drawable createPillBg(int fillColor, int strokeColor) {
        android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
        gd.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        gd.setCornerRadius(100f);
        gd.setColor(fillColor);
        gd.setStroke(2, strokeColor);
        return gd;
    }

    private void setupSongs(View root, ThemeManager tm) {
        RecyclerView rv = root.findViewById(R.id.songs_rv);
        if (rv == null) return;
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        SongAdapter adapter = new SongAdapter(AppData.getSongs(), tm, song -> {
            Intent i = new Intent(requireContext(), PlayerActivity.class);
            i.putExtra("title", song.title);
            i.putExtra("artist", song.artist);
            startActivity(i);
            requireActivity().overridePendingTransition(R.anim.slide_up_in, R.anim.fade_out);
        });
        rv.setAdapter(adapter);
        rv.setItemAnimator(null);
    }

    private void animateIn(View root) {
        View header = root.findViewById(R.id.header_row);
        View chips  = root.findViewById(R.id.chips_row);
        if (header != null) slideUp(header, 0);
        if (chips  != null) slideUp(chips,  100);
    }

    private void slideUp(View v, long delay) {
        v.setAlpha(0f); v.setTranslationY(28f);
        v.animate().alpha(1f).translationY(0f).setDuration(450).setStartDelay(delay)
            .setInterpolator(new DecelerateInterpolator(2f)).start();
    }
}
