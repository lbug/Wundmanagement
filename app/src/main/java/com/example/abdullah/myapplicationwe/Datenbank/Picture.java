package com.example.abdullah.myapplicationwe.Datenbank;

public class Picture {
    private int pictureID;
    private long timestamp;
    private String woundImagePath;
    private double woundHeight;
    private double woundWidth;

    private int woundSector;
    private double woundSize;

    public Picture(int id, long timestamp, String woundImagePath, double woundHeight, double woundWidth,double woundSize, int woundSector) {
        this.pictureID = id;
        this.timestamp = timestamp;
        this.woundImagePath = woundImagePath;
        this.woundHeight = woundHeight;
        this.woundWidth = woundWidth;
        this.woundSector = woundSector;
        this.woundSize = woundSize;
    }

    public long getPictureID() {
        return pictureID;
    }

    public void setPictureID(int pictureID) {
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

    public double getWoundHeight() {
        return woundHeight;
    }

    public void setWoundHeight(double woundHeight) {
        this.woundHeight = woundHeight;
    }

    public double getWoundWidth() {
        return woundWidth;
    }

    public void setWoundWidth(double woundWidth) {
        this.woundWidth = woundWidth;
    }

    public int getWoundSector() {
        return woundSector;
    }

    public void setWoundSector(int woundSector) {
        this.woundSector = woundSector;
    }

    public double getWoundSize() {
        return woundSize;
    }

    public void setWoundSize(double woundSize) {
        this.woundSize = woundSize;
    }
}
