package com.intouchapp.intouch.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class ListofStrings implements Parcelable {

    private List<String> relativeList = new ArrayList<>();
    private List<String> friendList = new ArrayList<>();
    private List<String> memberList = new ArrayList<>();

    public ListofStrings(List<String> relativeList, List<String> friendList, List<String> memberList) {
        this.relativeList = relativeList;
        this.friendList = friendList;
        this.memberList = memberList;
    }

    public ListofStrings() {

    }

    protected ListofStrings(Parcel in) {
        relativeList = in.createStringArrayList();
        friendList = in.createStringArrayList();
        memberList = in.createStringArrayList();
    }

    public static final Creator<ListofStrings> CREATOR = new Creator<ListofStrings>() {
        @Override
        public ListofStrings createFromParcel(Parcel in) {
            return new ListofStrings(in);
        }

        @Override
        public ListofStrings[] newArray(int size) {
            return new ListofStrings[size];
        }
    };

    public List<String> getRelativeList() {
        return relativeList;
    }

    public void setRelativeList(List<String> relativeList) {
        this.relativeList = relativeList;
    }

    public List<String> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<String> friendList) {
        this.friendList = friendList;
    }

    public List<String> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<String> memberList) {
        this.memberList = memberList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(relativeList);
        dest.writeStringList(friendList);
        dest.writeStringList(memberList);
    }
}
