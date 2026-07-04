package com.yvl.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.yvl.app.PlayerActivity;
import com.yvl.app.R;
import com.yvl.app.adapter.SongAdapter;
import com.yvl.app.util.*;

public class LibrarySongsFragment extends Fragment {
    @Override public View onCreateView(LayoutInflater inf, ViewGroup p, Bundle s) {
        View root = inf.inflate(R.layout.fragment_library_content, p, false);
        ThemeManager tm = ThemeManager.getInstance(requireContext());
        root.setBackgroundColor(tm.getBgColor());
        RecyclerView rv = root.findViewById(R.id.content_rv);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(new SongAdapter(AppData.getSongs(), tm, song -> {
            Intent i = new Intent(requireContext(), PlayerActivity.class);
            i.putExtra("title", song.title);
            startActivity(i);
        }));
        return root;
    }
}
