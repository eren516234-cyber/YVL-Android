package com.yvl.app.fragment;

import android.content.Intent;
import android.os.*;
import android.text.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.yvl.app.PlayerActivity;
import com.yvl.app.R;
import com.yvl.app.adapter.*;
import com.yvl.app.model.Song;
import com.yvl.app.util.*;
import java.util.*;

public class SearchFragment extends Fragment {
    private ThemeManager tm;
    private FontManager  fm;
    private SongAdapter  resultAdapter;
    private List<Song>   results = new ArrayList<>();
    private EditText     searchInput;
    private Handler      searchHandler = new Handler(Looper.getMainLooper());
    private Runnable     searchRunnable;

    @Override public View onCreateView(LayoutInflater inf, ViewGroup parent, Bundle state) {
        View root = inf.inflate(R.layout.fragment_search, parent, false);
        tm = ThemeManager.getInstance(requireContext());
        fm = FontManager.getInstance(requireContext());
        root.setBackgroundColor(tm.getBgColor());

        setupSearchBar(root);
        setupHistoryPills(root);
        setupResultsList(root);
        setupCategories(root);
        animateIn(root);

        JamendoApi.trending(12, new JamendoApi.SongsCallback() {
            public void onSongs(List<Song> songs) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    results.clear(); results.addAll(songs);
                    resultAdapter.notifyDataSetChanged();
                });
            }
            public void onError(String m) {}
        });
        return root;
    }

    private void setupSearchBar(View root) {
        searchInput = root.findViewById(R.id.search_input);
        if (searchInput == null) return;
        searchInput.setHintTextColor(tm.getMutedColor());
        searchInput.setTextColor(tm.getTextColor());
        searchInput.setTypeface(fm.getTypeface());
        searchInput.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            public void onTextChanged(CharSequence s, int a, int b, int c)     {}
            public void afterTextChanged(Editable s) {
                if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = () -> doSearch(s.toString().trim());
                searchHandler.postDelayed(searchRunnable, 400);
            }
        });
    }

    private void doSearch(String q) {
        if (q.isEmpty()) {
            JamendoApi.trending(12, new JamendoApi.SongsCallback() {
                public void onSongs(List<Song> songs) {
                    if (!isAdded()) return;
                    requireActivity().runOnUiThread(() -> { results.clear(); results.addAll(songs); resultAdapter.notifyDataSetChanged(); });
                }
                public void onError(String m) {}
            });
            return;
        }
        JamendoApi.search(q, 20, new JamendoApi.SongsCallback() {
            public void onSongs(List<Song> songs) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    results.clear(); results.addAll(songs);
                    resultAdapter.notifyDataSetChanged();
                });
            }
            public void onError(String m) {}
        });
    }

    private void setupResultsList(View root) {
        RecyclerView rv = root.findViewById(R.id.trending_rv);
        if (rv == null) return;
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        resultAdapter = new SongAdapter(results, tm, song -> {
            Intent i = new Intent(requireContext(), PlayerActivity.class);
            i.putExtra("title",  song.title);
            i.putExtra("artist", song.artist);
            i.putExtra("audio",  song.audioUrl);
            i.putExtra("art",    song.artUrl);
            startActivity(i);
            requireActivity().overridePendingTransition(R.anim.slide_up_in, R.anim.fade_out);
        });
        rv.setAdapter(resultAdapter);
        rv.setItemAnimator(null);
    }

    private void setupHistoryPills(View root) {
        LinearLayout row = root.findViewById(R.id.history_row);
        if (row == null) return;
        String[] history = {"The Weeknd", "Rock", "Hip-Hop", "Lo-Fi", "Rema"};
        for (String h : history) {
            TextView pill = new TextView(requireContext());
            pill.setText(h);
            pill.setPadding(36, 16, 36, 16);
            pill.setTextColor(tm.getTextColor());
            pill.setTextSize(13f);
            pill.setTypeface(fm.getTypeface());
            android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
            bg.setCornerRadius(100f);
            bg.setColor(tm.getSurfaceColor());
            bg.setStroke(1, tm.getBorderColor());
            pill.setBackground(bg);
            pill.setOnClickListener(v -> {
                if (searchInput != null) searchInput.setText(h);
            });
            row.addView(pill);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) pill.getLayoutParams();
            lp.setMarginEnd(10);
            pill.setLayoutParams(lp);
        }
    }

    private void setupCategories(View root) {
        RecyclerView catRv = root.findViewById(R.id.categories_rv);
        if (catRv == null) return;
        catRv.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        catRv.setAdapter(new CategoryAdapter(AppData.getCategories(), tm));
        catRv.setItemAnimator(null);
    }

    private void animateIn(View root) {
        View header = root.findViewById(R.id.search_header);
        if (header != null) {
            header.setAlpha(0f); header.setTranslationY(24f);
            header.animate().alpha(1f).translationY(0f).setDuration(400)
                .setInterpolator(new DecelerateInterpolator(2f)).start();
        }
    }
}
