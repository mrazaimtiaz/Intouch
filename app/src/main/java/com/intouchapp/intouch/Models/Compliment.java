package com.intouchapp.intouch.Models;

import java.util.List;

public class Compliment {

    private Double comp1,comp2,comp3,comp4,comp5,comp6;
    private List<String> u_id;

    public Compliment(){

    }

    public Compliment(Double comp1, Double comp2, Double comp3, Double comp4, Double comp5, Double comp6, List<String> u_id) {
        this.comp1 = comp1;
        this.comp2 = comp2;
        this.comp3 = comp3;
        this.comp4 = comp4;
        this.comp5 = comp5;
        this.comp6 = comp6;
        this.u_id = u_id;
    }

    public Double getComp1() {
        return comp1;
    }

    public void setComp1(Double comp1) {
        this.comp1 = comp1;
    }

    public Double getComp2() {
        return comp2;
    }

    public void setComp2(Double comp2) {
        this.comp2 = comp2;
    }

    public Double getComp3() {
        return comp3;
    }

    public void setComp3(Double comp3) {
        this.comp3 = comp3;
    }

    public Double getComp4() {
        return comp4;
    }

    public void setComp4(Double comp4) {
        this.comp4 = comp4;
    }

    public Double getComp5() {
        return comp5;
    }

    public void setComp5(Double comp5) {
        this.comp5 = comp5;
    }

    public Double getComp6() {
        return comp6;
    }

    public void setComp6(Double comp6) {
        this.comp6 = comp6;
    }

    public List<String> getU_id() {
        return u_id;
    }

    public void setU_id(List<String> u_id) {
        this.u_id = u_id;
    }
}
