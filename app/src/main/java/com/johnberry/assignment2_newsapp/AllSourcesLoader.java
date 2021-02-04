package com.johnberry.assignment2_newsapp;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;

import static java.net.HttpURLConnection.HTTP_OK;

public class AllSourcesLoader implements  Runnable {

    private static final String TAG = "AllSourcesLoader";
    private final MainActivity mainActivity;
    private static final String API_KEY = "80b0447675db40dda819d9d466b5a3e8";
    private static final String baseDataURL = "https://newsapi.org/v2/sources?apiKey=" + API_KEY;

    AllSourcesLoader(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    public void run() {

        System.out.println("All Sources Loader runnable called!");

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

                System.out.println("AllSourcesLoader received: " + sb);

//                HashMap<String, HashSet<String>> regionMap = parseJSON(sb.toString());
//                if (regionMap != null) {
//                    mainActivity.runOnUiThread(() -> mainActivity.setupRegions(regionMap));
//                }
            } else {

                BufferedReader reader =
                        new BufferedReader((new InputStreamReader(conn.getErrorStream())));

                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                conn.disconnect();

                System.out.println("AllSourcesLoader received: " + sb);
                Log.d(TAG, "run: " + sb.toString());


            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
