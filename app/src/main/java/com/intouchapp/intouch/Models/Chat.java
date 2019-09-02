package com.intouchapp.intouch.Models;

import com.google.firebase.Timestamp;

public class Chat {

private String id,id1,id2,category;
Timestamp timestamp;

    public Chat() {

    }

    public Chat(String id, String id1, String id2, String category, Timestamp timestamp) {
        this.id = id;
        this.id1 = id1;
        this.id2 = id2;
        this.category = category;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId1() {
        return id1;
    }

    public void setId1(String id1) {
        this.id1 = id1;
    }

    public String getId2() {
        return id2;
    }

    public void setId2(String id2) {
        this.id2 = id2;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
