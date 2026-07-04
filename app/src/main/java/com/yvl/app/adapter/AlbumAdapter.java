package com.yvl.app.adapter;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.yvl.app.R;
import com.yvl.app.util.ThemeManager;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.VH> {
    private final List<String> albums;
    private final ThemeManager tm;
    public AlbumAdapter(List<String> a, ThemeManager t) { albums=a; tm=t; }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
        return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_album, p, false));
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        h.name.setText(albums.get(pos));
        h.name.setTextColor(tm.getTextColor());
        h.sub.setTextColor(tm.getMutedColor());
        h.art.setBackgroundColor(0xFF1A1A1A);
        h.itemView.setBackgroundColor(pos%2==0 ? tm.getSurfaceColor() : 0);
        h.itemView.setAlpha(0f); h.itemView.setTranslationX(-16f);
        h.itemView.animate().alpha(1f).translationX(0f).setDuration(300).setStartDelay(pos*50L).start();
    }

    @Override public int getItemCount() { return albums.size(); }
    static class VH extends RecyclerView.ViewHolder {
        TextView name, sub; View art;
        VH(View v) { super(v); name=v.findViewById(R.id.album_name); sub=v.findViewById(R.id.album_sub); art=v.findViewById(R.id.album_art); }
    }
}
