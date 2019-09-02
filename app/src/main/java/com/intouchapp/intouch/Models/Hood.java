package com.intouchapp.intouch.Models;

import com.google.firebase.firestore.GeoPoint;

import java.util.List;

public class Hood {

    private String id;
    private GeoPoint longitude;
    private GeoPoint latitude;
    private String image;
    private String name;
    private String bio;
    private List<String> fallower;

    public Hood(){

    }

    public Hood(String id, GeoPoint longitude, GeoPoint latitude, String image, String name, String bio, List<String> fallower) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.image = image;
        this.name = name;
        this.bio = bio;
        this.fallower = fallower;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public GeoPoint getLongitude() {
        return longitude;
    }

    public void setLongitude(GeoPoint longitude) {
        this.longitude = longitude;
    }

    public GeoPoint getLatitude() {
        return latitude;
    }

    public void setLatitude(GeoPoint latitude) {
        this.latitude = latitude;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<String> getFallower() {
        return fallower;
    }

    public void setFallower(List<String> fallower) {
        this.fallower = fallower;
    }
}
