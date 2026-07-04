package com.yvl.app.adapter;

import android.graphics.*;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.yvl.app.R;
import com.yvl.app.util.ThemeManager;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.VH> {
    private final List<String> cats;
    private final ThemeManager tm;
    public CategoryAdapter(List<String> c, ThemeManager t) { cats=c; tm=t; }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
        return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_category_grid, p, false));
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        h.name.setText(cats.get(pos));
        h.name.setTextColor(tm.getTextColor());
        int[] bgs = {0xFF111111,0xFF1A1A1A,0xFF222222};
        h.card.setBackgroundColor(bgs[pos % bgs.length]);
        h.itemView.setAlpha(0f); h.itemView.setScaleX(0.92f); h.itemView.setScaleY(0.92f);
        h.itemView.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(250).setStartDelay(pos*40L).start();
        h.itemView.setOnClickListener(v -> {
            v.animate().scaleX(0.96f).scaleY(0.96f).setDuration(80).withEndAction(
                ()-> v.animate().scaleX(1f).scaleY(1f).setDuration(80).start()).start();
        });
    }

    @Override public int getItemCount() { return cats.size(); }
    static class VH extends RecyclerView.ViewHolder {
        TextView name; View card;
        VH(View v) { super(v); name=v.findViewById(R.id.cat_name); card=v.findViewById(R.id.cat_card); }
    }
}
