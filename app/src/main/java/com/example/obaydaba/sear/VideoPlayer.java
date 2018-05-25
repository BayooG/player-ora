package com.example.obaydaba.sear;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class VideoPlayer extends AppCompatActivity {
    private VideoView videoView;
    private MediaController mController;
    private boolean state;
    private int stopPosition=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_player);

        if( savedInstanceState != null ) {
            stopPosition = savedInstanceState.getInt("position");
        }

        Intent intent = getIntent();
        Bundle pass = intent.getExtras();
        String uri = pass.getString("uri");
        videoView = (VideoView) findViewById(R.id.video_view);
        mController = new MediaController(this);
        Uri u = Uri.parse(uri);
        videoView.setVideoURI(u);

        videoView.setMediaController(mController);
        videoView.start();


    }
    @Override
    public void onResume() {
        super.onResume();
        videoView.seekTo(stopPosition);
        if(state)
            videoView.resume(); //Or use resume() if it doesn't work. I'm not sure
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        stopPosition = videoView.getCurrentPosition();
        videoView.pause();
        state =videoView.isPlaying();
        outState.putBoolean("state",state);
        outState.putInt("position", stopPosition);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(videoView.getCurrentPosition()==videoView.getDuration())
            onBackPressed();
    }
}
