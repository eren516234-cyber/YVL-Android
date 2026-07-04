package com.yvl.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import androidx.fragment.app.Fragment;
import com.yvl.app.MusicGlobeActivity;
import com.yvl.app.R;
import com.yvl.app.util.ThemeManager;

public class AccountFragment extends Fragment {
    @Override public View onCreateView(LayoutInflater inf, ViewGroup parent, Bundle state) {
        View root = inf.inflate(R.layout.fragment_account, parent, false);
        ThemeManager tm = ThemeManager.getInstance(requireContext());
        root.setBackgroundColor(tm.getBgColor());

        TextView title = root.findViewById(R.id.account_title);
        if (title != null) {
            title.setTextColor(tm.getTextColor());
            title.setAlpha(0f); title.setTranslationY(24f);
            title.animate().alpha(1f).translationY(0f).setDuration(400)
                .setInterpolator(new DecelerateInterpolator(2f)).start();
        }

        // Music Globe card
        View globeCard = root.findViewById(R.id.globe_card);
        if (globeCard != null) {
            globeCard.setOnClickListener(v -> {
                startActivity(new Intent(requireContext(), MusicGlobeActivity.class));
                requireActivity().overridePendingTransition(R.anim.slide_up_in, R.anim.fade_out);
            });
        }

        // Settings card -> SettingsFragment
        View settingsItem = root.findViewById(R.id.settings_item);
        if (settingsItem != null) {
            settingsItem.setOnClickListener(v -> requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_up_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_down_out)
                .replace(com.yvl.app.R.id.fragment_container, new SettingsFragment())
                .addToBackStack(null).commit()
            );
        }

        return root;
    }
}
