package com.yvl.app.view;

import android.animation.*;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

public class VinylLogoView extends View {
    private final Paint discPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint ringPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint centerPaint= new Paint(Paint.ANTI_ALIAS_FLAG);
    private float rotation = 0f;
    private ValueAnimator spinAnim;

    public VinylLogoView(Context ctx) { super(ctx); init(); }
    public VinylLogoView(Context ctx, AttributeSet a) { super(ctx,a); init(); }

    private void init() {
        discPaint.setColor(0xFF111111);
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setColor(0x10FFFFFF);
        ringPaint.setStrokeWidth(1f);
        centerPaint.setColor(0xFFFFFFFF);
    }

    public void startSpin() {
        if (spinAnim != null) spinAnim.cancel();
        spinAnim = ValueAnimator.ofFloat(0f, 360f);
        spinAnim.setDuration(6000);
        spinAnim.setRepeatCount(ValueAnimator.INFINITE);
        spinAnim.setInterpolator(new android.view.animation.LinearInterpolator());
        spinAnim.addUpdateListener(a -> { rotation = (float)a.getAnimatedValue(); invalidate(); });
        spinAnim.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float w = getWidth(), h = getHeight();
        float cx = w/2, cy = h/2, r = Math.min(w,h)/2f - 4f;
        canvas.save(); canvas.rotate(rotation, cx, cy);
        discPaint.setShader(new SweepGradient(cx,cy,
            new int[]{0xFF000000,0xFF1A1A1A,0xFF000000,0xFF111111,0xFF000000},
            new float[]{0f,0.25f,0.5f,0.75f,1f}));
        canvas.drawCircle(cx,cy,r,discPaint);
        canvas.restore();
        // Groove rings
        for (float rr : new float[]{r*0.76f, r*0.56f, r*0.36f}) {
            canvas.drawCircle(cx,cy,rr,ringPaint);
        }
        // Center white dot
        canvas.drawCircle(cx,cy,r*0.18f,centerPaint);
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (spinAnim!=null) spinAnim.cancel();
    }
}
