package com.yvl.app.util;

import com.yvl.app.model.*;
import java.util.*;

public class AppData {
    public static List<Song> getSongs() {
        return Arrays.asList(
            new Song("Blinding Lights", "The Weeknd", "3:20"),
            new Song("Less Than Zero",  "The Weeknd", "2:11"),
            new Song("Was Ich Liebe",   "Rammstein",  "4:20"),
            new Song("Save Your Tears", "The Weeknd", "3:17"),
            new Song("Hornay",          "AY YOLA",    "3:12"),
            new Song("Flowers",         "Miley Cyrus","3:20"),
            new Song("Anti-Hero",       "Taylor Swift","3:21"),
            new Song("Meant To Be",     "bbnoS",      "2:46")
        );
    }
    public static List<String> getArtists() {
        return Arrays.asList("Kendrick Lamar","Billie Eilish","The Weeknd",
            "Taylor Swift","Rammstein","AY YOLA","Miley Cyrus","Eminem");
    }
    public static List<String> getPlaylists() {
        return Arrays.asList("Late Night","Morning Coffee","Workout",
            "Focus Mode","Road Trip","Lo-Fi Study");
    }
    public static List<String> getAlbums() {
        return Arrays.asList("After Hours","Damn.","When We All Fall Asleep",
            "Folklore","Mutter","Mr. Morale");
    }
    public static List<String> getCategories() {
        return Arrays.asList("Pop","Hip-Hop","Rock","R&B","K-Pop",
            "Electronic","Jazz","Lo-Fi","Classical","Reggaeton");
    }
    public static List<Song> getTrending() {
        return Arrays.asList(
            new Song("Flowers","Miley Cyrus","3:20"),
            new Song("Cruel Summer","Taylor Swift","3:18"),
            new Song("Last Night","Morgan Wallen","2:44"),
            new Song("Anti-Hero","Taylor Swift","3:21")
        );
    }
    public static List<Song> getGlobeSongs() {
        return Arrays.asList(
            new Song("Blinding Lights","The Weeknd",""),
            new Song("Go Ghost","Eminem",""),
            new Song("Less Than Zero","The Weeknd",""),
            new Song("Flowers","Miley Cyrus",""),
            new Song("Anti-Hero","Taylor Swift",""),
            new Song("As It Was","Harry Styles",""),
            new Song("Kill Bill","SZA",""),
            new Song("Golden Hour","JVKE",""),
            new Song("Was Ich Liebe","Capo",""),
            new Song("Save Your Tears","The Weeknd",""),
            new Song("Meant To Be","Bebe Rexha",""),
            new Song("Cruel Summer","Taylor Swift",""),
            new Song("Last Night","Morgan Wallen",""),
            new Song("Hornay","Unknown",""),
            new Song("Die For You","The Weeknd",""),
            new Song("Starboy","The Weeknd",""),
            new Song("In The Night","The Weeknd",""),
            new Song("After Hours","The Weeknd",""),
            new Song("Scared Lonely","Martin Garrix",""),
            new Song("Ghost","Justin Bieber","")
        );
    }
}
