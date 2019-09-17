package com.intouchapp.intouch.Models;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.util.Map;

public class User {

    private String u_id;
    private String email;
    private String username;
    private String name;
    private String bio;
    private String p_no;
    private Timestamp timestamp;

    private String n_id;
    private String h_id;

    private String public_avatar;
    private String rel_avatar;
    private String friend_avatar;
    private String family_avatar;

    private String code;

    private String device_token;

    public User() {
    }

    public User(String u_id, String email, String username, String name, String bio, String p_no, Timestamp timestamp, String n_id, String h_id, String public_avatar, String rel_avatar, String friend_avatar, String family_avatar, String code, String device_token) {
        this.u_id = u_id;
        this.email = email;
        this.username = username;
        this.name = name;
        this.bio = bio;
        this.p_no = p_no;
        this.timestamp = timestamp;
        this.n_id = n_id;
        this.h_id = h_id;
        this.public_avatar = public_avatar;
        this.rel_avatar = rel_avatar;
        this.friend_avatar = friend_avatar;
        this.family_avatar = family_avatar;
        this.code = code;
        this.device_token = device_token;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getP_no() {
        return p_no;
    }

    public void setP_no(String p_no) {
        this.p_no = p_no;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getN_id() {
        return n_id;
    }

    public void setN_id(String n_id) {
        this.n_id = n_id;
    }

    public String getH_id() {
        return h_id;
    }

    public void setH_id(String h_id) {
        this.h_id = h_id;
    }

    public String getPublic_avatar() {
        return public_avatar;
    }

    public void setPublic_avatar(String public_avatar) {
        this.public_avatar = public_avatar;
    }

    public String getRel_avatar() {
        return rel_avatar;
    }

    public void setRel_avatar(String rel_avatar) {
        this.rel_avatar = rel_avatar;
    }

    public String getFriend_avatar() {
        return friend_avatar;
    }

    public void setFriend_avatar(String friend_avatar) {
        this.friend_avatar = friend_avatar;
    }

    public String getFamily_avatar() {
        return family_avatar;
    }

    public void setFamily_avatar(String family_avatar) {
        this.family_avatar = family_avatar;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }
}
