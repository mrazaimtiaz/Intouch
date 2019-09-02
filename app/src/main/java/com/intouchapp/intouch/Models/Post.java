package com.intouchapp.intouch.Models;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.util.List;
import java.util.Map;


public class Post implements Parcelable {
    private String u_id;
    private String p_id;
    private String type;
    private String description;
    private String image;
    private String shareWith;
    private List<String> like;
    private Map<String,String> comment;
    private Timestamp timestamp;

    public Post(){

    }

    public Post(String u_id, String p_id, String type, String description, String image, String shareWith, List<String> like, Map<String, String> comment, Timestamp timestamp) {
        this.u_id = u_id;
        this.p_id = p_id;
        this.type = type;
        this.description = description;
        this.image = image;
        this.shareWith = shareWith;
        this.like = like;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    protected Post(Parcel in) {
        u_id = in.readString();
        p_id = in.readString();
        type = in.readString();
        description = in.readString();
        image = in.readString();
        shareWith = in.readString();
        like = in.createStringArrayList();
        timestamp = in.readParcelable(Timestamp.class.getClassLoader());
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getP_id() {
        return p_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getShareWith() {
        return shareWith;
    }

    public void setShareWith(String shareWith) {
        this.shareWith = shareWith;
    }

    public List<String> getLike() {
        return like;
    }

    public void setLike(List<String> like) {
        this.like = like;
    }

    public Map<String, String> getComment() {
        return comment;
    }

    public void setComment(Map<String, String> comment) {
        this.comment = comment;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(u_id);
        dest.writeString(p_id);
        dest.writeString(type);
        dest.writeString(description);
        dest.writeString(image);
        dest.writeString(shareWith);
        dest.writeStringList(like);
        dest.writeParcelable(timestamp, flags);
    }
}
