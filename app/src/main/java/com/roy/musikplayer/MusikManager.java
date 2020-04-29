package com.roy.musikplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

// Music Manager to get all songs from External Storage
public class MusikManager {

    public ArrayList<HashMap<String,String>> getAllSongs(File file) {
        ArrayList<HashMap<String,String>> songList = new ArrayList<HashMap<String, String>>();

        File[] allFiles = file.listFiles();

        for (File individualFile : allFiles){
            boolean f  = individualFile.isDirectory();
            if (individualFile.isDirectory() && !individualFile.isHidden()){
                songList.addAll(getAllSongs(individualFile));
            }
            else {
                // Filter Audio songs and add it to list
                if (individualFile.getName().endsWith(".mp3") || individualFile.getName().endsWith(".aac") || individualFile.getName().endsWith(".wma") ){
                    HashMap<String,String> song = new HashMap<String, String>();
                    song.put("songTitle",individualFile.getName().substring(0,(individualFile.getName().length()-4)));
                    song.put("songPath",individualFile.getPath());
                    songList.add(song);
                }
            }
        }
        return songList;
    }
}