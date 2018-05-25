package com.example.obaydaba.sear;
import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.R.transition.move;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {
    private static final String IBRARY = "Library";
    static final int VIEW_MODE_LISTVIEW = 0;
    static final int VIEW_MODE_GRIDVIEW = 1;
    final public int MY_PERMISSION = 1;
    NavigationView navigationView;
    private ViewStub stubGrid;
    private ViewStub stubList;
    private ListView listView;
    private GridView gridView;
    private ListViewAdaptor listViewAdaptor;
    private GridViewAdaptor gridViewAdaptor;
    private List<Product> productList;
    private int currentViewMode ;
    private Set<String> pref;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    public List<Product> library;
    public ArrayList<File> songs, fLibrary;
    private MediaMetadataRetriever mData;
    private Product temp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setTitleColor(R.color.white);
        setTitle("Library");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        stubList = (ViewStub) findViewById(R.id.stub_list);
        stubGrid = (ViewStub) findViewById(R.id.stub_grid);
        stubList.inflate();
        stubGrid.inflate();
        listView = (ListView) findViewById(R.id.mylistview);
        gridView = (GridView) findViewById(R.id.mygridview);
        listView.setOnItemClickListener(onItemClick);
        gridView.setOnItemClickListener(onItemClick);
        navigationView.setCheckedItem(R.id.nav_library);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1 && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Permission Required !", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION);
        }
        SharedPreferences sharedPreferences = getSharedPreferences("ViewMode", MODE_PRIVATE);
        currentViewMode = sharedPreferences.getInt("currentViewMode", VIEW_MODE_LISTVIEW);
        SharedPreferences preferences = getSharedPreferences("songs", MODE_PRIVATE);
        pref = preferences.getStringSet("songs", new HashSet<String>());

        if (pref.size() == 0) {
            pref = getPref();

            SharedPreferences.Editor editor = getSharedPreferences("songs", MODE_PRIVATE).edit();
            editor.putStringSet(IBRARY, pref);
            editor.apply();

            songs =new ArrayList<>();
            productList = new ArrayList<>();
            for (String item : pref) {
                songs.add(new File(item));
                mData = new MediaMetadataRetriever();
                temp = new Product();
                mData.setDataSource(Uri.parse(item).getPath());
                temp.file = item;
                temp.name = (new File(item).getName()).replace(".mp3","").replace("m4a","");
                temp.name.replace(".mp3", "").replace(".MP3", "").replace("M4A", "").replace("m4a", "");
                if (mData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) == null)
                    temp.title = "Untitled";
                else
                    temp.title = mData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

                if (mData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) == null)
                    temp.artist = "Unknown Artist";
                else
                    temp.artist = mData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

                Bitmap image;

                try {
                    byte art[] = mData.getEmbeddedPicture();
                    image = BitmapFactory.decodeByteArray(art, 0, art.length);
                } catch (Exception e) {
                    image = null;
                }

                if (image != null) {
                    temp.cover = new BitmapDrawable(image);
                    Runtime.getRuntime().gc();
                    System.gc();

                } else {
                    temp.cover = getResources().getDrawable(R.drawable.cover);
                }
                productList.add(temp);
            }
            library = productList;
        }
        switchView();
    }

    /*Over Here*/
    AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            toast("Playing:" + productList.get(position).title);
            startActivity(new Intent(getApplicationContext(), Main2Activity.class)
                    .putExtra("pos", position).putExtra("songlist", songs));
        }
    };
    private void switchView() {
        if (VIEW_MODE_LISTVIEW == currentViewMode) {
            stubList.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);

        } else {
            stubList.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
        }
        setAdapters();
    }

    private void setAdapters() {
        if (VIEW_MODE_LISTVIEW == currentViewMode) {
            listViewAdaptor = new ListViewAdaptor(this, R.layout.list_item, productList);
            listView.setAdapter(listViewAdaptor);
        } else {
            gridViewAdaptor = new GridViewAdaptor(this, R.layout.grid_item, productList);
            gridView.setAdapter(gridViewAdaptor);
        }

    }

    public Set<String> getPref(){
        Set<String> s1,s2 ;
        s1 =findSong(Environment.getExternalStorageDirectory());
        String secStore = System.getenv("SECONDARY_STORAGE");
        try{

            File sd = new File(secStore);
            if(sd.exists()){
                s2 =findSong(sd);
                s1.addAll(s2);
            }
        }catch (Exception e ){}
        return s1;
    }

    private Set<String> findSong (File root) {
        //File root = Environment.getExternalStorageDirectory();
        Set<String> al = new HashSet<String>();
        File[] files = root.listFiles();
        for (File singlFile : files) {
            if (singlFile.isDirectory() && !singlFile.isHidden()) {

                al.addAll(findSong(singlFile));

            } else {
                if (singlFile.getName().endsWith(".mp3") || singlFile.getName().endsWith(".m4a")
                        || singlFile.getName().endsWith(".M4A") || singlFile.getName().endsWith(".MP3")) {
                    al.add(singlFile.toString());

                }
            }
        }
        return  al;
    }

    public void toast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        //Setting listener for the SearchView

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        ComponentName componentName = new ComponentName(getApplicationContext(), MainActivity.class);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));
//???//
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                productList = library;
                songs = fLibrary;
                //adp.getFilter().filter(newText);
                //List<> = productList.stream().filter(p-> p.title.contains(newText));
                List<Product> temp = new ArrayList<Product>();
                ArrayList<File> ftemp = new ArrayList<File>();
                File tempFile;
                for (Product p : productList) {
                    if (p.title.contains(newText) || p.artist.contains(newText)) {
                        temp.add(p);
                        tempFile = new File(p.file);
                        ftemp.add(tempFile);
                    }
                }
                productList = temp;
                songs = ftemp;
                switchView();

                return false;
            }
        });


        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();
        switch (id) {
            case R.id.itme_menu_1:
                if (VIEW_MODE_LISTVIEW == currentViewMode) {
                    currentViewMode = VIEW_MODE_GRIDVIEW;
                } else {
                    currentViewMode = VIEW_MODE_LISTVIEW;
                }
                switchView();

                SharedPreferences sharedPreferences = getSharedPreferences("ViewMode", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.apply();
                break;

        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.nav_fav){
            startActivity(new Intent(MainActivity.this,Favorite.class));
        }

        if (id == R.id.nav_play) {
            startActivity(new Intent(MainActivity.this, Main2Activity.class));
        }

        if (id == R.id.nav_stream) {
            startActivity(new Intent(MainActivity.this, Stream.class));
        }

        if(id == R.id.nav_down){
            startActivity(new Intent(MainActivity.this,Download.class));
        }
        if(id ==R.id.fav_vid){
            startActivity(new Intent(getApplicationContext(),VideoLibrary.class));
        }

        if(id== R.id.nav_about){
            startActivity( new Intent(getApplicationContext(),about.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    protected void onDestroy() {

     super.onDestroy();
        Runtime.getRuntime().gc();
        System.gc();



    }
    @Override
    protected void onStop() {
        super.onStop();
        Runtime.getRuntime().gc();
        System.gc();

    }
    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.nav_library);

    }
}