package com.yvl.app.model;

public class Song {
    public String title, artist, duration, audioUrl, artUrl, album;

    public Song(String title, String artist, String duration) {
        this.title = title; this.artist = artist; this.duration = duration;
        this.audioUrl = ""; this.artUrl = ""; this.album = "";
    }

    public Song(String title, String artist, String duration, String audioUrl, String artUrl, String album) {
        this.title = title; this.artist = artist; this.duration = duration;
        this.audioUrl = audioUrl; this.artUrl = artUrl; this.album = album;
    }
}
