package com.johnberry.assignment2_newsapp;

import java.io.Serializable;

public class Article implements Serializable {

    private String author, title, description, url, urlToImage, publishedAt;

    Article(String author, String title, String description, String url, String urlToImage, String publishedAt){

        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
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
}
