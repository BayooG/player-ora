package com.example.obaydaba.sear;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.obaydaba.sear.R.id.fav;

public class Favorite extends AppCompatActivity
implements NavigationView.OnNavigationItemSelectedListener{

    static final int VIEW_MODE_LISTVIEW = 0;
    static final int VIEW_MODE_GRIDVIEW = 1;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private List<Product> productList;
    private int currentViewMode = 0;

    private ListViewAdaptor listViewAdaptor;
    private GridViewAdaptor gridViewAdaptor;

    public List<Product> library;
    private ViewStub stubGrid;
    private ViewStub stubList;
    private ListView listView;
    private GridView gridView;
    NavigationView navigationView;
    Set<String> favList;
    ArrayList<File> songs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        SharedPreferences preferences = getSharedPreferences("songs",MODE_PRIVATE);
        favList = preferences.getStringSet("fav",new HashSet<String>());
        songs = new ArrayList<>();
        for (String s :favList) {
            songs.add(new File(s));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.favBar);
        setSupportActionBar(toolbar);
        setTitle("Favorite");
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_favorite);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        stubList = (ViewStub) findViewById(R.id.stub_list);
        stubGrid = (ViewStub) findViewById(R.id.stub_grid);
        stubList.inflate();
        stubGrid.inflate();
        listView = (ListView) findViewById(R.id.mylistview);
        gridView = (GridView) findViewById(R.id.mygridview);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_fav);

        SharedPreferences sharedPreferences = getSharedPreferences("ViewMode", MODE_PRIVATE);
        currentViewMode = sharedPreferences.getInt("currentViewMode", VIEW_MODE_LISTVIEW);


        getProductList();

        listView.setOnItemClickListener(onItemClick);
        gridView.setOnItemClickListener(onItemClick);
        switchView();



    }

    /*Over Here*/
    AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            toast(productList.get(position).title);
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


    public void getProductList(){
        productList = new ArrayList<>();

        MediaMetadataRetriever mData;
        Product temp;

        for (String  item :favList) {

            mData = new MediaMetadataRetriever();
            mData.setDataSource(Uri.parse(item).getPath());
            temp = new Product();

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
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.nav_library){
            Intent intent =new Intent(getApplicationContext(),MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }

        if (id == R.id.nav_play) {
            startActivity(new Intent(getApplicationContext(), Main2Activity.class));
        }

        if (id == R.id.nav_stream) {
            startActivity(new Intent(getApplicationContext(), Stream.class));
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_favorite);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);

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


    public void toast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.nav_fav);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer= (DrawerLayout) findViewById(R.id.activity_favorite);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
