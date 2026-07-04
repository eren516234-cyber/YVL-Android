package com.yvl.app.util;

import com.yvl.app.model.Song;
import org.json.*;
import java.net.URLEncoder;
import java.util.*;

public class JamendoApi {
    private static final String CLIENT_ID = "b6747d04";
    private static final String BASE = "https://api.jamendo.com/v3.0/tracks/";

    public interface SongsCallback {
        void onSongs(List<Song> songs);
        void onError(String msg);
    }

    public static void search(String query, int limit, SongsCallback cb) {
        try {
            String enc = URLEncoder.encode(query, "UTF-8");
            String url = BASE + "?client_id=" + CLIENT_ID
                    + "&format=jsonpretty&limit=" + limit
                    + "&search=" + enc
                    + "&include=musicinfo&audioformat=mp31&imagesize=600";
            NetworkHelper.get(url, new NetworkHelper.Callback() {
                public void onResult(String json) { cb.onSongs(parse(json)); }
                public void onError(Exception e) { cb.onError(e.getMessage()); }
            });
        } catch (Exception e) { cb.onError(e.getMessage()); }
    }

    public static void trending(int limit, SongsCallback cb) {
        String url = BASE + "?client_id=" + CLIENT_ID
                + "&format=jsonpretty&limit=" + limit
                + "&order=popularity_total&include=musicinfo&audioformat=mp31&imagesize=600";
        NetworkHelper.get(url, new NetworkHelper.Callback() {
            public void onResult(String json) { cb.onSongs(parse(json)); }
            public void onError(Exception e) { cb.onError(e.getMessage()); }
        });
    }

    public static void genre(String tag, int limit, SongsCallback cb) {
        try {
            String url = BASE + "?client_id=" + CLIENT_ID
                    + "&format=jsonpretty&limit=" + limit
                    + "&tags=" + URLEncoder.encode(tag, "UTF-8")
                    + "&order=popularity_total&include=musicinfo&audioformat=mp31&imagesize=600";
            NetworkHelper.get(url, new NetworkHelper.Callback() {
                public void onResult(String json) { cb.onSongs(parse(json)); }
                public void onError(Exception e) { cb.onError(e.getMessage()); }
            });
        } catch (Exception e) { cb.onError(e.getMessage()); }
    }

    private static List<Song> parse(String json) {
        List<Song> list = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(json);
            JSONArray results = root.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject t = results.getJSONObject(i);
                String title    = t.optString("name", "Unknown");
                String artist   = t.optString("artist_name", "Unknown");
                int dur         = t.optInt("duration", 0);
                String durFmt   = String.format("%d:%02d", dur / 60, dur % 60);
                String audio    = t.optString("audio", "");
                String art      = t.optString("image", "");
                String album    = t.optString("album_name", "");
                list.add(new Song(title, artist, durFmt, audio, art, album));
            }
        } catch (Exception ignored) {}
        return list;
    }
}
