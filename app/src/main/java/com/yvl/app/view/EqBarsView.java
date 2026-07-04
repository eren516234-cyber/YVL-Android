package com.yvl.app.view;

import android.animation.*;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

public class EqBarsView extends View {
    private static final int BAR_COUNT = 7;
    private final float[] heights = new float[BAR_COUNT];
    private final Paint barPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final ValueAnimator[] anims = new ValueAnimator[BAR_COUNT];
    private int barColor = 0xFFFFFFFF;

    public EqBarsView(Context ctx) { super(ctx); init(); }
    public EqBarsView(Context ctx, AttributeSet a) { super(ctx, a); init(); }

    private void init() {
        barPaint.setColor(barColor);
        for (int i = 0; i < BAR_COUNT; i++) heights[i] = 0.3f;
    }

    public void setPlaying(boolean p) {
        if (p) startAnims(); else stopAnims();
    }

    public void setColor(int c) { barColor = c; barPaint.setColor(c); invalidate(); }

    private void startAnims() {
        float[] peaks = {0.4f,0.9f,0.6f,1.0f,0.7f,0.85f,0.5f};
        for (int i = 0; i < BAR_COUNT; i++) {
            final int idx = i;
            if (anims[i] != null) anims[i].cancel();
            anims[i] = ValueAnimator.ofFloat(0.1f, peaks[i], 0.1f);
            anims[i].setDuration(400 + i * 70L);
            anims[i].setStartDelay(i * 80L);
            anims[i].setRepeatCount(ValueAnimator.INFINITE);
            anims[i].setRepeatMode(ValueAnimator.REVERSE);
            anims[i].setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
            anims[i].addUpdateListener(a -> { heights[idx]=(float)a.getAnimatedValue(); invalidate(); });
            anims[i].start();
        }
    }

    private void stopAnims() {
        for (ValueAnimator a : anims) if (a!=null) a.cancel();
        for (int i = 0; i < BAR_COUNT; i++) heights[i] = 0.15f;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float w = getWidth(), h = getHeight();
        float gap = 4f, barW = (w - gap*(BAR_COUNT-1)) / BAR_COUNT;
        for (int i = 0; i < BAR_COUNT; i++) {
            float barH = heights[i] * h;
            float left = i * (barW + gap);
            RectF rect = new RectF(left, h - barH, left + barW, h);
            float radius = barW / 2f;
            canvas.drawRoundRect(rect, radius, radius, barPaint);
        }
    }

    @Override protected void onDetachedFromWindow() { super.onDetachedFromWindow(); stopAnims(); }
}
