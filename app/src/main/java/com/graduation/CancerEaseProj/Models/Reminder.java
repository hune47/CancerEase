package com.graduation.CancerEaseProj.Models;

public class Reminder {
    private int id;
    private String pres_name, dosage_time, start_date_time ,end_date_time;

    public Reminder() {
    }

    public Reminder(String pres_name, String dosage_time, String start_date_time, String end_date_time) {
        this.pres_name = pres_name;
        this.dosage_time = dosage_time;
        this.start_date_time = start_date_time;
        this.end_date_time = end_date_time;
    }

    public Reminder(int id, String pres_name, String dosage_time, String start_date_time, String end_date_time) {
        this.id = id;
        this.pres_name = pres_name;
        this.dosage_time = dosage_time;
        this.start_date_time = start_date_time;
        this.end_date_time = end_date_time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPres_name() {
        return pres_name;
    }

    public void setPres_name(String pres_name) {
        this.pres_name = pres_name;
    }

    public String getDosage_time() {
        return dosage_time;
    }

    public void setDosage_time(String dosage_time) {
        this.dosage_time = dosage_time;
    }

    public String getStart_date_time() {
        return start_date_time;
    }

    public void setStart_date_time(String start_date_time) {
        this.start_date_time = start_date_time;
    }

    public String getEnd_date_time() {
        return end_date_time;
    }

    public void setEnd_date_time(String end_date_time) {
        this.end_date_time = end_date_time;
    }
}
