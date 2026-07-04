package com.yvl.app.view;

import android.animation.*;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

public class WaveformView extends View {
    private static final int BAR_COUNT = 10;
    private final float[] BASE = {0.4f,0.8f,1.0f,0.6f,0.9f,0.5f,0.7f,0.3f,0.85f,0.65f};
    private final float[] heights = new float[BAR_COUNT];
    private final Paint barPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private ValueAnimator phaseAnim;
    private float phase = 0f;

    public WaveformView(Context ctx) { super(ctx); init(); }
    public WaveformView(Context ctx, AttributeSet a) { super(ctx, a); init(); }

    private void init() {
        barPaint.setColor(0x4DFFFFFF);
        for (int i = 0; i < BAR_COUNT; i++) heights[i] = 0.3f;
    }

    public void startWave() {
        if (phaseAnim != null) phaseAnim.cancel();
        phaseAnim = ValueAnimator.ofFloat(0f, (float)(Math.PI * 200));
        phaseAnim.setDuration(60000);
        phaseAnim.setRepeatCount(ValueAnimator.INFINITE);
        phaseAnim.setInterpolator(new android.view.animation.LinearInterpolator());
        phaseAnim.addUpdateListener(a -> {
            phase = (float)a.getAnimatedValue();
            for (int i = 0; i < BAR_COUNT; i++) {
                heights[i] = BASE[i] + 0.4f*(float)Math.sin(phase*0.08 + i*0.7);
                heights[i] = Math.max(0.05f, Math.min(1f, heights[i]));
            }
            invalidate();
        });
        phaseAnim.start();
    }

    public void stopWave() { if (phaseAnim!=null) phaseAnim.cancel(); }

    @Override
    protected void onDraw(Canvas canvas) {
        float w = getWidth(), h = getHeight();
        float gap = 3f, barW = (w - gap*(BAR_COUNT-1)) / BAR_COUNT;
        for (int i = 0; i < BAR_COUNT; i++) {
            float barH = heights[i] * h;
            float left = i * (barW + gap);
            RectF r = new RectF(left, h-barH, left+barW, h);
            canvas.drawRoundRect(r, 2f, 2f, barPaint);
        }
    }

    @Override protected void onDetachedFromWindow() { super.onDetachedFromWindow(); stopWave(); }
}
