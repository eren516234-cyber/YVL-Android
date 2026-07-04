package com.yvl.app.fragment;

import android.os.Bundle;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import androidx.fragment.app.Fragment;
import com.yvl.app.R;
import com.yvl.app.util.ThemeManager;
import com.yvl.app.view.WaveformView;

public class ListenTogetherFragment extends Fragment {
    private boolean inSession = false;

    @Override public View onCreateView(LayoutInflater inf, ViewGroup parent, Bundle state) {
        View root = inf.inflate(R.layout.fragment_listen_together, parent, false);
        ThemeManager tm = ThemeManager.getInstance(requireContext());
        root.setBackgroundColor(tm.getBgColor());

        // Headers animate in
        TextView listenTv = root.findViewById(R.id.header_listen);
        TextView togetherTv= root.findViewById(R.id.header_together);
        if (listenTv != null)  { listenTv.setTextColor(tm.getTextColor()); slideUp(listenTv,0); }
        if (togetherTv != null){ togetherTv.setTextColor(tm.getMutedColor()); slideUp(togetherTv,80); }

        WaveformView waveform = root.findViewById(R.id.waveform);
        if (waveform != null) waveform.startWave();

        Button joinBtn = root.findViewById(R.id.join_btn);
        View sessionCard = root.findViewById(R.id.session_card);
        if (joinBtn != null) joinBtn.setOnClickListener(v -> {
            joinBtn.setText("In Session • 3 listening");
            joinBtn.setBackgroundColor(0x26E05252);
            sessionCard.animate().scaleX(1.02f).scaleY(1.02f).setDuration(150).withEndAction(
                () -> sessionCard.animate().scaleX(1f).scaleY(1f).setDuration(150).start()
            ).start();
        });

        // Animate friend cards
        View friendsSection = root.findViewById(R.id.friends_section);
        if (friendsSection != null) slideUp(friendsSection, 200);

        return root;
    }

    private void slideUp(View v, long delay) {
        v.setAlpha(0f); v.setTranslationY(28f);
        v.animate().alpha(1f).translationY(0f).setDuration(450).setStartDelay(delay)
            .setInterpolator(new DecelerateInterpolator(2f)).start();
    }
}
