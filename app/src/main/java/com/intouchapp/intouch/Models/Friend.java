package com.intouchapp.intouch.Models;

import java.util.List;

public class Friend {
    private List<String> f_id;
    String h_id;

    public Friend() {

    }


    public Friend(List<String> f_id, String h_id) {
        this.f_id = f_id;
        this.h_id = h_id;
    }

    public List<String> getF_id() {
        return f_id;
    }

    public void setF_id(List<String> f_id) {
        this.f_id = f_id;
    }

    public String getH_id() {
        return h_id;
    }

    public void setH_id(String h_id) {
        this.h_id = h_id;
    }
}
