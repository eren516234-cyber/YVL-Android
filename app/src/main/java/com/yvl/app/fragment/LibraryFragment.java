package com.yvl.app.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.yvl.app.R;
import com.yvl.app.adapter.LibraryPagerAdapter;
import com.yvl.app.util.ThemeManager;

public class LibraryFragment extends Fragment {
    private final String[] TABS = {"Artists","Playlists","Albums","Songs"};
    private int currentTab = 0;
    private TextView[] tabViews;
    private View indicator;

    @Override public View onCreateView(LayoutInflater inf, ViewGroup parent, Bundle state) {
        View root = inf.inflate(R.layout.fragment_library, parent, false);
        ThemeManager tm = ThemeManager.getInstance(requireContext());
        root.setBackgroundColor(tm.getBgColor());

        ViewPager2 pager = root.findViewById(R.id.library_pager);
        pager.setAdapter(new LibraryPagerAdapter(this));
        pager.setOffscreenPageLimit(4);

        indicator = root.findViewById(R.id.tab_indicator);

        LinearLayout tabsRow = root.findViewById(R.id.tabs_row);
        tabViews = new TextView[TABS.length];
        for (int i = 0; i < TABS.length; i++) {
            final int idx = i;
            TextView tv = new TextView(requireContext());
            tv.setText(TABS[i]);
            tv.setTextSize(36f);
            tv.setTypeface(android.graphics.Typeface.create("sans-serif-black", android.graphics.Typeface.BOLD));
            tv.setPadding(0,0,44,0);
            tv.setTextColor(i==0 ? tm.getTextColor() : tm.getMutedColor());
            tv.setOnClickListener(v -> selectTab(idx, pager, tm));
            tabsRow.addView(tv);
            tabViews[i] = tv;
        }

        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override public void onPageSelected(int pos) { selectTab(pos, pager, tm); }
        });

        View headerArea = root.findViewById(R.id.header_area);
        if (headerArea != null) {
            headerArea.setAlpha(0f); headerArea.setTranslationY(20f);
            headerArea.animate().alpha(1f).translationY(0f).setDuration(400)
                .setInterpolator(new DecelerateInterpolator(2f)).start();
        }
        return root;
    }

    private void selectTab(int idx, ViewPager2 pager, ThemeManager tm) {
        currentTab = idx;
        for (int i=0;i<tabViews.length;i++) {
            tabViews[i].setTextColor(i==idx ? tm.getTextColor() : tm.getMutedColor());
            tabViews[i].animate().scaleX(i==idx?1.0f:0.97f).scaleY(i==idx?1.0f:0.97f).setDuration(180).start();
        }
        pager.setCurrentItem(idx, true);
        animateIndicator(idx, tm);
    }

    private void animateIndicator(int idx, ThemeManager tm) {
        if (indicator == null) return;
        float[] widths = {0.22f,0.24f,0.17f,0.13f};
        indicator.setBackgroundColor(tm.getTextColor());
        indicator.animate().scaleX(widths[idx]).setDuration(300)
            .setInterpolator(new OvershootInterpolator(1.3f)).start();
    }
}
