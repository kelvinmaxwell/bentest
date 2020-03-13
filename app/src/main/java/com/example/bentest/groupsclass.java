package com.example.bentest;

import java.util.ArrayList;

public class groupsclass {
    private String title, thumbnailUrl,bit;
    private String year;
    private String rating,price,description;
    private ArrayList<String> genre;

    public groupsclass() {
    }

    public groupsclass(String name, String thumbnailUrl, String year, String rating,
                       ArrayList<String> genre, String price) {
        this.title = name;
        this.thumbnailUrl = thumbnailUrl;
        this.year = year;
        this.rating = rating;
        this.genre = genre;
        this.description=description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
    public String getdescription() {
        return description;
    }

    public void setdescription(String description) {
        this.description = description;
    }





}
