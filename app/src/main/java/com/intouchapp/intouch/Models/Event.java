package com.intouchapp.intouch.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.util.List;

public class Event implements Parcelable {
    String e_id;
    String u_id;
    String type;
    String shareWith;
    String description;
    String place;
    String category;
    String date;
    String time;
    String image;
    Timestamp timestamp;
    List<String> joined;
    List<String> rejected;

    public  Event(){

    }

    public Event(String e_id, String u_id, String type, String shareWith, String description, String place, String category, String date, String time, String image, Timestamp timestamp, List<String> joined, List<String> rejected) {
        this.e_id = e_id;
        this.u_id = u_id;
        this.type = type;
        this.shareWith = shareWith;
        this.description = description;
        this.place = place;
        this.category = category;
        this.date = date;
        this.time = time;
        this.image = image;
        this.timestamp = timestamp;
        this.joined = joined;
        this.rejected = rejected;
    }

    protected Event(Parcel in) {
        e_id = in.readString();
        u_id = in.readString();
        type = in.readString();
        shareWith = in.readString();
        description = in.readString();
        place = in.readString();
        category = in.readString();
        date = in.readString();
        time = in.readString();
        image = in.readString();
        timestamp = in.readParcelable(Timestamp.class.getClassLoader());
        joined = in.createStringArrayList();
        rejected = in.createStringArrayList();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public String getE_id() {
        return e_id;
    }

    public void setE_id(String e_id) {
        this.e_id = e_id;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getShareWith() {
        return shareWith;
    }

    public void setShareWith(String shareWith) {
        this.shareWith = shareWith;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getJoined() {
        return joined;
    }

    public void setJoined(List<String> joined) {
        this.joined = joined;
    }

    public List<String> getRejected() {
        return rejected;
    }

    public void setRejected(List<String> rejected) {
        this.rejected = rejected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(e_id);
        dest.writeString(u_id);
        dest.writeString(type);
        dest.writeString(shareWith);
        dest.writeString(description);
        dest.writeString(place);
        dest.writeString(category);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(image);
        dest.writeParcelable(timestamp, flags);
        dest.writeStringList(joined);
        dest.writeStringList(rejected);
    }
}
