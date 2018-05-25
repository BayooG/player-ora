package com.example.obaydaba.sear;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Stream extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout ;
    private ActionBarDrawerToggle mToggle ;
    private Button fetch;
    private EditText url;
    private String link;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);

        Toolbar toolbar = (Toolbar) findViewById(R.id.streamBar);
        setSupportActionBar(toolbar);

        setTitle("Stream");

        mDrawerLayout =(DrawerLayout) findViewById(R.id.streamDrawer);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_sview);
        navigationView.setNavigationItemSelectedListener(this);


        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1 && checkSelfPermission(Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED){
            if(shouldShowRequestPermissionRationale(Manifest.permission.INTERNET)){
                Toast.makeText(this, "Permission Required !", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.INTERNET}, 1);
        }


        fetch = (Button) findViewById(R.id.stream);
        url = (EditText) findViewById(R.id.url);

        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                link=url.getText().toString();

                if(!Patterns.WEB_URL.matcher(link).matches()){
                    AlertDialog.Builder dig = new AlertDialog.Builder(getApplicationContext());
                    dig.setMessage("Not a valid URL");


                }
                else{
                    startActivity(new Intent(Stream.this,StreamMedia.class).putExtra("stream",link));
                }

            }
        });


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.nav_library){
            Intent intent =new Intent(getApplicationContext(),MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
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


        if (id == R.id.nav_play) {
            startActivity(new Intent(getApplicationContext(),Main2Activity.class));
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.streamDrawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }
}
