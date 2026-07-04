package com.yvl.app;

import android.animation.*;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.media.*;
import android.os.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import com.yvl.app.model.Song;
import com.yvl.app.util.*;
import com.yvl.app.view.*;
import java.util.*;

public class PlayerActivity extends AppCompatActivity {

    private boolean playing = false;
    private boolean liked   = false;
    private boolean showLyrics = false;
    private int lyricsStyle = LyricsManager.STYLE_KARAOKE;

    private VinylView vinylView;
    private EqBarsView eqBarsView;
    private SeekBar seekBar;
    private TextView timeCurrent, timeTotal, songTitle, songArtist;
    private ImageView playPauseBtn, likeBtn, artView;
    private View lyricsPanel, lyricsTab, queueTab;
    private RecyclerView lyricsRv;
    private TextView lyricsEmptyText;

    private MediaPlayer mediaPlayer;
    private Handler seekHandler = new Handler(Looper.getMainLooper());
    private Runnable seekRunnable;

    private List<LyricsManager.LyricLine> lyricLines = new ArrayList<>();
    private LyricsAdapter lyricsAdapter;
    private ThemeManager tm;
    private FontManager fm;

    private String currentTitle  = "YVL Music";
    private String currentArtist = "Unknown";
    private String currentAudio  = "";
    private String currentArt    = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tm = ThemeManager.getInstance(this);
        fm = FontManager.getInstance(this);
        getWindow().setBackgroundDrawableResource(android.R.color.black);
        setContentView(R.layout.activity_player);
        applyThemeColors();

        currentTitle  = getIntent().getStringExtra("title");  if (currentTitle  == null) currentTitle  = "YVL Music";
        currentArtist = getIntent().getStringExtra("artist"); if (currentArtist == null) currentArtist = "Unknown";
        currentAudio  = getIntent().getStringExtra("audio");  if (currentAudio  == null) currentAudio  = "";
        currentArt    = getIntent().getStringExtra("art");    if (currentArt    == null) currentArt    = "";

        vinylView    = findViewById(R.id.vinyl_view);
        eqBarsView   = findViewById(R.id.eq_bars);
        seekBar      = findViewById(R.id.seek_bar);
        timeCurrent  = findViewById(R.id.time_current);
        timeTotal    = findViewById(R.id.time_total);
        playPauseBtn = findViewById(R.id.btn_play_pause);
        likeBtn      = findViewById(R.id.btn_like);
        songTitle    = findViewById(R.id.song_title_player);
        songArtist   = findViewById(R.id.song_artist_player);
        artView      = findViewById(R.id.album_art);
        lyricsPanel  = findViewById(R.id.lyrics_panel);
        lyricsTab    = findViewById(R.id.tab_lyrics);
        queueTab     = findViewById(R.id.tab_queue);
        lyricsRv     = findViewById(R.id.lyrics_rv);
        lyricsEmptyText = findViewById(R.id.lyrics_empty);

        songTitle.setText(currentTitle);
        songArtist.setText(currentArtist);
        songTitle.setTypeface(fm.getBoldTypeface());
        songArtist.setTypeface(fm.getTypeface());

