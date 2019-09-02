package com.intouchapp.intouch.Models;

import com.google.firebase.Timestamp;

public class Message {
    String id,message,s_id;
    Timestamp timestamp;
    String name;

    public Message() {

    }

    public Message(String id, String message, String s_id, Timestamp timestamp, String name) {
        this.id = id;
        this.message = message;
        this.s_id = s_id;
        this.timestamp = timestamp;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getS_id() {
        return s_id;
    }

    public void setS_id(String s_id) {
        this.s_id = s_id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
