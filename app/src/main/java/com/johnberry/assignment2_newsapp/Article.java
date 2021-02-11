package com.johnberry.assignment2_newsapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;

import com.caverock.androidsvg.SVG;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;

public class Article implements Serializable {

    private String author, title, description, url, urlToImage, publishedAt;
    private Bitmap bitmap;

    Article(String author, String title, String description, String url, String urlToImage, String publishedAt){

        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
        downloadDrawable(urlToImage);

    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

    private void downloadDrawable(String urlDownload){
        try {
            InputStream is = (InputStream) new URL(urlDownload).getContent();
            this.bitmap = BitmapFactory.decodeStream(is);
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