        setupSeekBar();
        setupButtons();
        setupLyricsTabs();
        setupMediaPlayer();
        fetchLyrics();
        animateIn();
    }

    private void applyThemeColors() {
        View root = findViewById(android.R.id.content);
        if (root != null) root.setBackgroundColor(tm.getBgColor());
        getWindow().setStatusBarColor(tm.getBgColor());
        getWindow().setNavigationBarColor(tm.getBgColor());
    }

    private void setupMediaPlayer() {
        if (currentAudio == null || currentAudio.isEmpty()) {
            vinylView.setPlaying(false);
            eqBarsView.setPlaying(false);
            return;
        }
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA).build());
            mediaPlayer.setDataSource(currentAudio);
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                playing = true;
                playPauseBtn.setImageResource(R.drawable.ic_pause);
                vinylView.setPlaying(true);
                eqBarsView.setPlaying(true);
                int dur = mp.getDuration();
                seekBar.setMax(dur > 0 ? dur : 1000);
                timeTotal.setText(fmtMs(dur));
                startSeekUpdate();
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                playing = false;
                playPauseBtn.setImageResource(R.drawable.ic_play);
                vinylView.setPlaying(false);
                eqBarsView.setPlaying(false);
            });
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                playing = false; return true;
            });
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Toast.makeText(this, "Cannot load audio", Toast.LENGTH_SHORT).show();
        }
    }

    private void startSeekUpdate() {
        if (seekRunnable != null) seekHandler.removeCallbacks(seekRunnable);
        seekRunnable = new Runnable() {
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int pos = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(pos);
                    timeCurrent.setText(fmtMs(pos));
                    updateLyricsHighlight(pos);
                }
                seekHandler.postDelayed(this, 500);
            }
        };
        seekHandler.post(seekRunnable);
    }

    private void setupSeekBar() {
        seekBar.setMax(1000);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar sb, int p, boolean user) {
                if (user && mediaPlayer != null) {
                    mediaPlayer.seekTo(p);
                    timeCurrent.setText(fmtMs(p));
                    updateLyricsHighlight(p);
                }
            }
            public void onStartTrackingTouch(SeekBar sb) {}
            public void onStopTrackingTouch(SeekBar sb)  {}
        });
    }

    private void setupButtons() {
        ImageView backBtn = findViewById(R.id.btn_back);
        if (backBtn != null) backBtn.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, R.anim.slide_down);
        });

        playPauseBtn.setOnClickListener(v -> togglePlay());
        likeBtn.setOnClickListener(v -> toggleLike());

        ImageView shuffleBtn = findViewById(R.id.btn_shuffle);
        ImageView prevBtn    = findViewById(R.id.btn_prev);
        ImageView nextBtn    = findViewById(R.id.btn_next);
        ImageView repeatBtn  = findViewById(R.id.btn_repeat);

        if (shuffleBtn != null) shuffleBtn.setOnClickListener(v -> pulseView(v));
        if (repeatBtn  != null) repeatBtn.setOnClickListener(v -> pulseView(v));
        if (prevBtn    != null) prevBtn.setOnClickListener(v -> {
            if (mediaPlayer != null) mediaPlayer.seekTo(0);
        });
        if (nextBtn    != null) nextBtn.setOnClickListener(v -> pulseView(v));

        // Lyrics style cycling button
        View styleBtn = findViewById(R.id.btn_lyrics_style);
        if (styleBtn != null) styleBtn.setOnClickListener(v -> {
            lyricsStyle = (lyricsStyle + 1) % 3;
            refreshLyricsStyle();
        });
    }

    private void setupLyricsTabs() {
        if (lyricsTab == null || queueTab == null) return;
        selectLyricsTab(true);
        lyricsTab.setOnClickListener(v -> selectLyricsTab(true));
        queueTab.setOnClickListener(v  -> selectLyricsTab(false));

        if (lyricsRv != null) {
            lyricsRv.setLayoutManager(new LinearLayoutManager(this));
            lyricsAdapter = new LyricsAdapter(lyricLines, lyricsStyle, tm, fm);
            lyricsRv.setAdapter(lyricsAdapter);
        }
    }

    private void selectLyricsTab(boolean lyrics) {
        showLyrics = lyrics;
        int accent = tm.getAccentColor();
        int muted  = tm.getMutedColor();
        ((TextView)lyricsTab).setTextColor(lyrics ? accent : muted);
        ((TextView)queueTab).setTextColor(lyrics ? muted : accent);
        if (lyricsPanel != null) lyricsPanel.setVisibility(lyrics ? View.VISIBLE : View.GONE);
    }

    private void fetchLyrics() {
        LyricsManager.fetch(currentArtist, currentTitle, new LyricsManager.LyricsCallback() {
            public void onLyrics(List<LyricsManager.LyricLine> lines, String plain) {
                runOnUiThread(() -> {
                    lyricLines = lines;
                    if (lyricsAdapter != null) {
                        lyricsAdapter.setLines(lines);
                    }
                    if (lyricsEmptyText != null) lyricsEmptyText.setVisibility(View.GONE);
                });
            }
            public void onError() {
                runOnUiThread(() -> {
                    if (lyricsEmptyText != null) {
                        lyricsEmptyText.setVisibility(View.VISIBLE);
                        lyricsEmptyText.setText("No lyrics found");
                    }
                });
            }
        });
    }

    private void updateLyricsHighlight(long posMs) {
        if (lyricLines.isEmpty() || lyricsAdapter == null) return;
        int cur = LyricsManager.getCurrentLine(lyricLines, posMs);
        lyricsAdapter.setCurrentLine(cur);
        if (lyricsRv != null && cur >= 0)
            lyricsRv.smoothScrollToPosition(Math.max(0, cur - 2));
    }

    private void refreshLyricsStyle() {
        if (lyricsAdapter != null) lyricsAdapter.setStyle(lyricsStyle);
        View styleBtn = findViewById(R.id.btn_lyrics_style);
        if (styleBtn instanceof TextView) {
            String[] labels = {"Scroll", "Karaoke", "Minimal"};
            ((TextView)styleBtn).setText(labels[lyricsStyle]);
        }
    }

    private void togglePlay() {
        if (mediaPlayer == null) return;
        playing = !playing;
        if (playing) { mediaPlayer.start(); startSeekUpdate(); }
        else           mediaPlayer.pause();
        vinylView.setPlaying(playing);
        eqBarsView.setPlaying(playing);
        playPauseBtn.setImageResource(playing ? R.drawable.ic_pause : R.drawable.ic_play);
    }

    private void toggleLike() {
        liked = !liked;
        likeBtn.setImageResource(liked ? R.drawable.ic_heart_filled : R.drawable.ic_heart);
        pulseView(likeBtn);
    }

    private void pulseView(View v) {
        v.animate().scaleX(1.35f).scaleY(1.35f).setDuration(110).withEndAction(
            () -> v.animate().scaleX(1f).scaleY(1f).setDuration(110).start()
        ).start();
    }

    private void animateIn() {
        View contentPanel = findViewById(R.id.content_panel);
        if (contentPanel != null) {
            contentPanel.setTranslationY(60f);
            contentPanel.setAlpha(0f);
            contentPanel.animate().translationY(0f).alpha(1f)
                .setDuration(450).setStartDelay(80)
                .setInterpolator(new DecelerateInterpolator(2f)).start();
        }
    }

    private String fmtMs(int ms) {
        int s = ms / 1000;
        return String.format("%d:%02d", s / 60, s % 60);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if (seekRunnable != null) seekHandler.removeCallbacks(seekRunnable);
        if (mediaPlayer != null) { mediaPlayer.release(); mediaPlayer = null; }
    }

    @Override protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) mediaPlayer.pause();
    }

    // ── Lyrics RecyclerView Adapter ──────────────────────────────────────
    static class LyricsAdapter extends RecyclerView.Adapter<LyricsAdapter.VH> {
        private List<LyricsManager.LyricLine> lines;
        private int currentLine = -1;
        private int style;
        private final ThemeManager tm;
        private final FontManager  fm;

        LyricsAdapter(List<LyricsManager.LyricLine> lines, int style, ThemeManager tm, FontManager fm) {
            this.lines = lines; this.style = style; this.tm = tm; this.fm = fm;
        }

        void setLines(List<LyricsManager.LyricLine> l) {
            lines = l; currentLine = -1; notifyDataSetChanged();
        }
        void setCurrentLine(int cur) {
            int prev = currentLine; currentLine = cur;
            if (prev >= 0 && prev < lines.size()) notifyItemChanged(prev);
            if (cur  >= 0 && cur  < lines.size()) notifyItemChanged(cur);
        }
        void setStyle(int s) { style = s; notifyDataSetChanged(); }

        @Override public int getItemCount() { return lines.size(); }

        @Override public VH onCreateViewHolder(android.view.ViewGroup p, int t) {
            TextView tv = new TextView(p.getContext());
            tv.setLayoutParams(new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new VH(tv);
        }

        @Override public void onBindViewHolder(VH h, int pos) {
            String text = lines.get(pos).text;
            boolean active = pos == currentLine;
            TextView tv = h.tv;
            tv.setText(text);
            tv.setTypeface(active ? fm.getBoldTypeface() : fm.getTypeface());

            switch (style) {
                case LyricsManager.STYLE_KARAOKE:
                    tv.setTextSize(active ? 22f : 16f);
                    tv.setTextColor(active ? tm.getAccentColor() : 0x55FFFFFF);
                    tv.setAlpha(active ? 1f : 0.55f);
                    tv.setPadding(48, active ? 20 : 12, 48, active ? 20 : 12);
                    tv.setGravity(android.view.Gravity.CENTER);
                    break;
                case LyricsManager.STYLE_MINIMAL:
                    tv.setTextSize(active ? 20f : 0f);
                    tv.setTextColor(tm.getTextColor());
                    tv.setAlpha(active ? 1f : 0f);
                    tv.setPadding(48, 8, 48, 8);
                    tv.setGravity(android.view.Gravity.CENTER);
                    break;
                default: // SCROLL
                    tv.setTextSize(active ? 18f : 15f);
                    tv.setTextColor(active ? tm.getAccentColor() : tm.getMutedColor());
                    tv.setAlpha(1f);
                    tv.setPadding(48, 10, 48, 10);
                    tv.setGravity(android.view.Gravity.START);
                    break;
            }
        }

        static class VH extends RecyclerView.ViewHolder {
            final TextView tv;
            VH(TextView v) { super(v); tv = v; }
        }
    }
}
