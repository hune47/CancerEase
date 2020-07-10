package com.graduation.CancerEaseProj.Models;

import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.Timestamp;

public class Appointment {
    private  int confirmed;
    private  String id, name, clinic;
    @ServerTimestamp
    private Timestamp timestamp;

    public Appointment() {
    }

    public Appointment(int confirmed, String id, String name, String clinic, Timestamp timestamp) {
        this.confirmed = confirmed;
        this.id = id;
        this.name = name;
        this.clinic = clinic;
        this.timestamp = timestamp;
    }

    public Appointment(int confirmed, String name, String clinic, Timestamp timestamp) {
        this.confirmed = confirmed;
        this.name = name;
        this.clinic = clinic;
        this.timestamp = timestamp;
    }

    public int getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(int confirmed) {
        this.confirmed = confirmed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClinic() {
        return clinic;
    }

    public void setClinic(String clinic) {
        this.clinic = clinic;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
