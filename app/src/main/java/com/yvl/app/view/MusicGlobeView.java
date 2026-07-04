package com.yvl.app.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.util.*;
import android.view.*;

public class MusicGlobeView extends View {
    private static final int N = 20;
    private static final int[] HUES = {340,200,260,160,30,190,0,45,220,280,80,55,130,310,355,240,210,290,170,100};

    private final float[] ptX = new float[N], ptY = new float[N], ptZ = new float[N];
    private final float[] fibTheta = new float[N], fibPhi = new float[N];
    private float rotY = 0.4f, rotX = 0.25f;
    private float velX = 0f, velY = 0f;
    private float dragStartX, dragStartY, dragStartRotY, dragStartRotX;
    private boolean dragging = false;
    private long lastDragTime;
    private float lastDragX, lastDragY;
    private int selectedIdx = -1;
    private ValueAnimator autoRotAnim;

    private final Paint spherePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint gridPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dotPaint    = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint glowPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint shinePaint  = new Paint(Paint.ANTI_ALIAS_FLAG);

    private static final float R = 160f, DIST = 600f;

    public MusicGlobeView(Context ctx) { super(ctx); init(); }
    public MusicGlobeView(Context ctx, AttributeSet a) { super(ctx, a); init(); }

    private void init() {
        double golden = Math.PI * (3 - Math.sqrt(5));
        for (int i = 0; i < N; i++) {
            double y = 1 - (i / (double)(N-1)) * 2;
            double r = Math.sqrt(1 - y*y);
            double theta = golden * i;
            fibTheta[i] = (float) Math.atan2(r * Math.sin(theta), r * Math.cos(theta));
            fibPhi[i]   = (float) Math.acos(y);
        }

        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(0.8f);
        gridPaint.setColor(0x26648CFF);

        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(20f);

        shinePaint.setColor(0x1FFFFFFF);

        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    public void startAutoRotate() {
        if (autoRotAnim != null) autoRotAnim.cancel();
        autoRotAnim = ValueAnimator.ofFloat(0f, 1f);
        autoRotAnim.setDuration(16);
        autoRotAnim.setRepeatCount(ValueAnimator.INFINITE);
        autoRotAnim.addUpdateListener(a -> {
            if (!dragging) {
                rotY += 0.0025f;
                if (Math.abs(velX) > 0.0003f || Math.abs(velY) > 0.0003f) {
                    velX *= 0.94f; velY *= 0.94f;
                    rotY += velX; rotX = clamp(rotX + velY, -1.1f, 1.1f);
                }
            }
            updatePoints(); invalidate();
        });
        autoRotAnim.start();
    }

    private void updatePoints() {
        float cosRx = (float)Math.cos(rotX), sinRx = (float)Math.sin(rotX);
        for (int i = 0; i < N; i++) {
            float sinPhi = (float)Math.sin(fibPhi[i]), cosPhi = (float)Math.cos(fibPhi[i]);
            float theta = fibTheta[i] + rotY;
            float x = R * sinPhi * (float)Math.cos(theta);
            float yr= R * cosPhi;
            float zr= R * sinPhi * (float)Math.sin(theta);
            ptX[i] = x;
            ptY[i] = yr * cosRx - zr * sinRx;
            ptZ[i] = yr * sinRx + zr * cosRx;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float w = getWidth(), h = getHeight();
        float cx = w/2f, cy = h * 0.42f;

        // Atmosphere glow
        RadialGradient atmGrad = new RadialGradient(cx, cy, R+22,
            new int[]{0x00000000, 0x2E1E3CB4, 0x00000000},
            new float[]{0.7f,0.9f,1f}, Shader.TileMode.CLAMP);
        Paint atmPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        atmPaint.setShader(atmGrad);
        canvas.drawCircle(cx, cy, R+22, atmPaint);

        // Sphere body
        RadialGradient sphereGrad = new RadialGradient(cx - R*0.2f, cy - R*0.2f, R*1.4f,
            new int[]{0xFF1A2035, 0xFF0D1220, 0xFF060810, 0xFF000000},
            new float[]{0f,0.4f,0.8f,1f}, Shader.TileMode.CLAMP);
        spherePaint.setShader(sphereGrad);
        canvas.drawCircle(cx, cy, R, spherePaint);

        // Grid lines (simplified lat lines)
        canvas.save();
        canvas.clipRect(cx-R, cy-R, cx+R, cy+R);
        float[] latAngles = {-60f,-30f,0f,30f,60f};
        for (float deg : latAngles) {
            float phi = deg * (float)Math.PI / 180f;
            drawLatLine(canvas, cx, cy, phi);
        }
        canvas.restore();

        // Shine highlight
        RadialGradient shineGrad = new RadialGradient(cx - R*0.3f, cy - R*0.3f, R*0.7f,
            new int[]{0x1EFFFFFF, 0x00FFFFFF}, new float[]{0f,1f}, Shader.TileMode.CLAMP);
        shinePaint.setShader(shineGrad);
        canvas.drawCircle(cx, cy, R, shinePaint);

        // Edge shadow
        Paint edgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        edgePaint.setStyle(Paint.Style.STROKE);
        edgePaint.setStrokeWidth(8f);
        edgePaint.setColor(0xB3000000);
        canvas.drawCircle(cx, cy, R, edgePaint);

        // Sort by Z for depth ordering
        Integer[] order = new Integer[N];
        for (int i=0;i<N;i++) order[i]=i;
        java.util.Arrays.sort(order,(a,b)->Float.compare(ptZ[a],ptZ[b]));

        // Draw song dots
        for (int idx : order) {
            float scale = (ptZ[idx] + R + DIST) / (2*R + DIST);
            float px = cx + ptX[idx] * scale * 1.1f;
            float py = cy + ptY[idx] * scale * 1.1f;
            boolean isBack = ptZ[idx] < -R * 0.15f;
            float opacity = isBack ? 0.15f + scale*0.3f : 0.75f + scale*0.25f;
            float dotR = 6f + scale * 5f;
            if (idx == selectedIdx) dotR = 14f;

            int h2 = HUES[idx];
            float[] hsv1 = {h2, 0.7f, 0.3f};
            int color1 = Color.HSVToColor((int)(opacity*255), hsv1);

            dotPaint.setColor(color1);
            canvas.drawCircle(px, py, dotR, dotPaint);

            if (!isBack) {
                Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
                stroke.setStyle(Paint.Style.STROKE);
                stroke.setStrokeWidth(1f);
                stroke.setColor(Color.argb((int)(opacity*80),255,255,255));
                canvas.drawCircle(px, py, dotR, stroke);
            }
        }
    }

    private void drawLatLine(Canvas canvas, float cx, float cy, float phi) {
        float cosRx = (float)Math.cos(rotX), sinRx = (float)Math.sin(rotX);
        Path path = new Path(); boolean first = true;
        for (int i = 0; i <= 60; i++) {
            float theta = (float)(i / 60.0 * 2 * Math.PI) + rotY;
            float x = R * (float)Math.cos(phi) * (float)Math.cos(theta);
            float yr= R * (float)Math.sin(phi);
            float zr= R * (float)Math.cos(phi) * (float)Math.sin(theta);
            float y = yr*cosRx - zr*sinRx;
            float z = yr*sinRx + zr*cosRx;
            if (z < -R*0.05f) { first = true; continue; }
            float scale = (z + R + DIST)/(2*R+DIST);
            float px = cx + x*scale*1.1f, py = cy + y*scale*1.1f;
            if (first) { path.moveTo(px,py); first=false; } else path.lineTo(px,py);
        }
        gridPaint.setStrokeWidth(phi==0?1.2f:0.7f);
        canvas.drawPath(path, gridPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dragging=true; dragStartX=e.getX(); dragStartY=e.getY();
                dragStartRotY=rotY; dragStartRotX=rotX; velX=0; velY=0;
                lastDragX=e.getX(); lastDragY=e.getY(); lastDragTime=System.currentTimeMillis();
                return true;
            case MotionEvent.ACTION_MOVE:
                float dx=(e.getX()-dragStartX)*0.007f, dy=(e.getY()-dragStartY)*0.005f;
                rotY=dragStartRotY+dx; rotX=clamp(dragStartRotX+dy,-1.1f,1.1f);
                long now=System.currentTimeMillis(); long dt=Math.max(1,now-lastDragTime);
                velX=(e.getX()-lastDragX)*0.007f/dt*16f;
                velY=(e.getY()-lastDragY)*0.005f/dt*16f;
                lastDragX=e.getX(); lastDragY=e.getY(); lastDragTime=now;
                return true;
            case MotionEvent.ACTION_UP: case MotionEvent.ACTION_CANCEL:
                dragging=false; return true;
        }
        return super.onTouchEvent(e);
    }

    private float clamp(float v, float lo, float hi) { return Math.max(lo, Math.min(hi, v)); }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (autoRotAnim!=null) autoRotAnim.cancel();
    }
}
