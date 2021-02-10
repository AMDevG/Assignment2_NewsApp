package com.johnberry.assignment2_newsapp;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static java.net.HttpURLConnection.HTTP_OK;

public class SourceLoaderRunnable implements Runnable {

    private static final String TAG = "AllSourcesLoader";
    private final MainActivity mainActivity;
    private final String sourceID;
    private static final String API_KEY = "80b0447675db40dda819d9d466b5a3e8";
    private static final String baseDataURL = "https://newsapi.org/v2/top-headlines?sources=";

    private ArrayList<Article> articleArrayList = new ArrayList<Article>();


    SourceLoaderRunnable(MainActivity ma, String sourceID) {
        mainActivity = ma;
        this.sourceID = sourceID;

    }


    @Override
    public void run() {
        String formattedURL = baseDataURL + sourceID + "&apiKey=" + API_KEY;
        Uri dataUri = Uri.parse(formattedURL);
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
            String storyStr = jsonResponse.getString("articles");
            ArrayList<Article> storyObjects = parseJSON(storyStr);


            if (storyObjects != null) {
                mainActivity.runOnUiThread(() -> mainActivity.setStoryFragments(storyObjects));
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

    private ArrayList<Article> parseJSON(String s) {

        try {
            JSONArray jObjMain = new JSONArray(s);

            for (int i = 0; i < jObjMain.length(); i++) {
                JSONObject story = (JSONObject) jObjMain.get(i);
//                System.out.println("Parser processing article: " + story);
//                System.out.println("---------------------------------");

                String author = story.getString("author");
                String title = story.getString("title");
                String url = story.getString("url");
                String urlToImage = story.getString("urlToImage");
                String description = story.getString("description");
                String publishedAt = story.getString("publishedAt");

//
                Article newArticle = new Article(author, title, description, url, urlToImage, publishedAt);
                articleArrayList.add(newArticle);

            }
            return articleArrayList;
        } catch (
                Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
