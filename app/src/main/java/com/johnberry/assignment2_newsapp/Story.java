package com.johnberry.assignment2_newsapp;

import java.io.Serializable;

public class Story implements Serializable {

    private String id, name, description, url, category, language, country;

    Story(String id, String name, String description, String url, String category, String language, String country){
        this.id = id;
        this.name = name;
        this.description = description;
        this.url = url;
        this.category = category;
        this.language = language;
        this.country = country;
    }

    public String getId() {
        return id;
    }

    public String getSourceName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }
    public String getLanguage(){
        return language;
    }

    public String getCountry() {
        return country;
    }

    public String getCategory() {
        return category;
    }
}
