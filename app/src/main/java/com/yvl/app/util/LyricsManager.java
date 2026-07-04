package com.yvl.app.util;

import org.json.*;
import java.net.URLEncoder;
import java.util.*;

public class LyricsManager {
    public static final int STYLE_SCROLL   = 0;
    public static final int STYLE_KARAOKE  = 1;
    public static final int STYLE_MINIMAL  = 2;

    public static class LyricLine {
        public final float timeMs;
        public final String text;
        public LyricLine(float timeMs, String text) { this.timeMs = timeMs; this.text = text; }
    }

    public interface LyricsCallback {
        void onLyrics(List<LyricLine> lines, String plain);
        void onError();
    }

    public static void fetch(String artist, String title, LyricsCallback cb) {
        try {
            String url = "https://lrclib.net/api/get?artist_name="
                    + URLEncoder.encode(artist, "UTF-8")
                    + "&track_name=" + URLEncoder.encode(title, "UTF-8");
            NetworkHelper.get(url, new NetworkHelper.Callback() {
                public void onResult(String json) {
                    try {
                        JSONObject obj = new JSONObject(json);
                        String synced = obj.optString("syncedLyrics", "");
                        String plain  = obj.optString("plainLyrics", "");
                        if (!synced.isEmpty()) {
                            cb.onLyrics(parseSynced(synced), plain);
                        } else if (!plain.isEmpty()) {
                            cb.onLyrics(parsePlain(plain), plain);
                        } else {
                            cb.onError();
                        }
                    } catch (Exception e) { cb.onError(); }
                }
                public void onError(Exception e) { cb.onError(); }
            });
        } catch (Exception e) { cb.onError(); }
    }

    private static List<LyricLine> parseSynced(String raw) {
        List<LyricLine> lines = new ArrayList<>();
        for (String line : raw.split("\n")) {
            line = line.trim();
            if (line.startsWith("[") && line.contains("]")) {
                try {
                    String tag  = line.substring(1, line.indexOf(']'));
                    String text = line.substring(line.indexOf(']') + 1).trim();
                    String[] parts = tag.split("[:.]");
                    if (parts.length >= 3) {
                        float ms = Integer.parseInt(parts[0]) * 60000f
                                 + Integer.parseInt(parts[1]) * 1000f
                                 + Integer.parseInt(parts[2]) * 10f;
                        if (!text.isEmpty()) lines.add(new LyricLine(ms, text));
                    }
                } catch (Exception ignored) {}
            }
        }
        return lines;
    }

    private static List<LyricLine> parsePlain(String raw) {
        List<LyricLine> lines = new ArrayList<>();
        String[] parts = raw.split("\n");
        for (int i = 0; i < parts.length; i++) {
            String text = parts[i].trim();
            if (!text.isEmpty()) lines.add(new LyricLine(i * 4000f, text));
        }
        return lines;
    }

    public static int getCurrentLine(List<LyricLine> lines, long posMs) {
        int cur = 0;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).timeMs <= posMs) cur = i;
            else break;
        }
        return cur;
    }
}
