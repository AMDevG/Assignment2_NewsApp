package com.johnberry.assignment2_newsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final HashMap<String, ArrayList<String>> topicData = new HashMap<>();
    private Menu opt_menu;
    private final ArrayList<String> drawerArray = new ArrayList<>();
    private HashMap<String, ArrayList<Story>> languageMap = new HashMap<>();
    private HashMap<String, ArrayList<Story>> countryMap = new HashMap<>();
    private HashMap<String, ArrayList<Story>> topicMap = new HashMap<>();

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private List<Fragment> fragments;
    private MyPageAdapter pageAdapter;
    private ViewPager pager;
    public static int screenWidth, screenHeight;

    private ArrayList<String> topicList = new ArrayList<String>();
    private ArrayList<String> countryList = new ArrayList<String>();
    private ArrayList<String> languageList = new ArrayList<String>();
    private ArrayList<String> sourceList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (topicData.isEmpty()) {
//            System.out.println("newsData Hashmap is empty; Calling AllSourcesLoader");
            new Thread(new AllSourcesLoader(this)).start();
        }
        setContentView(R.layout.activity_main);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.drawer_list);

        mDrawerList.setOnItemClickListener(
                (parent, view, position, id) -> {
                    selectItem(position);
                    mDrawerLayout.closeDrawer(mDrawerList);
                }
        );

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );

        fragments = new ArrayList<>();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);
    }



