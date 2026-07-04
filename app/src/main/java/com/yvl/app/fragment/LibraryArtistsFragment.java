package com.yvl.app.fragment;

import android.os.Bundle;
import android.view.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.yvl.app.R;
import com.yvl.app.adapter.ArtistAdapter;
import com.yvl.app.util.*;

public class LibraryArtistsFragment extends Fragment {
    @Override public View onCreateView(LayoutInflater inf, ViewGroup p, Bundle s) {
        View root = inf.inflate(R.layout.fragment_library_content, p, false);
        ThemeManager tm = ThemeManager.getInstance(requireContext());
        root.setBackgroundColor(tm.getBgColor());
        RecyclerView rv = root.findViewById(R.id.content_rv);
        rv.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rv.setAdapter(new ArtistAdapter(AppData.getArtists(), tm));
        return root;
    }
}
