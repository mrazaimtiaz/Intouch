package com.intouchapp.intouch.Models;

import com.google.firebase.Timestamp;

public class Request {

    String id;
    String s_id;
    String r_id;
    String status;
    String type;
    Timestamp timeStamp;

    public Request() {
    }

    public Request(String id, String s_id, String r_id, String status, String type, Timestamp timeStamp) {
        this.id = id;
        this.s_id = s_id;
        this.r_id = r_id;
        this.status = status;
        this.type = type;
        this.timeStamp = timeStamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }
}
