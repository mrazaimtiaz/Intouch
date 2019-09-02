package com.intouchapp.intouch.Models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.List;
import java.util.Map;

public class House {

    private String h_id;
    private String n_id;
    private String image;
    private GeoPoint location;
    private String name;
    private String bio;
    private List<String> members;
    private Timestamp timestamp;


    public House(){

    }

    public House(String h_id, String n_id, String image, GeoPoint location, String name, String bio, List<String> members, Timestamp timestamp) {
        this.h_id = h_id;
        this.n_id = n_id;
        this.image = image;
        this.location = location;
        this.name = name;
        this.bio = bio;
        this.members = members;
        this.timestamp = timestamp;
    }

    public String getH_id() {
        return h_id;
    }

    public void setH_id(String h_id) {
        this.h_id = h_id;
    }

    public String getN_id() {
        return n_id;
    }

    public void setN_id(String n_id) {
        this.n_id = n_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
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

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