///NEED TO PASS IN HASHMAP FROM LOADER
    public void setupStories(ArrayList<Story> storiesIn) {

        // Setup Hashmap to populate drawer adapter


        for (Story s : storiesIn){
            String topic = s.getCategory();
            String language = s.getLanguage();
            String country = s.getCountry();
            String source = s.getSourceName();

            // Sets up options submenus
            if(!topicList.contains(topic)) {
                topicList.add(topic);
            }
            if(!languageList.contains(language)) {
                languageList.add(language);
            }
            if(!countryList.contains(country)) {
                countryList.add(country);
            }
            if(!sourceList.contains(source)) {
                sourceList.add(source);
            }
        }

        for(String s : sourceList){
            drawerArray.add(s);
        }
        Collections.sort(topicList);
        Collections.sort(languageList);
        Collections.sort(countryList);
        Collections.sort(sourceList);

        for(String s : topicList){

            ArrayList<Story> mapList = new ArrayList<Story>();
            for(Story story : storiesIn){
                if(story.getCategory().equalsIgnoreCase(s)){
                    mapList.add(story);
                }
            topicMap.put(s,mapList);
            }
        }

        for(String s : countryList){
            ArrayList<Story> mapList = new ArrayList<Story>();
            for(Story story : storiesIn){
                if(story.getCategory().equalsIgnoreCase(s)){
                    mapList.add(story);
                }
                countryMap.put(s,mapList);
            }
        }

        for(String s : languageList){
            ArrayList<Story> mapList = new ArrayList<Story>();
            for(Story story : storiesIn){
                if(story.getCategory().equalsIgnoreCase(s)){
                    mapList.add(story);
                }
                languageMap.put(s,mapList);
            }
        }


        opt_menu.clear();
        onCreateOptionsMenu(opt_menu);



        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_item, drawerArray));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    // RUNS WHEN A DRAWER ITEM IS SELECTED!! CALLS API TO GET INFO FOR STORY
    private void selectItem(int position) {
        pager.setBackground(null);
        System.out.println("Item selected: " + drawerArray.get(position));
//        setStoryFragments();
//        currentSubRegion = subRegionDisplayed.get(position);
//        new Thread(new SubRegionLoader(this, currentSubRegion)).start();
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        opt_menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        MenuItem topicItem = menu.findItem(R.id.topic_menu);
        MenuItem countryItem = menu.findItem(R.id.country_menu);
        MenuItem languageItem = menu.findItem(R.id.language_menu);

        SubMenu topicSubMenu = topicItem.getSubMenu();
        SubMenu countrySubMenu = countryItem.getSubMenu();
        SubMenu languageSubMenu = languageItem.getSubMenu();

        topicSubMenu.add("All");
        countrySubMenu.add("All");
        languageSubMenu.add("All");

        if(topicList.size() > 0) {
            for (String s : topicList) {
                topicSubMenu.add(s);
            }
            for (String s : countryList) {
                try {
                    JSONArray countryCodes = translateCodes(s, 1);

                    for (int i = 0; i < countryCodes.length(); i++) {
                        JSONObject jCountry = countryCodes.getJSONObject(i);
//                        System.out.println("Country Object: " + jCountry);
                        String translatedName = jCountry.getString("name");
//                        System.out.println("S: " + s + " " + jCountry.getString("code"));
                        if (jCountry.getString("code").equalsIgnoreCase(s)) {
                            countrySubMenu.add(translatedName);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            for (String s : languageList) {
                try {
                    JSONArray languageCodes = translateCodes(s, 2);
                    for (int i = 0; i < languageCodes.length(); i++) {
                        JSONObject jLanguage = languageCodes.getJSONObject(i);
//                        System.out.println("Country Object: " + jCountry);
                        String translatedName = jLanguage.getString("name");
//                        System.out.println("S: " + s + " " + jLanguage.getString("code"));
                        if (jLanguage.getString("code").equalsIgnoreCase(s)) {
                            languageSubMenu.add(translatedName);
                        }

                    }
                }   catch(IOException e){
                        e.printStackTrace();
                    } catch(JSONException e){
                        e.printStackTrace();
                    }
               }
            }
        return true;
    }

    // CALLED FROM SUBREGION LOADER
    // COUNTRY OBJECTS ARE CREATED IN SUBREGION LOADER
    //
    public void setStoryFragments(ArrayList<Story> storyList) {

//        setTitle(currentSubRegion);

        for (int i = 0; i < pageAdapter.getCount(); i++)
            pageAdapter.notifyChangeInPosition(i);
        fragments.clear();

        // CREATES FRAGMENT FOR EACH STORY OBJECT PASSED
        for (int i = 0; i < storyList.size(); i++) {
            fragments.add(
                    StoryFragment.newInstance(storyList.get(i), i+1, storyList.size()));
        }

        pageAdapter.notifyDataSetChanged();
        pager.setCurrentItem(0);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }
        System.out.println("item selected: " + item);

        setTitle(item.getTitle());

//        subRegionDisplayed.clear();
//        ArrayList<String> lst = regionData.get(item.getTitle().toString());
//        if (lst != null) {
//            subRegionDisplayed.addAll(lst);
//        }

//        ((ArrayAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }



    private void hideOption(int id)
    {
        MenuItem item = opt_menu.findItem(id);
        item.setVisible(false);
    }

    private void showOption(int id)
    {
        MenuItem item = opt_menu.findItem(id);
        item.setVisible(true);
    }

    private void setOptionTitle(int id, String title)
    {
        MenuItem item = opt_menu.findItem(id);
        item.setTitle(title);
    }

    private void setOptionIcon(int id, int iconRes)
    {
        MenuItem item = opt_menu.findItem(id);
        item.setIcon(iconRes);
    }

    private JSONArray translateCodes(String codeIn, int typeCode) throws IOException, JSONException {
        InputStream is;
        JSONArray cleanJSON;

        if(typeCode == 1) {
             is = getResources().openRawResource(R.raw.country_codes);
        }
        else{
             is = getResources().openRawResource(R.raw.language_codes);
        }

        Writer writer = new StringWriter();
        char[] buffer = new char[1024];

        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            is.close();
        }

        String jsonString = writer.toString();
        JSONObject jsonCodes = new JSONObject(jsonString);

        if(typeCode == 1) {
             cleanJSON = jsonCodes.getJSONArray("countries");
        }
        else{
             cleanJSON = jsonCodes.getJSONArray("languages");
        }
        return cleanJSON ;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private class MyPageAdapter extends FragmentPagerAdapter {
        private long baseId = 0;


        MyPageAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            // give an ID different from position when position has been changed
            return baseId + position;
        }

        /**
         * Notify that the position of a fragment has been changed.
         * Create a new ID for each position to force recreation of the fragment
         * @param n number of items which have been changed
         */
        void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            baseId += getCount() + n;
        }

    }

}