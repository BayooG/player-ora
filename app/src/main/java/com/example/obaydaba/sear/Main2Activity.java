package com.example.obaydaba.sear;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Main2Activity extends Activity implements View.OnClickListener,NavigationView.OnNavigationItemSelectedListener {
    private ImageView iv;
    public  TextView textView,artist;
    static MediaPlayer mp= null;
    public AudioManager audioManager;
    private NavigationView navigationView;
    private SharedPreferences pos;
    ArrayList<File> mySongs;
    Set<String> favList;
    int position;
    Uri u;
    SeekBar volumeControl;
    Bitmap image;
    Thread UpdateSeekBar;
    ImageView cover ;
    Bundle b;
    SeekBar sb;
    ImageButton btplay,btNxt,btPv,btFF,btFB,fav;
    Set<String> library;
    MediaMetadataRetriever mData;
    boolean running=true;
    boolean crapState=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        btplay = (ImageButton) findViewById(R.id.btplay);
        btNxt = (ImageButton) findViewById(R.id.btnext);
        btPv = (ImageButton) findViewById(R.id.btprev);
        btFB = (ImageButton) findViewById(R.id.btfb);
        btFF = (ImageButton) findViewById(R.id.btff);
        cover = (ImageView) findViewById(R.id.Cover);
        fav = (ImageButton) findViewById(R.id.fav);
        mData = new MediaMetadataRetriever();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeControl = (SeekBar) findViewById(R.id.sBar2);
        volumeControl.setMax(maxVolume);
        volumeControl.setProgress(curVolume);
        volumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);

            }

        });
        textView = (TextView) findViewById(R.id.songname);
        artist = (TextView) findViewById(R.id.artist);
        btplay.setOnClickListener(this);
        btNxt.setOnClickListener(this);
        btPv.setOnClickListener(this);
        btFB.setOnClickListener(this);
        btFF.setOnClickListener(this);
        fav.setOnClickListener(this);
        sb = (SeekBar) findViewById(R.id.seekBar);
        UpdateSeekBar = new Thread() {
            @Override
            public void run() {
                int totalDuration = mp.getDuration();
                int currentposition = 0;
                while (currentposition < totalDuration) {
                    if (running) {
                        try {
                            sleep(500);
                            if(mp!=null){
                                try{
                                    currentposition = mp.getCurrentPosition();

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
                    } else
                        yield();
                }

            }
        };


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.player);


        SharedPreferences preferences = getSharedPreferences("songs",MODE_PRIVATE);
        favList =preferences.getStringSet("fav",new HashSet<String>());

        if(mp!=null){
            if(!UpdateSeekBar.isInterrupted()){

                UpdateSeekBar.interrupt();
            }
            if(mp.isPlaying()){
                crapState=true;
                btplay.setImageDrawable(getResources().getDrawable(R.mipmap.ic_pause_circle_outline_black_36dp));
            }
        }
        else{
            if(mp!=null) {
                mp.stop();
                mp.release();
                mp = null;
                if (UpdateSeekBar != null) {
                    UpdateSeekBar.interrupt();
                }
            }
        }




        Intent i=getIntent();
        b= i.getExtras();



        if(b!=null){

            if(mp!=null){
                mp.stop();
                mp.release();
                mp=null;
                if(!UpdateSeekBar.isInterrupted()){
                    UpdateSeekBar.interrupt();
                }

                if(savedInstanceState!=null)
                    running=savedInstanceState.getBoolean("Seek");
            }

            if(mp==null){

                mySongs = (ArrayList) b.getParcelableArrayList("songlist");
                position= b.getInt("pos",0);
                btplay.setImageDrawable(getResources().getDrawable(R.mipmap.ic_pause_circle_outline_black_36dp));

                u =Uri.parse(mySongs.get(position).toString());


                if(favList.contains(u.toString())){
                    fav.setImageDrawable(getResources().getDrawable(R.mipmap.ic_favorite_black_36dp));

                }
                else{
                    fav.setImageDrawable(getResources().getDrawable(R.mipmap.ic_favorite_border_black_36dp));
                }




                textView.setText(mySongs.get(position).getName().replace(".mp3","").replace(".m4a",""));
                mp=MediaPlayer.create(getApplicationContext(),u);
                //mData = new MediaMetadataRetriever();
                mData.setDataSource(u.getPath());
                /*if(mData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)==null)
                    textView.setText("Untitled");
                else
                    textView.setText(mData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
*/
                try{
                    byte art[]=mData.getEmbeddedPicture();
                    image= BitmapFactory.decodeByteArray(art, 0, art.length);
                    cover.setImageDrawable(new BitmapDrawable(image));
                }
                catch(Exception e)
                {
                    image=null;
                    if(image!=null)
                        cover.setImageDrawable(new BitmapDrawable(image));
                }
                if(mData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)==null)
                    artist.setText("Unknown Artist");
                else
                    artist.setText(mData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));

                //textView.setText( mySongs.get(position).toString());
                //textView.setText(file.title());
                mp.start();
                sb.setMax(mp.getDuration());
                UpdateSeekBar.start();

            }
            else{
                mp.stop();
                mp.release();
            }
        }
        else {

                cover.setImageDrawable(getResources().getDrawable(R.drawable.cover));

            library= preferences.getStringSet("Library",new HashSet<String>());
            mySongs = new ArrayList<>();
            for(String s:library){
                mySongs.add(new File(s));
                Log.i("retrieved","r");

            }





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

        if(crapState==true){
            crapState=false;
            pos =getSharedPreferences("songs",MODE_PRIVATE);
            position=pos.getInt("position",0);
            textView.setText(pos.getString("name","Untitled"));
            artist.setText(pos.getString("artist","Unknown Artist"));
        }

        /*try {
            MediaPlayer temp;
            temp =new MediaPlayer();
            u=Uri.parse(mySongs.get((position+1)%mySongs.size()).getPath());
            temp.setDataSource(getApplicationContext(),u);
            mp.setNextMediaPlayer(temp);

        } catch (IOException e) {
            e.printStackTrace();
        }*/


    }
    @Override
    public void onClick(View v) {
        Runtime.getRuntime().gc();
        System.gc();
        int id =v.getId();
        if(mp!=null){
            switch (id){

                case R.id.fav:


                    if(favList.contains(u.toString())){
                        favList.remove(u.toString());
                        fav.setImageDrawable(getResources().getDrawable(R.mipmap.ic_favorite_border_black_36dp));

                    }
                    else{
                        favList.add(u.toString());
                        fav.setImageDrawable(getResources().getDrawable(R.mipmap.ic_favorite_black_36dp));
                        SharedPreferences.Editor editor =getSharedPreferences("songs",MODE_PRIVATE).edit();
                        editor.putStringSet("fav",favList);
                        editor.apply();

                    }
                    break;


                case R.id.btplay:
                    if (mp.isPlaying())
                    {
                        //  btplay.setText(">");
                        mp.pause();
                        btplay.setImageDrawable(getResources().getDrawable(R.mipmap.ic_play_circle_outline_black_36dp));
                    }
                    else{
                        mp.start();
                        btplay.setImageDrawable(getResources().getDrawable(R.mipmap.ic_pause_circle_outline_black_36dp));

                    }
                    break;
                case R.id.btff:
                    mp.seekTo(mp.getCurrentPosition()+5000);
                    break;
                case R.id.btfb:
                    mp.seekTo(mp.getCurrentPosition()-5000);
                    break;
                case R.id.btnext:
                    if(!UpdateSeekBar.isInterrupted()){
                        UpdateSeekBar.interrupt();
                    }
                    mp.stop();
                    mp.release();
                    position=(position+1)%mySongs.size();
                    if(!mySongs.get(position).exists())
                        startActivity(new Intent(getApplicationContext(),Main2Activity.class));

                    textView.setText(mySongs.get(position).getName().replace(".mp3","").replace(".m4a","").replace(".MP3","").replace(".M4A",""));
                    u =Uri.parse(mySongs.get(position).toString());


                    if(favList.contains(u.toString())){
                        fav.setImageDrawable(getResources().getDrawable(R.mipmap.ic_favorite_black_36dp));
                    }
                    else{
                        fav.setImageDrawable(getResources().getDrawable(R.mipmap.ic_favorite_border_black_36dp));
                    }

                    mp=MediaPlayer.create(getApplicationContext(),u);
                    mData.setDataSource(u.getPath());

                    try{
                        byte art[]=mData.getEmbeddedPicture();
                        image= BitmapFactory.decodeByteArray(art, 0, art.length);
                    }
                    catch(Exception e) {image=null;}
                    if(image!=null)
                        cover.setImageDrawable(new BitmapDrawable(image));
                    else{
                        cover.setImageDrawable(getResources().getDrawable(R.drawable.cover));
                    }
                    image=null;
                    if(mData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)==null)
                        artist.setText("Unknown Artist");
                    else
                        artist.setText(mData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
                    mp.start();
                    sb.setMax(mp.getDuration());
                    break;
                case R.id.btprev:

                    if(!UpdateSeekBar.isInterrupted()){
                        UpdateSeekBar.interrupt();
                    }
                    mp.stop();
                    mp.release();

                    position=(position-1 <0)? mySongs.size()-1:position-1;
                    u =Uri.parse(mySongs.get(position).toString());


                    if(favList.contains(u.toString())){
                        fav.setImageDrawable(getResources().getDrawable(R.mipmap.ic_favorite_black_36dp));
                    }
                    else{
                        fav.setImageDrawable(getResources().getDrawable(R.mipmap.ic_favorite_border_black_36dp));
                    }

                    mp=MediaPlayer.create(getApplicationContext(),u);
                    mData = new MediaMetadataRetriever();
                    mData.setDataSource(u.getPath());
                    if(mData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)==null)
                        textView.setText("Untitled");
                    else
                        textView.setText(mData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
                    try{
                        byte art[]=mData.getEmbeddedPicture();
                        image= BitmapFactory.decodeByteArray(art, 0, art.length);
                    }
                    catch(Exception e) {image=null;}
                    if(image!=null)
                        cover.setImageDrawable(new BitmapDrawable(image));
                    if(mData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)==null)
                        artist.setText("Unknown Artist");
                    else
                        artist.setText(mData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));

                    mp.start();
                    sb.setMax(mp.getDuration());
                    break;

            }

        }
        else if(mp==null)
        {
            if(id ==R.id.btplay) {
                Random r = new Random();
                position = r.nextInt(mySongs.size());

                u =Uri.parse(mySongs.get(position).toString());
                mp=MediaPlayer.create(getApplicationContext(),u);
                mData = new MediaMetadataRetriever();
                mData.setDataSource(u.getPath());
                if(mData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)==null)
                    textView.setText("Untitled");
                else
                    textView.setText(mData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));

                try{
                    byte art[]=mData.getEmbeddedPicture();
                    image= BitmapFactory.decodeByteArray(art, 0, art.length);
                }
                catch(Exception e)
                {
                    image=null;
                    if(image!=null)
                        cover.setImageDrawable(new BitmapDrawable(image));
                }
                if(mData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)==null)
                    artist.setText("Unknown Artist");
                else
                    artist.setText(mData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));

                //textView.setText( mySongs.get(position).toString());
                //textView.setText(file.title());
                btplay.setImageDrawable(getResources().getDrawable(R.mipmap.ic_pause_circle_outline_black_36dp));
                mp.start();
                sb.setMax(mp.getDuration());
                UpdateSeekBar.start();

            }
        }


    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


            outState.putBoolean("seek",running);
            outState.putString("name", String.valueOf(textView.getText()));
            outState.putString("artist", String.valueOf(artist.getText()));




    }
    @Override
    protected void onPause() {
        super.onPause();




        UpdateSeekBar.interrupt();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mp.isPlaying()){
            SharedPreferences.Editor editor = getSharedPreferences("songs",MODE_PRIVATE).edit();
            editor.putInt("position",position);
            editor.putString("name",mySongs.get(position).getName());
            editor.putString("artist",artist.getText().toString());
            editor.apply();


        }

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.nav_library){
            Intent intent =new Intent(Main2Activity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            //onBackPressed();
        }

        if(id == R.id.nav_stream ){
            startActivity(new Intent(Main2Activity.this,Stream.class));
        }

        if(id == R.id.nav_down){
            startActivity(new Intent(getApplicationContext(),Download.class));
        }
        if(id ==R.id.fav_vid){
            startActivity(new Intent(getApplicationContext(),VideoLibrary.class));
        }

        if(id== R.id.nav_about){
            startActivity( new Intent(getApplicationContext(),about.class));
        }

        if(id == R.id.nav_fav){
            startActivity(new Intent(getApplicationContext(),Favorite.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main2);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
       // volumeControl.incrementProgressBy(keyCode);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.nav_play);
    }
}

