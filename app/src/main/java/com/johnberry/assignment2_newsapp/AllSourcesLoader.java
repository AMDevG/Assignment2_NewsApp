package com.johnberry.assignment2_newsapp;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static java.net.HttpURLConnection.HTTP_OK;

public class AllSourcesLoader implements  Runnable {

    private static final String TAG = "AllSourcesLoader";
    private final MainActivity mainActivity;
    private static final String API_KEY = "80b0447675db40dda819d9d466b5a3e8";
    private static final String baseDataURL = "https://newsapi.org/v2/sources?apiKey=" + API_KEY;

    private ArrayList<Story> storyArrayList = new ArrayList<Story>();


    AllSourcesLoader(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    public void run() {

        Uri dataUri = Uri.parse(baseDataURL);
        String urlToUse = dataUri.toString();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.addRequestProperty("User-Agent","");
            conn.connect();

            StringBuilder sb = new StringBuilder();
            String line;

            if (conn.getResponseCode() == HTTP_OK) {
                BufferedReader reader =
                        new BufferedReader((new InputStreamReader(conn.getInputStream())));

                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                conn.disconnect();

            } else {

                BufferedReader reader =
                        new BufferedReader((new InputStreamReader(conn.getErrorStream())));

                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                conn.disconnect();

                Log.d(TAG, "run: " + sb.toString());


            }

            JSONObject jsonResponse = new JSONObject(sb.toString());
            String storyStr = jsonResponse.getString("sources");
            ArrayList<Story> storyObjects = parseJSON(storyStr);

//            System.out.println("Story Objects are: " + storyObjects);

            if (storyObjects != null) {
                mainActivity.runOnUiThread(() -> mainActivity.setupStories(storyObjects));
            }



        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private ArrayList<Story> parseJSON(String s) {

        HashMap<String, HashSet<String>> storyMap = new HashMap<>();


        try {
            JSONArray jObjMain = new JSONArray(s);

            for (int i = 0; i < jObjMain.length(); i++) {
                JSONObject story = (JSONObject) jObjMain.get(i);
//                System.out.println("Parser processing story: " + story);
//                System.out.println("---------------------------------");

                String id = story.getString("id");
                String name = story.getString("name");
                String description = story.getString("description");
                String url = story.getString("url");
                String country = story.getString("country");
                String language = story.getString("language");
                String category = story.getString("category");

                Story newStory = new Story(id, name, description, url, category, language, country);
                storyArrayList.add(newStory);

            }
            return storyArrayList;
        } catch (
                Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
