package com.yvl.app;

import android.animation.*;
import android.content.Intent;
import android.graphics.Color;
import android.os.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.yvl.app.view.VinylLogoView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Pure black window
        getWindow().setBackgroundDrawableResource(android.R.color.black);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_splash);

        View root        = findViewById(R.id.splash_root);
        View ring1       = findViewById(R.id.ring1);
        View ring2       = findViewById(R.id.ring2);
        View ring3       = findViewById(R.id.ring3);
        View logoGroup   = findViewById(R.id.logo_group);
        VinylLogoView vinyl = findViewById(R.id.vinyl_logo);
        TextView yvlText = findViewById(R.id.yvl_text);
        TextView tagline = findViewById(R.id.tagline);
        View expandLine  = findViewById(R.id.expand_line);
        View dotsRow     = findViewById(R.id.dots_row);
        TextView credit  = findViewById(R.id.credit);

        // Phase 1 (300ms): show rings, float logo, fade up YVL
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            vinyl.startSpin();
            pulseRing(ring1, 1800, 0);
            pulseRing(ring2, 2200, 350);
            pulseRing(ring3, 2600, 700);
            floatAnim(logoGroup);
            fadeUp(yvlText, 0);
        }, 300);

        // Phase 2 (1200ms): tagline + expand line + dots
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            fadeUp(tagline, 0);
            expandLine.setVisibility(View.VISIBLE);
            expandLine.setPivotX(0);
            expandLine.setScaleX(0);
            expandLine.animate().scaleX(1f).setDuration(800)
                .setInterpolator(new DecelerateInterpolator()).start();
            dotsRow.setVisibility(View.VISIBLE);
            blinkDots(dotsRow);
        }, 1200);

        // Phase 3 (2200ms): credit
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            fadeUp(credit, 0);
        }, 2200);

        // Navigate to main (3000ms)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 3000);
    }

    private void pulseRing(View v, long dur, long delay) {
        v.setVisibility(View.VISIBLE);
        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(v,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 0.8f, 2.4f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.8f, 2.4f),
            PropertyValuesHolder.ofFloat(View.ALPHA,  0.8f, 0f));
        anim.setDuration(dur);
        anim.setStartDelay(delay);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.start();
    }

    private void floatAnim(View v) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(v, View.TRANSLATION_Y, 0f, -28f, 0f);
        anim.setDuration(4000);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
    }

    private void fadeUp(View v, long delay) {
        v.setVisibility(View.VISIBLE);
        v.setAlpha(0f);
        v.setTranslationY(28f);
        v.animate().alpha(1f).translationY(0f).setDuration(600).setStartDelay(delay)
            .setInterpolator(new DecelerateInterpolator()).start();
    }

    private void blinkDots(View dotsRow) {
        if (!(dotsRow instanceof ViewGroup)) return;
        ViewGroup group = (ViewGroup) dotsRow;
        for (int i = 0; i < group.getChildCount(); i++) {
            View dot = group.getChildAt(i);
            ObjectAnimator blink = ObjectAnimator.ofFloat(dot, View.ALPHA, 1f, 0.2f, 1f);
            blink.setDuration(1200);
            blink.setStartDelay(i * 200L);
            blink.setRepeatCount(ValueAnimator.INFINITE);
            blink.start();
        }
    }
}
