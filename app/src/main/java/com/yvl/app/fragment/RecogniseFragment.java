package com.yvl.app.fragment;

import android.animation.*;
import android.graphics.Color;
import android.os.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import androidx.fragment.app.Fragment;
import com.yvl.app.R;
import com.yvl.app.util.ThemeManager;
import com.yvl.app.view.WaveformView;

public class RecogniseFragment extends Fragment {
    private enum State { IDLE, LISTENING, FOUND }
    private State state = State.IDLE;
    private View ringView1, ringView2, ringView3, resultCard, historySection;
    private View orbView;
    private WaveformView waveform;
    private TextView statusText;
    private ObjectAnimator[] ringAnims;

    @Override public View onCreateView(LayoutInflater inf, ViewGroup parent, Bundle state2) {
        View root = inf.inflate(R.layout.fragment_recognise, parent, false);
        ThemeManager tm = ThemeManager.getInstance(requireContext());
        root.setBackgroundColor(tm.getBgColor());

        orbView        = root.findViewById(R.id.orb_btn);
        ringView1      = root.findViewById(R.id.ring1);
        ringView2      = root.findViewById(R.id.ring2);
        ringView3      = root.findViewById(R.id.ring3);
        waveform       = root.findViewById(R.id.waveform);
        statusText     = root.findViewById(R.id.status_text);
        resultCard     = root.findViewById(R.id.result_card);
        historySection = root.findViewById(R.id.history_section);

        TextView title = root.findViewById(R.id.recognise_title);
        if (title != null) { title.setTextColor(tm.getTextColor()); slideUp(title, 0); }

        orbView.setOnClickListener(v -> {
            if (state == State.IDLE) startListening();
            else if (state == State.FOUND) resetToIdle();
        });

        View playNowBtn = root.findViewById(R.id.play_now_btn);
        if (playNowBtn != null) playNowBtn.setOnClickListener(v -> {
            startActivity(new android.content.Intent(requireContext(), com.yvl.app.PlayerActivity.class));
        });

        setState(State.IDLE, tm);
        return root;
    }

    private void startListening() {
        ThemeManager tm = ThemeManager.getInstance(requireContext());
        setState(State.LISTENING, tm);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isAdded()) setState(State.FOUND, tm);
        }, 4000);
    }

    private void resetToIdle() {
        setState(State.IDLE, ThemeManager.getInstance(requireContext()));
    }

    private void setState(State s, ThemeManager tm) {
        state = s;
        // Stop all ring anims
        stopRings();
        if (waveform != null) waveform.stopWave();

        if (statusText != null) {
            statusText.setText(s == State.FOUND ? "Song identified! ✓"
                : s == State.LISTENING ? "Listening…" : "Tap to identify what's playing");
        }
        if (resultCard != null) resultCard.setVisibility(s == State.FOUND ? View.VISIBLE : View.GONE);
        if (historySection != null) historySection.setVisibility(s == State.IDLE ? View.VISIBLE : View.GONE);

        if (s == State.LISTENING) {
            startRings();
            if (waveform != null) waveform.startWave();
            if (waveform != null) waveform.setVisibility(View.VISIBLE);
        } else {
            if (waveform != null) waveform.setVisibility(View.GONE);
        }

        // Orb style
        if (orbView != null) {
            android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
            gd.setShape(android.graphics.drawable.GradientDrawable.OVAL);
            gd.setColor(s == State.FOUND ? tm.getTextColor() : tm.getSurfaceColor());
            gd.setStroke(4, s != State.IDLE ? tm.getTextColor() : tm.getBorderColor());
            orbView.setBackground(gd);
            if (s == State.FOUND) {
                orbView.animate().scaleX(1.08f).scaleY(1.08f).setDuration(200).withEndAction(
                    () -> orbView.animate().scaleX(1f).scaleY(1f).setDuration(200).start()).start();
            }
        }

        if (s == State.FOUND && resultCard != null) {
            resultCard.setAlpha(0f); resultCard.setTranslationY(16f);
            resultCard.animate().alpha(1f).translationY(0f).setDuration(350)
                .setInterpolator(new DecelerateInterpolator(2f)).start();
        }
    }

    private void startRings() {
        View[] rings = {ringView1, ringView2, ringView3};
        for (View ring : rings) if (ring != null) ring.setVisibility(View.VISIBLE);
        ringAnims = new ObjectAnimator[3];
        for (int i = 0; i < 3; i++) {
            if (rings[i] == null) continue;
            ringAnims[i] = ObjectAnimator.ofPropertyValuesHolder(rings[i],
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 2.4f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 2.4f),
                PropertyValuesHolder.ofFloat(View.ALPHA, 0.4f, 0f));
            ringAnims[i].setDuration(1400 + i*500L);
            ringAnims[i].setStartDelay(i * 300L);
            ringAnims[i].setRepeatCount(ValueAnimator.INFINITE);
            ringAnims[i].setInterpolator(new DecelerateInterpolator());
            ringAnims[i].start();
        }
    }

    private void stopRings() {
        if (ringAnims != null) for (ObjectAnimator a : ringAnims) if (a != null) a.cancel();
        View[] rings = {ringView1, ringView2, ringView3};
        for (View r : rings) if (r != null) { r.setVisibility(View.GONE); r.setScaleX(1f); r.setScaleY(1f); r.setAlpha(1f); }
    }

    private void slideUp(View v, long delay) {
        v.setAlpha(0f); v.setTranslationY(24f);
        v.animate().alpha(1f).translationY(0f).setDuration(450).setStartDelay(delay)
            .setInterpolator(new DecelerateInterpolator(2f)).start();
    }
}
