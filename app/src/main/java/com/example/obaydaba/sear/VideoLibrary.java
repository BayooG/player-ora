package com.example.obaydaba.sear;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.R.attr.id;
import static android.R.attr.x;
import static com.example.obaydaba.sear.R.id.url;

public class VideoLibrary extends AppCompatActivity
implements NavigationView.OnNavigationItemSelectedListener{
    SharedPreferences preferences;
    Set<String> library;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private ViewStub stubList;
    private ListView listView;
    private VideoList videoList,flist;
    private List<Video>  vList;
    private ArrayList<String> vids;
    private Video temp;
    private MediaMetadataRetriever mData;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_library);
        Toolbar toolbar = (Toolbar) findViewById(R.id.vidBar);
        setSupportActionBar(toolbar);
        setTitle("Video Library");
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_vid);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        stubList =(ViewStub) findViewById(R.id.stub_vid_list);
        stubList.inflate();
        listView = (ListView) findViewById(R.id.video_list);
        listView.setOnItemClickListener(onItemClick);
        //navigationView.setCheckedItem(R.id.nav_vid);


        preferences = getSharedPreferences("videos",MODE_PRIVATE);
        library = preferences.getStringSet("videos",new HashSet<String>());

       // if(library.size()>0){
            library = getVids();
      //  }
        SharedPreferences.Editor editor = getSharedPreferences("videos",MODE_PRIVATE).edit();
        editor.putStringSet("videos",library);
        editor.apply();

        vList = new ArrayList<>();
        vids = new ArrayList<>();
        for (String item :library) {
            temp = new Video();
            vids.add(item);
            mData= new MediaMetadataRetriever();
            mData.setDataSource(Uri.parse(item).getPath());///??????/////
            long t = Long.valueOf(  mData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            t=t/1000;
            temp.time=String.valueOf(t/60)+":"+String.valueOf(t%60);
            temp.reso=mData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                    +"x"+
                    mData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            temp.vidName=(new File(item).getName()).replace(".MP4","")
                    .replace(".mp4","").replace(".M4V","").replace("m4v","");
            mData.release();
            vList.add(temp);
        }

        setAdapter();


    }

    private void setAdapter() {
        videoList = new VideoList(getApplicationContext(),R.layout.video_item,vList);
        listView.setAdapter(videoList);
    }

    AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            startActivity(new Intent(getApplicationContext(), VideoPlayer.class)
                    .putExtra("uri",vids.get(position)));
        }
    };

    private Set<String> getVids() {
        Set<String> s1,s2;

        s1 =findVids(Environment.getExternalStorageDirectory());
        String secStore = System.getenv("SECONDARY_STORAGE");


           try{

               File sd = new File(secStore);
               if(sd.exists()){
                   s2 =findVids(sd);
                   s1.addAll(s2);
               }
           }catch (Exception e ){
               s2 = new HashSet<>();
           }

        return s1;
    }

    private Set<String> findVids(File root) {
        //File root = Environment.getExternalStorageDirectory();
        Set<String> al = new HashSet<String>();
        File[] files = root.listFiles();
        for (File singlFile : files) {
            if (singlFile.isDirectory() && !singlFile.isHidden()) {

                al.addAll(findVids(singlFile));

            } else {
                if (singlFile.getName().endsWith(".mp4") || singlFile.getName().endsWith(".m4v") ||
                        singlFile.getName().endsWith(".M4V") || singlFile.getName().endsWith(".MP4")) {
                    al.add(singlFile.toString());

                }
            }
        }
        return  al;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search_action);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        //Setting listener for the SearchView

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        ComponentName componentName = new ComponentName(getApplicationContext(), VideoLibrary.class);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

        return true;

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }
}
