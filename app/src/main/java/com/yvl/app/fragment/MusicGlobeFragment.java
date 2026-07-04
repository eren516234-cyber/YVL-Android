package com.yvl.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.yvl.app.MusicGlobeActivity;
import com.yvl.app.R;
import com.yvl.app.util.ThemeManager;

public class MusicGlobeFragment extends Fragment {
    @Override public View onCreateView(LayoutInflater inf, ViewGroup parent, Bundle state) {
        View root = inf.inflate(R.layout.fragment_music_globe, parent, false);
        ThemeManager tm = ThemeManager.getInstance(requireContext());
        root.setBackgroundColor(tm.getBgColor());
        root.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), MusicGlobeActivity.class));
            requireActivity().overridePendingTransition(R.anim.slide_up_in, R.anim.fade_out);
        });
        return root;
    }
}
