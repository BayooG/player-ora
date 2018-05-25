package com.example.obaydaba.sear;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.net.URL;

import static com.example.obaydaba.sear.R.id.url;

public class Download extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout ;
    private ActionBarDrawerToggle mToggle ;
    DrawerLayout drawer ;
    EditText editText;
    Button get;
    String link;
    String format;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        editText = (EditText) findViewById(url);
        get = (Button) findViewById(R.id.download);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawer = (DrawerLayout) findViewById(R.id.activity_down);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.downBar);
        setSupportActionBar(toolbar);


        mDrawerLayout =(DrawerLayout) findViewById(R.id.activity_down);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        setTitle("Media Downloader");

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1 && checkSelfPermission(Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED){
            if(shouldShowRequestPermissionRationale(Manifest.permission.INTERNET)){
                Toast.makeText(this, "Permission Required !", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.INTERNET}, 1);
        }
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1 && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                Toast.makeText(this, "Permission Required !", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }



        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                link = editText.getText().toString();

                /*if(link.endsWith(".mp3")) {
                    format=".mp3";
                }
                if(link.endsWith(".m4a")){
                    format=".m4a";
                }

                if(link.endsWith(".mp4")) {
                    format=".mp4";
                }
                if(link.endsWith(".m4v")){
                    format=".m4v";
                }*/

                File folder = new File(Environment.getExternalStorageDirectory() + "/MediaPlayerDownloads");
                if (!folder.exists()) {
                    folder.mkdir();
                }




                Uri u = Uri.parse(link);

                DownloadManager downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(u);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                Long refrence = downloadManager.enqueue(request);

            }
        });



    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.nav_library){
            Intent intent =new Intent(getApplicationContext(),MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }

        if (id == R.id.nav_play) {
            this.
                    startActivity(new Intent(Download.this, Main2Activity.class));
        }

        if (id == R.id.nav_stream) {
            startActivity(new Intent(Download.this, Stream.class));
        }


        if(id ==R.id.fav_vid){
            startActivity(new Intent(getApplicationContext(),VideoLibrary.class));
        }

        if(id== R.id.nav_about){
            startActivity( new Intent(getApplicationContext(),about.class));
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_favorite);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
