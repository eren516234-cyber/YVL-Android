package com.yvl.app.util;

import android.content.*;
import android.graphics.*;

public class ThemeManager {
    private static ThemeManager instance;
    private final SharedPreferences prefs;

    private static final String KEY_THEME = "theme_id";

    // Theme IDs
    public static final int THEME_VOID   = 0;
    public static final int THEME_PURPLE = 1;
    public static final int THEME_OCEAN  = 2;
    public static final int THEME_SOLAR  = 3;
    public static final int THEME_CYBER  = 4;

    public static final String[] THEME_NAMES = {"Void", "Purple", "Ocean", "Solar", "Cyber"};

    // [VOID, PURPLE, OCEAN, SOLAR, CYBER]
    private static final int[] BG      = {0xFF000000, 0xFF0D0010, 0xFF000D1A, 0xFF150500, 0xFF00150A};
    private static final int[] SURFACE = {0xFF111111, 0xFF1C0030, 0xFF001840, 0xFF2A0E00, 0xFF00281A};
    private static final int[] ACCENT  = {0xFFFFFFFF, 0xFF9D4EDD, 0xFF0EA5E9, 0xFFF97316, 0xFF10B981};
    private static final int[] ACCENT2 = {0xFFCCCCCC, 0xFFEC4899, 0xFF38BDF8, 0xFFFBBF24, 0xFF34D399};
    private static final int[] TEXT    = {0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF};
    private static final int[] MUTED   = {0xFF555555, 0xFF7B5EA7, 0xFF1E4D7B, 0xFF7A3D00, 0xFF0A5C3A};
    private static final int[] BORDER  = {0xFF222222, 0xFF2D1050, 0xFF002055, 0xFF3A1800, 0xFF003322};

    // Gradient start/end for accent glow
    public static final int[][] GRAD = {
        {0xFF333333, 0xFF111111},
        {0xFF9D4EDD, 0xFF5B2D8E},
        {0xFF0EA5E9, 0xFF0369A1},
        {0xFFF97316, 0xFFB45309},
        {0xFF10B981, 0xFF047857}
    };

    private ThemeManager(Context ctx) {
        prefs = ctx.getSharedPreferences("yvl_theme", Context.MODE_PRIVATE);
    }

    public static synchronized ThemeManager getInstance(Context ctx) {
        if (instance == null) instance = new ThemeManager(ctx.getApplicationContext());
        return instance;
    }

    public int getThemeId()          { return prefs.getInt(KEY_THEME, THEME_VOID); }
    public void setThemeId(int id)   { prefs.edit().putInt(KEY_THEME, id).apply(); }
    public void reset()              { prefs.edit().clear().apply(); }

    private int idx() { return Math.min(getThemeId(), BG.length - 1); }

    public int getBgColor()      { return BG[idx()]; }
    public int getSurfaceColor() { return SURFACE[idx()]; }
    public int getAccentColor()  { return ACCENT[idx()]; }
    public int getAccent2Color() { return ACCENT2[idx()]; }
    public int getTextColor()    { return TEXT[idx()]; }
    public int getMutedColor()   { return MUTED[idx()]; }
    public int getBorderColor()  { return BORDER[idx()]; }

    public int getGradStart()    { return GRAD[idx()][0]; }
    public int getGradEnd()      { return GRAD[idx()][1]; }

    // Legacy compat
    public boolean isRainbow()   { return getThemeId() != THEME_VOID; }
}
