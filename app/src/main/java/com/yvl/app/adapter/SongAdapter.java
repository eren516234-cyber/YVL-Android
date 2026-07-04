package com.yvl.app.adapter;

import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.yvl.app.R;
import com.yvl.app.model.Song;
import com.yvl.app.util.*;
import com.yvl.app.view.EqBarsView;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.VH> {
    public interface OnSongClick { void onClick(Song s); }
    private final List<Song> songs;
    private final ThemeManager tm;
    private final OnSongClick click;
    private int playingIdx = -1;

    public SongAdapter(List<Song> songs, ThemeManager tm, OnSongClick c) {
        this.songs = songs; this.tm = tm; this.click = c;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
        return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_song, p, false));
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        Song s = songs.get(pos);
        FontManager fm = FontManager.getInstance(h.itemView.getContext());
        int dp = (int) h.itemView.getResources().getDisplayMetrics().density;

        h.title.setText(s.title);
        h.artist.setText(s.artist);
        if (h.duration != null) h.duration.setText(s.duration);
        h.title.setTextColor(tm.getTextColor());
        h.artist.setTextColor(tm.getMutedColor());
        h.title.setTypeface(fm.getBoldTypeface());
        h.artist.setTypeface(fm.getTypeface());

        boolean isPlaying = (pos == playingIdx);
        h.eqBars.setPlaying(isPlaying);
        h.eqBars.setVisibility(isPlaying ? View.VISIBLE : View.GONE);
        h.eqBars.setColor(tm.getAccentColor());
        if (h.playBtn != null) h.playBtn.setVisibility(isPlaying ? View.GONE : View.VISIBLE);

        // Album art
        if (h.artView != null) {
            if (s.artUrl != null && !s.artUrl.isEmpty()) {
                loadImageAsync(h.artView, s.artUrl);
            } else {
                h.artView.setImageBitmap(makeArtPlaceholder(s.title, tm.getGradStart(), tm.getGradEnd()));
            }
        }

        // Rounded card background
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(16 * dp);
        bg.setColor(isPlaying ? addAlpha(tm.getAccentColor(), 0x22) : tm.getSurfaceColor());
        bg.setStroke(1, tm.getBorderColor());
        h.itemView.setBackground(bg);

        h.itemView.setAlpha(0f);
        h.itemView.animate().alpha(1f).setDuration(260).setStartDelay(pos * 45L).start();

        h.itemView.setOnClickListener(v -> {
            int prev = playingIdx;
            playingIdx = (playingIdx == pos) ? -1 : pos;
            if (prev >= 0 && prev < songs.size()) notifyItemChanged(prev);
            notifyItemChanged(pos);
            if (click != null && playingIdx == pos) click.onClick(s);
        });
    }

    private void loadImageAsync(ImageView iv, String url) {
        new Thread(() -> {
            try {
                java.net.URL u = new java.net.URL(url);
                android.net.http.SslCertificate ignored2;
                java.io.InputStream is = u.openStream();
                Bitmap bmp = BitmapFactory.decodeStream(is);
                is.close();
                if (bmp != null) {
                    Bitmap round = roundBitmap(bmp, 16);
                    iv.post(() -> iv.setImageBitmap(round));
                }
            } catch (Exception ignored) {}
        }).start();
    }

    static Bitmap roundBitmap(Bitmap src, float radiusDp) {
        int sz = Math.min(src.getWidth(), src.getHeight());
        Bitmap out = Bitmap.createBitmap(sz, sz, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(out);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        float r = radiusDp * src.getWidth() / 100f;
        c.drawRoundRect(0, 0, sz, sz, r, r, p);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        c.drawBitmap(Bitmap.createScaledBitmap(src, sz, sz, true), 0, 0, p);
        return out;
    }

    static Bitmap makeArtPlaceholder(String title, int c1, int c2) {
        int sz = 180;
        Bitmap bmp = Bitmap.createBitmap(sz, sz, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(new LinearGradient(0, 0, sz, sz, c1, c2, Shader.TileMode.CLAMP));
        canvas.drawRoundRect(0, 0, sz, sz, sz * 0.2f, sz * 0.2f, paint);
        paint.setShader(null);
        paint.setColor(0xCCFFFFFF);
        paint.setTextSize(72f);
        paint.setTextAlign(Paint.Align.CENTER);
        String letter = (title != null && !title.isEmpty())
            ? String.valueOf(title.charAt(0)).toUpperCase() : "?";
        canvas.drawText(letter, sz / 2f, sz / 2f + 26f, paint);
        return bmp;
    }

    private int addAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0x00FFFFFF);
    }

    @Override public int getItemCount() { return songs.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, artist, duration;
        EqBarsView eqBars;
        View playBtn;
        ImageView artView;
        VH(View v) {
            super(v);
            title    = v.findViewById(R.id.song_title);
            artist   = v.findViewById(R.id.song_artist);
            duration = v.findViewById(R.id.song_duration);
            eqBars   = v.findViewById(R.id.eq_bars);
            playBtn  = v.findViewById(R.id.play_btn);
            artView  = v.findViewById(R.id.song_art);
        }
    }
}
