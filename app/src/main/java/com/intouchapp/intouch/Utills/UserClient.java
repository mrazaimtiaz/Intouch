package com.intouchapp.intouch.Utills;


import android.app.Application;

import com.intouchapp.intouch.Models.User;


public class UserClient extends Application {

    private User user = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
