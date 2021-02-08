package com.johnberry.assignment2_newsapp;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final HashMap<String, ArrayList<String>> newsData = new HashMap<>();
    private Menu opt_menu;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private List<Fragment> fragments;
//    private MyPageAdapter pageAdapter;
    private ViewPager pager;
    public static int screenWidth, screenHeight;

    private ArrayList<String> topicList = new ArrayList<String>();
    private ArrayList<String> countryList = new ArrayList<String>();
    private ArrayList<String> languageList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        System.out.println("Loaded main Activity");
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.drawer_list);

//        mDrawerList.setOnItemClickListener(
//                (parent, view, position, id) -> {
//                    selectItem(position);
//                    mDrawerLayout.closeDrawer(mDrawerList);
//                }
//        );

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );

        fragments = new ArrayList<>();

        pager = findViewById(R.id.viewpager);
//        pager.setAdapter(pageAdapter);

        if (newsData.isEmpty()) {
            System.out.println("newsData Hashmap is empty; Calling AllSourcesLoader");
            new Thread(new AllSourcesLoader(this)).start();
        }

    }


///NEED TO PASS IN HASHMAP FROM LOADER
    public void setupStories(ArrayList<Story> storiesIn) {
        System.out.println("setupStories received this many: " + storiesIn.size());

//        newsData.clear();

//        for (String s : storiesIn.keySet()) {
//            HashSet<String> hSet = storyMapIn.get(s);
//            if (hSet == null)
//                continue;
//            ArrayList<String> topicList = new ArrayList<>(hSet);
//            Collections.sort(topicList);
////            storyMapIn.put(s, subRegions);
//        }

//        ArrayList<String> topicList = new ArrayList<>();

        for (Story s : storiesIn){

            String topic = s.getCategory();
            String language = s.getLanguage();
            String country = s.getCountry();

            if(!topicList.contains(topic)) {
                topicList.add(topic);
            }
            if(!languageList.contains(language)) {
                languageList.add(language);
            }
            if(!countryList.contains(country)) {
                countryList.add(country);
            }
        }

        Collections.sort(topicList);
        for (String s : topicList)
            opt_menu.add(s);

//        ArrayList<String> lst = newsData.get(top.get(0));
//        if (lst != null) {
////            subRegionDisplayed.addAll(lst);
//        }

//        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_item, subRegionDisplayed));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        opt_menu = menu;
        return true;
    }
}