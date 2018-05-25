package com.example.obaydaba.sear;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import static com.example.obaydaba.sear.Main2Activity.mp;
import static com.example.obaydaba.sear.R.id.seekBar;
import static com.example.obaydaba.sear.R.id.url;

public class StreamMedia extends AppCompatActivity {
    private  MediaPlayer mediaPlayer;
    Thread UpdateSeekBar;
    SeekBar sb;
    TextView name;
    ImageButton play , replay, forward;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_media);

        play   = (ImageButton) findViewById(R.id.play_stream);
        replay = (ImageButton)findViewById(R.id.replay_stream);
        forward= (ImageButton)findViewById(R.id.forward_stream);


        Bundle bundle = getIntent().getExtras();
        String stream =bundle.getString("stream");
        sb = (SeekBar) findViewById(R.id.seekStream);
        mediaPlayer = new MediaPlayer();
        mediaPlayer = new MediaPlayer();

        UpdateSeekBar = new Thread() {
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentposition = 0;
                while (currentposition < totalDuration) {
                        try {
                            sleep(500);
                            if(mediaPlayer!=null){
                                try{
                                    currentposition = mediaPlayer.getCurrentPosition();

                                }
                                catch (Exception e){
                                    currentposition=0;
                                }

                            }else{
                                currentposition=0;
                            }

                            sb.setProgress(currentposition);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                }

            }
        };





        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(stream);
            mediaPlayer.prepare();
            mediaPlayer.start();
            sb.setMax(mediaPlayer.getDuration());
            UpdateSeekBar.start();
        } catch (IOException e) {
            Toast.makeText(this, "NO CONTENT AVAILABLE!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(mp!=null){
                    mp.seekTo(seekBar.getProgress());
                }
            }
        });


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    play.setImageDrawable(getResources().getDrawable(R.mipmap.ic_play_circle_outline_black_36dp));
                }
                else{
                    mediaPlayer.start();
                    play.setImageDrawable(getResources().getDrawable(R.mipmap.ic_pause_circle_outline_black_36dp));

                }

            }
        });


        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.getCurrentPosition()-10000>0){

                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                }


            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mediaPlayer.getCurrentPosition()+30000<mediaPlayer.getDuration()){

                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+30000);
                }
            }
        });


        if(mediaPlayer==null){
            onBackPressed();
        }
    }




}
