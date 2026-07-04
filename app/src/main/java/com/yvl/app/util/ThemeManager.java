package com.yvl.app.util;

import android.content.*;
import android.graphics.Color;

public class ThemeManager {
    private static ThemeManager instance;
    private final SharedPreferences prefs;

    private static final String KEY_RAINBOW = "rainbow_enabled";
    private static final String KEY_COLOR   = "rainbow_color_idx";
    private static final String KEY_CUSTOM  = "custom_color";

    public static final int[] RAINBOW_BG  = {0xFFFFFFFF,0xFFE0F0FF,0xFFFFE0F0,0xFFE0FFE8,0xFFFFFBE0,0xFFF0E0FF,0xFFFFE8E0,0xFFE0FFF8};
    public static final int[] RAINBOW_TXT = {0xFF000000,0xFF003366,0xFF660033,0xFF003316,0xFF4D3A00,0xFF330066,0xFF661A00,0xFF003328};
    public static final int[] RAINBOW_ACC = {0xFF000000,0xFF0066CC,0xFFCC0066,0xFF006633,0xFFCC9900,0xFF6600CC,0xFFCC3300,0xFF009966};

    private ThemeManager(Context ctx) {
        prefs = ctx.getSharedPreferences("yvl_theme", Context.MODE_PRIVATE);
    }
    public static synchronized ThemeManager getInstance(Context ctx) {
        if (instance == null) instance = new ThemeManager(ctx.getApplicationContext());
        return instance;
    }

    public boolean isRainbow()      { return prefs.getBoolean(KEY_RAINBOW, false); }
    public int getColorIdx()        { return prefs.getInt(KEY_COLOR, 0); }
    public int getCustomColor()     { return prefs.getInt(KEY_CUSTOM, -1); }
    public void setRainbow(boolean b){ prefs.edit().putBoolean(KEY_RAINBOW,b).apply(); }
    public void setColorIdx(int idx) { prefs.edit().putInt(KEY_COLOR,idx).apply(); }
    public void setCustomColor(int c){ prefs.edit().putInt(KEY_CUSTOM,c).apply(); }
    public void clearCustomColor()   { prefs.edit().remove(KEY_CUSTOM).apply(); }
    public void reset()              { prefs.edit().clear().apply(); }

    public int getBgColor() {
        int custom = getCustomColor();
        if (custom != -1) return custom;
        if (isRainbow()) return RAINBOW_BG[Math.min(getColorIdx(), RAINBOW_BG.length-1)];
        return 0xFF000000;
    }
    public int getTextColor() {
        if (getCustomColor() != -1 || isRainbow()) {
            int idx = getColorIdx();
            return getCustomColor() != -1 ? 0xFF000000 : RAINBOW_TXT[Math.min(idx, RAINBOW_TXT.length-1)];
        }
        return 0xFFFFFFFF;
    }
    public int getSurfaceColor() {
        if (getCustomColor() != -1 || isRainbow()) return 0x12000000;
        return 0xFF111111;
    }
    public int getAccentColor() {
        if (getCustomColor() != -1) return 0xFF000000;
        if (isRainbow()) return RAINBOW_ACC[Math.min(getColorIdx(), RAINBOW_ACC.length-1)];
        return 0xFFFFFFFF;
    }
    public int getBorderColor() {
        if (getCustomColor() != -1 || isRainbow()) return 0x1F000000;
        return 0xFF222222;
    }
    public int getMutedColor() {
        if (getCustomColor() != -1 || isRainbow()) return 0x66000000;
        return 0xFF555555;
    }
}
