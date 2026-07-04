package com.yvl.app;

import android.animation.*;
import android.os.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.yvl.app.view.*;

public class PlayerActivity extends AppCompatActivity {

    private boolean playing = true;
    private boolean liked   = false;
    private ValueAnimator progressAnim;
    private VinylView vinylView;
    private EqBarsView eqBarsView;
    private SeekBar seekBar;
    private TextView timeCurrent, timeTotal;
    private ImageView playPauseBtn, likeBtn;
    private float progress = 0.44f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(android.R.color.black);
        setContentView(R.layout.activity_player);

        vinylView   = findViewById(R.id.vinyl_view);
        eqBarsView  = findViewById(R.id.eq_bars);
        seekBar     = findViewById(R.id.seek_bar);
        timeCurrent = findViewById(R.id.time_current);
        timeTotal   = findViewById(R.id.time_total);
        playPauseBtn= findViewById(R.id.btn_play_pause);
        likeBtn     = findViewById(R.id.btn_like);
        ImageView backBtn = findViewById(R.id.btn_back);

        timeTotal.setText("3:40");
        seekBar.setMax(1000);
        seekBar.setProgress((int)(progress * 1000));
        updateTime();

        startProgressAnim();
        vinylView.setPlaying(true);
        eqBarsView.setPlaying(true);

        playPauseBtn.setOnClickListener(v -> togglePlay());
        likeBtn.setOnClickListener(v -> toggleLike());
        backBtn.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, R.anim.slide_down);
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar sb, int p, boolean user) {
                if (user) { progress = p / 1000f; updateTime(); }
            }
            public void onStartTrackingTouch(SeekBar sb) { if(progressAnim!=null) progressAnim.pause(); }
            public void onStopTrackingTouch(SeekBar sb)  { if(playing) startProgressAnim(); }
        });

        // Slide up animation
        View contentPanel = findViewById(R.id.content_panel);
        contentPanel.setTranslationY(60f);
        contentPanel.setAlpha(0f);
        contentPanel.animate().translationY(0f).alpha(1f)
            .setDuration(450).setStartDelay(100)
            .setInterpolator(new DecelerateInterpolator(2f)).start();
    }

    private void togglePlay() {
        playing = !playing;
        vinylView.setPlaying(playing);
        eqBarsView.setPlaying(playing);
        playPauseBtn.setImageResource(playing ? R.drawable.ic_pause : R.drawable.ic_play);
        if (playing) startProgressAnim(); else { if(progressAnim!=null) progressAnim.pause(); }
    }

    private void toggleLike() {
        liked = !liked;
        likeBtn.setImageResource(liked ? R.drawable.ic_heart_filled : R.drawable.ic_heart);
        // Pop animation
        likeBtn.animate().scaleX(1.4f).scaleY(1.4f).setDuration(120).withEndAction(
            () -> likeBtn.animate().scaleX(1f).scaleY(1f).setDuration(120).start()
        ).start();
    }

    private void startProgressAnim() {
        if (progressAnim != null) progressAnim.cancel();
        progressAnim = ValueAnimator.ofFloat(progress, 1f);
        progressAnim.setDuration((long)((1f - progress) * 220000));
        progressAnim.addUpdateListener(a -> {
            progress = (float)a.getAnimatedValue();
            seekBar.setProgress((int)(progress * 1000));
            updateTime();
        });
        progressAnim.start();
    }

    private void updateTime() {
        int totalSec = 220;
        int elapsed  = (int)(progress * totalSec);
        timeCurrent.setText(fmt(elapsed));
    }

    private String fmt(int sec) {
        return String.format("%d:%02d", sec/60, sec%60);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if (progressAnim != null) progressAnim.cancel();
    }
}
