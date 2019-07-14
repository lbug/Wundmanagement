package com.example.abdullah.myapplicationwe.Datenbank;

public class Picture {
    private long pictureID;
    private long timestamp;
    private String woundImagePath;
    private double woundLength;
    private double woundWidth;

    public Picture(long id, long timestamp, String woundImagePath, double woundLength, double woundWidth) {
        this.pictureID = id;
        this.timestamp = timestamp;
        this.woundImagePath = woundImagePath;
        this.woundLength = woundLength;
        this.woundWidth = woundWidth;
    }

    public long getPictureID() {
        return pictureID;
    }

    public void setPictureID(long pictureID) {
        this.pictureID = pictureID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getWoundImagePath() {
        return woundImagePath;
    }

    public void setWoundImagePath(String woundImagePath) {
        this.woundImagePath = woundImagePath;
    }

    public double getWoundLength() {
        return woundLength;
    }

    public void setWoundLength(double woundLength) {
        this.woundLength = woundLength;
    }

    public double getWoundWidth() {
        return woundWidth;
    }

    public void setWoundWidth(double woundWidth) {
        this.woundWidth = woundWidth;
    }
}
