package com.yvl.app.view;

import android.animation.*;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class VinylView extends View {
    private final Paint discPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint ringPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint armPaint    = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float rotation = 0f;
    private boolean playing = false;
    private ValueAnimator spinAnim;

    public VinylView(Context ctx) { super(ctx); init(); }
    public VinylView(Context ctx, AttributeSet a) { super(ctx, a); init(); }

    private void init() {
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setColor(0x07FFFFFF);
        ringPaint.setStrokeWidth(1.5f);
        centerPaint.setColor(0xFF000000);
        labelPaint.setShader(new LinearGradient(0,0,0,100,0xFF1A1A1A,0xFF333333,Shader.TileMode.CLAMP));
        textPaint.setColor(0xFFFFFFFF);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
        shadowPaint.setColor(0xCC000000);
        shadowPaint.setMaskFilter(new BlurMaskFilter(40, BlurMaskFilter.Blur.NORMAL));
        armPaint.setStyle(Paint.Style.STROKE);
        armPaint.setStrokeWidth(6f);
        armPaint.setStrokeCap(Paint.Cap.ROUND);
        armPaint.setColor(0xFFCCCCCC);
        armPaint.setShadowLayer(8, 2, 4, 0x80000000);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    public void setPlaying(boolean p) {
        playing = p;
        if (p) startSpin(); else stopSpin();
    }

    private void startSpin() {
        if (spinAnim != null && spinAnim.isRunning()) return;
        spinAnim = ValueAnimator.ofFloat(rotation, rotation + 360f);
        spinAnim.setDuration(8000);
        spinAnim.setRepeatCount(ValueAnimator.INFINITE);
        spinAnim.setInterpolator(new LinearInterpolator());
        spinAnim.addUpdateListener(a -> { rotation = (float)a.getAnimatedValue() % 360f; invalidate(); });
        spinAnim.start();
    }

    private void stopSpin() {
        if (spinAnim != null) { spinAnim.cancel(); spinAnim = null; }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float w = getWidth(), h = getHeight();
        float cx = w * 0.5f, cy = h * 0.48f;
        float r  = Math.min(w, h) * 0.42f;

        // Shadow
        canvas.drawCircle(cx, cy + 20f, r, shadowPaint);

        // Disc
        canvas.save();
        canvas.rotate(rotation, cx, cy);
        Paint dp = new Paint(Paint.ANTI_ALIAS_FLAG);
        dp.setShader(new SweepGradient(cx, cy,
            new int[]{0xFF000000,0xFF1A1A1A,0xFF000000,0xFF0D0D0D,0xFF000000,0xFF111111,0xFF000000},
            new float[]{0f,0.13f,0.26f,0.39f,0.52f,0.75f,1f}));
        canvas.drawCircle(cx, cy, r, dp);
        canvas.restore();

        // Groove rings
        for (float rr : new float[]{r*0.90f, r*0.74f, r*0.57f, r*0.40f}) {
            canvas.drawCircle(cx, cy, rr, ringPaint);
        }

        // Center label (white border)
        Paint border = new Paint(Paint.ANTI_ALIAS_FLAG);
        border.setColor(0xFFFFFFFF); border.setStyle(Paint.Style.STROKE); border.setStrokeWidth(3f);
        Paint labelFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelFill.setShader(new LinearGradient(cx-r*0.17f,cy-r*0.17f,cx+r*0.17f,cy+r*0.17f,0xFF1A1A1A,0xFF333333,Shader.TileMode.CLAMP));
        canvas.drawCircle(cx, cy, r*0.18f, labelFill);
        canvas.drawCircle(cx, cy, r*0.18f, border);

        // "YVL" text in center
        textPaint.setTextSize(r * 0.13f);
        canvas.drawText("YVL", cx, cy + r*0.045f, textPaint);

        // Center spindle
        centerPaint.setColor(0xFF000000);
        canvas.drawCircle(cx, cy, r*0.02f, centerPaint);

        // Tonearm
        drawTonearm(canvas, cx, cy, r);
    }

    private void drawTonearm(Canvas canvas, float cx, float cy, float r) {
        float pivotX = cx + r + 20f, pivotY = cy - r * 0.8f;
        float angle  = playing ? 27f : 8f;
        float len    = r * 1.0f;

        canvas.save();
        canvas.rotate(angle, pivotX, pivotY);
        // Arm shaft
        Paint shaft = new Paint(Paint.ANTI_ALIAS_FLAG);
        shaft.setStyle(Paint.Style.STROKE);
        shaft.setStrokeWidth(5f);
        shaft.setStrokeCap(Paint.Cap.ROUND);
        shaft.setShader(new LinearGradient(pivotX, pivotY, pivotX, pivotY + len,
            0xFFFFFFFF, 0xFF555555, Shader.TileMode.CLAMP));
        canvas.drawLine(pivotX, pivotY, pivotX, pivotY + len, shaft);
        // Needle tip
        Paint tip = new Paint(Paint.ANTI_ALIAS_FLAG);
        tip.setColor(0xFFFFFFFF);
        canvas.drawCircle(pivotX, pivotY + len, 8f, tip);
        canvas.restore();

        // Pivot circle
        Paint pivot = new Paint(Paint.ANTI_ALIAS_FLAG);
        pivot.setColor(0xFF444444);
        canvas.drawCircle(pivotX, pivotY, 12f, pivot);
        pivot.setColor(0xFFCCCCCC);
        pivot.setStyle(Paint.Style.STROKE);
        pivot.setStrokeWidth(2f);
        canvas.drawCircle(pivotX, pivotY, 12f, pivot);
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopSpin();
    }
}
