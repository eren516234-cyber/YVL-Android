package com.yvl.app.util;

import android.content.*;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

public class FontManager {
    private static FontManager instance;
    private final SharedPreferences prefs;
    private static final String KEY_FONT = "font_id";

    public static final int FONT_DEFAULT    = 0;
    public static final int FONT_SERIF      = 1;
    public static final int FONT_MONO       = 2;
    public static final int FONT_CONDENSED  = 3;
    public static final int FONT_THIN       = 4;
    public static final int FONT_LIGHT      = 5;
    public static final int FONT_MEDIUM     = 6;
    public static final int FONT_BOLD       = 7;

    public static final String[] FONT_NAMES    = {"Default", "Serif", "Mono", "Condensed", "Thin", "Light", "Medium", "Bold"};
    public static final String[] FONT_FAMILIES = {
        "sans-serif", "serif", "monospace", "sans-serif-condensed",
        "sans-serif-thin", "sans-serif-light", "sans-serif-medium", "sans-serif-black"
    };
    public static final String[] FONT_PREVIEWS = {
        "Aa  Roboto", "Aa  Serif", "Aa  Mono", "Aa  Condensed",
        "Aa  Thin", "Aa  Light", "Aa  Medium", "Aa  Bold"
    };

    private FontManager(Context ctx) {
        prefs = ctx.getSharedPreferences("yvl_font", Context.MODE_PRIVATE);
    }

    public static synchronized FontManager getInstance(Context ctx) {
        if (instance == null) instance = new FontManager(ctx.getApplicationContext());
        return instance;
    }

    public int getFontId()         { return prefs.getInt(KEY_FONT, FONT_DEFAULT); }
    public void setFontId(int id)  { prefs.edit().putInt(KEY_FONT, id).apply(); }

    public Typeface getTypeface() {
        int id = getFontId();
        String family = FONT_FAMILIES[Math.min(id, FONT_FAMILIES.length - 1)];
        try { return Typeface.create(family, Typeface.NORMAL); }
        catch (Exception e) { return Typeface.DEFAULT; }
    }

    public Typeface getBoldTypeface() {
        int id = getFontId();
        String family = FONT_FAMILIES[Math.min(id, FONT_FAMILIES.length - 1)];
        try { return Typeface.create(family, Typeface.BOLD); }
        catch (Exception e) { return Typeface.DEFAULT_BOLD; }
    }

    public void applyToView(View v) {
        if (v instanceof TextView) {
            TextView tv = (TextView) v;
            boolean bold = (tv.getTypeface() != null && tv.getTypeface().isBold());
            tv.setTypeface(bold ? getBoldTypeface() : getTypeface());
        }
    }
}
