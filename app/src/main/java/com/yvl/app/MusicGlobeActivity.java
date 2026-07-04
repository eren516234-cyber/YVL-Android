package com.yvl.app;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.yvl.app.view.MusicGlobeView;

public class MusicGlobeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(android.R.color.black);
        setContentView(R.layout.activity_music_globe);
        MusicGlobeView globe = findViewById(R.id.globe_view);
        globe.startAutoRotate();
        findViewById(R.id.btn_back_globe).setOnClickListener(v -> finish());
    }
}
