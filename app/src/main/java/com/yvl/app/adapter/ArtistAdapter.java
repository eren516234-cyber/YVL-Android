package com.yvl.app.adapter;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.yvl.app.R;
import com.yvl.app.util.ThemeManager;
import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.VH> {
    private final List<String> artists;
    private final ThemeManager tm;
    public ArtistAdapter(List<String> a, ThemeManager t) { artists=a; tm=t; }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
        return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_artist_grid, p, false));
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        h.name.setText(artists.get(pos));
        h.name.setTextColor(tm.getTextColor());
        h.card.setBackgroundColor(pos%2==0 ? tm.getSurfaceColor() : 0xFF1A1A1A);
        h.itemView.setAlpha(0f); h.itemView.setScaleX(0.9f); h.itemView.setScaleY(0.9f);
        h.itemView.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(300).setStartDelay(pos*50L).start();
    }

    @Override public int getItemCount() { return artists.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView name; View card;
        VH(View v) { super(v); name=v.findViewById(R.id.artist_name); card=v.findViewById(R.id.artist_card); }
    }
}
