package com.yvl.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.yvl.app.PlayerActivity;
import com.yvl.app.R;
import com.yvl.app.adapter.*;
import com.yvl.app.util.*;

public class SearchFragment extends Fragment {
    @Override public View onCreateView(LayoutInflater inf, ViewGroup parent, Bundle state) {
        View root = inf.inflate(R.layout.fragment_search, parent, false);
        ThemeManager tm = ThemeManager.getInstance(requireContext());
        root.setBackgroundColor(tm.getBgColor());

        // Animate header
        View header = root.findViewById(R.id.search_header);
        if (header != null) {
            header.setAlpha(0f); header.setTranslationY(24f);
            header.animate().alpha(1f).translationY(0f).setDuration(400)
                .setInterpolator(new DecelerateInterpolator(2f)).start();
        }

        // History pills
        setupHistoryPills(root, tm);

        // Categories grid
        RecyclerView catRv = root.findViewById(R.id.categories_rv);
        if (catRv != null) {
            catRv.setLayoutManager(new GridLayoutManager(requireContext(), 2));
            catRv.setAdapter(new CategoryAdapter(AppData.getCategories(), tm));
            catRv.setItemAnimator(null);
            catRv.setAlpha(0f);
            catRv.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(400).setStartDelay(150)
                .setInterpolator(new OvershootInterpolator(1.1f)).start();
        }

        // Trending
        RecyclerView trendRv = root.findViewById(R.id.trending_rv);
        if (trendRv != null) {
            trendRv.setLayoutManager(new LinearLayoutManager(requireContext()));
            trendRv.setAdapter(new SongAdapter(AppData.getTrending(), tm, song -> {
                Intent i = new Intent(requireContext(), PlayerActivity.class);
                i.putExtra("title", song.title);
                startActivity(i);
                requireActivity().overridePendingTransition(R.anim.slide_up_in, R.anim.fade_out);
            }));
        }
        return root;
    }

    private void setupHistoryPills(View root, ThemeManager tm) {
        LinearLayout histRow = root.findViewById(R.id.history_row);
        if (histRow == null) return;
        String[] history = {"The Weeknd","Rammstein","AY YOLA","bbnoS","Rema"};
        for (String h : history) {
            TextView pill = new TextView(requireContext());
            pill.setText(h);
            pill.setPadding(32,14,32,14);
            pill.setTextColor(tm.getTextColor());
            pill.setTextSize(13f);
            pill.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
            android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
            bg.setCornerRadius(100f);
            bg.setColor(tm.getSurfaceColor());
            bg.setStroke(1, tm.getBorderColor());
            pill.setBackground(bg);
            histRow.addView(pill);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)pill.getLayoutParams();
            lp.setMarginEnd(10);
            pill.setLayoutParams(lp);
        }
    }
}
