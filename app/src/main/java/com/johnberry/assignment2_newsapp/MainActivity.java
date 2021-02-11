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
    private HashMap<String, ArrayList<Story>> sourceMap = new HashMap<>();
    private ArrayList<Story> storyMaster = new ArrayList<Story>();

    private HashMap<String, String> selectedFilters = new HashMap<>();

    private HashMap<String, String> languageCodeMap = new HashMap<String, String>();
    private HashMap<String, String> countryCodeMap = new HashMap<String, String>();

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private List<Fragment> fragments;
    private MyPageAdapter pageAdapter;
    private ViewPager pager;
    public static int screenWidth, screenHeight;
    private String sourceID;

    private ArrayList<String> topicList = new ArrayList<String>();
    private ArrayList<String> countryList = new ArrayList<String>();
    private ArrayList<String> languageList = new ArrayList<String>();
    private ArrayList<String> sourceList = new ArrayList<String>();

    private String topMenu;
    private String currentSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (topicData.isEmpty()) {
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

    public void setupStories(ArrayList<Story> storiesIn) {

        for (Story s : storiesIn){
            storyMaster.add(s);
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
        setTitle("Globe News (" + String.format("%d%n", drawerArray.size()) + ")");
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_item, drawerArray));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    // RUNS WHEN A DRAWER ITEM IS SELECTED!! CALLS API TO GET INFO FOR STORY
    private void selectItem(int position) {
        pager.setBackground(null);
        String itemSelected = drawerArray.get(position);

        currentSource = itemSelected.toString();
        setTitle(currentSource);

        for (Story s : storyMaster) {
            if (s.getSourceName().equalsIgnoreCase(itemSelected)) {
                sourceID = s.getId();
            }
            mDrawerLayout.closeDrawer(mDrawerList);
        }
        new Thread(new SourceLoaderRunnable(this, sourceID)).start();
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

        topicSubMenu.add("All topics");
        countrySubMenu.add("All countries");
        languageSubMenu.add("All languages");

        if(topicList.size() > 0) {
            for (String s : topicList) {
                topicSubMenu.add(s);
            }
            for (String s : countryList) {
                try {
                    JSONArray countryCodes = translateCodes(s, 1);

                    for (int i = 0; i < countryCodes.length(); i++) {
                        JSONObject jCountry = countryCodes.getJSONObject(i);
                        String translatedName = jCountry.getString("name");
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
                        String translatedName = jLanguage.getString("name");
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

    // CALLED FROM SourceLoaderRunnable LOADER after drawer selection made
    public void setStoryFragments(ArrayList<Article> storyList) {

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
        String currMenu;
        String selectedFilter = item.toString();

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }

        for(String s : topicList){
            if(selectedFilter.equalsIgnoreCase(s)){
                if(selectedFilters.containsKey("topic")){
                    selectedFilters.replace("topic", s);
                }
                else{
                    selectedFilters.put("topic", s);
                }
            }
        }

        for(String s : countryList){
            String upperS = s.toUpperCase();
            String translatedCountry = countryCodeMap.get(upperS);

            if(selectedFilter.equalsIgnoreCase(translatedCountry)){
                if(selectedFilters.containsKey("country")){
                    selectedFilters.replace("country", s);
                }
                else{
                    selectedFilters.put("country", s);
                }
            }
        }

        for(String s : languageList){
            String upperS = s.toUpperCase();
            String translatedLanguage = languageCodeMap.get(upperS);

            if(selectedFilter.equalsIgnoreCase(translatedLanguage)){
                if(selectedFilters.containsKey("language")){
                    selectedFilters.replace("language", s);
                }
                else{
                    selectedFilters.put("language", s);
                }
            }
        }

        drawerArray.clear();

        switch(item.toString()){
            case "Topics":
                currMenu = "Topics";
                topMenu = currMenu;
                break;
            case "Countries":
                currMenu = "Countries";
                topMenu = currMenu;
                break;
            case "Languages":
                currMenu = "Languages";
                topMenu = currMenu;
                break;
        }

        String topicVal, countryVal, langVal;
        topicVal = selectedFilters.get("topic");
        countryVal = selectedFilters.get("country");
        langVal = selectedFilters.get("language");

        ArrayList<String> filterTypes = new ArrayList<>();
        int topicFlag = 0;
        int countryFlag= 0;
        int langFlag = 0;

            for(String k : selectedFilters.keySet()){
                if(k != null){
                    switch(k) {
                        case "topic":
                            topicFlag = 1;
                            break;
                        case "country":
                            countryFlag = 1;
                            break;
                        case "language":
                            langFlag = 1;
                    }
                }
            }

        if(selectedFilter =="All topics"){
            selectedFilters.remove("topic");
            topicFlag = 0;
            topicVal = null;
        }
        else if(selectedFilter =="All countries"){
            selectedFilters.remove("country");
            countryFlag = 0;
            countryVal = null;
        }
        else if(selectedFilter =="All languages"){
            selectedFilters.remove("language");
            langFlag = 0;
            langVal = null;
        }

            for(Story story : storyMaster){
                if (langFlag == 0 && topicFlag == 0 && countryFlag == 0){
                    drawerArray.add(story.getSourceName());
                }
                if (langFlag == 1 && topicFlag == 1 && countryFlag == 1){
                    if(story.getCategory().equalsIgnoreCase(topicVal) && story.getCountry().equalsIgnoreCase(countryVal) && story.getLanguage().equalsIgnoreCase(langVal)){
                        drawerArray.add(story.getSourceName());
                    }
                }
                else if(langFlag == 1 && topicFlag == 1 && countryFlag == 0){
                    if(story.getCategory().equalsIgnoreCase(topicVal) && story.getLanguage().equalsIgnoreCase(langVal)){
                        drawerArray.add(story.getSourceName());
                    }
                }
                else if(langFlag == 1 && topicFlag == 0 && countryFlag == 0){
                    if(story.getLanguage().equalsIgnoreCase(langVal)){
                        drawerArray.add(story.getSourceName());
                    }
                }
                else if(langFlag == 0 && topicFlag == 1 && countryFlag == 1){
                    if(story.getCategory().equalsIgnoreCase(topicVal) && story.getCountry().equalsIgnoreCase(countryVal)){
                        drawerArray.add(story.getSourceName());
                    }
                }
                else if(langFlag == 0 && topicFlag == 0 && countryFlag == 1){
                    if(story.getCountry().equalsIgnoreCase(countryVal)){
                        drawerArray.add(story.getSourceName());
                    }
                }
                else if(langFlag == 1 && topicFlag == 0 && countryFlag == 1){
                    if(story.getLanguage().equalsIgnoreCase(langVal) && story.getCountry().equalsIgnoreCase(countryVal)){
                        drawerArray.add(story.getSourceName());
                    }
                }
                else if(langFlag == 0 && topicFlag == 1 && countryFlag == 0){
                    if(story.getCategory().equalsIgnoreCase(topicVal)){
                        drawerArray.add(story.getSourceName());
                    }
                }
            }

        ((ArrayAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_item, drawerArray));

        setTitle("Globe News (" + String.format("%d%n", drawerArray.size()) + ")");
        return super.onOptionsItemSelected(item);
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


    private JSONArray translateCodes(String codeIn, int typeCode) throws IOException, JSONException {
        InputStream is;
        JSONArray cleanJSON;
        JSONArray cleanJSONLang;

        //Create global hashmap of country and languagecodes;
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

            for(int i = 0; i < cleanJSON.length(); i++){
                JSONObject codeObj = (JSONObject) cleanJSON.get(i);
                String code = codeObj.getString("code");
                String name = codeObj.getString("name");
                countryCodeMap.put(code, name);
            }

            return cleanJSON;
        }
        else{
            cleanJSONLang = jsonCodes.getJSONArray("languages");

            for(int i = 0; i < cleanJSONLang.length(); i++){
                JSONObject codeObj = (JSONObject) cleanJSONLang.get(i);
                String code = codeObj.getString("code");
                String name = codeObj.getString("name");
                languageCodeMap.put(code, name);
            }
            return cleanJSONLang ;
        }
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

        void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            baseId += getCount() + n;
        }
    }
}