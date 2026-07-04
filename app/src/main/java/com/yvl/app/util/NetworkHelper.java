package com.yvl.app.util;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class NetworkHelper {
    public interface Callback {
        void onResult(String json);
        void onError(Exception e);
    }

    public static void get(String urlStr, Callback cb) {
        new Thread(() -> {
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(12000);
                conn.setReadTimeout(15000);
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("User-Agent", "YVL/1.0");
                int code = conn.getResponseCode();
                InputStream is = (code < 400) ? conn.getInputStream() : conn.getErrorStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line).append('\n');
                reader.close();
                if (cb != null) cb.onResult(sb.toString());
            } catch (Exception e) {
                if (cb != null) cb.onError(e);
            }
        }).start();
    }
}
