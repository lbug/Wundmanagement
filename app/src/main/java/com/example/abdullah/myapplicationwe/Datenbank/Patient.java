package com.example.abdullah.myapplicationwe.Datenbank;

public class Patient {
    private long PatientID;


    public Patient(long PatientID) {
        this.PatientID = PatientID;
    }

    public long getId() {
        return PatientID;
    }

    public void setId(long PatientID) {
        this.PatientID = PatientID;
    }

}
