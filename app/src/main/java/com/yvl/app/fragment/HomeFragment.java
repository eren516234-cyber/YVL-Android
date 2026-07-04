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
import com.yvl.app.model.Song;
import com.yvl.app.util.*;
import com.yvl.app.view.VinylLogoView;
import java.util.*;

public class HomeFragment extends Fragment {
    private ThemeManager tm;
    private FontManager  fm;
    private SongAdapter  songAdapter;
    private List<Song>   songs = new ArrayList<>(AppData.getSongs());

    private static final String[][] MOODS = {
        {"For You",  "pop"},
        {"Rock",     "rock"},
        {"Hip-Hop",  "hiphop"},
        {"K-Pop",    "kpop"},
        {"Lo-Fi",    "lofi"}
    };

    @Override public View onCreateView(LayoutInflater inf, ViewGroup parent, Bundle state) {
        View root = inf.inflate(R.layout.fragment_home, parent, false);
        tm = ThemeManager.getInstance(requireContext());
        fm = FontManager.getInstance(requireContext());

        root.setBackgroundColor(tm.getBgColor());
        setupHeader(root);
        setupMoods(root);
        setupSongsList(root);
        animateIn(root);

        VinylLogoView miniVinyl = root.findViewById(R.id.mini_vinyl);
        if (miniVinyl != null) miniVinyl.startSpin();

        loadSongs("pop");
        return root;
    }

    private void setupHeader(View root) {
        TextView yvlTitle = root.findViewById(R.id.yvl_title);
        if (yvlTitle != null) {
            yvlTitle.setTextColor(tm.getAccentColor());
            yvlTitle.setTypeface(fm.getBoldTypeface());
        }
        root.findViewById(R.id.account_btn).setOnClickListener(v ->
            ((com.yvl.app.MainActivity)requireActivity()).getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_up_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_down_out)
                .replace(com.yvl.app.R.id.fragment_container, new AccountFragment())
                .addToBackStack(null).commit()
        );
    }

    private void setupMoods(View root) {
        LinearLayout chipsRow = root.findViewById(R.id.chips_row);
        if (chipsRow == null) return;
        for (int i = 0; i < MOODS.length; i++) {
            final int idx = i;
            final String tag = MOODS[i][1];
            TextView chip = new TextView(requireContext());
            chip.setText(MOODS[i][0]);
            chip.setPadding(40, 20, 40, 20);
            chip.setTextSize(14f);
            chip.setTypeface(fm.getBoldTypeface());
            chip.setTextColor(idx == 0 ? tm.getBgColor() : tm.getMutedColor());
            chip.setBackground(pillBg(idx == 0 ? tm.getAccentColor() : Color.TRANSPARENT, tm.getBorderColor()));
            chip.setOnClickListener(v -> {
                selectChip(chipsRow, chip, idx);
                loadSongs(tag);
            });
            chipsRow.addView(chip);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) chip.getLayoutParams();
            lp.setMarginEnd(10);
            chip.setLayoutParams(lp);
        }
    }

    private void selectChip(LinearLayout row, TextView sel, int selIdx) {
        for (int i = 0; i < row.getChildCount(); i++) {
            TextView c = (TextView) row.getChildAt(i);
            boolean active = (c == sel);
            c.setTextColor(active ? tm.getBgColor() : tm.getMutedColor());
            c.setBackground(pillBg(active ? tm.getAccentColor() : Color.TRANSPARENT, tm.getBorderColor()));
            c.animate().scaleX(active ? 1.06f : 1f).scaleY(active ? 1.06f : 1f).setDuration(180).start();
        }
    }

    private android.graphics.drawable.Drawable pillBg(int fill, int stroke) {
        android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
        gd.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        gd.setCornerRadius(100f);
        gd.setColor(fill);
        gd.setStroke(2, stroke);
        return gd;
    }

    private void setupSongsList(View root) {
        RecyclerView rv = root.findViewById(R.id.songs_rv);
        if (rv == null) return;
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        songAdapter = new SongAdapter(songs, tm, song -> openPlayer(song));
        rv.setAdapter(songAdapter);
        rv.setItemAnimator(null);
    }

    private void loadSongs(String genre) {
        JamendoApi.genre(genre, 20, new JamendoApi.SongsCallback() {
            public void onSongs(List<Song> result) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    songs.clear();
                    songs.addAll(result.isEmpty() ? AppData.getSongs() : result);
                    if (songAdapter != null) songAdapter.notifyDataSetChanged();
                });
            }
            public void onError(String msg) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (songs.isEmpty()) { songs.addAll(AppData.getSongs()); }
                    if (songAdapter != null) songAdapter.notifyDataSetChanged();
                });
            }
        });
    }

    private void openPlayer(Song song) {
        Intent i = new Intent(requireContext(), PlayerActivity.class);
        i.putExtra("title",  song.title);
        i.putExtra("artist", song.artist);
        i.putExtra("audio",  song.audioUrl);
        i.putExtra("art",    song.artUrl);
        startActivity(i);
        requireActivity().overridePendingTransition(R.anim.slide_up_in, R.anim.fade_out);
    }

    private void animateIn(View root) {
        View header = root.findViewById(R.id.header_row);
        View chips  = root.findViewById(R.id.chips_row);
        if (header != null) slideUp(header, 0);
        if (chips  != null) slideUp(chips,  100);
    }

    private void slideUp(View v, long delay) {
        v.setAlpha(0f); v.setTranslationY(30f);
        v.animate().alpha(1f).translationY(0f).setDuration(420).setStartDelay(delay)
            .setInterpolator(new DecelerateInterpolator(2f)).start();
    }
}
