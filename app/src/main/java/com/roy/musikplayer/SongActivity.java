package com.roy.musikplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class SongActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    private ImageButton buttonPlay, buttonNext, buttonPrevious, buttonRepeat, buttonShuffle;
    private TextView songTitle;
    private SeekBar songProgress;
    private TextView songCurrentTime, songTotalTime;
    private ImageButton backToList;

    // MediaPlayer
    public MyMediaPlayer mediaPlayer = MyMediaPlayer.getMediaPlayer() ;
    private Handler handler = new Handler();
    private MusikManager musikManager;
    private MusikUtilities musikUtilities;

    private int currentSongIndex = 0;

    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private ArrayList<HashMap<String, String>> songList = new ArrayList<HashMap<String, String>>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        // Player Buttons
        buttonPlay = findViewById(R.id.buttonPlay);
        buttonNext = findViewById(R.id.buttonNext);
        buttonPrevious = findViewById(R.id.buttonPrevious);
        buttonRepeat = findViewById(R.id.buttonRepeat);
        buttonShuffle = findViewById(R.id.buttonShuffle);

        songTitle = findViewById(R.id.songTitle);
        songProgress = findViewById(R.id.songProgress);
        songCurrentTime = findViewById(R.id.songCurrentTime);
        songTotalTime = findViewById(R.id.songTotalTime);

        backToList = findViewById(R.id.backToList);
        songProgress = findViewById(R.id.songProgress);

        musikManager = new MusikManager();
        musikUtilities = new MusikUtilities();

        // Listeners
        songProgress.setOnSeekBarChangeListener(this);
        mediaPlayer.setOnCompletionListener(this);

        // Get Songs and Song Index
        songList = musikManager.getAllSongs(Environment.getExternalStorageDirectory());
        String currentSong = getIntent().getStringExtra("songIndex");
        currentSongIndex = Integer.valueOf(currentSong);
        playSong(currentSongIndex);



        // Play button
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Check if the song is already playing
                if (mediaPlayer.isPlaying()){
                    if (mediaPlayer != null){
                        mediaPlayer.pause();
                        buttonPlay.setImageResource(R.drawable.ic_play);
                    }
                }else {
                    // Resume song
                    if (mediaPlayer != null){
                        mediaPlayer.start();
                        buttonPlay.setImageResource(R.drawable.ic_pause);
                    }
                }
            }
        });

        //  Play Next Song Button
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentSongIndex < (songList.size()-1)){
                    playSong(currentSongIndex + 1);
                    currentSongIndex = currentSongIndex + 1;
                } else {
                    playSong(0);
                    currentSongIndex = 0;
                }
            }
        });

        //  Play Previous Song Button
        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSongIndex > 0){
                    playSong(currentSongIndex - 1);
                    currentSongIndex = currentSongIndex -1;
                }else {
                    playSong(songList.size() - 1);
                    currentSongIndex = songList.size() - 1;
                }
            }
        });

        // Back to Playlist
        backToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SongActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Repeat Button
        buttonRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRepeat){
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "Repeat - OFF",Toast.LENGTH_LONG).show();
                    buttonRepeat.setImageResource(R.drawable.ic_repeat);
                }else {
                    isRepeat=true;
                    Toast.makeText(getApplicationContext(), "Repeat - ON",Toast.LENGTH_LONG).show();
                    isShuffle = false;
                    buttonRepeat.setImageResource(R.drawable.ic_repeat_on);
                    buttonShuffle.setImageResource(R.drawable.ic_shuffle);
                }
            }
        });

        // Shuffle Button
        buttonShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShuffle){
                    isShuffle = false;
                    Toast.makeText(getApplicationContext(), "Shuffle - OFF",Toast.LENGTH_LONG).show();
                    buttonShuffle.setImageResource(R.drawable.ic_shuffle);
                }else {
                    isShuffle=true;
                    Toast.makeText(getApplicationContext(), "Shuffle - ON",Toast.LENGTH_LONG).show();
                    isRepeat = false;
                    buttonShuffle.setImageResource(R.drawable.ic_shuffle_on);
                    buttonRepeat.setImageResource(R.drawable.ic_repeat);

                }
            }
        });

    }

    //Method to play song
    public void playSong(int songIndex){
        try{
            mediaPlayer.reset();
            mediaPlayer.setDataSource(songList.get(songIndex).get("songPath"));
            mediaPlayer.prepare();
            mediaPlayer.start();

            // Displaying Song Name
            String songTitleName = songList.get(songIndex).get("songTitle");
            songTitle.setText(songTitleName);

            buttonPlay.setImageResource(R.drawable.ic_pause);


            songProgress.setProgress(0);
            songProgress.setProgress(100);

            // Updating Progress Bar
            updateProgressBar();


        }catch (IllegalStateException | IOException e){
            e.printStackTrace();
        }
    }

    // Update timer
    public void updateProgressBar() {
        handler.postDelayed(updateTimeTask,100);
    }


    // Background Thread  to handle Timer and Seekbar
    private Runnable updateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = mediaPlayer.getCurrentPosition();

            songTotalTime.setText(""+musikUtilities.milliSecondsToTimer(totalDuration));

            songCurrentTime.setText(""+musikUtilities.milliSecondsToTimer(currentDuration));


            int progress = (int)(musikUtilities.getProgressPercentage(currentDuration, totalDuration));

            songProgress.setProgress(progress);

            handler.postDelayed(this, 100);
        }
    };

    /* if Repeat is ON play same song
     if Shuffle is ON play random song */
    @Override
    public void onCompletion(MediaPlayer mp) {
        if (isRepeat){
            playSong(currentSongIndex);
        } else if (isShuffle){
            Random random = new Random();
            currentSongIndex = random.nextInt((songList.size() - 1) -0 + 1) +0 ;
            playSong(currentSongIndex);
        } else {
            if (currentSongIndex < (songList.size() - 1)){
                playSong(currentSongIndex + 1 );
                currentSongIndex = currentSongIndex + 1 ;
            } else {
                playSong(0);
                currentSongIndex = 0;
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    // Starts moving the progress bar
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(updateTimeTask);
    }

    // Stops moving progress bar
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(updateTimeTask);
        int totalDuration = mediaPlayer.getDuration();
        int currentPosition = musikUtilities.progressToTimer(seekBar.getProgress(), totalDuration);

        mediaPlayer.seekTo(currentPosition);

        updateProgressBar();
    }

    // Back pressed go to Playlist or Previous Activity
    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(SongActivity.this,MainActivity.class);
        startActivity(backIntent);

    }
}
