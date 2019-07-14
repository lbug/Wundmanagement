package com.example.abdullah.myapplicationwe.Datenbank;

public class Wound {
    private long WoundID;

    public Wound(long WoundID) {
        this.WoundID = WoundID;
    }

    //Getter & Setter
    public long getWoundID() {
        return WoundID;
    }
    public void setWoundID(long woundID) {
        WoundID = woundID;
    }

}
