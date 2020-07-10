package com.graduation.CancerEaseProj.Models;

import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.Timestamp;

public class Report {
    private String title;
    private String doctor_name;
    private String report;
    @ServerTimestamp
    private Timestamp timestamp;

    public Report() {
    }

    public Report(String title, String doctor_name, String report) {
        this.title = title;
        this.doctor_name = doctor_name;
        this.report = report;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDoctor_name() {
        return doctor_name;
    }

    public void setDoctor_name(String doctor_name) {
        this.doctor_name = doctor_name;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
