package com.yvl.app.adapter;

import android.animation.*;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.yvl.app.R;
import com.yvl.app.model.Song;
import com.yvl.app.util.ThemeManager;
import com.yvl.app.view.EqBarsView;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.VH> {
    public interface OnSongClick { void onClick(Song s); }
    private final List<Song> songs;
    private final ThemeManager tm;
    private final OnSongClick click;
    private int playingIdx = -1;

    public SongAdapter(List<Song> songs, ThemeManager tm, OnSongClick c) {
        this.songs=songs; this.tm=tm; this.click=c;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
        return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_song, p, false));
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        Song s = songs.get(pos);
        h.title.setText(s.title);
        h.artist.setText(s.artist);
        h.duration.setText(s.duration);
        h.title.setTextColor(tm.getTextColor());
        h.artist.setTextColor(tm.getMutedColor());
        h.duration.setTextColor(tm.getMutedColor());
        boolean playing = pos == playingIdx;
        h.eqBars.setPlaying(playing);
        h.eqBars.setVisibility(playing ? View.VISIBLE : View.GONE);
        h.eqBars.setColor(tm.getTextColor());
        h.playBtn.setVisibility(playing ? View.GONE : View.VISIBLE);
        h.itemView.setBackgroundColor(playing ? 0x14FFFFFF : 0x00000000);

        h.itemView.setAlpha(0f); h.itemView.setTranslationX(-20f);
        h.itemView.animate().alpha(1f).translationX(0f).setDuration(300).setStartDelay(pos*60L).start();

        h.itemView.setOnClickListener(v -> {
            int prev = playingIdx;
            playingIdx = (playingIdx == pos) ? -1 : pos;
            if (prev >= 0 && prev < songs.size()) notifyItemChanged(prev);
            notifyItemChanged(pos);
            if (click != null && playingIdx == pos) click.onClick(s);
        });
    }

    @Override public int getItemCount() { return songs.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, artist, duration;
        EqBarsView eqBars;
        View playBtn;
        VH(View v) {
            super(v);
            title    = v.findViewById(R.id.song_title);
            artist   = v.findViewById(R.id.song_artist);
            duration = v.findViewById(R.id.song_duration);
            eqBars   = v.findViewById(R.id.eq_bars);
            playBtn  = v.findViewById(R.id.play_btn);
        }
    }
}
