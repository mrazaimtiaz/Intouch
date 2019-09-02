package com.intouchapp.intouch.Models;

import java.util.List;

public class Relative {
    private List<String> r_id;
    String h_id;

    public Relative() {

    }
    public Relative(List<String> r_id, String h_id) {
        this.r_id = r_id;
        this.h_id = h_id;
    }

    public List<String> getR_id() {
        return r_id;
    }

    public void setR_id(List<String> r_id) {
        this.r_id = r_id;
    }

    public String getH_id() {
        return h_id;
    }

    public void setH_id(String h_id) {
        this.h_id = h_id;
    }
}
