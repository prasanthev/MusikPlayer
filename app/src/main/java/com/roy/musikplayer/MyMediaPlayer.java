package com.roy.musikplayer;

import android.media.MediaPlayer;
import android.os.Environment;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

// Singleton Media Player
public class MyMediaPlayer extends MediaPlayer {

    private static MyMediaPlayer mediaPlayer ;

    private MyMediaPlayer(){

    }

    // Create Media Player only when NULL
    public static MyMediaPlayer getMediaPlayer(){
        if (mediaPlayer == null){
            mediaPlayer =  new MyMediaPlayer();
        }
        return mediaPlayer;
    }

    @Override
    public void start() throws IllegalStateException {
        super.start();
    }

    @Override
    public void prepare() throws IOException, IllegalStateException {
        super.prepare();
    }

    @Override
    public void reset() {
        super.reset();
    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, IllegalStateException, SecurityException {
        super.setDataSource(path);
    }
}
