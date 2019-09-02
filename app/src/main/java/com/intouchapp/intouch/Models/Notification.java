package com.intouchapp.intouch.Models;


import com.google.firebase.Timestamp;

public class Notification {
    private String id,c_id,s_id,r_id,category,type,status;

    private Timestamp timestamp;

    public Notification(){

    }

    public Notification(String id, String c_id, String s_id, String r_id, String category, String type, String status, Timestamp timestamp) {
        this.id = id;
        this.c_id = c_id;
        this.s_id = s_id;
        this.r_id = r_id;
        this.category = category;
        this.type = type;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getC_id() {
        return c_id;
    }

    public void setC_id(String c_id) {
        this.c_id = c_id;
    }

    public String getS_id() {
        return s_id;
    }

    public void setS_id(String s_id) {
        this.s_id = s_id;
    }

    public String getR_id() {
        return r_id;
    }

    public void setR_id(String r_id) {
        this.r_id = r_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
